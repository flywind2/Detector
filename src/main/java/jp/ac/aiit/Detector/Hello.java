package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Debug;
import jp.ac.aiit.Detector.util.Tool;
import jp.ac.aiit.Detector.matcher.*;
import org.bytedeco.javacpp.opencv_objdetect;

import java.util.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 * テスト用クラス。いずれ消す
 */

public class Hello {

    public static void main(String[] args) throws Exception {
		HistogramMatcher hm = new HistogramMatcher();
		String path = System.getProperty("user.dir");
		for (int i = 0; i < 9; i++) {
			int j = (i % 9) + 1;
			hm.addImage(path + "/src/main/resources/images/pic" + j + ".jpg");
		}
		hm.setImageColorType(CV_LOAD_IMAGE_GRAYSCALE);
		hm.setAllowableValue(0.82);
		System.out.println(hm.run());
		System.out.println("実行にかかった時間は " + hm.getProcessingTime() + " ミリ秒です。");
	}

	/**
	 * {
	 * pic1.jpg={pic2.jpg=0.8478878991152214},
	 * pic4.jpg={
	 * 	pic5.jpg=0.8499969644136627,
	 * 	pic6.jpg=0.8448676025663246
	 * 	},
	 * pic9.jpg={},
	 * pic3.jpg={},
	 * pic7.jpg={pic8.jpg=0.8658020594575646}
	 * }
	 * @param args
	 */

    public static void mains(String[] args) {
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
                = new opencv_objdetect.CascadeClassifier(Tool.getResourcePath("/lbpcascade_frontalface.xml"));

        Mat image = imread(Tool.getResourcePath("/lena.png"));
        IplImage im = cvLoadImage(Tool.getResourcePath("/lena.png"));

        System.out.println((int)(image.data().get((int)(0*image.cols()+0*image.step()+2)) & 0xFF));
        System.out.println((int)(image.ptr(0,0).get(2) & 0xFF));
        System.out.println(image.asCvMat().rows(1).cols(1));
        Rect faceDetections = new Rect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println(String.format("Detected %s faces", faceDetections));

        rectangle(image, new Point(faceDetections.x(), faceDetections.y()), new Point(faceDetections.x() + faceDetections.width(), faceDetections.y() + faceDetections.height()), new Scalar(0, 255, 0, 0));

        String filename = "build/faceDetection.png";
        System.out.println(String.format("Writing %s", filename));
        imwrite(filename, image);

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Mat image = Highgui.imread(Tool.getResourcePath(Hello.class, "/lena.png"));

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
				= new opencv_objdetect.CascadeClassifier(Tool.getResourcePath("/lbpcascade_frontalface.xml"));
		Mat image = imread(Tool.getResourcePath("/lena.png"));

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
