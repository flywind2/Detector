package jp.ac.aiit.Detector.data.db;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbManagerTest extends TestCase {

    Connection conn;
    public void testGetConnection() throws Exception {
        conn = DbManager.getConnection();

        assertNotNull(conn);
    }

    public void testClose() throws Exception {
        if (conn == null) {
            conn = DbManager.getConnection();
        }

        try {
            conn.close();
        } catch (SQLException e) {
            assert false;
        }
        assert true;
    }


    public void testConcatWithCommas() throws Exception {
        List<String> ls = new ArrayList<String>();
        ls.add("test");
        ls.add("test1");
        ls.add("test2");
        String ret = DbManager.concatWithCommas(ls);

        assertEquals("test,test1,test2", ret);
    }
}