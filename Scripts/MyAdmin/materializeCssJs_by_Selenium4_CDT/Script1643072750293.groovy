import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kazurayam.materialstore.FileType
import com.kazurayam.materialstore.Material
import com.kazurayam.materialstore.Metadata
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v96.network.Network

Objects.requireNonNull(driver)
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

// learnined https://rahulshettyacademy.com/blog/index.php/2021/11/04/selenium-4-key-feature-network-interception/#t-1636047172263

ChromeDriver chrome = (ChromeDriver)driver

DevTools devTool = chrome.getDevTools()

devTool.createSession()

devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optonal.empty()))

List<Response> responses = new ArrayList<>()

LocalDateTime lastResponseReceivedAt = LocalDateTime.now()

// log responses
devTool.addListener(Network.responseReceived(), { responseReceived ->
	Response res = new Response(
		responseReceived.getStatus(),
		new URL(responseReceived.getUrl()),
		responseReceived.getMimeType().toString()
	)
	println res.toString()
	responses.add(res)
	//
	lastResponseReceivedAt = LocalDateTime.now()
})

// navigate to the site again
chrome.navigate(driver.getCurrentUrl())

// wait until the stream of responses ceaces
for (;;) {
	Duration duration = Duration.between(lastResponseReceivedAt, LocalDateTime.now())
	if (duration.getSeconds() > 1) {
		chromeService.closeTab(tab)
		launcher.close()
		break
	}
	Thread.sleep(250)
}

responses.each { resp ->
	if (resp.getStatus() == 200) {
		// download a resource from a URL to save into a temp file
		Path tempFile = Files.createTempFile(null, null);
		downloadWebResourceIntoFile(resp.getUrl(), tempFile)
		
		// copy the file into the masterialstore
		FileType fileType = FileType.ofMimeType(resp.getMimeType())
		Metadata metadata = Metadata.builderWithUrl(resp.getUrl()).put("profile", profile).build()
		Material mat = store.write(jobName, jobTimestamp, fileType, metadata, tempFile)
		assert mat != null
	}
}

void downloadWebResourceIntoFile(URL url, Path file) {
	int BUFFER_SIZE = 4096
	BufferedInputStream bis = new BufferedInputStream(url.openStream())
	FileOutputStream fos = new FileOutputStream(file.toFile())
	byte[] data = new byte[BUFFER_SIZE];
	int byteContent;
	while ((byteContent = bis.read(data,0, BUFFER_SIZE)) != -1) {
		fos.write(data, 0, byteContent)
	}	
}


/**
 * 
 */
class Response {
	
	private final Integer status
	private final URL url
	private final String mimeType
	
	Response(Integer status, URL url, String mimeType) {
		this.status = status
		this.url = url
		this.mimeType = mimeType
	}
	Integer getStatus() {
		return this.status
	}
	URL getUrl() {
		return this.url
	}
	String getMimeType() {
		return this.mimeType
	}
	@Override
	String toString() {
		Map<String,String> m = new HashMap<>()
		m.put("status", Integer.toString(getStatus()))
		m.put("url", getUrl().toString())
		m.put("mime-type", getMimeType())
		Gson gson = new GsonBuilder().setPrettyPrinting().create()
		return gson.toJson(m)
	}
}