package jp.ac.aiit.Detector;

import org.bytedeco.javacpp.*;

import java.util.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * class Histogram
 */
public class Histogram {

	/**
	 *
	 */
	private final static double MATCH_REVERSE_RATE = 0.17;

	/**
	 *
	 */
	private final static double MATCH_RATE         = 0.83;

	/**
	 *
	 */
	private List<String> images;

	private Map<Integer, Double> matchRateList;

	/**
	 *
	 */
	public void Histogram()
	{
		matchRateList = new HashMap<Integer, Double>();
		matchRateList.put(CV_COMP_INTERSECT, MATCH_RATE);
		matchRateList.put(CV_COMP_HELLINGER, MATCH_REVERSE_RATE);
	}

	/**
	 * 画像(path)をセットする.
	 * @param images
	 */
	public void setImages(List<String> images)
	{
		this.images = images;
	}

	/**
	 * 取り込まれたimagesの画像データから
	 * グレイスケールのヒストグラムを生成する.
	 * @return List<CvHistogram>
	 */
	public List<CvHistogram> createGrayScaleHistogram() {
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
	public List<CvHistogram> createColorHistogram() {
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
	 * @param hist1
	 * @param hist2
	 * @param cvComp
	 * @return boolean
	 */
	private boolean allowableRange(CvHistogram hist1, CvHistogram hist2, int cvComp) {
		double dRate = findMatchRateByCvComp(cvComp);
		double rate = cvCompareHist(hist1, hist2, cvComp);
		if (cvComp == CV_COMP_INTERSECT) {
			return rate < dRate;
		} else if (cvComp == CV_COMP_CHISQR) {
			return rate < dRate;
		} else if (cvComp == CV_COMP_BHATTACHARYYA) {
			return rate < dRate;
		} else {
			return rate < dRate;
		}
	}

	/**
	 *
	 * @param cvComp
	 * @return double matchRate
	 */
	private double findMatchRateByCvComp(int cvComp)
	{
		return matchRateList.get(cvComp);
	}

	/**
	 * ヒストグラム比較の実行.(戻り値調整中...)
	 * @param histograms
	 */
	public void execute(List<CvHistogram> histograms) {
		List<CvHistogram> hists = histograms;
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
				if (allowableRange(hists.get(i), hists.get(j), CV_COMP_HELLINGER)) {
					if (!group.containsKey(bName)) {
						group.put(bName, new HashMap<String, Boolean>());
					}
					group.get(name).put(bName, true);
					group.get(bName).put(name, true);
				}
			}
		}
		System.out.println(group);
	}
}
