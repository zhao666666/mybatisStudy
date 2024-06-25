package org.example.jdbc.v3;

import org.example.jdbc.User;

import java.util.List;

/**
 * @author ZhaoJie
 * @date 2024/6/25 17:48
 */
public class JdbcV3 {
    public static void main(String[] args) {
        queryUser();
    }

    /**
     *
     * 通过JDBC查询用户信息
     */
    public static void queryUser(){
        try {
            String sql = "SELECT id,user_name,real_name,password,age,d_id from t_user where id = ?";
            List<User> list = DBUtils.query(sql, User.class,1);
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
