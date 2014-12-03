package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Debug;

import java.util.*;

/**
 * テスト用クラス。いずれ消す
 */

public class Hello {
    public static void main(String[] args) {
        Debug.debug("test", "aaaaaaa");
        List l = new ArrayList<String>();
        l.add("test1");
        l.add("test2");
        Debug.debug("message:", l);
        Map m = new HashMap<String, String>();
        m.put("1", "test1");
        m.put("2", "test2");
        Debug.debug("test;", m);
        Calendar cal = Calendar.getInstance();
        Debug.debug("test;" , cal);
    }

    public int plus(int arg) {
        return arg + 5;
    }
}
