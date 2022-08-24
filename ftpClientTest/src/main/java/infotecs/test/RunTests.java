package infotecs.test;

import org.testng.TestNG;

import java.util.Collections;

public class RunTests {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestSuites(Collections.singletonList("testsuite.xml"));
        testng.run();
    }
}
