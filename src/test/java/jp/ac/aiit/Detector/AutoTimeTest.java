package jp.ac.aiit.Detector;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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

        //コミット時間取得
        //実行時間取得
        //コミットメッセージ取得


        //環境変数からAPIキーとGITメッセージを取得
        //取得不可の場合はコンソールログ出力する
        String id = System.getenv("GOOGLE_USERNAME");
        String pass = System.getenv("GOOGLE_PASS");
        String jdkVer = System.getenv("TRAVIS_JDK_VERSION");
        String message = System.getenv("GIT_MESSAGE");
        if (pass == null) {
            System.out.println("gradle test");
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
        row.getCustomElements().setValueLocal("JDK", jdkVer);
        row.getCustomElements().setValueLocal("処理時間", "じかん");
        row = service.insert(listFeedUrl, row);

    }

    private SpreadsheetService getService(String id, String pass) throws AuthenticationException{

        // Spreadsheetsサービスへの認証を行う
        SpreadsheetService service = new SpreadsheetService(APP_NAME);
        service.setUserCredentials(id, pass);

        return service;
    }
}
