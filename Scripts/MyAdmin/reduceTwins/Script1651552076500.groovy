import com.kazurayam.materialstore.base.inspector.Inspector
import com.kazurayam.materialstore.core.filesystem.MaterialList
import com.kazurayam.materialstore.base.reduce.MaterialProductGroup
import com.kazurayam.materialstore.base.reduce.Reducer
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.util.function.BiFunction

/**
 * Test Cases/MyAdmin/reduce
 * 
 */
assert store != null
assert leftMaterialList != null
assert rightMaterialList != null

WebUI.comment("reduce started; store=${store}")
WebUI.comment("reduce started; leftMaterialList=${leftMaterialList}")
WebUI.comment("reduce started; rightMaterialList=${rightMaterialList}")

assert leftMaterialList.size() > 0
assert rightMaterialList.size() > 0

MaterialProductGroup mpg = 
        MaterialProductGroup.builder(leftMaterialList, rightMaterialList)
			.ignoreKeys("profile", "URL.host", "URL.port", "URL.protocol", "URL.query")
			.labelLeft("ProductionEnv")
			.labelRight("DevelopmentEnv")
			.sort("step")
			.build()

Inspector inspector = Inspector.newInstance(store)
MaterialProductGroup reduced = inspector.reduceAndSort(mpg)
	
return reduced