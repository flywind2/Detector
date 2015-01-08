package jp.ac.aiit.Detector.util;

import jp.ac.aiit.Detector.Histogram;
import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import junit.framework.TestCase;
import org.bytedeco.javacpp.opencv_core;

import java.util.ArrayList;
import java.util.List;

public class HistogramTest extends TestCase {

	public void unsetImageRun() {
		HistogramMatcher hm = new HistogramMatcher();
		hm.run();
	}

}
