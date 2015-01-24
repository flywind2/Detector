package jp.ac.aiit.Detector.matcher;

import com.google.common.collect.Lists;
import jp.ac.aiit.Detector.DetectorResult;
import jp.ac.aiit.Detector.util.Debug;
import org.bytedeco.javacpp.*;

import java.util.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * class Histogram
 */
public class HistogramMatcher extends BaseMatcher {

	/**
	 * 画像のカラータイプ
	 * - フルカラーの認識
	 * -- CV_LOAD_IMAGE_COLOR
	 * - モノカラーの認識
	 * -- CV_LOAD_IMAGE_GRAYSCALE
	 */
	private int imageColorType = CV_LOAD_IMAGE_GRAYSCALE;

	/**
	 * ヒストグラムの計算方式の種類
	 */
	private int compareType   = CV_COMP_INTERSECT;

	/**
	 * 認識の許容範囲
	 */
	private double allowableValue = 0.8;

	/**
	 * Constructor
	 */
	public HistogramMatcher() {
		super();
	}

	/**
	 * ヒストグラムを生成する時のカラータイプを設定する。
	 *  - CV_LOAD_IMAGE_GRAY_SCALE: GrayScale.
	 *  - CV_LOAD_IMAGE_COLOR     : RgbColorScale.
	 * @param imageColorType
	 */
	public void setImageColorType(int imageColorType) {
		this.imageColorType = imageColorType;
	}

	/**
	 * ヒストグラム比較の計算方式を設定する。
	 * @param compareType
	 */
	public void setCompareType(int compareType) {
		this.compareType = compareType;
	}

	/**
	 * ヒストグラムの許容範囲を設定する.
	 * @param allowableValue
	 */
	public void setAllowableValue(double allowableValue) {
		this.allowableValue = allowableValue;
	}

	/**
	 * 実行
	 */
	public DetectorResult run() {
		clearResult();
		if (images.isEmpty()) {
			return null;
		}
		Map<String, CvHistogram> hists = createHistogram();
		Map<String, Boolean> skip = new HashMap<String, Boolean>();
		int len = hists.size();
		for (int i = 0; i < len; i++) {
			String name = images.get(i);
			if (skip.containsKey(name)) {
				continue;
			}
			result.put(name, name, getCompareHistValue(hists.get(name), hists.get(name), compareType));
			for (int j = i + 1; j < len; j++) {
				String bName = images.get(j);
				double histValue = getCompareHistValue(hists.get(name), hists.get(bName), compareType);
				if (allowableRange(histValue)) {
					result.put(name, bName, histValue);
					skip.put(bName, true);
				}
			}
		}
		return result;
	}

	/**
	 * 指定された画像全ての組み合わせで一致率を返却する。
	 * 処理時間が長いので実行の際は注意をする事。
	 *
	 * @return
	 */
	public DetectorResult fullScan() {
		clearResult();
		if (images.isEmpty()) {
			return null;
		}
		Map<String, CvHistogram> hists = createHistogram();
		int len = hists.size();
		for (int i = 0; i < len; i++) {
			String name = images.get(i);
			result.put(name, name, getCompareHistValue(hists.get(name), hists.get(name), compareType));
			for (int j = 0; j < len; j++) {
				String bName = images.get(j);
				double histValue = getCompareHistValue(hists.get(name), hists.get(bName), compareType);
				result.put(name, bName, histValue);
			}
		}
		return result;
	}

	/**
	 * ヒストグラムの作成
	 * @return List<CvHistogram>
	 */
	public Map<String, CvHistogram> createHistogram() {
		if (this.imageColorType == CV_LOAD_IMAGE_GRAYSCALE) {
			return createGrayScaleHistogram();
		}
		return createColorHistogram();
	}

	/**
	 * 取り込まれたimagesの画像データから
	 * グレイスケールのヒストグラムを生成する.
	 * @return List<CvHistogram>
	 */
	private Map<String, CvHistogram> createGrayScaleHistogram() {
		Map<String, CvHistogram> hists = new HashMap<>();
		for (int i = 0; i < images.size(); i++) {
			IplImage img = cvLoadImage(images.get(i), CV_LOAD_IMAGE_GRAYSCALE);
			IplImage dst = cvCreateImage(cvSize(img.width(), img.height()), img.depth(), 1);
			FloatPointer ranges = new FloatPointer(0.0f, 256.0f);
			IntPointer histSize = new IntPointer(255, 255);
			CvHistogram hist = cvCreateHist(1, histSize, CV_HIST_ARRAY, ranges, 1);
			cvCopy(img, dst);
			cvCalcHist(dst, hist);
			cvNormalizeHist(hist, 1.0);
			hists.put(images.get(i), hist);

			cvReleaseImage(img);
			cvReleaseImage(dst);
		}
		return hists;
	}

