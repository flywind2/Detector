package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Debug;

import java.util.*;

import jp.ac.aiit.Detector.util.Tool;
import org.bytedeco.javacpp.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;


/**
 * テスト用クラス。いずれ消す
 */

public class Hello {

    public static void main(String[] args) throws Exception {
		Histogram hst = new Histogram();
		List<String> images = new ArrayList<String>();
		String path = System.getProperty("user.dir");
		for (int i = 0; i < 50; i++) {
			int j = (i % 11) + 1;
			images.add(path + "/src/main/resources/pic"+ j +".jpg");
		}
		hst.setImages(images);
		long start = System.currentTimeMillis();
		List<CvHistogram> histograms = hst.createGrayScaleHistogram();
		hst.execute(histograms);
		long stop = System.currentTimeMillis();
		System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
    }

	public void test() {
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
				= new opencv_objdetect.CascadeClassifier(Tool.getResourcePath(Hello.class, "/lbpcascade_frontalface.xml"));
		Mat image = imread(Tool.getResourcePath(Hello.class, "/lena.png"));

		Rect faceDetections = new Rect();
		faceDetector.detectMultiScale(image, faceDetections);
		System.out.println(String.format("Detected %s faces", faceDetections));

		rectangle(image, new Point(faceDetections.x(), faceDetections.y()), new Point(faceDetections.x() + faceDetections.width(), faceDetections.y() + faceDetections.height()), new Scalar(0, 255, 0, 0));

		String filename = "build/faceDetection.png";
		System.out.println(String.format("Writing %s", filename));
		imwrite(filename, image);
	}
}
