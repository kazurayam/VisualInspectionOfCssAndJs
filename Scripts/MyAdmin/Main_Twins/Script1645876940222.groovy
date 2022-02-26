import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.ks.globalvariable.ExecutionProfilesLoader
import com.kazurayam.materialstore.MaterialstoreFacade
import com.kazurayam.materialstore.filesystem.JobName
import com.kazurayam.materialstore.filesystem.JobTimestamp
import com.kazurayam.materialstore.filesystem.MaterialList
import com.kazurayam.materialstore.filesystem.Store
import com.kazurayam.materialstore.filesystem.Stores
import com.kazurayam.materialstore.metadata.QueryOnMetadata
import com.kazurayam.materialstore.resolvent.ArtifactGroup
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * MyAdmin/MyAdmin_visual_inspection_twins
 */

Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path root = projectDir.resolve("store")
Store store = Stores.newInstance(root)
JobName jobName = new JobName("Main_Twins")
ExecutionProfilesLoader profilesLoader = new ExecutionProfilesLoader()



//---------------------------------------------------------------------
/*
 * Materialize stage
 */
// visit the Production environment
String profile1 = "MyAdmin_ProductionEnv"
profilesLoader.loadProfile(profile1)
WebUI.comment("Execution Profile ${profile1} was loaded")
JobTimestamp timestampP = JobTimestamp.now()
WebUI.callTestCase(
	findTestCase("MyAdmin/materialize"),
	["profile": profile1, "store": store, "jobName": jobName, "jobTimestamp": timestampP]
)

// visit the Development environment
String profile2 = "MyAdmin_DevelopmentEnv"
profilesLoader.loadProfile(profile2)
WebUI.comment("Execution Profile ${profile2} was loaded")

JobTimestamp timestampD = JobTimestamp.laterThan(timestampP)
WebUI.callTestCase(
	findTestCase("MyAdmin/materialize"),
	["profile": profile2, "store": store, "jobName": jobName, "jobTimestamp": timestampD]
)



//---------------------------------------------------------------------
/*
 * Reduce stage
 */
// pickup the materials that belongs to the 2 "profiles"
MaterialList left = store.select(jobName, timestampP,
			QueryOnMetadata.builderWithMap([ "profile": profile1 ]).build()
			)

MaterialList right = store.select(jobName, timestampD,
			QueryOnMetadata.builderWithMap([ "profile": profile2 ]).build()
			)

			
// compare 2 MaterialList objects, generate the diff information
ArtifactGroup prepared = 
	ArtifactGroup.builder(left, right)
		.ignoreKeys("profile", "URL.protocol", "URL.port")
		.identifyWithRegex(["URL.query": "\\w{32}", "URL.host": "(my|dev)admin.kazurayam.com"])
		.build()

MaterialstoreFacade facade = MaterialstoreFacade.newInstance(store)		
ArtifactGroup reduced = facade.reduce(prepared)




/*
 * Report stage
 */
// difference greater than the criteria should be warned
double criteria = 0.0d

// compile HTML report
String fileName = jobName.toString() + "-index.html"
Path reportFile = facade.report(jobName, reduced, criteria, fileName)

assert Files.exists(reportFile)
WebUI.comment("The report can be found ${reportFile.toString()}")

int warnings = reduced.countWarnings(criteria)
if (warnings > 0) {
	KeywordUtil.markWarning("found ${warnings} differences.")
}
