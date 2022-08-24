package infotecs.client.utils;

public class Constants {
    public static final String JSON_FILENAME = "students.json";
    public static final String DATA_CHANNEL_PATTERN = "(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})";
    public static final int CMD_PORT = 21;
    public static final String STUDENT_PATTERN = "\\s*\\{\\s*\"id\"\\s*:\\s*(\\d+)\\s*,\\s*\"student\"\\s*:\\s*\"([A-Za-z ]+)\"\\s*}\\s*";
}
