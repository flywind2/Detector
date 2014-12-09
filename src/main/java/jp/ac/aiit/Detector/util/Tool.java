package jp.ac.aiit.Detector.util;

import java.io.File;

/**
 * Created by afx on 2014/12/10.
 */
public class Tool {

    /***
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
}
