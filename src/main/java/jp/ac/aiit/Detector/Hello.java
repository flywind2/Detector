package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import jp.ac.aiit.Detector.util.Debug;

import java.util.*;

import jp.ac.aiit.Detector.util.Tool;
import org.bytedeco.javacpp.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


/**
 * テスト用クラス。いずれ消す
 */

public class Hello {

    public static void main(String[] args) throws Exception {
		HistogramMatcher hm = new HistogramMatcher();
		List<String> images = new ArrayList<String>();
		String path = System.getProperty("user.dir");
		for (int i = 0; i < 10; i++) {
			int j = (i % 13) + 1;
			images.add(path + "/src/main/resources/images/pic"+ j +".jpg");
		}
		hm.setImages(images);
		hm.setImageColorType(CV_LOAD_IMAGE_GRAYSCALE);
		hm.setAllowableValue(0.8);
		long start = System.currentTimeMillis();
		System.out.println(hm.run());
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

/**
 {
 	pic8.jpg= {},
 	pic9.jpg= { 
 		pic11.jpg=true,
 		pic10.jpg=true
 	},
	pic13.jpg= {},
	pic12.jpg= {},
	pic11.jpg = { 
 		pic9.jpg = true
 	}, 
 	pic10.jpg = {
 		pic9.jpg = true
 	}
 }

 {
 	pic8.jpg={},
 	pic5.jpg={pic4.jpg=true},
 	pic12.jpg={},
 	pic1.jpg={pic2.jpg=true, pic9.jpg=true, pic10.jpg=true},
 	pic10.jpg={pic1.jpg=true, pic4.jpg=true}, pic3.jpg={}, pic2.jpg={pic1.jpg=true},
 	pic7.jpg={}, pic9.jpg={pic1.jpg=true, pic4.jpg=true},
 	pic13.jpg={},
 	pic6.jpg={pic4.jpg=true},
 	pic4.jpg={pic9.jpg=true,
 	pic5.jpg=true,
 	pic6.jpg=true,
 	pic10.jpg=true},
 	pic11.jpg={}
 }


 {
 	 pic1.jpg={pic2.jpg=true},
	 pic2.jpg={pic1.jpg=true},
	 pic3.jpg={},
	 pic4.jpg={pic10.jpg=true},
	 pic5.jpg={pic6.jpg=true},
	 pic6.jpg={pic5.jpg=true},
	 pic7.jpg={},
	 pic8.jpg={},
	 pic9.jpg={pic10.jpg=true},
	 pic10.jpg={pic9.jpg=true, pic4.jpg=true},
	 pic11.jpg={},
	 pic12.jpg={},
	 pic13.jpg={}
 }


 */