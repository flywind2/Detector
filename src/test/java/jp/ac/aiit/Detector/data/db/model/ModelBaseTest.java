package jp.ac.aiit.Detector.data.db.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ModelBaseTest {

    static Connection conn;

    @BeforeClass
    public static void testGetConnection() throws Exception {
        conn = ModelBase.getConnection();

        assertNotNull(conn);
    }

    //@AfterClass
    public static void testClose() throws Exception {
        if (conn == null) {
            conn = ModelBase.getConnection();
        }

        ModelBase.dropTable(ModelTest.class);

        try {
            conn.close();
        } catch (SQLException e) {
            assert false;
        }
        assert true;
    }


    @Test
    public void testConcatWithCommas() throws Exception {
        List<String> ls = new ArrayList<String>();
        ls.add("test");
        ls.add("test1");
        ls.add("test2");
        String ret = ModelBase.concatWithCommas(ls);

        assertEquals("test,test1,test2", ret);
    }

}