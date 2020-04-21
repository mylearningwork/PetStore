package utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class Utils extends TestBase {

	private static Workbook book;
	
	public static JavascriptExecutor js = (JavascriptExecutor) driver;

	private static Sheet sheet;
	static Log log = new Log();

	public static int getStatusCode(Response resp) {
		int statusCode = 0;
		try {
			statusCode = resp.getStatusCode();
			Log.info("Status code is : " + statusCode);
			return statusCode;
		} catch (Exception e) {
			Log.error("Error while getting Status code from Response... " + e.getStackTrace());
			return statusCode;
		}
	}

	public static String getStatusLine(Response resp) {
		String statusLine = null;
		try {
			statusLine = resp.getStatusLine();
			Log.info("Status Line is : " + statusLine);
			return statusLine;
		} catch (Exception e) {
			Log.error("Error while getting Status Line from Response... " + e.getStackTrace());
			return statusLine;
		}
	}

	public static Response getResponse(String url) {
		Response resp = null;
		try {
			resp = RestAssured.given().get(url);
			Log.info("Response is :" + resp);
			return resp;
		} catch (Exception e) {
			Log.error("Error while getting response... " + e.getStackTrace());
			return resp;
		}
	}

	public static String getHeaderValue(Response resp, String headerName) {
		String headerValue = null;
		try {
			headerValue = resp.getHeader(headerName);
			Log.info("Header value of header " + headerName + " is : " + headerValue);
			return headerValue;
		} catch (Exception e) {
			Log.error("Error while getting header value for header  " + headerName + ".. " + e.getStackTrace());
			return headerValue;
		}
	}


	/***
	 * @author :Alok
	 * @Method_Name: assertIfEqual.
	 * @Description :This method will compare two strings and return true if equal
	 *              or false if not equal.
	 * @param: strActual- Actual string.
	 * @param: strExpected- Expected string.
	 ***/
	public static boolean assertIfEqual(Object strActual, Object strExpected) {
		boolean b = false;
		try {
			Assert.assertEquals(strActual, strExpected);
			Log.info("Actual string: [ " + strActual + " ] is matching with Expected string: [ " + strExpected + " ]");
			b = true;

		} catch (AssertionError e) {
			Log.error("Actual string: [ " + strActual + " ] does not match with Expected string: [ " + strExpected
					+ " ]");
			b = false;
		}
		return b;
	}
}
