package infotecs.test;

import infotecs.client.exceptions.ParserException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static infotecs.client.Parser.*;
import static infotecs.test.utils.Constants.*;
import static infotecs.test.utils.FileUtils.readResourceFile;

public class ParserTests {
    @DataProvider
    public Object[] validateJSONData() {
        return new Object[] {CORRECT_JSON_FILENAME, EMPTY_JSON_FILENAME};
    }

    @DataProvider
    public Object[] validateParseData() {
        return new Object[][] {
                {CORRECT_JSON_FILENAME, STUDENTS},
                {EMPTY_JSON_FILENAME, new HashMap<Integer, String>()}
        };
    }

    @Test(dataProvider = "validateJSONData")
    public void testValidateCorrectJSON(String jsonFilename) {
        validateJSON(readResourceFile(jsonFilename));
    }

    @Test(
            expectedExceptions = ParserException.class,
            expectedExceptionsMessageRegExp = "Invalid format of json file"
    )
    public void testValidateIncorrectJSON() {
        validateJSON(readResourceFile(INCORRECT_JSON_FILENAME));
    }

    @Test(dataProvider = "validateParseData")
    public void testParseJSONToMap(String jsonFilename, Map<Integer, String> expected) {
        Assert.assertEquals(expected, parseJSONToMap(readResourceFile(jsonFilename)));
    }

    @Test(
            expectedExceptions = ParserException.class,
            expectedExceptionsMessageRegExp = "Invalid json file: several students with the same id"
    )
    public void testParseJSONWithSameIdsToMap() {
        parseJSONToMap(readResourceFile(SAME_IDS_JSON_FILENAME));
    }

    @Test(dataProvider = "validateParseData")
    public void testParseMapToJSON(String jsonFilename, Map<Integer, String> students) {
        String expected = readResourceFile(jsonFilename);
        Assert.assertEquals(expected, parseMapToJSON(students));
    }
}
