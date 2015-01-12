package jp.ac.aiit.Detector;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import jp.ac.aiit.Detector.util.Debug;
import jp.ac.aiit.Detector.util.Tool;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void run() throws ServiceException, IOException {

        //画像数
        int count = Tool.getResourcePathFileCount(getClass(), "/image");
        //実行時間取得
        Map<String, String> retLire = new HashMap<String, String>();
        retLire = runLire();

        //環境変数からAPIキーとGITメッセージを取得
        //取得不可の場合はコンソールログ出力する
        String id = System.getenv("GOOGLE_USERNAME");
        String pass = System.getenv("GOOGLE_PASS");
        String jdkVer = System.getenv("TRAVIS_JDK_VERSION");
        String message = System.getenv("GIT_MESSAGE");
        if (pass == null) {
            Debug.debug("画像数", count);
            Debug.debug("Detector実行時間", "");
            Debug.debug("Detector認識率", "");
            Debug.debug("Lire実行時間", retLire.get("tm"));
            Debug.debug("Lire認識率", retLire.get("rate"));
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

        //行の追加
        ListEntry row = new ListEntry();
        row.getCustomElements().setValueLocal("コミット情報", message);
        row.getCustomElements().setValueLocal("画像数", Integer.toString(count));
        row.getCustomElements().setValueLocal("JDK", jdkVer);
        row.getCustomElements().setValueLocal("処理時間", "じかん");
        row.getCustomElements().setValueLocal("認識率", "");
        row.getCustomElements().setValueLocal("Lire処理時間", retLire.get("tm") + "ms");
        row.getCustomElements().setValueLocal("Lire認識率", retLire.get("rate"));
        row = service.insert(listFeedUrl, row);

    }

    private SpreadsheetService getService(String id, String pass) throws AuthenticationException{

        // Spreadsheetsサービスへの認証を行う
        SpreadsheetService service = new SpreadsheetService(APP_NAME);
        service.setUserCredentials(id, pass);

        return service;
    }

    private Map<String, String> runLire() {

        Map<String, String> ret = new HashMap<String, String>();

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        ret.put("tm", Long.toString(end - start));

        return ret;
    }
}
