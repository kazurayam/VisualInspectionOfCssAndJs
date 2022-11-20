import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

import com.kazurayam.materialstore.base.materialize.MaterializingPageFunctions
import com.kazurayam.materialstore.base.materialize.StorageDirectory
import com.kazurayam.materialstore.base.materialize.Target
import com.kazurayam.materialstore.core.filesystem.Material
import com.kazurayam.materialstore.core.filesystem.Metadata
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil


import internal.GlobalVariable

/**
 * Test Cases/MyAdmin/materialize
 */

// check params which should be passed as the arguments of WebUI.callTestCases() call
Objects.requireNonNull(profile)
Objects.requireNonNull(store)
Objects.requireNonNull(jobName)
Objects.requireNonNull(jobTimestamp)

// check the GlobalVariables
assert GlobalVariable.URL != null, "GlobalVariable.URL is not defined"

System.setProperty("webdriver.chrome.driver", DriverFactory.getChromeDriverPath())
ChromeOptions options = new ChromeOptions()
ChromeDriver chrome = new ChromeDriver(options)
chrome.manage().window().setPosition(new Point(0,0))
chrome.manage().window().setSize(new Dimension(1024, 800))

DriverFactory.changeWebDriver(chrome)
WebUI.navigateToUrl("${GlobalVariable.URL}")

// -------- The top page is now open --------------------------------------

// take the screenshot using the AShot lib, save it into the store
URL url = new URL(WebUI.getUrl())
Target target = Target.builder(url).put("profile", profile).build()
StorageDirectory sd = new StorageDirectory(store, jobName, jobTimestamp)
Material screenshot = MaterializingPageFunctions.storeEntirePageScreenshot.accept(target, chrome, sd, Collections.emptyMap());


// download the web resources (HTML, CSS and JavaScript) of the page, save them into the store
try {
	if (GlobalVariable.visitSite_by_Selenium4_CDT) {
		// using Selenium 4 + DevTools
		WebUI.callTestCase(findTestCase("MyAdmin/materializeCssJs_by_Selenium4_CDT"),
			[
				"chrome": chrome,
				"store": store,
				"jobName": jobName,
				"jobTimestamp": jobTimestamp,
				"profile": profile
			]
		)
	} else {
		// using kklisura's CDT support
		WebUI.callTestCase(findTestCase("MyAdmin/materializeCssJs_by_kklisura_CDT"),
			[
				"chrome": chrome,
				"store": store,
				"jobName": jobName,
				"jobTimestamp": jobTimestamp,
				"profile": profile
			]
		)
	}
} catch (Exception e) {
	KeywordUtil.markFailedAndStop(e.getMessage())
}

// we have done materializing the screenshot and the page source
WebUI.closeBrowser()