import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kazurayam.materialstore.filesystem.FileType
import com.kazurayam.materialstore.filesystem.Material
import com.kazurayam.materialstore.filesystem.Metadata
import com.kazurayam.ks.visualinspection.DownloadUtil
import com.kazurayam.ks.visualinspection.ResponseInspected
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v96.network.Network

/**
 * Test Cases/MyAdmin/materializeCssJs_by_Selenium4_CDT
 * 
 */
Objects.requireNonNull(chrome)
assert chrome instanceof ChromeDriver
Objects.requireNonNull(store)
Objects.requireNonNull(jobName)
Objects.requireNonNull(jobTimestamp)
Objects.requireNonNull(profile)

/**
 * open a new Chrome browser window with the URL given,
 * listen to the responses and catch them,
 * then download the source of CSS and JS that are refered by the URL given.
 * save the files into the store directory
 */

// learnined 
// https://rahulshettyacademy.com/blog/index.php/2021/11/04/selenium-4-key-feature-network-interception/#t-1636047172263

DevTools devTool = chrome.getDevTools()

devTool.createSession()

devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()))

List<ResponseInspected> responses = new ArrayList<>()

LocalDateTime lastResponseReceivedAt = LocalDateTime.now()

// log responses
devTool.addListener(Network.responseReceived(), { event ->
	Integer status = event.getResponse().getStatus()
	URL url = new URL(event.getResponse().getUrl())
	String mimeType = event.getResponse().getMimeType().toString()
	ResponseInspected res = new ResponseInspected(status, url, mimeType)
	responses.add(res)
	//
	lastResponseReceivedAt = LocalDateTime.now()
	// log this event in the console to show progress
	println res.toString()
})

// navigate to the site again
String url = chrome.getCurrentUrl()
chrome.navigate().to(url)

// wait until the stream of responses ceaces
for (;;) {
	Duration duration = Duration.between(lastResponseReceivedAt, LocalDateTime.now())
	if (duration.getSeconds() > 5) {
		chrome.quit()
		break
	}
	Thread.sleep(250)
}


responses.each { resp ->
	if (resp != null && (200 <= resp.getStatus() && resp.getStatus() < 300)) {
		
		// download a resource from a URL to save into a temp file
		Path tempFile = Files.createTempFile(null, null);
		DownloadUtil.downloadWebResourceIntoFile(resp.getUrl(), tempFile)
		
		// copy the file into the masterialstore
		FileType fileType = FileType.ofMimeType(resp.getMimeType())
		Metadata metadata = Metadata.builder(resp.getUrl()).put("profile", profile).build()
		Material mat = store.write(jobName, jobTimestamp, fileType, metadata, tempFile)
		assert mat != null
	}
}
