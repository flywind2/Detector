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
}