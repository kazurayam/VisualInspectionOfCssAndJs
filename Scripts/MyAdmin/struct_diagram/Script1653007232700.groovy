// Test Cases/MyAdmin/struct_diagram

import java.awt.image.BufferedImage;

import com.kazurayam.materialstore.diagram.dot.DotGenerator
import com.kazurayam.materialstore.core.filesystem.FileType;
import com.kazurayam.materialstore.core.filesystem.JobName;
import com.kazurayam.materialstore.core.filesystem.JobTimestamp;
import com.kazurayam.materialstore.core.filesystem.Material;
import com.kazurayam.materialstore.core.filesystem.Metadata;
import com.kazurayam.materialstore.core.filesystem.Store;

assert store != null
assert jobName != null
assert jobTimestamp != null
assert mProductGroup != null


String dotBeforeZip = DotGenerator.generateDotOfMPGBeforeZip(mProductGroup)
generateDiagrams(store, jobName, jobTimestamp, dotBeforeZip, ["diagramType":"before zip"])

String dotAfterZip = DotGenerator.generateDot(mProductGroup)
generateDiagrams(store, jobName, jobTimestamp, dotAfterZip, ["diagramType":"after zip"])


/**
 * 
 * @param store
 * @param jobName
 * @param jobTimestamp
 * @param mProductGroup
 */
void generateDiagrams(Store store, JobName jobName, JobTimestamp jobTimestamp, String dotText, Map<String, Object> metadata) {
	Material dotMat =
		store.write(jobName, jobTimestamp, FileType.DOT,
				Metadata.builder(metadata).build(), dotText);
	assert dotMat.toFile(store).length() > 0
	
	BufferedImage bufferedImage = DotGenerator.toImage(dotText);
	Material pngMat =
		store.write(jobName, jobTimestamp, FileType.PNG,
				Metadata.builder(metadata).build(), bufferedImage);
	assert pngMat.toFile(store).length() > 0
}