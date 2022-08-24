package infotecs.test.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileUtils {
    public static String readResourceFile(String filename) {
        InputStream inputFileStream = FileUtils.class.getClassLoader().getResourceAsStream(filename);
        BufferedReader bufferedFileReader = new BufferedReader(new InputStreamReader(inputFileStream));
        return bufferedFileReader.lines().collect(Collectors.joining("\n"));
    }
}
