package jp.ac.aiit.Detector.data.db;

import jp.ac.aiit.Detector.util.Debug;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DB接続用クラス
 * 画像の情報を一時的に保持し、グルーピングを行うためにDBを利用する
 *
 *
 */
public class DbManager {

    private static Connection conn = null;

    protected static String createTableStatement = "create TABLE %s (%s);";

    protected static Map<String, String> fieldTypeMap = new HashMap<String, String>() {{
        put ("int", "INT");
        put ("long", "INT");
        put ("double", "DOUBLE");
        put ("float", "FLOAT");
        put ("boolean", "BOOLEAN");

        put ("java.lang.Integer", "INT");
        put ("java.lang.Long", "INT");
        put ("java.lang.Double", "DOUBLE");
        put ("java.lang.Float", "FLOAT");
        put ("java.lang.Boolean", "BOOLEAN");

        put ("java.lang.String", "varchar(255)");
    }};

    /**
     * コネクション取得
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:derby:memory:Detector;create=true");
            } catch (SQLException e) {
                Debug.debug(DbManager.class.getName(), e.getMessage());
                throw e;
            }
        }
        return conn;
    }

    /**
     * コネクションクローズした後、シャットダウンまで行う
     *
     * @throws SQLException
     */
    public static void close() throws SQLException{
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException e) {
            Debug.debug(DbManager.class.getName(), e.getMessage());
            throw e;
        }
        conn = null;

        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            Debug.debug(e.getSQLState());
            if (!e.getSQLState().equals("XJ015")) {
                Debug.debug(DbManager.class.getName(), e.getMessage());
                throw e;
            }
        }
    }

    public static void createTable(Class<?> clazz) throws SQLException{
        try {
            //Modelクラスからクラス変数を取得しDBカラムとして定義する
            Field[] fields = clazz.getFields();
            List<String> declarations = new ArrayList<String>();
            for (Field f : fields) {
                String fieldType = fieldTypeMap.get(f.getType().getName());
                if (fieldType == null) continue;
                declarations.add(f.getName() + " " + fieldType);
            }

            String fieldDeclaration = concatWithCommas(declarations);
            String query = String.format(
                    createTableStatement,
                    clazz.getName().toLowerCase(),
                    fieldDeclaration
            );
            Connection con = getConnection();

            //Table作成
            Statement statement = con.createStatement();
            statement.executeUpdate(query);

            statement.close();
        } catch (SQLException e) {
            Debug.debug(DbManager.class.getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * 文字列リストをカンマ区切りで返却する
     * @param words
     * @return
     */
    protected static String concatWithCommas(List<String> words) {
        StringBuffer wordList = new StringBuffer();
        for (String word : words) wordList.append(word + ",");
        return new String(wordList.deleteCharAt(wordList.length() - 1));
    }

    private DbManager() {
    }
}
