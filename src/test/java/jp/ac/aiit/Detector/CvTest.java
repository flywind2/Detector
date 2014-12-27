package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Debug;
import jp.ac.aiit.Detector.util.Tool;
import org.bytedeco.javacpp.opencv_core.*;
import org.junit.Test;

import java.io.PrintStream;

import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.junit.Assert.*;

public class CvTest {

    @Test
    public void testCvLib() throws Exception{
        PrintStream out = new PrintStream("log.log");

        System.getProperties().list(out);
        Mat image = imread(Tool.getResourcePath(this.getClass(), "/lena.png"));
    }
}
