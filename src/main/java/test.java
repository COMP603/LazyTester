import static org.junit.Assert.*;

import org.junit.Test;


public class test {
	
	public static String message;
	
	@Test 
	public void stringTest(String function, String a, String b) {
			assertEquals("Error in Function " + function + ": Return Type " + b + " and Method Type " + a + " do not match!",true, a.equals(b));
	}
	
	
	
	
}
