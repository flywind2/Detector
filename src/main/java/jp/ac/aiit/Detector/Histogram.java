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

	private List<String> images;

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
	 *
	 * @return
	 */
	public List<CvHistogram> createColorHistogram() {
		List<CvHistogram> hists = new ArrayList<CvHistogram>();
		for (int i = 0; i < images.size(); i++) {
			IplImage img = cvLoadImage(images.get(i), CV_LOAD_IMAGE_COLOR);
			List<IplImage> dst = new ArrayList<IplImage>();
			for (int k = 0; k < img.nChannels(); k++) {
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
	 * @param matchPermit
	 * @param matchType
	 * @return
	 */
	private boolean matchPattern(CvHistogram hist1, CvHistogram hist2, double matchPermit, int matchType) {
		double rate = cvCompareHist(hist1, hist2, matchType);
		if (matchType == CV_COMP_INTERSECT) {
			return rate < matchPermit;
		} else if (matchType == CV_COMP_CHISQR) {
			return rate < matchPermit;
		} else if (matchType == CV_COMP_BHATTACHARYYA) {
			return rate < matchPermit;
		} else if (matchType == CV_COMP_HELLINGER) {
			return rate < matchPermit;
		} else {
			return rate < matchPermit;
		}
	}

	/**
	 * ヒストグラム比較の実行.(戻り値調整中...)
	 * @param histograms
	 */
	public void execute(List<CvHistogram> histograms) {
		List<CvHistogram> hists = histograms;
		Map<Integer, Map<Integer, Boolean>> group = new HashMap<Integer, Map<Integer, Boolean>>();
		int len = hists.size();
		for (int i = 0; i < len; i++) {
			if (!group.containsKey(i)) {
				group.put(i, new HashMap<Integer, Boolean>());
			} else {
				continue;
			}
			for (int j = i + 1; j < len; j++) {

				if (matchPattern(hists.get(i), hists.get(j), 0.2, CV_COMP_HELLINGER)) {
					if (!group.containsKey(j)) {
						group.put(j, new HashMap<Integer, Boolean>());
					}
					group.get(i).put(j, true);
					group.get(j).put(i, true);
				}
			}
		}
		System.out.println(group);
	}
}
