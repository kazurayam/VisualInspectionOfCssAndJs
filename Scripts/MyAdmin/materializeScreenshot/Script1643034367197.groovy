import java.nio.file.Files
import java.nio.file.Path

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.WebDriver

import com.kazurayam.materialstore.filesystem.FileType
import com.kazurayam.materialstore.filesystem.Material
import com.kazurayam.materialstore.metadata.Metadata
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * Test Cases/MyAdmin/materializeScreenshot
 * 
 */
Objects.requireNonNull(chrome)
Objects.requireNonNull(store)
Objects.requireNonNull(jobName)
Objects.requireNonNull(jobTimestamp)
Objects.requireNonNull(profile)


// take a screenshot and save the image into a temporary file using Katalon's built-in keyword
Path tempFile = Files.createTempFile(null, null);
WebUI.takeFullPageScreenshot(tempFile.toAbsolutePath().toFile().toString(), [])

// copy the image file into the materialstore
URL url = new URL(chrome.getCurrentUrl())
Metadata metadata = Metadata.builder(url).put("profile", profile).build()
Material image = store.write(jobName, jobTimestamp, FileType.PNG, metadata, tempFile)
assert image != null

return image