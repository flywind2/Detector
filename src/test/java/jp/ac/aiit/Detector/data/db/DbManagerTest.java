package jp.ac.aiit.Detector.data.db;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;

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
}