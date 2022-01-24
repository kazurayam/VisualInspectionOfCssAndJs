import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime

import com.github.kklisura.cdt.launch.ChromeLauncher
import com.github.kklisura.cdt.protocol.commands.Network
import com.github.kklisura.cdt.protocol.commands.Page
import com.github.kklisura.cdt.services.ChromeDevToolsService
import com.github.kklisura.cdt.services.ChromeService
import com.github.kklisura.cdt.services.types.ChromeTab
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kazurayam.materialstore.FileType
import com.kazurayam.materialstore.Material
import com.kazurayam.materialstore.Metadata

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

final ChromeLauncher launcher = new ChromeLauncher()

// launch Chrome
final ChromeService chromeService = launcher.launch(false)

// create an emtpy tab, ie about:blank
final ChromeTab tab = chromeService.createTab()

// get DevTools service to this tab
final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab)

// get indivisual commands
final Page page = devToolsService.getPage()
final Network network = devToolsService.getNetwork()

List<Response> responses = new ArrayList<>()

// log responses
network.onResponseReceived({ event ->
	Response resp = new Response(event.getResponse().getStatus(), new URL(event.getResponse().getUrl()), event.getResponse().getMimeType())
	println resp.toString()
	responses.add(resp)
})

// watch the stream of responses.
// when the stream stopped for longer than 2 seconds,
// then the stream has possibly finished
LocalDateTime lastResponseReceivedAt = LocalDateTime.now()
network.onLoadingFinished({ event ->
	lastResponseReceivedAt = LocalDateTime.now()
})

// Enable network events.
network.enable()

// navigate to the site
page.navigate(driver.getCurrentUrl())

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

// now close the Chrome browser
devToolsService.waitUntilClosed();

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