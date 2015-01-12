package jp.ac.aiit.Detector.util;

import java.io.File;

public class Tool {

    /**
     * resoucesフォルダのファイルを指定し、絶対パスを返却する。
     * getResources()を利用するとWindowsでは
     * 「/C:/aaa/bbb/ccc.txt」と言う形で頭にスラッシュが入ってしまう。
     *
     * @param c Class getClass() またはstatic methodの場合はHello.class
     * @param s String resourcesフォルダのファイル。頭にスラッシュをつける事
     * @return
     */
    public static String getResourcePath(Class c, String s) {
        if (c.getResource(s) == null) {
            return null;
        }
        String p = c.getResource(s).getFile();
        File f = new File(p);
        return f.getAbsolutePath();
    }

    /**
     * resoucesフォルダ内のフォルダにファイルがいくつあるか返却する
     *
     * @param c
     * @param s
     * @return
     */
    public static int getResourcePathFileCount(Class c, String s) {
        int ret = 0;
        if (c.getResource(s) == null) {
            return 0;
        }
        String p = c.getResource(s).getFile();
        File f = new File(p);
        File[] fs = f.listFiles();

        //ファイルの場合はnullなので1を返却する
        if (fs == null) {
            ret = 1;
        } else {
            ret = fs.length;
        }

        return ret;
    }
}
