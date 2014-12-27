package jp.ac.aiit.Detector.data.db;

import jp.ac.aiit.Detector.util.Debug;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * DB接続用クラス
 * 画像の情報を一時的に保持し、グルーピングを行うためにDBを利用する
 *
 *
 */
public class DbManager {

    private static Connection conn = null;

    protected static String createTableStatement = "create TABLE %s (%s)";
    protected static String dropTableStatement = "drop TABLE %s";

    protected static QueryRunner qr = new QueryRunner();
    protected static Map<Class<?>, Map<String, String>> queryStatementMap
            = new HashMap<Class<?>, Map<String, String>>();

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

    /**
     * モデルクラスからテーブルをドロップする
     *
     * @param clazz
     * @throws SQLException
     */
    public static void dropTable(Class<?> clazz) throws SQLException {
        try {
            Connection con = getConnection();
            Statement statement = con.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(dropTableStatement, clazz.getSimpleName().toLowerCase()));
            statement.close();
        } catch (SQLException e) {
            Debug.debug("Error: " + e.getMessage());
        }
    }

    /**
     * モデルクラスからテーブルを作成する。
     * すでに存在している場合はドロップする
     *
     * @param clazz
     * @param forceDrop
     * @throws SQLException
     */
    public static void createTable(Class<?> clazz, boolean forceDrop) throws SQLException{
        if (forceDrop) {
            dropTable(clazz);
        }

        createTable(clazz);
    }

    /**
     * モデルクラスからテーブルを作成する
     *
     * @param clazz
     * @throws SQLException
     */
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
                    clazz.getSimpleName().toLowerCase(),
                    fieldDeclaration
            );
            Connection con = getConnection();

            //Table作成
            Statement statement = con.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(query);

            statement.close();

        } catch (SQLException e) {
            Debug.debug(DbManager.class.getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * モデルごとにSQL文を作成するメソッド
     *
     * @param clazz
     * @return
     */
    public static void registerTable (Class<?> clazz) {
        try {
            Field[] fields = clazz.getFields();
            List<String> fieldNames = new ArrayList<String>();
            List<String> insertions = new ArrayList<String>();
            List<String> fieldEquations = new ArrayList<String>();

            for (Field f: fields) {
                fieldNames.add(f.getName());
                insertions.add("?");
                fieldEquations.add(String.format("%s = ?", f.getName()));
            }

            String insertStatement = String.format(
                    "insert into %s(%s) values(%s)",
                    clazz.getSimpleName().toLowerCase(),
                    concatWithCommas(fieldNames),
                    concatWithCommas(insertions)
            );

            String selectStatement = String.format(
                    "select * from %s",
                    clazz.getSimpleName().toLowerCase()
            );

            String updateStatement = String.format(
                    "update %s set %s",
                    clazz.getSimpleName().toLowerCase(),
                    concatWithCommas(fieldEquations)
            );

            String deleteStatement = String.format(
                    "delete from %s",
                    clazz.getSimpleName().toLowerCase()
            );

            Map<String, String> queryStatements = new HashMap<String, String> ();
            queryStatements.put("insert", insertStatement);
            queryStatements.put("select", selectStatement);
            queryStatements.put ("update", updateStatement);
            queryStatements.put ("delete", deleteStatement);
            queryStatementMap.put (clazz, queryStatements);
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
        }
    }

    /**
     * モデルのインスタンスを渡してインサート実行
     * @param obj
     * @return
     * @throws Exception
     */
    public static void insert (Object obj) throws Exception {
        try {
            Class<?> clazz = obj.getClass();
            Map<String, String> queryStatements = getQueryStatementMap(clazz);
            String insertStatement = queryStatements.get("insert");
            List<Object> fieldValues = new ArrayList<Object>();
            for (Field f: clazz.getFields()) fieldValues.add(f.get(obj));
            qr.update(getConnection(), insertStatement, fieldValues.toArray());
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * SELECTを行うメソッド
     *
     * @param clazz
     * @param whereStatement Where句自体を文字列で
     * @param param Where句の値を配列で
     * @return
     * @throws Exception
     */
    public static List select (Class<?> clazz, String whereStatement, Object[] param) throws Exception{
        try {
            ResultSetHandler h = new BeanListHandler(clazz);
            Map<String, String> queryStatements = getQueryStatementMap(clazz);
            String selectStatement = queryStatements.get("select") + " " + whereStatement;
            return (List)qr.query(getConnection(), selectStatement, h, param);
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * UPDATEを行うメソッド
     *
     * @param obj
     * @param whereStatement Where句自体を文字列で
     * @param params Where句の値を配列で
     * @throws Exception
     */
    public static void update (Object obj, String whereStatement, Object[] params) throws Exception{
        try {
            Class<?> clazz = obj.getClass();
            Map<String, String> queryStatements = getQueryStatementMap(clazz);
            List<Object> params_ = new ArrayList<Object>();
            for (Field f: clazz.getFields()) params_.add(f.get(obj));
            params_.addAll(Arrays.asList(params));
            String updateStatement = queryStatements.get("update") + " " + whereStatement;
            qr.update(getConnection(), updateStatement, params_.toArray());
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * DELETEを行うメソッド
     *
     * @param clazz
     * @param whereStatement
     * @param params
     * @throws Exception
     */
    public static void delete (Class<?> clazz, String whereStatement, Object[] params) throws Exception{
        try {
            Map<String,String> queryStatements = getQueryStatementMap(clazz);
            String deleteStatement = queryStatements.get("delete") + " " + whereStatement;
            qr.update(getConnection(), deleteStatement, params);
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
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

    /**
     * queryStatementMapの返却。
     * ない場合は登録する。
     *
     * @param clazz
     * @return
     */
    protected static Map getQueryStatementMap(Class<?> clazz) {
        Map<String, String> queryStatements = queryStatementMap.get(clazz);
        if (queryStatements == null) {
            registerTable(clazz);
            queryStatements = queryStatementMap.get(clazz);
        }
        return queryStatements;
    }

    private DbManager() {
    }
}
