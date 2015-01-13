package jp.ac.aiit.Detector.util;

import java.io.File;

public class Tool {

    /**
     * resoucesフォルダのファイルを指定し、絶対パスを返却する。
     * getResources()を利用するとWindowsでは
     * 「/C:/aaa/bbb/ccc.txt」と言う形で頭にスラッシュが入ってしまう。
     *
     * @param s String resourcesフォルダのファイル。頭にスラッシュをつける事
     * @return
     */
    public static String getResourcePath(String s) {
        if (Tool.class.getResource(s) == null) {
            return null;
        }
        String p = Tool.class.getResource(s).getFile();
        File f = new File(p);
        return f.getAbsolutePath();
    }

    /**
     * resoucesフォルダ内のフォルダにファイルがいくつあるか返却する
     *
     * @param s
     * @return
     */
    public static int getResourcePathFileCount(String s) {
        int ret = 0;
        if (Tool.class.getResource(s) == null) {
            return 0;
        }
        String p = Tool.class.getResource(s).getFile();
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
