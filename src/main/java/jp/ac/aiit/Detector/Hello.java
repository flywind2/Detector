package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Debug;

import java.util.*;

import org.bytedeco.javacpp.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;


/**
 * テスト用クラス。いずれ消す
 */

public class Hello {
    public static void main(String[] args) {
        Debug.debug("test", "aaaaaaa");
        List l = new ArrayList<String>();
        l.add("test1");
        l.add("test2");
        Debug.debug("message:", l);
        Map m = new HashMap<String, String>();
        m.put("1", "test1");
        m.put("2", "test2");
        Debug.debug("test;", m);
        Calendar cal = Calendar.getInstance();
        Debug.debug("test;" , cal);

        opencv_objdetect.CascadeClassifier faceDetector
                = new opencv_objdetect.CascadeClassifier(Hello.class.getResource("/lbpcascade_frontalface.xml").getPath().substring(1));
        Mat image = imread(Hello.class.getResource("/lena.png").getPath().substring(1));

        Rect faceDetections = new Rect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println(String.format("Detected %s faces", faceDetections));

        rectangle(image, new Point(faceDetections.x(), faceDetections.y()), new Point(faceDetections.x() + faceDetections.width(), faceDetections.y() + faceDetections.height()), new Scalar(0, 255, 0, 0));

        String filename = "build/faceDetection.png";
        System.out.println(String.format("Writing %s", filename));
        imwrite(filename, image);
    }

    public int plus(int arg) {
        return arg + 5;
    }
}
