package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Tool;
import org.bytedeco.javacpp.opencv_core.*;
import org.junit.Test;

import static org.bytedeco.javacpp.opencv_highgui.imread;

public class CvTest {

    @Test
    public void testCvLib() {
        // opencvライブラリ呼び出しテスト
        Mat image = imread(Tool.getResourcePath("/lena.png"));
    }
}
