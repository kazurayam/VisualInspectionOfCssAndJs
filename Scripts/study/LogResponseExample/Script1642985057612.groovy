import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Network;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;

/**
 * LogResponseExample
 */

// Create chrome launcher.
final ChromeLauncher launcher = new ChromeLauncher();

// Launch chrome either as headless (true) or regular (false).
final ChromeService chromeService = launcher.launch(false);

// Create empty tab ie about:blank.
final ChromeTab tab = chromeService.createTab();

// Get DevTools service to this tab
final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

// Get individual commands
final Page page = devToolsService.getPage();
final Network network = devToolsService.getNetwork();

// Log response with onResponseReceived event handler.
network.onResponseReceived(
	{event ->
		System.out.printf(
			"request: %s %s %s%s",
			event.getResponse().getStatus(),
			event.getResponse().getUrl(),
			event.getResponse().getMimeType(),
			System.lineSeparator())
	});

/*
network.onLoadingFinished(
	{ event ->
	  // Close the tab and close the browser when loading finishes.
	  chromeService.closeTab(tab);
	  launcher.close();
	});
*/

// Enable network events.
network.enable();

// Navigate to github.com.
page.navigate("https://www.yahoo.co.jp/");

devToolsService.waitUntilClosed();

