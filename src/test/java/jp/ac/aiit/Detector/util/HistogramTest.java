package jp.ac.aiit.Detector.util;

import static org.junit.Assert.*;

import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import org.junit.Test;


public class HistogramTest {

	@Test
	public void setImageRun() {
		HistogramMatcher hm = new HistogramMatcher();
		String imgName = Tool.getResourcePath("/image/pic1.JPG");
		for (int i = 0; i < 2; i++) {
			hm.addImage(imgName);
		}
		assertNotEquals("set image run test", hm.run(), null);
	}

	@Test
	public void unsetImageRun() {
		HistogramMatcher hm = new HistogramMatcher();
		assertEquals("no set image run test.", hm.run(), null);
	}

}
