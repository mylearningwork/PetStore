package com.testPages;

import org.codehaus.plexus.util.ExceptionUtils;
import org.testng.annotations.Test;
import utilities.Log;
import utilities.TestBase;
import pages.PetStoreFunctions;

public class PetstoreTest extends TestBase {

	@Test(enabled = true, priority = 1)
	public void validateAddPetTest() {

		try {
			PetStoreFunctions.validateAddPet();

		} catch (Exception | AssertionError e) {
			Log.fail("Add pet scenario has failed" + ExceptionUtils.getFullStackTrace(e));
		}

	}

	@Test(enabled = true, priority = 2)
	public void validateUpdatePetTest() {

		try {
			PetStoreFunctions.validateUpdatePet(1);

		} catch (Exception | AssertionError e) {
			Log.fail("Update pet scenario has failed" + ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Test(enabled = true, priority = 3)
	public void validateGetPetTest() {
		try {
			PetStoreFunctions.validateGetPet(1);

		} catch (Exception | AssertionError e) {
			Log.fail("Get pet scenario has failed" + ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Test(enabled = true, priority = 4)
	public void validateDeletePetTest() {
		try {
			PetStoreFunctions.validateDeletePet(1);

		} catch (Exception | AssertionError e) {
			Log.fail("Delete pet scenario has failed" + ExceptionUtils.getFullStackTrace(e));
		}
	}

}
