
import utils.JDBCUtilsByDruid;
import java.sql.Connection;
import java.sql.SQLException;


public class JDBCPoolConnectionTest {
    public static void main(String[] args) throws SQLException {
        Connection connection = JDBCUtilsByDruid.getConnection();
        System.out.println(connection);
    }

//    public static void main(String[] args) {
//        System.out.println("你好，世界！");
//        System.out.println("当前编码：" + System.getProperty("file.encoding"));
//
//    }



}

