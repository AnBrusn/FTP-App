package infotecs.test;

import infotecs.client.Data;
import infotecs.client.exceptions.NoSuchStudentException;
import infotecs.test.utils.Constants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class DataTests {
    private Data testData;

    @DataProvider
    public Object[][] getStudentsByNameData() {
        return new Object[][] {
                {Constants.FIRST_STUDENT, new HashMap<Integer, String>() {{ put(1, Constants.FIRST_STUDENT); put(3, Constants.FIRST_STUDENT); }}},
                {Constants.SECOND_STUDENT, new HashMap<Integer, String>() {{ put(2, Constants.SECOND_STUDENT); }}},
                {Constants.FORTH_STUDENT, new HashMap<Integer, String>() {{ put(4, Constants.FORTH_STUDENT); }}},
                {"not existing", new HashMap<Integer, String>()}
        };
    }

    @BeforeClass
    public void initTestData() {
        testData = new Data(Constants.STUDENTS);
    }

    @Test
    public void testGetStudents() {
        Assert.assertEquals(testData.getStudents(), Constants.STUDENTS);
    }

    @Test(dataProvider = "getStudentsByNameData")
    public void testGetStudentsByName(String name, Map<Integer, String> expected) {
        Assert.assertEquals(expected, testData.getStudentsByName(name));
    }

    @Test
    public void testGetStudentById() {
        for (Map.Entry<Integer, String> student : Constants.STUDENTS.entrySet()) {
            Assert.assertEquals(student.getValue(), testData.getStudentById(student.getKey()));
        }
    }

    @Test(
            expectedExceptions = NoSuchStudentException.class,
            expectedExceptionsMessageRegExp = "There is no student with id=\\d+"
    )
    public void testGetStudentByIdNotExisting() {
        testData.getStudentById(Constants.NOT_EXISTING_ID);
    }

    @Test
    public void testAddStudent() {
        Data tmpTestData= new Data(Constants.STUDENTS);
        Assert.assertEquals(Constants.MAX_EXISTING_ID + 1, tmpTestData.addStudent("new"));
    }

    @Test
    public void testDeleteStudentById() {
        Data tmpTestData= new Data(Constants.STUDENTS);
        for (int id : Constants.STUDENTS.keySet()) {
            Assert.assertEquals(Constants.STUDENTS.get(id), tmpTestData.deleteStudentById(id));
        }
    }

    @Test(expectedExceptions = NoSuchStudentException.class)
    public void testDeleteStudentByIdNotExisting() {
        testData.getStudentById(Constants.NOT_EXISTING_ID);
    }
}
