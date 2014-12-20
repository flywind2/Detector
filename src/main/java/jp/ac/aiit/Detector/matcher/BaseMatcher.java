package jp.ac.aiit.Detector.matcher;

import java.util.List;

/**
 * Created by development on 2014/12/20.
 */
public abstract class BaseMatcher {

	protected List<String> images;

	/**
	 * 画像(path)をセットする.
	 * @param images
	 */
	public void setImages(List<String> images) {
		this.images = images;
	}

	/**
	 * 実行メソッド
	 */
	abstract void execute();
}
