package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.apache.maven.shared.utils.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class TestBase {

	public static String log4jPropertyFilePath = System.getProperty("user.dir")
			+ "\\src\\main\\resources\\log4j.properties";
	public static String g_strDownloadPath = System.getProperty("user.dir") + "\\Resources\\Downloads";
	public final static Logger logger = Logger.getLogger(TestBase.class.getName());
	public static ExtentReports extentReport;
	public static ExtentTest eTest;
	public static Properties prop;
	private static String filePath = System.getProperty("user.dir") + "\\screenshots\\";
	public FileInputStream fis = null;
	static String chromeDriverPath = System.getProperty("user.dir") + "\\drivers\\chromedriver.exe";
	static String gerkoDriverPath = System.getProperty("user.dir") + "\\drivers\\geckodriver.exe";
	static String extentReportPath = System.getProperty("user.dir") + "\\extentReports";
	static String downloadsFolderPart = System.getProperty("user.dir") + "\\Resources\\Downloads";
	static Log log;

	public static WebDriver driver = null;
	// Latest Element which has been found and used in findAnd... Method
	public static WebElement g_eleLatest = null;
	// Default Max wait time in seconds
	public static int g_nMaxWaitTime = 60;
	// Default Min wait time in seconds
	public static int g_nMinWaitTime = 3;
	// Default No wait time in seconds
	public static int g_nNoWaitTime = 1;
	// Max wait time in seconds for Error messages
	public static int g_nMaxErrMsgWaitTime = 3;
	// Sleep time in milliseconds between steps
	public static int g_nSleepTime = 3000;
	// SeleniumUtil Globals

	static {

		extentReport = new ExtentReports(System.getProperty("user.dir") + "\\extentReports\\"
				+"Test_Report_"+ new SimpleDateFormat("dd_mm_yyyy_hh_mm_ss").format(Calendar.getInstance().getTime()) + ".html",
				false);

		extentReport.addSystemInfo("App Store App", "Alok");

	}

	public TestBase() {

		prop = new Properties();
		try {
			fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties");
			prop.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// This will initialize page factory web elements of the class who extends this
		// class.
		PageFactory.initElements(driver, this);
	}

	public static WebDriver getDriver() {
		return driver;
	}

	@BeforeSuite()
	public void cleanUp() throws Exception {

		//Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
		//Runtime.getRuntime().exec("taskkill /F /IM Excel.exe");
		System.out.println("Killed process chromedriver.exe"); //
		System.out.println("Killed process chrome.exe");
		System.out.println("Killed process excel.exe");

		logger.info("Configuring extent report and launching browsser.");

		initialize();

	}

	@BeforeMethod()
	public void driverAndExtentReportSetup(Method method) throws Exception {

		eTest = extentReport.startTest(method.getName());
		eTest.assignCategory("API Testing");
		logger.info(method.getName() + " test started");

	}

	@AfterMethod()
	public void afterMethod(ITestResult result) throws InterruptedException {

		if (result.getStatus() == ITestResult.SUCCESS) {
			String screenshot = takeScreenShot(result.getName());
			eTest.log(LogStatus.PASS, result.getName() + " has passed.");
			eTest.addScreenCapture(screenshot);
		} else if (result.getStatus() == ITestResult.FAILURE) {
			String screenshot = takeScreenShot(result.getName());
			eTest.log(LogStatus.FAIL, result.getName() + " test has failed");
			eTest.addScreenCapture(screenshot);
		}

	}

	@BeforeClass

	public void thisClassTestStarted() {
		logger.info("***** Test case execution of Class " + getClass().getName() + "started******");
	}

	@AfterClass(alwaysRun = true)
	public void endTest() {
		logger.info("***************All test cases of Class " + getClass().getName() + " executed******");

	}



	@AfterSuite(alwaysRun = true)
	public void tearDown() throws Exception {
		// to delete directory
		FileUtils.deleteDirectory(extentReportPath);
		FileUtils.mkdir(extentReportPath);
		// extent report related code below
		extentReport.endTest(eTest);
		extentReport.flush();
		Thread.sleep(1000);

		// to send extent report in email.
		/*
		 * try {
		 * SendMailSSLWithAttachment.sendReportByEmail(prop.getProperty("fromEmail"),
		 * prop.getProperty("fromEmailPassword"), prop.getProperty("toEmail")); }
		 * 
		 * catch(Exception e) {
		 * 
		 * log.error(" Emailing of report failed :  "+e); }
		 */

		logger.info("*********** All test classes run. Extent report generated and put in : " + extentReportPath
				+ " .Quitting browser**********");

	}



	public static WebDriver initialize() throws Exception {

		boolean bPageLoaded = false;

		try {

			String browserName = prop.getProperty("browser");
			String appURL = prop.getProperty("appURL");

			if (browserName.equalsIgnoreCase("chrome")) {
				// This chromedriver is enabled to catch browser f12 console JavaScript erros as
				// well. See method collectBrowserJSerrorMessages() in GenericUtilities as well.
				// Download setting
				HashMap<String, Object> hChromePrefsMap = new HashMap<String, Object>();
				hChromePrefsMap.put("profile.default_content_settings.popups", 0);
				hChromePrefsMap.put("download.default_directory", g_strDownloadPath);

				ChromeOptions objChromeOptions = new ChromeOptions();
				objChromeOptions.setExperimentalOption("prefs", hChromePrefsMap);
				// To disable message " chrome is being cntrolled by Automated software..."
				objChromeOptions.addArguments("disable-infobars");
				objChromeOptions.addArguments("disable-infobars");
				objChromeOptions.addArguments("--no-sandbox");
				objChromeOptions.addArguments("--allow-insecure-localhost");
				// below code will run chrome in headless mode.
				// options.addArguments("--headless");
				objChromeOptions.addArguments("--disable-gpu");
				DesiredCapabilities objDesiredCapabilities = DesiredCapabilities.chrome();
				objDesiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				objDesiredCapabilities.setCapability(ChromeOptions.CAPABILITY, objChromeOptions);
				objDesiredCapabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
						UnexpectedAlertBehaviour.ACCEPT);

				LoggingPreferences loggingPreferences = new LoggingPreferences();
				loggingPreferences.enable(LogType.BROWSER, Level.ALL);
				objDesiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);

				System.setProperty("webdriver.chrome.driver", chromeDriverPath);
				driver = new ChromeDriver(objDesiredCapabilities);

			} else if (browserName.equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.gecko.driver", gerkoDriverPath);
				driver = new FirefoxDriver();
			}

			driver.manage().window().maximize();
			driver.manage().deleteAllCookies();

			bPageLoaded = waitForPageToLoad();

			if (bPageLoaded) {
				logger.info("Successfully launched the browser");
			} else {
				logger.error("Browser did not launched within max wait time :: " + g_nMaxWaitTime + " Secs.");
			}
			driver.manage().timeouts().pageLoadTimeout(Integer.parseInt((String) prop.get("PAGE_LOAD_TIMEOUT")),
					TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(Integer.parseInt((String) prop.get("IMPLICIT_WAIT")),
					TimeUnit.SECONDS);

			logger.info("********* Launching " + browserName + " browser*****************");

			driver.get(appURL);
			logger.info("********* Opening URL-- " + appURL + " *****************");

			return driver;
		} catch (Exception e) {

			logger.error(" Failed to initialize driver and browser setup :" + e.getMessage());
			return null;
		}

	}

	public static boolean waitForPageToLoad(int... nMaxWaiTimeInSec) {
		boolean bResult = false;
		String strJSScript = "return document.readyState";
		String strReadtStausToWait = "complete";
		String strActualStatus = "";
		int nTimer = 0;
		int nMaxWaitTime = -1;

		if (nMaxWaiTimeInSec.length > 0)
			nMaxWaitTime = nMaxWaiTimeInSec[0];
		else
			nMaxWaitTime = g_nMaxWaitTime;

		logger.info("Waiting for page to load... Max Wait Time is :: " + nMaxWaitTime + " Secs.");

		try {
			if (driver == null) {
				logger.error("Driver object is NULL :: Failed to Wait");
				return bResult;
			}

			do {

				JavascriptExecutor jsExec = (JavascriptExecutor) driver;
				strActualStatus = jsExec.executeScript(strJSScript).toString();
				Thread.sleep(1000);
				nTimer++;
				logger.info("Current Page Status :: " + strActualStatus + " :: Waited For " + nTimer + " Seconds");
				if (strActualStatus.trim().equalsIgnoreCase(strReadtStausToWait)) {
					bResult = true;
					break;
				}

			} while (nTimer <= nMaxWaitTime);

			return bResult;

		} catch (Exception ex) {
			logger.error(" Page load failed :" + ex.getLocalizedMessage());
			ex.printStackTrace();
			return bResult;
		}
	}

	/***
	 * @author Alok
	 * @description This method will take screenshot when called.Usually used by
	 *              extent report for adding screenshots to report after a test
	 *              method is executed.
	 * @param methodName
	 * @return True- If screenshot is taken and saved successfully.False- If some
	 *         exception occurs while taking and copying screenshot..
	 */

	
	public static String takeScreenShot(String methodName) {

		String DestFileDir = null;
		try { // driver.manage().window().maximize();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
			String destFileName = formater.format(calendar.getTime());
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			DestFileDir = filePath + methodName + destFileName + ".png";
			FileUtils.copyFile(scrFile, new File(DestFileDir));
			Log.info("***Placed screen shot in " + filePath + " ***");
		} catch (IOException e) {
			Log.error("Unable to take screenshot :" + e.getStackTrace());
		}
		return DestFileDir;
	}
}
