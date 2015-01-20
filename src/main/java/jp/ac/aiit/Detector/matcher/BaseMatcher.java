package jp.ac.aiit.Detector.matcher;

import jp.ac.aiit.Detector.DetectorResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseMatcher {

	/**
	 * 処理の結果
	 */
	protected DetectorResult result;
	protected List<String> images;
	private long startTime = 0;
	private long endTime   = 0;

	/**
	 * Constructor
	 */
	public BaseMatcher() {
		images = new ArrayList<String>();
		result = new DetectorResult();
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
	 * 結果をクリアする.
	 */
	public void clearResult() {
		if (!result.isEmpty()) {
			result.clear();
		}
	}

	/**
	 * 出力結果の取得
	 * @return result
	 */
	public DetectorResult getResult() {
		return result;
	}

	/**
	 * 処理時間を取得する
	 * @return long
	 */
	public long getProcessingTime() {
		return endTime - startTime;
	}

	/**
	 * タイマーウォッチをリセット
	 */
	public void resetTimeWatch() {
		startTime = 0;
		endTime   = 0;
	}

	/**
	 * タイムウォッチ開始
	 */
	protected void startTimeWatch() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * タイムウォッチ終了
	 */
	protected void endTimeWatch() {
		endTime = System.currentTimeMillis();
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

	/**
	 * 実行
	 * @return result
	 */
	abstract  DetectorResult run();

}
