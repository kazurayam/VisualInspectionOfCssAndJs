import com.github.kklisura.cdt.launch.ChromeLauncher
import com.github.kklisura.cdt.protocol.commands.Network
import com.github.kklisura.cdt.protocol.commands.Page
import com.github.kklisura.cdt.services.ChromeDevToolsService
import com.github.kklisura.cdt.services.ChromeService
import com.github.kklisura.cdt.services.types.ChromeTab
import java.time.Duration
import java.time.LocalDateTime

/**
 * LogResponseExample
 */

// Create chrome launcher.
final ChromeLauncher launcher = new ChromeLauncher()

// Launch chrome either as headless (true) or regular (false).
final ChromeService chromeService = launcher.launch(false)

// Create empty tab ie about:blank.
final ChromeTab tab = chromeService.createTab()

// Get DevTools service to this tab
final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab)

// Get individual commands
final Page page = devToolsService.getPage()
final Network network = devToolsService.getNetwork()

// Log responses with onResponseReceived event handler.
network.onResponseReceived({ event ->
	System.out.printf(
		"response: %s %s %s%s",
		event.getResponse().getStatus(),
		event.getResponse().getUrl(),
		event.getResponse().getMimeType(),
		System.lineSeparator())
})

LocalDateTime lastResponseReceivedAt = LocalDateTime.now()

// watch the stream of responses. when the stream blocked longer than 1 second, then the stream has possibly finished
network.onLoadingFinished({ event ->
	lastResponseReceivedAt = LocalDateTime.now()
})


// Enable network events.
network.enable()

// Navigate to github.com.
page.navigate("http://myadmin.kazurayam.com/");
//page.navigate("http://http://devadmin.kazurayam.com");

// When the stream of responses seemed no more arriving,
// Close the tab and close the browser.
for (;;) {
	Duration duration = Duration.between(lastResponseReceivedAt, LocalDateTime.now())
	if (duration.getSeconds() > 1) {
		chromeService.closeTab(tab)
		launcher.close()
		break
	}
	Thread.sleep(250)
}

devToolsService.waitUntilClosed();
