package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import jp.ac.aiit.Detector.util.Tool;
import org.junit.Test;

import java.io.File;

import static jp.ac.aiit.Detector.util.Debug.debug;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;
import static org.junit.Assert.assertTrue;

public class DetectTrimTest {

    @Test
    public void trimTest() {
        HistogramMatcher hm = new HistogramMatcher();
        File[] files = Tool.getResourcePathFileList("/trim_image");
        hm.setImageColorType(CV_LOAD_IMAGE_COLOR);
        for (File file: files) {
            String fileName = file.getAbsolutePath();
            hm.addImage(fileName);
        }
        DetectorResult result = hm.fullScan();
        debug(result.pPrintString());
    }
}
