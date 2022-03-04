import java.nio.file.Files
import java.nio.file.Path

import com.kazurayam.materialstore.filesystem.JobName
import com.kazurayam.materialstore.MaterialstoreFacade
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * Test Cases/MyAdmin/report
 */
assert store != null
assert mProductGroup != null
assert mProductGroup.isReadyToReport()
assert criteria != null

JobName jobName = mProductGroup.getJobName()

WebUI.comment("report started; criteria=${criteria}, mProductGroup=${mProductGroup.getDescription()}, jobName=${jobName}, store=${store}")

// the file name of HTML report
String fileName = jobName.toString()+ "-index.html"

MaterialstoreFacade facade = MaterialstoreFacade.newInstance(store)
Path report = facade.report(mProductGroup, criteria, fileName)

assert Files.exists(report)
WebUI.comment("The report can be found at ${report.toString()}")

int warnings = mProductGroup.countWarnings(criteria)
return warnings