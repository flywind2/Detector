package jp.ac.aiit.Detector.matcher;

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
	private double allowableValue = 0.3;

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
	public Map<String, Map<String, Boolean>> run() {
		if (this.images.isEmpty()) {
			return null;
		}
		List<CvHistogram> hists = createHistogram();
		Map<String, Map<String, Boolean>> group = new HashMap<String, Map<String, Boolean>>();
		int len = hists.size();
		for (int i = 0; i < len; i++) {
			String name = this.images.get(i);
			if (!group.containsKey(name)) {
				group.put(name, new HashMap<String, Boolean>());
			} else {
				continue;
			}
			for (int j = i + 1; j < len; j++) {
				String bName = this.images.get(j);
				if (allowableRange(hists.get(i), hists.get(j), this.compareType)) {
					if (!group.containsKey(bName)) {
						group.put(bName, new HashMap<String, Boolean>());
					}
					group.get(name).put(bName, true);
					group.get(bName).put(name, true);
				}
			}
		}
		return group;
	}


	/**
	 * ヒストグラムの作成
	 * @return List<CvHistogram>
	 */
	public List<CvHistogram> createHistogram() {
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
	private List<CvHistogram> createGrayScaleHistogram() {
		List<CvHistogram> hists = new ArrayList<CvHistogram>();
		for (int i = 0; i < images.size(); i++) {
			IplImage img = cvLoadImage(images.get(i), CV_LOAD_IMAGE_GRAYSCALE);
			IplImage dst = cvCreateImage(cvSize(img.width(), img.height()), img.depth(), 1);
			FloatPointer ranges = new FloatPointer(0.0f, 256.0f);
			IntPointer histSize = new IntPointer(255, 255);
			CvHistogram hist = cvCreateHist(1, histSize, CV_HIST_ARRAY, ranges, 1);
			cvCopy(img, dst);
			cvCalcHist(dst, hist);
			cvNormalizeHist(hist, 1.0);
			hists.add(hist);
		}
		return hists;
	}

	/**
	 * 取り込まれたimagesの画像データから
	 * RGBのヒストグラムを生成する.
	 * @return List<CvHistogram>
	 */
	private List<CvHistogram> createColorHistogram() {
		List<CvHistogram> hists = new ArrayList<CvHistogram>();
		for (int i = 0; i < images.size(); i++) {
			IplImage img = cvLoadImage(images.get(i), CV_LOAD_IMAGE_COLOR);
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
			hists.add(hist);
		}
		return hists;
	}

	/**
	 * マッチパターンを決めて、ヒストグラムの比較を行う。
	 * Correlation(CV_COMP_CORREL)                   : 0.870168
	 * Intersection(CV_COMP_INTERSECT)               : 0.847536
	 * Chi-square(CV_COMP_CHSQR)                     : 0.075862
	 * Bhattacharyya distance(CV_COMP_BHATTACHARYYA) : 0.139961
	 * @param hist1
	 * @param hist2
	 * @param cvComp
	 * @return boolean
	 */
	private boolean allowableRange(CvHistogram hist1, CvHistogram hist2, int cvComp) {
		double similarityValue = cvCompareHist(hist1, hist2, cvComp);
		if (cvComp == CV_COMP_INTERSECT ||
				cvComp == CV_COMP_CORREL) {
			return similarityValue < allowableValue;
		}
		return similarityValue > allowableValue;
	}

}
