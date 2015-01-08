package jp.ac.aiit.Detector.matcher;

import java.util.List;
import java.util.Map;

public abstract class BaseMatcher {

	protected List<String> images;

	/**
	 * 画像(path)をセットする.
	 * @param images
	 */
	public void setImages(List<String> images) {
		this.images = images;
	}

	abstract  Map<String, Map<String, Boolean>> run();

}
