package jp.ac.aiit.Detector.util;

import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import org.junit.Test;

import java.io.File;

import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class HistogramTest {

	@Test
	public void grayScaleRun() {
		File[] files = Tool.getResourcePathFileList("/image");
		HistogramMatcher hm = new HistogramMatcher();
		hm.setImageColorType(CV_LOAD_IMAGE_GRAYSCALE);
		for (File file: files) {
			String fileName = file.getAbsolutePath();
			hm.addImage(fileName);
		}
		hm.run();
		// Runの中に認識結果が有る.
		assertNotEquals("gray scale type run", hm.run(), null);
	}

	@Test
	public void colorRun() {
		File[] files = Tool.getResourcePathFileList("/image");
		HistogramMatcher hm = new HistogramMatcher();
		hm.setImageColorType(CV_LOAD_IMAGE_COLOR);
		for (File file: files) {
			String fileName = file.getAbsolutePath();
			hm.addImage(fileName);
		}
		assertNotEquals("color type run", hm.run(), null);
	}

	@Test
	public void unsetImageRun() {
		HistogramMatcher hm = new HistogramMatcher();
		assertEquals("no set image run test.", hm.run(), null);
	}

}