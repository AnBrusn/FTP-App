package infotecs.test.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String SERVER_LOGIN = "testuser";
    public static final String SERVER_PASSWORD = "mypassword";
    public static final String SERVER_IP = "127.0.0.1";
    public static final String SERVER_INCORRECT_LOGIN = "incorrecttestuser";
    public static final String SERVER_INCORRECT_PASSWORD = "incorrectmypassword";

    public static final String CORRECT_JSON_FILENAME = "testJSON.json";
    public static final String INCORRECT_JSON_FILENAME = "incorrectJSON.json";
    public static final String EMPTY_JSON_FILENAME = "emptyJSON.json";
    public static final String SAME_IDS_JSON_FILENAME = "sameIdsJSON.json";
    public static final int NOT_EXISTING_ID = 1001;
    public static final int MAX_EXISTING_ID = 4;
    public static final String FIRST_STUDENT = "a b";
    public static final String SECOND_STUDENT = "c d";
    public static final String FORTH_STUDENT = "e";

    public static final Map<Integer, String> STUDENTS = new HashMap<Integer, String>() {{
        put(1, FIRST_STUDENT);
        put(2, SECOND_STUDENT);
        put(3, FIRST_STUDENT);
        put(4, FORTH_STUDENT);
    }};
}
