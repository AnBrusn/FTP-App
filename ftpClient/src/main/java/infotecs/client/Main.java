package infotecs.client;

import infotecs.client.exceptions.NoSuchStudentException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

import static infotecs.client.utils.Constants.JSON_FILENAME;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PrintStream console = new PrintStream(System.out);
    private static Data studentData;
    private static FTPConnector connector;

    private static String chooseOption() {
        final String[] options = {
                "get all students",
                "get students by name",
                "get student by id",
                "add student",
                "delete student",
                "exit"
        };
        for (int i = 0; i < options.length; i++) {
            console.printf("%n[%d] %s", i + 1, options[i]);
        }
        console.print("\nChosen option: ");
        String chosen = scanner.next();
        scanner.nextLine();
        console.println();
        return chosen;
    }

    private static void printMenu() throws IOException {
        console.println("Hi! What do you want to do?");
        boolean exit = false;
        while (!exit) {
            switch (chooseOption()) {
                case "1":
                    printAllStudents();
                    break;
                case "2":
                    printStudentsByName();
                    break;
                case "3":
                    printStudentByIdDialog();
                    break;
                case "4":
                    printAddStudentDialog();
                    break;
                case "5":
                    printDeleteStudentDialog();
                    break;
                case "6":
                    exit = true;
                    printExitDialog();
                    break;
                default:
                    console.println("There is no such option. Try another one");
            }
            console.print("Press any key...");
            System.in.read();
        }
    }

    private static void printAllStudents() {
        Map<Integer, String> sortedStudents = studentData.getStudents();
        if (sortedStudents.isEmpty()) {
            console.println("There are no students");
        } else {
            console.println("All students sorted by names:");
            for (Map.Entry<Integer, String> student : sortedStudents.entrySet()) {
                console.printf("%s (id=%d)%n", student.getValue(), student.getKey());
            }
        }
    }

    private static void printStudentsByName() {
        console.print("Enter student name: ");
        String name = scanner.nextLine();
        Map<Integer, String> studentsByName = studentData.getStudentsByName(name);
        if (studentsByName.isEmpty()) {
            console.println("There are no students with this name");
        } else {
            for (Map.Entry<Integer, String> student : studentsByName.entrySet()) {
                console.printf("%s (id=%d)%n", student.getValue(), student.getKey());
            }
        }
    }

    private static void printStudentByIdDialog() {
        console.print("Enter the student's id to find: ");
        try {
            String foundName = studentData.getStudentById(scanner.nextInt());
            console.println("Name: " + foundName);
        } catch (NoSuchStudentException e) {
            console.println(e.getMessage());
        }
    }

    private static void printAddStudentDialog() {
        console.print("Enter new student's name: ");
        String newStudent = scanner.nextLine();
        if (newStudent.matches("[a-zA-Z ]+")) {
            console.println("Student added with id " + studentData.addStudent(newStudent));
        } else {
            console.println("A name should contain only letters, digits or whitespaces");
        }
    }

    private static void printDeleteStudentDialog() {
        console.print("Enter the student's id to delete: ");
        try {
            String foundName = studentData.deleteStudentById(scanner.nextInt());
            console.println("Student " + foundName + " deleted");
        } catch (NoSuchStudentException e) {
            console.println(e.getMessage());
        }
    }

    private static void printExitDialog() {
        console.print("Save changes? [y|n]: ");
        if (scanner.next().equalsIgnoreCase("y")) {
            try {
                connector.upload(Parser.parseMapToJSON(studentData.getStudents()), JSON_FILENAME);
                console.println("It's all saved");
            } catch (IOException e) {
                console.println(e.getMessage());
            }
        }
        try {
            connector.disconnect();
            console.println("Bye :)");
        } catch (IOException e) {
            console.println("Connection wasn't closed in correct way. %s");
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            console.print("Pass login, password and server IP as arguments, for example: ");
            console.println("java -jar FTPClient.jar myuser mypassword 127.0.0.1");
            return;
        }

        // for all uncaught exceptions print message to console
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> console.println(throwable.getMessage()));

        connector = new FTPConnector(args[0], args[1], args[2]);
        console.println("Wait a little, we are connecting...");
        connector.connect();

        console.print("Do you want to start in active mode? [y|n]: ");
        if (scanner.next().equalsIgnoreCase("y")) {
            connector.setActiveMode(true);
        }

        String jsonData = connector.download(JSON_FILENAME);
        Parser.validateJSON(jsonData);
        studentData = new Data(Parser.parseJSONToMap(jsonData));
        printMenu();
    }
}
