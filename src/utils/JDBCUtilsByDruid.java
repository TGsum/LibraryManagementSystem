package utils;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtilsByDruid {
    private static DataSource ds;

    static {
        try {
            // 使用类加载器从类路径加载配置文件
            InputStream inputStream = JDBCUtilsByDruid.class.getClassLoader()
                    .getResourceAsStream("druid.properties");

            Properties properties = new Properties();
            properties.load(inputStream);
            ds = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            // 将检查异常转换为运行时异常，方便调用方处理
            throw new ExceptionInInitializerError("初始化Druid连接池失败: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close(); // 归还连接到池中
        } catch (SQLException e) {
            throw new RuntimeException("资源关闭失败", e);
        }
    }
}