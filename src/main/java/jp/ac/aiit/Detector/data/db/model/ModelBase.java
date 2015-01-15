package jp.ac.aiit.Detector.data.db.model;

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
 * DB接続、テーブル作成、参照更新削除を行う
 * 本クラスを継承することで利用可能。
 * 画像の情報を一時的に保持し、グルーピングを行うためにDBを利用する
 *
 *
 */
public class ModelBase {

    private static Connection connection = null;

    protected static String createTableStatement = "create TABLE %s (%s)";
    protected static String dropTableStatement = "drop TABLE %s";


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
     * @throws SQLException
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:derby:memory:Detector;create=true");
            } catch (SQLException e) {
                Debug.debug(ModelBase.class.getName(), e.getMessage());
            }
        }
        return connection;
    }

    /**
     * コネクションクローズした後、シャットダウンまで行う
     *
     * @throws SQLException
     */
    public static void close() throws SQLException{
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            Debug.debug(ModelBase.class.getName(), e.getMessage());
            throw e;
        }
        connection = null;

        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            Debug.debug(e.getSQLState());
            if (!e.getSQLState().equals("XJ015")) {
                Debug.debug(ModelBase.class.getName(), e.getMessage());
                throw e;
            }
        }
    }

    /**
     * モデルクラスからテーブルをドロップする
     *
     * @param clazz
     */
    public static void dropTable(Class<?> clazz) {
        try {
            Statement statement = connection.createStatement();
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
     */
    public static void createTable(Class<?> clazz, boolean forceDrop) {
        if (forceDrop) {
            dropTable(clazz);
        }

        createTable(clazz);
    }

    /**
     * モデルクラスからテーブルを作成する
     *
     * @param clazz
     */
    public static void createTable(Class<?> clazz) {
        try {
            //Modelクラスからクラス変数を取得しDBカラムとして定義する
            Field[] fields = clazz.getDeclaredFields();
            List<String> declarations = new ArrayList<String>();
            for (Field f : fields) {
                f.setAccessible(true);
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

            //Table作成
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(query);

            statement.close();

        } catch (SQLException e) {
            Debug.debug(ModelBase.class.getName(), e.getMessage());
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

    private Connection con;
    private QueryRunner qr;
    private Map<Class<?>, Map<String, String>> queryStatementMap;
    private Class<?> clazz;

    public ModelBase() {
        this.con = getConnection();
        this.qr = new QueryRunner();
        this.queryStatementMap = new HashMap<Class<?>, Map<String, String>>();
        this.clazz = getClass();
    }

    /**
     * テーブルを作成するメソッド
     *
     */
    public void createTable() {
        createTable(this.clazz, true);
    }

    /**
     * モデルごとにSQL文を作成するメソッド
     *
     * @return
     */
    public void registerTable () {
        try {
            Field[] fields = this.clazz.getFields();
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
                    this.clazz.getSimpleName().toLowerCase(),
                    concatWithCommas(fieldNames),
                    concatWithCommas(insertions)
            );

            String selectStatement = String.format(
                    "select * from %s",
                    this.clazz.getSimpleName().toLowerCase()
            );

            String updateStatement = String.format(
                    "update %s set %s",
                    this.clazz.getSimpleName().toLowerCase(),
                    concatWithCommas(fieldEquations)
            );

            String deleteStatement = String.format(
                    "delete from %s",
                    this.clazz.getSimpleName().toLowerCase()
            );

            Map<String, String> queryStatements = new HashMap<String, String> ();
            queryStatements.put("insert", insertStatement);
            queryStatements.put("select", selectStatement);
            queryStatements.put ("update", updateStatement);
            queryStatements.put ("delete", deleteStatement);
            queryStatementMap.put(this.clazz, queryStatements);
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
        }
    }

    /**
     * モデルのインスタンスを渡してインサート実行
     * @return
     * @throws Exception
     */
    public void insert() throws Exception {
        try {
            Map<String, String> queryStatements = getQueryStatementMap();
            String insertStatement = queryStatements.get("insert");
            List<Object> fieldValues = new ArrayList<Object>();
            for (Field f: this.clazz.getFields()) {
                fieldValues.add(f.get(this));
            }
            qr.update(this.con, insertStatement, fieldValues.toArray());
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * SELECTを行うメソッド
     *
     * @param whereStatement Where句自体を文字列で
     * @param param Where句の値を配列で
     * @return
     * @throws Exception
     */
    public List select(String whereStatement, Object[] param) throws Exception{
        try {
            ResultSetHandler h = new BeanListHandler(clazz);
            Map<String, String> queryStatements = getQueryStatementMap();
            String selectStatement = queryStatements.get("select") + " " + whereStatement;
            return (List)qr.query(this.con, selectStatement, h, param);
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * UPDATEを行うメソッド
     *
     * @param whereStatement Where句自体を文字列で
     * @param params Where句の値を配列で
     * @throws Exception
     */
    public void update(String whereStatement, Object[] params) throws Exception{
        try {
            Map<String, String> queryStatements = this.getQueryStatementMap();
            List<Object> params_ = new ArrayList<Object>();
            for (Field f: this.clazz.getFields()) {
                params_.add(f.get(this));
            }
            params_.addAll(Arrays.asList(params));
            String updateStatement = queryStatements.get("update") + " " + whereStatement;
            qr.update(this.con, updateStatement, params_.toArray());
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * DELETEを行うメソッド
     *
     * @param whereStatement
     * @param params
     * @throws Exception
     */
    public void delete(String whereStatement, Object[] params) throws Exception{
        try {
            Map<String,String> queryStatements = this.getQueryStatementMap();
            String deleteStatement = queryStatements.get("delete") + " " + whereStatement;
            qr.update(this.con, deleteStatement, params);
        } catch (Exception e) {
            Debug.debug("Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * queryStatementMapの返却。
     * ない場合は登録する。
     *
     * @return
     */
    private Map<String, String> getQueryStatementMap() {
        Map<String, String> queryStatements = queryStatementMap.get(this.clazz);
        if (queryStatements == null) {
            registerTable();
            queryStatements = queryStatementMap.get(this.clazz);
        }
        return queryStatements;
    }

}
