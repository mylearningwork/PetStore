package pages;

import java.io.FileReader;

import org.testng.Assert;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import net.minidev.json.parser.JSONParser;
import utilities.Constants;
import utilities.Log;

public class PetStoreFunctions {

	static Response resp = null;

	
	/***
	 * This reusable method adds a pet.
	 */
	
	public static void validateAddPet() throws Exception {

		Object obj = new JSONParser().parse(
				new FileReader(System.getProperty("user.dir") + "\\src\\main\\resources\\TestData\\petdata.json"));
		resp = RestAssured.given().contentType("application/json").body(obj.toString()).post(Constants.addPetUrl);
		Log.info("Url is --"+Constants.addPetUrl);
		Log.info("Pet details received from server is : " + resp.getBody().prettyPrint());

		Assert.assertTrue(resp.getStatusCode() == 200);
		Assert.assertTrue(resp.asString().contains("available"));
		Assert.assertTrue(resp.asString().contains("doggie"));

		Log.info("Status code is  :" + resp.getStatusCode());
		Log.pass("Successfully added new pet in store");
	}

	/***
	 * This reusable method updates a pet by pet id.
	 */
	public static void validateUpdatePet(int petId) {

		resp = RestAssured.given().formParameter("name", "Alok Rai").post(Constants.updatePetUrl+petId);
		Log.info("Pet details received from server is : " + resp.getBody().prettyPrint());

		Assert.assertTrue(resp.getStatusCode() == 200);
		Assert.assertTrue(resp.then().extract().jsonPath().get("message").toString().equals("1"));

		Log.info("Status code is  :" + resp.getStatusCode());
		Log.info("Pet updated successfully");

	}

	/***
	 * This reusable method gets a pet by pet id.
	 */
	public static void validateGetPet(int petId) {
		
		resp = RestAssured.given().get(Constants.getPetURL+petId);
		Log.info("Pet details received from server is : " + resp.getBody().prettyPrint());

		Assert.assertTrue(resp.getStatusCode() == 200);
		Assert.assertTrue(resp.asString().contains("available"));

		Log.info("Status code is  :" + resp.getStatusCode());
		Log.info("Successfully received pet data from Pet store.");

	}
	/***
	 * This reusable method deletes a pet by pet id.
	 */

	public static void validateDeletePet(int petId) {
		resp = RestAssured.given().post(Constants.deletePetUrl+petId);
		Log.info("Pet details received from server is : " + resp.getBody().prettyPrint());

		Assert.assertTrue(resp.getStatusCode() == 200);
		Assert.assertTrue(resp.then().extract().jsonPath().get("message").toString().equals("1"));

		Log.info("Status code is  :" + resp.getStatusCode());
		Log.info("Pet deleted successfully..");
	}
}
