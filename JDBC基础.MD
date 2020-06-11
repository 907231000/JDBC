## JDBC

#### 概念

​		Java Database Connectivity  ，Java 数据库连接

​		本质是官方定义的一套操作关系型数据库的规则即接口，各个数据库厂商各自实现这套接口，提供数据库驱动jar包，我们可以使用这套接口(JDBC)编程，真正执行的代码是驱动jar包中的实现类

#### JDBC快速入门

​		步骤

​				1、导入驱动jar包

​				2、注册驱动

​				3、获取数据库连接对象 Connection

​				4、定义SQL

​				5、获取执行sql语句的对象 statement

​				6、执行SQL

​				7、接受返回结果

​				8、处理结果

​				9、释放资源

## JDBC中各个接口和类的详解

#### DriverManager 驱动管理类

​		功能：	

##### 				1、注册驱动

​						`static void registerDriver(Driver driver)`

​						而写代码使用的是：`Class.forName(com.mysql.cj.jdbc.Driver);`也就是加载了“com.mysql.cj.jdbc.Driver”这个类，我们去jdbc_mysql的jar包查看一下这个类的信息，发现类中存在静态代码块：

```java
static {
        try {
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }
```

​					所以其实Class.forName也只是在执行registerDirver方法，告诉程序需要使用哪个驱动jar

​					MySQL5后注册驱动的代码可以省略			

##### 			2、获取数据库连接

```java
public static Connection getConnection（ String url , String user , String password）;
/*
	参数
		url：指定连接的路径
			语法：jdbc:mysql://ip地址或域名：端口号/数据库名称
			例子：jdbc:mysql://localhost:3306/db1
		user：用户名
		password：密码
		如果连接的是本机的MySQL服务器，并且端口是3306，则url可以简写为：jdbc:mysql:///数据库名称
		
*/
```

#### Connection 数据库连接的接口

​			**功能：**

##### 					1、获取执行sql的对象

​							1、Statement createStatement（）；

​							2、preparedStatement prepareStatement（String sql）；

##### 					2、管理事务

​							1、开启 `void setAutoCommit（boolean autoCommit）`：调用方法参数为false，即设置手动提交，即开启事务

​							2、提交 void commit( );

​							3、回滚 void rollback( );

#### Statement 执行SQL的对象

**1、执行sql**

​			1、`boolean execute(Strint sql)`：可以执行任意语句

​			2、int executeUpdate(String sql)`:可以执行DML（增删改）、DDL（对库表的操作），返回值是影响的行数

**2、下面封装了一个使用本地Mysql的默认端口号的，可执行单挑DML，DDL语句的方法**

```java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class methodJDBC {
    public static void  methodsql(String database,String sql,String user,String password){
        Connection conn = null;
        Statement stat = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            String mysql = sql;



            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database+"?serverTimezone=GMT%2B8", user, password);

            stat = conn.createStatement();

            int count = stat.executeUpdate(mysql);


            System.out.println("本次操作影响了"+count+"行数据");


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }
}

```



#### ResultSet 结果集对象

结果集对象，封装查询结果

**方法**

​	1、next( ) 游标向下移动一行

​	2、getxxx( 参数 ) 获取一列  

​					xxx：代表数据类型

​					参数：情况1：int：代表列的编号，从1开始！！

​								情况2：String：列名称，直接传入列名

**举例**

下面的代码，使用查询语句，并将查询结果保存到对象中，序列化，再反序列化

```java
/*
	Person类中定义对应数据库中的三个属性id，name，balance，补充构造方法，getter和setter，toString
*/

import java.io.Serializable;

public class Person implements Serializable {
    private int id;
    private String name;
    private int balance;

    public Person(int id, String name, int balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }

    public Person() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
```

```java
/*
JDBC查询数据库，返回值传入Person对象，并进行序列化
*/

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;

public class jdbcSelect {

