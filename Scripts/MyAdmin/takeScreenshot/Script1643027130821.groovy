import java.nio.file.Files
import java.nio.file.Path

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.WebDriver

import com.kazurayam.materialstore.FileType
import com.kazurayam.materialstore.Material
import com.kazurayam.materialstore.Metadata
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

Objects.requireNonNull(store)
Objects.requireNonNull(jobName)
Objects.requireNonNull(jobTimestamp)
Objects.requireNonNull(metadata)
assert metadata instanceof Metadata


// take a screenshot and save the image into a temporary file using Katalon's built-in keyword
Path tempFile = Files.createTempFile(null, null);
WebUI.takeFullPageScreenshot(tempFile.toAbsolutePath().toFile().toString(), [])

// copy the image file into the materialstore
Material image = store.write(jobName, jobTimestamp, FileType.PNG, metadata, tempFile)
assert image != null

return image