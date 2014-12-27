package jp.ac.aiit.Detector.data.db;

import com.sun.org.apache.xpath.internal.operations.Mod;
import jp.ac.aiit.Detector.data.db.model.ModelTest;
import junit.framework.TestCase;
import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class DbManagerTest {

    static Connection conn;

    @BeforeClass
    public static void testGetConnection() throws Exception {
        conn = DbManager.getConnection();

        assertNotNull(conn);
    }

    @AfterClass
    public static void testClose() throws Exception {
        if (conn == null) {
            conn = DbManager.getConnection();
        }

        DbManager.dropTable(ModelTest.class);

        try {
            conn.close();
        } catch (SQLException e) {
            assert false;
        }
        assert true;
    }

    @Test
    public void createAndSelectAndUpdateTable() throws Exception {
        DbManager.createTable(ModelTest.class, true);

        ModelTest test = new ModelTest();
        test.setId(1);
        test.setName("test");
        DbManager.insert(test);

        List<ModelTest> ret = DbManager.select(test.getClass(), "where id=? and name=?", new Object[] {1, "test"});
        for(ModelTest r: ret) {
            assertEquals(1, r.getId());
            assertEquals("test", r.getName());
        }

        test.setId(2);
        test.setName("test2");
        DbManager.update(test, "where id=?", new Object[] {1});
        List<ModelTest> ret1 = DbManager.select(test.getClass(), "", null);
        for(ModelTest r: ret1) {
            assertEquals(2, r.getId());
            assertEquals("test2", r.getName());
        }

        DbManager.delete(ModelTest.class, "", null);
        List<ModelTest> ret2 = DbManager.select(test.getClass(), "", null);
        assertEquals(0, ret2.size());

    }


    @Test
    public void testConcatWithCommas() throws Exception {
        List<String> ls = new ArrayList<String>();
        ls.add("test");
        ls.add("test1");
        ls.add("test2");
        String ret = DbManager.concatWithCommas(ls);

        assertEquals("test,test1,test2", ret);
    }

}