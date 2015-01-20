package jp.ac.aiit.Detector;


import java.util.HashMap;
import java.util.Map;

/**
 * 画像認識処理の結果を格納するクラス
 *
 * 構造：
 * 　Map<String, Map<String, Double>>を基礎とした構造。
 * 　例えば、001.jpgと002.jpgが類似画像で、003.jpgが類似画像なし、というフォルダを探索した場合
 * 　{"001.jpg"={"001.jpg"=1.0, "002.jpg"=0.9}, "003.jpg"={"003.jpg"=1} }
 * 　という形で格納される。Valueは類似度で、0.0~1.0の範囲となる。1.0が完全一致。
 *
 */
public class DetectorResult {

    private Map<String, Map<String, Double>> data;

    private Map<String, Double> group;

    public DetectorResult() {
        data = new HashMap<>();
    }

    /**
     * Map<String, Map<String, Double>の形で返却する。
     *
     * @return
     */
    public Map<String, Map<String, Double>> toMap() {
        return data;
    }

    /**
     * グループ追加メソッド。
     *
     * @param groupKey グループのキー名。ファイル名が入る。
     * @param groupFile 上記グループに所属するファイル名
     * @param score 一致しているスコア。0.0-1.0の範囲となり、1.0が完全一致
     */
    public void put(String groupKey, String groupFile, Double score) {

        if (!data.containsKey(groupKey)) {
            group = new HashMap<>();
        } else {
            group = data.get(groupKey);
        }

        group.put(groupFile, score);
        data.put(groupKey, group);
    }

    @Override
    public String toString() {
        return toMap().toString();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void clear() {
        data.clear();
    }
}
