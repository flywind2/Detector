package jp.ac.aiit.Detector.util;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tool {

    /**
     * resoucesフォルダのファイルを指定し、絶対パスを返却する。
     * getResources()を利用するとWindowsでは
     * 「/C:/aaa/bbb/ccc.txt」と言う形で頭にスラッシュが入ってしまう。
     *
     * なお、getResources()の仕様としてはresourcesフォルダを探すわけではなく、クラスパスが通った箇所を全て探索する。
     * そのため、/images フォルダで探索しようとするとresourcesではなく別のフォルダを見に行く可能性がある。
     * ファイル名も含めて一意になるようにフォルダ構成や指定パスを考える事で想定外の場所を探索することを防ぐ。
     *
     * 例：/images/pic1.jpgならばresourcesフォルダ配下のものを取得する。
     *
     * @param s String resourcesフォルダのファイル。頭にスラッシュをつける事
     * @return
     */
    public static String getResourcePath(String s) {
        //.classは実行classのクラスパスが通ったところを探索するという意味なので
        //なんでも良い模様
        if (Tool.class.getResource(s) == null) {
            return null;
        }
        String p = Tool.class.getResource(s).getFile();
        File f = new File(p);
        return f.getAbsolutePath();
    }

    /**
     * resoucesフォルダ内のフォルダにあるファイルリストを返却する
     * 引数がファイルの場合は自身を返却する（要素数１のリスト）
     *
     * @param s
     * @return
     */
    public static File[] getResourcePathFileList(String s) {

        if (Tool.class.getResource(s) == null) {
            return null;
        }
        String p = Tool.class.getResource(s).getFile();
        File f = new File(p);
        File[] fs = f.listFiles();

        //ファイルの場合はnullなので1を返却する
        if (fs == null) {
            File[] files = new File[1];
            files[0] = f;
            return files;
        } else {
            return fs;
        }
    }

}
