package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Tool;
import org.bytedeco.javacpp.opencv_core.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import static org.bytedeco.javacpp.opencv_highgui.imread;

public class CvTest {

    @Test
    public void testCvLib() {
        PrintStream out = null;
        try {
            out = new PrintStream("log.log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.setOut(out);
        System.getProperties().list(out);

        System.load("/home/travis/build/pbl2014/Detector/lib/libopencv_core.so.2.4");

        Mat image = imread(Tool.getResourcePath(this.getClass(), "/lena.png"));
    }
}
