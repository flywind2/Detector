package jp.ac.aiit.Detector.util;

import junit.framework.TestCase;

import java.io.File;

public class ToolTest extends TestCase {

    public void testGetResourcePath() throws Exception {
        String ret = Tool.getResourcePath(getClass(), "test");
        assertEquals(null, ret);

        String ret1 = Tool.getResourcePath(getClass(), "/aaa.txt");
        assertEquals((new File("build/resources/test/aaa.txt")).getAbsolutePath(), ret1);
    }
}