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

	public void execute(List<CvHistogram> histograms) {
		List<CvHistogram> hists = histograms;
		Map<Integer, Map<Integer, Boolean>> group = new HashMap<Integer, Map<Integer, Boolean>>();
		int len = hists.size();
		for (int i = 0; i < len; i++) {
			if (!group.containsKey(i)) {
				group.put(i, new HashMap<Integer, Boolean>());
			}
			for (int j = i; j < len; j++) {
				if (j == i) {
					continue;
				}
				if (group.containsKey(i)) {
					if (group.get(i).containsKey(j)) {
						continue;
					}
				}
				double matchRate = cvCompareHist(hists.get(i), hists.get(j), CV_COMP_INTERSECT);
				if (matchRate > 0.84) {
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
