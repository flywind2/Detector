package jp.ac.aiit.Detector.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseMatcher {

	protected List<String> images;

	/**
	 * Constructor
	 */
	public BaseMatcher() {
		this.images = new ArrayList<String>();
	}

	/**
	 * 画像の追加
	 * @param image
	 */
	public void addImage(String image) {
		this.images.add(image);
	}

	/**
	 * 画像の複数追加
	 * @param images
	 */
	public void addImages(List<String> images) {
		for (String image: images) {
			this.images.add(image);
		}
	}

	/**
	 * 画像のクリア
	 */
	public void clearImages() {
		this.images.clear();
	}

	/**
	 * 画像(path)をセットする.
	 * @param images
	 */
	public void setImages(List<String> images) {
		this.images = images;
	}

	abstract  Map<String, Map<String, Boolean>> run();

}
