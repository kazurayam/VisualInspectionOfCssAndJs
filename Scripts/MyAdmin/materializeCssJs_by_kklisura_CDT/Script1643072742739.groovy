import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime

import org.openqa.selenium.chrome.ChromeDriver

import com.github.kklisura.cdt.launch.ChromeArguments
import com.github.kklisura.cdt.launch.ChromeLauncher
import com.github.kklisura.cdt.protocol.commands.Network
import com.github.kklisura.cdt.protocol.commands.Page
import com.github.kklisura.cdt.services.ChromeDevToolsService
import com.github.kklisura.cdt.services.ChromeService
import com.github.kklisura.cdt.services.types.ChromeTab
import com.kazurayam.ks.visualinspection.DownloadUtil
import com.kazurayam.ks.visualinspection.ResponseInspected
import com.kazurayam.materialstore.core.filesystem.FileType
import com.kazurayam.materialstore.core.filesystem.FileTypeUtil
import com.kazurayam.materialstore.core.filesystem.Material
import com.kazurayam.materialstore.core.filesystem.Metadata

/**
 * Test Cases/MyAdmin/materializeCssJs_by_kklisura_CDT
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

final ChromeLauncher launcher = new ChromeLauncher()

// launch Chrome
final ChromeArguments chromeArguments = 
	new ChromeArguments.Builder()
		.additionalArguments("disable-features","ChromeWhatsNewUI")
		.build()
final ChromeService chromeService = launcher.launch(chromeArguments)

// create an emtpy tab, ie about:blank
final ChromeTab tab = chromeService.createTab()

// get DevTools service to this tab
final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab)

// get indivisual commands
final Page page = devToolsService.getPage()
final Network network = devToolsService.getNetwork()

List<ResponseInspected> responses = new ArrayList<>()

// log responses
network.onResponseReceived({ event ->
	ResponseInspected resp = 
		new ResponseInspected(
				event.getResponse().getStatus(), 
				new URL(event.getResponse().getUrl()), 
				event.getResponse().getMimeType())
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
page.navigate(chrome.getCurrentUrl())

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
		DownloadUtil.downloadWebResourceIntoFile(resp.getUrl(), tempFile)
		
		// copy the file into the masterialstore
		FileType fileType = FileTypeUtil.ofMimeType(resp.getMimeType())
		Metadata metadata = Metadata.builder(resp.getUrl()).put("profile", profile).build()
		Material mat = store.write(jobName, jobTimestamp, fileType, metadata, tempFile)
		assert mat != null
	}
}
