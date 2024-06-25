package org.example.jdbc.v1;

import org.example.jdbc.User;

import java.sql.*;

/**
 * @author ZhaoJie
 * @date 2024/6/25 10:08
 */
public class JdbcDemo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        query();
    }

    private static void query() throws ClassNotFoundException, SQLException {
        // 注册 JDBC 驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 打开连接
        Connection conn = DriverManager.getConnection("jdbc:mysql://47.97.98.104:3306/mybatisdb?characterEncoding=utf-8&serverTimezone=UTC", "root", "");
        // 执行查询
        Statement stmt = conn.createStatement();
        String sql = "SELECT id,user_name,real_name,password,age,d_id from t_user where id = 1";
        ResultSet rs = stmt.executeQuery(sql);
        User user = new User();
        // 获取结果集
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String userName = rs.getString("user_name");
            String realName = rs.getString("real_name");
            String password = rs.getString("password");
            Integer did = rs.getInt("d_id");
            user.setId(id);
            user.setUserName(userName);
            user.setRealName(realName);
            user.setPassword(password);
            user.setDId(did);
            System.out.println(user);
        }
    }
}
