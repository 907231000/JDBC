package cn.zt.jdbc.login;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * 
 * @author ZhangTao
 * @date 2020/6/3
 * @version 1.0
 * 
 *    jdbc.properties放再src目录下，里面保存了url，登录数据库的用户名密码，数据库的驱动
 */

public class userlogin {

    public static void login(String username ,String password){

        String sql = "Select username,password from userlogin";

        ResultSet resultSet = null;
        Connection conn = null;
        Statement stat = null;

        FileReader fileReader = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties prop = new Properties();
            ClassLoader classLoader = userlogin.class.getClassLoader();
            URL resource = classLoader.getResource("jdbc.properties");
            String path = resource.getPath();
            fileReader = new FileReader(path);
            prop.load(fileReader);

            String url = prop.getProperty("url");
            String un = prop.getProperty("user");
            String pw = prop.getProperty("password");

             conn = DriverManager.getConnection(url, un, pw);

             stat = conn.createStatement();

             resultSet = stat.executeQuery(sql);

            Boolean isCorrect = false;
            while (resultSet.next()){
                if (username .equals(resultSet.getString(1))  &&
                    password .equals(resultSet.getString(2))
                ){
                    isCorrect = true;
                }
            }
            if (isCorrect){
                System.out.println("登录成功");
            }else {
                System.out.println("登录失败");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }  catch (SQLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (resultSet!=null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (stat!=null){
                try {
                    stat.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (fileReader!=null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }


    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入用户名");
        String userName = sc.nextLine();
        System.out.println("请输入密码");
        String password = sc.nextLine();

        login(userName,password);
    }

}
