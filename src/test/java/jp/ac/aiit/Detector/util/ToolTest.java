package jp.ac.aiit.Detector.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import static org.junit.Assert.*;

public class ToolTest {

    @Test
    public void testGetResourcePath() throws Exception {
        String ret = Tool.getResourcePath("test");
        assertEquals(null, ret);

        String ret1 = Tool.getResourcePath("/aaa.txt");
        assertEquals((new File("build/resources/test/aaa.txt")).getAbsolutePath(), ret1);
    }

    @Test
    public void testGetResoucePathFileCount() throws Exception {
        int ret = Tool.getResourcePathFileCount("test");
        assertEquals(0, ret);

        int ret1 = Tool.getResourcePathFileCount("/aaa.txt");
        assertEquals(1, ret1); //fileの場合は1

        int ret2 = Tool.getResourcePathFileCount("/count");
        assertEquals(2, ret2);

    }

    @Test
    public void testResources() throws Exception {

        Debug.debug(Tool.getResourcePath("/image/pic1.JPG"));

    }
}