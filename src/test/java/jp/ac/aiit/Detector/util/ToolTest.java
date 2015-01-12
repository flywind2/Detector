package jp.ac.aiit.Detector.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import static org.junit.Assert.*;

public class ToolTest {

    @Test
    public void testGetResourcePath() throws Exception {
        String ret = Tool.getResourcePath(getClass(), "test");
        assertEquals(null, ret);

        String ret1 = Tool.getResourcePath(getClass(), "/aaa.txt");
        assertEquals((new File("build/resources/test/aaa.txt")).getAbsolutePath(), ret1);
    }

    @Test
    public void testGetResoucePathFileCount() throws Exception {
        int ret = Tool.getResourcePathFileCount(getClass(), "test");
        assertEquals(0, ret);

        int ret1 = Tool.getResourcePathFileCount(getClass(), "/aaa.txt");
        assertEquals(1, ret1); //fileの場合は1

        int ret2 = Tool.getResourcePathFileCount(getClass(), "/count");
        assertEquals(2, ret2);

    }
}