import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * 
 * @author ZhangTao
 * @version 2.0
 * @date 2020/6/3
 * @Describtion 用户登录的2.0版本，将判断结合到sql语句中，只要判断查询是否有结果即可确定登录是否成功
 * 
 */
public class userlogin2 {

    public static Boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String sql = "Select * from userlogin where username='" + username + "' and password ='"+password+"'";

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

            return resultSet.next();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

            return false;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入用户名");
        String userName = sc.nextLine();
        System.out.println("请输入密码");
        String password = sc.nextLine();

        Boolean login = login(userName, password);

        if (login==true){
            System.out.println("welcome");
        }else{
            System.out.println("sorry");
        }
    }

}