    public static void main(String[] args) {

        Connection conn = null;
        Statement stat = null;
        ObjectOutputStream oops = null;
        FileOutputStream fos = null;
        ResultSet resultSet = null;

        try {
            fos = new FileOutputStream("D:in.txt");

            oops = new ObjectOutputStream(fos);

            Class.forName("com.mysql.cj.jdbc.Driver");

            String sql = "Select * from acccount";

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1?serverTimezone=GMT%2B8"
                    , "root", "000000");

            stat = conn.createStatement();

            resultSet = stat.executeQuery(sql);

            while (resultSet.next()) {

                try {
                    oops.writeObject(new Person(resultSet.getInt(1),
                            resultSet.getString(2), resultSet.getInt(3)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


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
            try {
                oops.close();

                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
}

```

```java
/*
从文件中反序列化并打印
*/

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class READ {
    public static void main(String[] args) throws IOException, ClassNotFoundException {


        ObjectInputStream oips = new ObjectInputStream(new FileInputStream("D:\\in.txt"));

        Object o = oips.readObject();

        System.out.println(o);

    }
}
```



#### JDBC工具类

​		使用jdbc操作数据库有大量的重复的代码，可以封装他们，做一个属于自己的工具类，简化代码

```java

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

public class JDBCutils {

    /**
     * @author ZhangTao
     * @Version 1
     */


    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {

        try {
/*
	有很多方式从文件中读取数据，但是以赋值形式写出的，可以使用Properties，使用非常方便
*/
            Properties prop = new Properties();
/*
	FileReader如何传入相对路径，当配置文件放在src目录下时
	1、类.class.getClassLoader   获取类加载器
	2、classloader.getResouce    获取url资源
	3、resouce.getPath           获取字符串格式的相对路径
*/
            ClassLoader classLoader = JDBCutils.class.getClassLoader();
            URL resource = classLoader.getResource("jdbc.properties");
            String path = resource.getPath();
            prop.load(new FileReader(path));

            url = prop.getProperty("url");
            username = prop.getProperty("user");
            password = prop.getProperty("password");

            Class.forName(prop.getProperty("driver"));

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }

    public static void close(Statement stat, Connection conn) {

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

    public static void close(ResultSet resultSet, Statement stat, Connection conn) {
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
}
```





#### 练习，登录验证

```java
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

```

下面是2.0版本

```java
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

```



#### PreparedStatement 执行SQL的对象  

**1、SQL注入问题：**

​		我们发现对上述的代码进行测试时，当输入请输入任意用户名与密码 a' or 'a' = 'a 时可以成功登录

```mysql
Select * from userlogin where username='adverster' and password ='a' or 'a' = 'a'
```



**2、解决SQL注入问题**

​		使用PreparedStatement对象来解决

​		预编译SQL：参数使用？作为占位符

步骤：	

```java
//1、导入驱动包 mysql-connector-java-*.*.*-bin.jar到项目libs目录下，右键add as library

//2、注册驱动
	Class.forName(com.mysql.cj.jdbc.Driver);

//3、获取Connection对象
	DriverManager.getConnection(url,username,password)
        
//4、定义SQL，sql参数使用?占位符
    Select * from userlogin where username= ? and password = ?;

//5、获取PrepaerdStatement对象，和createStatement不同，要传入SQL语句作为参数
	Connection.prepareStatement(String sql)
        
//6、给?赋值
     preparedStatement.setXXX(参数1，参数2)
        //参数1：问号的位置，从1开始 参数2：问号的值
        
//7、执行SQL，不需要传入SQL语句
	preparedStatement.execute   
```

注意：后期都会使用preparedStatement进行增删改查，防止SQL注入，效率更高，下面上传登录验证的三号版本

```java
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

```

#### JDBC管理事务

1、事务：一个包含多个步骤的业务操作，如果这个业务操作被事务所管理，多个步骤要么同时成功，要么同时失败 

2、操作

​	开启事务

​	提交事务	

​	回滚事务

3、使用的是Connection对象来管理事务

代码演示

```java
import cn.zt.jdbc.gongjulei.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class demo01 {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stat1 = null;
        PreparedStatement stat2 = null;
        try {
            conn = JDBCutils.getConnection();

            //获取数据库连接后开启事务
            conn.setAutoCommit(false);

            String sql1 = "Update acccount set balance = balance - ? where id = ?";

            String sql2 = "Update acccount set balance = balance + ? where id = ?";

            stat1 = conn.prepareStatement(sql1);
            stat2 = conn.prepareStatement(sql2);

            stat1.setInt(1,500);
            stat1.setInt(2,1);

            stat2.setInt(1,500);
            stat2.setInt(2,2);

            int count1 = stat1.executeUpdate();


            int count2 = stat2.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            //出了任何异常，我们都希望事务能回滚，所以catch一个大的异常，并在catch中回滚
            e.printStackTrace();
            if (conn!=null){
                try {
                    conn.rollback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        }finally {
            JDBCutils.close(stat1,conn);
            JDBCutils.close(stat2,null);
        }

    }
}

```





## JDBC连接池

#### 概念

其实就是一个容器（集合），存放数据库连接对象，当用户来访问数据库时，从容器中获取连接对象，用户访问完之后，会将连接对象归还给容器

#### 优点

1、节约资源

2、访问高效

#### 实现

1、标准接口：DataSource javax.sql包下

​			方法

​						获取连接：getConnection( )

​						归还连接：如果连接对象Connection是从连接池中获取的，那么Connection.Close不会关闭连接而是归还连接池

2、一般我们不去实现，由数据库厂商来实现

​					1、C3P0：数据库连接池技术

​					2、Druid：由阿里巴巴提供的数据库连接池技术，高效

#### C3P0：数据库连接池技术

**步骤**

1、导入jar包（两个） c3p0-0.9.5.2.jar   machange-commons-java-0.2.12.jar

2、定义配置文件：

​		名称：c3p0.properties or c3p0-config.xml

​		路径：直接将文件放在src目录下会自动加载

3、创建核心对象 数据库连接池对象ComboPooledDataSource

4、获取连接 getConnection

5、conn对象close即可归还

示范

```java
 public static void main(String[] args) throws SQLException {

        DataSource ds = new ComboPooledDataSource();

        Connection conn = ds.getConnection();

        String sql = "Select * from acccount where id = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        preparedStatement.setInt(1,1);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            int id = resultSet.getInt(1);

            int balance = resultSet.getInt(3);
            System.out.println(id+"---"+balance);
        }

    }
```



#### Druid数据库连接池技术

**步骤**

1、导入jar包

2、定义配置文件

​		特点

​			1、是properties形式

​			2、可以叫任意名称

​			3、可以放在任意目录下，需要手动加载

3、获取数据库连接池对象：通过工厂类来获取 DruidDataSoureceFactory

4、getConnection...

示范

```java
public static void main(String[] args) throws Exception {



        String sql ="SELECT * from acccount where id = ?" ;
        Properties prop = new Properties();

        InputStream resourceAsStream = 		 DruidDemo.class.getClassLoader().getResourceAsStream("druid.properties");
        prop.load(resourceAsStream);
        DataSource ds = DruidDataSourceFactory.createDataSource(prop);
        Connection conn = ds.getConnection();

        System.out.println(conn);


    }
```



#### Spring JDBC

Spring框架对JDBC简单封装，提供了JDBCTemplate对象简化开发

**基本步骤**

​	1、导入jar包

​	2、创建JDBCTemplate对象，依赖于数据源DataSource

```java
JdbcTemplate template = new JdbcTemplate(dataSource)
```

​	3、调用JDBCTemplate方法来完成CRUD的操作

​		update方法：执行DML，增删改语句

​		queryForMap：查询结果，将结果集封装为Map

​		queryForList：查询结果，将结果集封装为List

​		query：封装为javaBean，参数由RowMapper，使用BeanPropertyRowMapper，可以完成数据到JavaBean的自动封装

```java
new BeanPropertyRowMapper<T>(T.class)
```

​		queryForObject：将结果封装为对象:一般用于聚合函数的使用