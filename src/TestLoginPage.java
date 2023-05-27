

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class TestLoginPage {

	@Test
	public void testValidateUsername001() {
		// Test valid usernames for 4 characters
		try {
			LoginPage.validateUsername("john");
		} catch (InvalidCredentialException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testValidateUsername002() {
		// Test invalid usernames for null
		try {
			LoginPage.validateUsername(null);
			fail("Expected InvalidCredentialException not thrown");
		} catch (InvalidCredentialException e) {
			assertEquals("Username must be between 4 and 50 characters.", e.getMessage());
		}
	}

	@Test
	public void testValidateUsername003() {
		// Test invalid usernames for blank

		try {
			LoginPage.validateUsername("");
			fail("Expected InvalidCredentialException not thrown");
		} catch (InvalidCredentialException e) {
			assertEquals("Username must be between 4 and 50 characters.", e.getMessage());
		}
	}

	@Test
	public void testValidateUsername004() {
		// Test invalid usernames for less than 4 characters

		try {
			LoginPage.validateUsername("joh");
			fail("Expected InvalidCredentialException not thrown");
		} catch (InvalidCredentialException e) {
			assertEquals("Username must be between 4 and 50 characters.", e.getMessage());
		}

	}

	@Test
	public void testValidateUsername005() {
		// Test invalid usernames for more than 50 characters

		try {
			LoginPage.validateUsername("john123456789012345678901234567890123456789012345678901");
			fail("Expected InvalidCredentialException not thrown");
		} catch (InvalidCredentialException e) {
			assertEquals("Username must be between 4 and 50 characters.", e.getMessage());
		}
	}

	// Test valid usernames for exactly 50 characters
	@Test
	public void testValidateUsername006() {
		try {
			LoginPage.validateUsername("john12345678901234567890123456789012345678901234567890");
		} catch (InvalidCredentialException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testValidatePassword001() {
		// Test valid passwords between 1000 and 9999
		try {
			LoginPage.validatePassword(1000);
			LoginPage.validatePassword(9999);
		} catch (InvalidCredentialException e) {
			fail("Unexpected exception: " + e.getMessage());
		}

	}

	@Test
	public void testValidatePassword002() {
		// Test invalid passwords less than 1000
		try {
			LoginPage.validatePassword(999);
			fail("Expected InvalidCredentialException not thrown");
		} catch (InvalidCredentialException e) {
			assertEquals("Password Must be a 4-digit nummer.", e.getMessage());
		}
	}

	@Test
	public void testValidatePassword003() {
		// Test invalid password more than 9999
		try {
			LoginPage.validatePassword(10000);
			fail("Expected InvalidCredentialException not thrown");
		} catch (InvalidCredentialException e) {
			assertEquals("Password Must be a 4-digit nummer.", e.getMessage());
		}
	}

}