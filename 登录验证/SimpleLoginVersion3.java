import java.sql.*;
import java.util.Scanner;

public class userlogin3 {
    /**
     * @author ZhangTao
     * @date 2020/6/4
     * @version 3.0
     * @Describtion 解决了SQL注入的问题
     */
    public static boolean login(String u, String p) {
        if (u==null || p ==null){
            return false;
        }

        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet resultSet = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1?serverTimezone=GMT%2B8",
                    "root", "000000");

            String sql = "SELECT * from userlogin where username = ? and password = ?";


            stat = conn.prepareStatement(sql);

            stat.setString(1, u);
            stat.setString(2, p);

            resultSet = stat.executeQuery();

            if (resultSet.next()) {
                return true;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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

        }


        return false;
    }

    public static void judge(boolean flag) {
        if (flag == true) {
            System.out.println("Welcome！");
        } else {
            System.out.println("Sorry!");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your username");
        String u = sc.nextLine();
        System.out.println("Enter your password");
        String p = sc.nextLine();

        judge(login(u, p));

    }
}
