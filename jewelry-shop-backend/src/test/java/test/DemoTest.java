package test;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DemoTest {

    @Test
    public void testLogin() {
        String expected = "admin";
        String actual = "admin";
        Assert.assertEquals(actual, expected);
    }
}