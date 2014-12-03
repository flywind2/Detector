package jp.ac.aiit.Detector;

import jp.ac.aiit.Detector.util.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    public int plus(int arg) {
        return arg + 5;
    }
}
