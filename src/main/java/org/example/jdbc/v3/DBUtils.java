package org.example.jdbc.v3;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ZhaoJie
 * @date 2024/6/25 17:26
 */
public class DBUtils {
    private static final String JDBC_URL = "jdbc:mysql://47.97.98.104:3306/mybatisdb?characterEncoding=utf-8&serverTimezone=UTC";
    private static final String JDBC_NAME = "root";
    private static final String JDBC_PASSWORD = "";

    private static Connection conn;

    /**
     * 对外提供获取数据库连接的方法
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        if(conn == null){
            try{
                conn = DriverManager.getConnection(JDBC_URL,JDBC_NAME,JDBC_PASSWORD);
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception();
            }
        }
        return conn;
    }

    /**
     * 关闭资源
     * @param conn
     */
    public static void close(Connection conn ){
        close(conn,null);
    }

    public static void close(Connection conn, Statement sts ){
        close(conn,sts,null);
    }

    public static void close(Connection conn, Statement sts , ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(sts != null){
            try {
                sts.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(conn != null){
            try {
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 执行数据库的DML操作
     * @return
     */
    public static Integer update(String sql,Object ... paramter) throws Exception{
        conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(paramter != null && paramter.length > 0){
            for (int i = 0; i < paramter.length; i++) {
                ps.setObject(i+1,paramter[i]);
            }
        }
        int i = ps.executeUpdate();
        close(conn,ps);
        return i;
    }

    /**
     * 查询方法的简易封装
     * @param sql
     * @param clazz
     * @param parameter
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> query(String sql, Class clazz, Object ... parameter) throws  Exception{
        conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        //参数遍历
        if(parameter != null && parameter.length > 0){
            for (int i = 0; i < parameter.length; i++) {
                ps.setObject(i+1,parameter[i]);
            }
        }
        ResultSet rs = ps.executeQuery();
        // 获取对应的表结构的元数据
        ResultSetMetaData metaData = ps.getMetaData();
        List<T> list = new ArrayList<>();
        while(rs.next()){
            // 根据 字段名称获取对应的值 然后将数据要封装到对应的对象中
            int columnCount = metaData.getColumnCount();
            //反射获取对象
            Object o = clazz.newInstance();
            for (int i = 1; i < columnCount+1; i++) {
                // 根据每列的名称获取对应的值
                String columnName = metaData.getColumnName(i);
                Object columnValue = rs.getObject(columnName);
                setFieldValueForColumn(o,columnName,columnValue);
            }
            list.add((T) o);
        }
        return list;
    }

    /**
     * 根据字段名称设置 对象的属性
     * @param o
     * @param columnName
     */
    private static void setFieldValueForColumn(Object o, String columnName,Object columnValue) {
        Class<?> clazz = o.getClass();
        try {
            // 根据字段获取属性
            Field field = clazz.getDeclaredField(columnName);
            // 私有属性放开权限
            field.setAccessible(true);
            field.set(o,columnValue);
            field.setAccessible(false);
        }catch (Exception e){
            // 说明不存在 那就将 _ 转换为 驼峰命名法
            if(columnName.contains("_")){
                Pattern linePattern = Pattern.compile("_(\\w)");
                columnName = columnName.toLowerCase();
                Matcher matcher = linePattern.matcher(columnName);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
                }
                matcher.appendTail(sb);
                // 再次调用复制操作
                setFieldValueForColumn(o,sb.toString(),columnValue);
            }
        }
    }
}
