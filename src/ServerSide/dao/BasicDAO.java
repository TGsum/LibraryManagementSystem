package ServerSide.dao;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.JDBCUtilsByDruid;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 通用 DAO 父类，支持自动事务控制与外部事务复用，适用于任何实体类
 * 泛型 <T> 指代实体类型，例如 Book、Borrower 等
 */
public class BasicDAO<T> {

    private final QueryRunner qr = new QueryRunner();

    // 支持数据库字段下划线 -> Java属性驼峰自动映射
    private static final RowProcessor ROW_PROCESSOR =
            new BasicRowProcessor(new CamelCaseBeanProcessor());

    // ======================== 不带 Connection（独立连接、自动关闭） ========================

    public int insert(String sql, Object... parameters) {
        try (Connection conn = JDBCUtilsByDruid.getConnection()) {
            return qr.update(conn, sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(String sql, Object... parameters) {
        return insert(sql, parameters);
    }

    public int delete(String sql, Object... parameters) {
        return insert(sql, parameters);
    }

    public List<T> queryMulti(String sql, Class<T> clazz, Object... parameters) {
        try (Connection conn = JDBCUtilsByDruid.getConnection()) {
            return qr.query(conn, sql, new BeanListHandler<>(clazz, ROW_PROCESSOR), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public T querySingle(String sql, Class<T> clazz, Object... parameters) {
        try (Connection conn = JDBCUtilsByDruid.getConnection()) {
            return qr.query(conn, sql, new BeanHandler<>(clazz, ROW_PROCESSOR), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object queryScalar(String sql, Object... parameters) {
        try (Connection conn = JDBCUtilsByDruid.getConnection()) {
            return qr.query(conn, sql, new ScalarHandler(), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ======================== 带 Connection（供事务统一管理） ========================

    public int insert(Connection conn, String sql, Object... parameters) {
        try {
            return qr.update(conn, sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(Connection conn, String sql, Object... parameters) {
        return insert(conn, sql, parameters);
    }

    public int delete(Connection conn, String sql, Object... parameters) {
        return insert(conn, sql, parameters);
    }

    public List<T> queryMulti(Connection conn, String sql, Class<T> clazz, Object... parameters) {
        try {
            return qr.query(conn, sql, new BeanListHandler<>(clazz, ROW_PROCESSOR), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public T querySingle(Connection conn, String sql, Class<T> clazz, Object... parameters) {
        try {
            return qr.query(conn, sql, new BeanHandler<>(clazz, ROW_PROCESSOR), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object queryScalar(Connection conn, String sql, Object... parameters) {
        try {
            return qr.query(conn, sql, new ScalarHandler(), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
