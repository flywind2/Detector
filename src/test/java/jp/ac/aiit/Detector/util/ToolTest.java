package jp.ac.aiit.Detector.util;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

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
        File[] ret = Tool.getResourcePathFileList("test");
        assertEquals(null, ret);

        File[] ret1 = Tool.getResourcePathFileList("/aaa.txt");
        assertEquals(1, ret1.length); //fileの場合は1

        File[] ret2 = Tool.getResourcePathFileList("/count");
        assertEquals(2, ret2.length);

    }
}