	/**
	 * 取り込まれたimagesの画像データから
	 * RGBのヒストグラムを生成する.
	 * @return List<CvHistogram>
	 */
	private Map<String, CvHistogram> createColorHistogram() {
		Map<String, CvHistogram> hists = new HashMap<>();

		List<List<String>> dividedImages = Lists.partition(images, 100);
		List<Thread> threads = new ArrayList<>();
		for (List<String> img: dividedImages) {
			CreateHistogram create = new CreateHistogram();
			create.setTargetMap(hists);
			create.setTargetImages(img);
			Thread t = new Thread(create);
			t.start();
			threads.add(t);
		}

		for (Thread t: threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				Debug.debug(e.toString());
			}
		}


		/*
		for (int i = 0; i < images.size(); i++) {
			IplImage img       = cvLoadImage(images.get(i), CV_LOAD_IMAGE_COLOR);
			List<IplImage> dst = new ArrayList<IplImage>();
			for (int j = 0; j < img.nChannels(); j++) {
				dst.add(cvCreateImage(cvSize(img.width(), img.height()), img.depth(), 1));
			}
			FloatPointer ranges = new FloatPointer(0.0f, 256.0f);
			IntPointer histSize = new IntPointer(255, 255);
			CvHistogram hist = cvCreateHist(1, histSize, CV_HIST_ARRAY, ranges, 1);
			cvSplit(img, dst.get(0), dst.get(1), dst.get(2), null);
			for (int k = 0; k < img.nChannels(); k++) {
				cvCalcHist(dst.get(k), hist);
				cvNormalizeHist(hist, 1.0);
			}

			//IplImageの解放(メモリリーク対策)
			cvReleaseImage(img);
			for (IplImage ii: dst) {
				cvReleaseImage(ii);
			}
			hists.put(images.get(i), hist);
		}
		*/
		return hists;
	}

	/**
	 * ヒストグラムの比較
	 * @param hist1
	 * @param hist2
	 * @param cvComp
	 * @return
	 */
	private double getCompareHistValue(CvHistogram hist1, CvHistogram hist2, int cvComp) {
		return cvCompareHist(hist1, hist2, cvComp);
	}

	/**
	 * マッチパターンを決めて、ヒストグラムの比較を行う。
	 * Correlation(CV_COMP_CORREL)                   : 0.870168
	 * Intersection(CV_COMP_INTERSECT)               : 0.847536
	 * Chi-square(CV_COMP_CHSQR)                     : 0.075862
	 * Bhattacharyya distance(CV_COMP_BHATTACHARYYA) : 0.139961
	 * @param histValue
	 * @return boolean
	 */
	private boolean allowableRange(double histValue) {
		if (this.compareType == CV_COMP_INTERSECT ||
				this.compareType == CV_COMP_CORREL) {
			return histValue > allowableValue;
		}
		return histValue < allowableValue;
	}

	public class CreateHistogram implements Runnable {
		private Map<String, CvHistogram> targetMap;

		private List<String> targetImages;

		public void setTargetMap(Map<String, CvHistogram> targetMap) {
			this.targetMap = targetMap;
		}

		public void setTargetImages(List<String> targetImages) {
			this.targetImages = targetImages;
		}

		public void run() {
			for (String image: targetImages) {
				IplImage img       = cvLoadImage(image, CV_LOAD_IMAGE_COLOR);
				List<IplImage> dst = new ArrayList<IplImage>();
				for (int j = 0; j < img.nChannels(); j++) {
					dst.add(cvCreateImage(cvSize(img.width(), img.height()), img.depth(), 1));
				}
				FloatPointer ranges = new FloatPointer(0.0f, 256.0f);
				IntPointer histSize = new IntPointer(255, 255);
				CvHistogram hist = cvCreateHist(1, histSize, CV_HIST_ARRAY, ranges, 1);
				cvSplit(img, dst.get(0), dst.get(1), dst.get(2), null);
				for (int k = 0; k < img.nChannels(); k++) {
					cvCalcHist(dst.get(k), hist);
					cvNormalizeHist(hist, 1.0);
				}

				//IplImageの解放(メモリリーク対策)
				cvReleaseImage(img);
				for (IplImage ii: dst) {
					cvReleaseImage(ii);
				}
				targetMap.put(image, hist);
			}
		}
	}
}

