package infotecs.client;

import infotecs.client.exceptions.ParserException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static infotecs.client.utils.Constants.STUDENT_PATTERN;

public class Parser {

    public static void validateJSON(String jsonString) {
        String jsonPattern = "\\s*\\{\\s*\"students\"\\s*:\\s*\\[(" +
                STUDENT_PATTERN +
                "(," +
                STUDENT_PATTERN +
                ")*)?\\s*]\\s*}\\s*";
        Matcher matcher = Pattern.compile(jsonPattern).matcher(jsonString);
        if (!matcher.matches()) {
            throw new ParserException("Invalid format of json file");
        }
    }

    public static Map<Integer, String> parseJSONToMap(String jsonString) {
        Map<Integer, String> data = new HashMap<>();
        Matcher matcher = Pattern.compile(STUDENT_PATTERN).matcher(jsonString);
        while (matcher.find()) {
            if (data.containsKey(Integer.parseInt(matcher.group(1)))) {
                throw new ParserException("Invalid json file: several students with the same id");
            }
            data.put(Integer.parseInt(matcher.group(1)), matcher.group(2));
        }
        return data;
    }

    public static String parseMapToJSON(Map<Integer, String> data) {
        StringBuilder jsonString = new StringBuilder("{\n  \"students\": [");
        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            jsonString.append("\n    {\n      \"id\": ")
                    .append(entry.getKey())
                    .append(",\n      \"student\": \"")
                    .append(entry.getValue())
                    .append("\"\n    },");
        }
        if (!data.isEmpty()) {
            jsonString.deleteCharAt(jsonString.length() - 1);
        }
        jsonString.append("\n  ]\n}");
        return jsonString.toString();
    }
}
