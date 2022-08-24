package infotecs.client;

import infotecs.client.exceptions.FTPConnectionException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static infotecs.client.utils.Constants.*;

public class FTPConnector {

    private final String login;
    private final String password;
    private final String serverIP;
    private boolean isActiveMode;
    private Socket cmdConnection;
    private BufferedWriter cmdWriter;
    private BufferedReader cmdReader;
    private Socket dataConnection;
    private ServerSocket serverSocket;

    public FTPConnector(String login, String password, String serverIP) {
        this.login = login;
        this.password = password;
        this.serverIP = serverIP;
        this.isActiveMode = false;
    }

    public void connect() throws IOException {
        cmdConnection = new Socket(serverIP, CMD_PORT);
        cmdWriter = new BufferedWriter(new OutputStreamWriter(cmdConnection.getOutputStream()));
        cmdReader = new BufferedReader(new InputStreamReader(cmdConnection.getInputStream()));
        getAndCheckResponse(220, "Can not connect to server");
        loginToServer(login, password);
    }

    private void loginToServer(String login, String password) throws IOException {
        cmdWriter.write(String.format("USER %s%n", login));
        cmdWriter.flush();
        getAndCheckResponse(331, "Login is incorrect");
        cmdWriter.write(String.format("PASS %s%n", password));
        cmdWriter.flush();
        getAndCheckResponse(230, "Authentication failed");
    }

    public void setActiveMode(boolean activeMode) {
        isActiveMode = activeMode;
    }

    private void setMode() throws IOException {
        if (isActiveMode) {
            connectInActiveMode();
        } else {
            connectInPassiveMode();
        }
    }

    public void connectInActiveMode() throws IOException {
        serverSocket = new ServerSocket(0);
        int localPort = serverSocket.getLocalPort();    // get random host port
        String inetString = cmdConnection.getLocalAddress().getHostAddress().replace('.', ',');  // get host IP
        // message: "PORT xxx,xxx,xxx,xxx,yyy,zzz   address: ip=xxx.xxx.xxx.xxx port=yyy*256+zzz
        String ip = String.format("%s,%d,%d", inetString, localPort >> 8, localPort & 255);
        cmdWriter.write(String.format("PORT %s%n", ip));    // send the address for data connection to ftp server
        cmdWriter.flush();
        getAndCheckResponse(200, "Can't connect in active mode");
    }

    public void connectInPassiveMode() throws IOException {
        cmdWriter.write("PASV\n");  // set to passive mode
        cmdWriter.flush();
        // message: "Entering passive mode (xxx,xxx,xxx,xxx,yyy,zzz)    address: ip=xxx.xxx.xxx.xxx port=yyy*256+zzz
        String response = getAndCheckResponse(227, "Can't connect in passive mode");    // server responds with address for data connection
        Matcher matcher = Pattern.compile(DATA_CHANNEL_PATTERN).matcher(response);
        if (!matcher.find()) {
            throw new FTPConnectionException("Can not set passive mode. Can not get IP from server");
        }
        String dataIP = matcher.group(1).replace(',', '.');
        int dataPort = Integer.parseInt(matcher.group(2)) * 256 + Integer.parseInt(matcher.group(3));
        dataConnection = new Socket(dataIP, dataPort);
    }

    public String download(String filename) throws IOException {
        setMode();
        cmdWriter.write(String.format("RETR %s%n", filename));
        cmdWriter.flush();
        getAndCheckResponse(150, "Can't find the file");
        dataConnection = isActiveMode ? serverSocket.accept() : dataConnection;
        BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));
        String jsonString = dataReader.lines().collect(Collectors.joining("\n"));
        dataReader.close();
        getAndCheckResponse(226, "Can't read the file");
        return jsonString;
    }

    public void upload(String jsonString, String filename) throws IOException {
        setMode();
        cmdWriter.write(String.format("STOR %s%n", filename));
        cmdWriter.flush();
        getAndCheckResponse(150, "Can't find the file");
        dataConnection = isActiveMode ? serverSocket.accept() : dataConnection;
        BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataConnection.getOutputStream()));
        dataWriter.write(jsonString + "\r\n");
        dataWriter.flush();
        dataWriter.close();
        getAndCheckResponse(226, "Can't save file");
    }

    // for testing
    public void delete(String filename) throws IOException {
        cmdWriter.write(String.format("DELE %s%n", filename));
        cmdWriter.flush();
        getAndCheckResponse(250, "Can't delete file");
    }

    public void disconnect() throws IOException {
        cmdWriter.write("QIUT\n");
        cmdWriter.flush();
        cmdWriter.close();
        cmdReader.close();
        dataConnection.close();
        cmdConnection.close();
    }

    private String getAndCheckResponse(int code, String msg) throws IOException {
        String response = cmdReader.readLine();
        if (!response.startsWith(String.valueOf(code))) {
            throw new FTPConnectionException(msg);
        }
        return response;
    }
}
