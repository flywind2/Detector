package jp.ac.aiit.Detector;

import com.google.common.base.Objects;
import com.google.common.base.Stopwatch;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import jp.ac.aiit.Detector.util.Tool;
import jp.ac.aiit.DetectorLire.LireDemo;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static jp.ac.aiit.Detector.util.Debug.debug;
import static org.junit.Assert.assertTrue;

/**
 * ライブラリの実行時間を自動で計測するクラス
 * コンソールログ出力またはスプレッドシートへの記録まで行う。
 *
 * 本クラスはテスト時に自動的に実行されるものとする
 *
 */
public class AutoTimeTest {

    private final String APP_NAME = "pbl2014-Detector-0.1";

    @Test
    public void run() throws Exception {

        //画像数
        File[] files = Tool.getResourcePathFileList("/image");
        int count = files.length;
        //実行時間取得
        Map<String, String> retLire = runLire();
        Map<String, String> retDetector = runDetector();

        //環境変数からAPIキーとGITメッセージを取得
        //取得不可の場合はコンソールログ出力する
        String id = System.getenv("GOOGLE_USERNAME");
        String pass = System.getenv("GOOGLE_PASS");
        String jdkVer = System.getenv("TRAVIS_JDK_VERSION");
        String message = System.getenv("GIT_MESSAGE");
        if (pass == null) {
            debug("画像数", count);
            debug("Detector実行時間", retDetector.get("tm"));
            debug("Detector認識率", retDetector.get("rate"));
            debug("Lire実行時間", retLire.get("tm"));
            debug("Lire認識率", retLire.get("rate"));
            return;
        }

        SpreadsheetService service = getService(id, pass);

        // ファイルを取得
        FeedURLFactory urlFactory = FeedURLFactory.getDefault();
        SpreadsheetQuery spreadsheetQuery = new SpreadsheetQuery(urlFactory.getSpreadsheetsFeedUrl());
        spreadsheetQuery.setTitleQuery("processing_time");
        SpreadsheetFeed spreadsheetFeed = service.query(spreadsheetQuery, SpreadsheetFeed.class);
        SpreadsheetEntry sheet = spreadsheetFeed.getEntries().get(0);

        // WorkSheetを取得
        URL url = sheet.getWorksheetFeedUrl();
        WorksheetQuery query = new WorksheetQuery(url);
        query.setTitleQuery("集計");
        WorksheetFeed feed = service.query(query, WorksheetFeed.class);
        List<WorksheetEntry> worksheetEntryList = feed.getEntries();

        //WorkSheetを確定し、文言の追加
        WorksheetEntry worksheet = worksheetEntryList.get(0);
        URL listFeedUrl = worksheet.getListFeedUrl();
        ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

        //行の追加(nullだとエラーとなるので注意)
        ListEntry row = new ListEntry();
        row.getCustomElements().setValueLocal("コミット情報", message);
        row.getCustomElements().setValueLocal("画像数", Integer.toString(count));
        row.getCustomElements().setValueLocal("JDK", jdkVer);
        row.getCustomElements().setValueLocal("処理時間", retDetector.get("tm"));
        row.getCustomElements().setValueLocal("認識率", retDetector.get("rate"));
        row.getCustomElements().setValueLocal("Lire処理時間", retLire.get("tm"));
        row.getCustomElements().setValueLocal("Lire認識率", retLire.get("rate"));
        row = service.insert(listFeedUrl, row);

    }

    private SpreadsheetService getService(String id, String pass) throws AuthenticationException{

        // Spreadsheetsサービスへの認証を行う
        SpreadsheetService service = new SpreadsheetService(APP_NAME);
        service.setUserCredentials(id, pass);

        return service;
    }

    private Map<String, String> runLire() throws Exception{

        Map<String, String> ret = new HashMap<>();
        Stopwatch sw = Stopwatch.createUnstarted();
        sw.start();
        //処理時間計測
        LireDemo lire = new LireDemo();
        DetectorResult lireResult = lire.search();
        sw.stop();
        ret.put("tm", sw.toString());

        //認識率計測
        String rate = calcRate(lireResult);
        ret.put("rate", rate);

        return ret;
    }

    private Map<String, String> runDetector() throws Exception {
        Map<String, String> ret = new HashMap<>();
        Stopwatch sw = Stopwatch.createUnstarted();
        sw.start();
        //処理時間計測
        //
        sw.stop();
        ret.put("tm", sw.toString());

        //認識率計測
        String rate = "test";
        ret.put("rate", rate);

        return ret;
    }

    private String calcRate(DetectorResult result) {
        //組み合わせグループ単位で正しい組み合わせか、間違った組み合わせかを判断し、認識率を算出する
        //例：p001_01, p001_02, p002_01, p002_02, p002_03, d001というファイル名で
        // p001グループには01, 02 p002グループには01, 02, 03 d001グループはd001のみ という組み合わせが正しいとした場合
        // p001グループに01, 02以外があったらp001グループは正しくないとし、他があっていたら66%(2/3)と算出する

        //imageフォルダから正しい組み合わせを作成する
        DetectorResult imageFolderMap = new DetectorResult();
        File[] files = Tool.getResourcePathFileList("/image");
        for (File f: files) {
            String name = f.getName();

            if (name.indexOf("d") == 0) {
                //dなら一人グループ
                imageFolderMap.put(name, name, 1.0);
            } else if (name.indexOf("p") == 0) {
                //pならグループわけ
                //pxxx_01.jpg
                String pxxx_01 = name.split("_")[0] + "_01" + name.substring(name.lastIndexOf("."));
                imageFolderMap.put(pxxx_01, name, 1.0);
            }
        }

        debug(imageFolderMap.toString());
        int count = 0;
        int valid = 0;

        for (Map.Entry<String, Map<String, Double>> e: imageFolderMap.toMap().entrySet()) {
            count++;
            if (result.toMap().get(e.getKey()) != null
                    && Objects.equal(e.getValue().keySet(), result.toMap().get(e.getKey()).keySet())) {
                //認識エンジンの結果から同じキーのHashMapが取得でき
                //かつそこから同じkeySet(キー値のset)が取得できたら認識しているとみなす
                valid++;
            }
        }

        BigDecimal v = new BigDecimal(valid);
        BigDecimal c = new BigDecimal(count);
        return v.divide(c).toString();
    }
}
