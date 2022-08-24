package infotecs.test;

import infotecs.client.FTPConnector;
import infotecs.client.exceptions.FTPConnectionException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static infotecs.test.utils.Constants.*;
import static infotecs.test.utils.FileUtils.readResourceFile;

public class FTPConnectorTests {
    private FTPConnector connector = new FTPConnector(SERVER_LOGIN, SERVER_PASSWORD, SERVER_IP);

    @DataProvider
    public Object[][] incorrectLoginToServerData() {
        return new Object[][] {
                {SERVER_LOGIN, SERVER_INCORRECT_PASSWORD},
                {SERVER_INCORRECT_LOGIN, SERVER_PASSWORD}
        };
    }

    @BeforeClass
    public void init() throws IOException {
        connector.connect();
    }

    @AfterClass
    public void cleanUp() throws IOException {
        connector.disconnect();
    }

    @Test(
            dataProvider = "incorrectLoginToServerData",
            expectedExceptions = FTPConnectionException.class,
            expectedExceptionsMessageRegExp = "Authentication failed"
    )
    public void testIncorrectLoginToServer(String login, String pwd) throws IOException {
        new FTPConnector(login, pwd, SERVER_IP).connect();
    }

    @Test
    public void testConnectAndLoginToServer() throws IOException {
        new FTPConnector(SERVER_LOGIN, SERVER_PASSWORD, SERVER_IP).connect();
    }

    @Test
    public void testConnectInActiveMode() throws IOException {
        connector.connectInActiveMode();
    }

    @Test
    public void testConnectInPassiveMode() throws IOException {
        connector.connectInPassiveMode();
    }

    @Test
    public void testFileUpload() throws IOException {
        connector.setActiveMode(false);
        String json = readResourceFile(CORRECT_JSON_FILENAME);
        connector.upload(json, CORRECT_JSON_FILENAME);
    }

    @Test(dependsOnMethods = { "testFileUpload" })
    public void testFileDownload() throws IOException {
        String expected = readResourceFile(CORRECT_JSON_FILENAME);
        connector.download(CORRECT_JSON_FILENAME);
        Assert.assertEquals(expected, connector.download(CORRECT_JSON_FILENAME));
    }

    @Test(
            expectedExceptions = FTPConnectionException.class,
            expectedExceptionsMessageRegExp = "Can't find the file",
            dependsOnMethods = { "testFileDownload" }
    )
    public void testFileDelete() throws IOException {
        connector.delete(CORRECT_JSON_FILENAME);
        connector.download(CORRECT_JSON_FILENAME);
    }
}
