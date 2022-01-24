import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kazurayam.materialstore.Metadata
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable


// check params which should be passed as the arguments of WebUI.callTestCases() call
Objects.requireNonNull(profile)
Objects.requireNonNull(store)
Objects.requireNonNull(jobName)
Objects.requireNonNull(jobTimestamp)

// check the GlobalVariables
assert GlobalVariable.URL != null, "GlobalVariable.URL is not defined"

WebUI.openBrowser('')
WebUI.setViewPortSize(1024, 800)

WebUI.navigateToUrl("${GlobalVariable.URL}")

// -------- The top page is now open --------------------------------------

URL url = new URL(WebUI.getUrl())

// take the screenshot and the page source, save them into the store; using the Katalon keyword
WebUI.callTestCase(findTestCase("MyAdmin/takeScreenshot"),
	[
		"store": store,
		"jobName": jobName,
		"jobTimestamp": jobTimestamp,
		"metadata": Metadata.builderWithUrl(url)
									.put("profile", profile)
									.build()
	]
)

// scrape for the CSS and JavaScript refered by the page and materialize them into the store
WebUI.callTestCase(findTestCase("MyAdmin/materializeCssJs_by_kklisura_cdt"),
	[
		"driver": DriverFactory.getWebDriver(),
		"store": store,
		"jobName": jobName,
		"jobTimestamp": jobTimestamp,
		"profile": profile
	]
)


// we have done materializing the screenshot and the page source
WebUI.closeBrowser()
