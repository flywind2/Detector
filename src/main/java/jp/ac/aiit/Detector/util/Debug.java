package jp.ac.aiit.Detector.util;

/**
 * Debugクラス
 * デバッグ用の文字を出力したい場合はこのクラスを使う事。
 * System,out,printlnは使用しない。
 *
 */
public class Debug {

    private static final boolean DEBUG = true;

    public static void debug(String out) {
        //TODO SLF4Jを使用したログ出力に対応
        if(DEBUG){
            System.out.println(out);
        }
    }

    public static void debug(String message, Object value) {
        if (value == null) {
            debug(message, "null");
        } else if (value instanceof String) {
            debug(message, (String) value);
        } else {
            debug(message, value.toString());
        }
    }

    private static void debug(String message, String value) {
        debug(message + " " + value);
    }

    private Debug() {
    }
}
