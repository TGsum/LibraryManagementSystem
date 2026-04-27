package ServerSide.service.tableService;


import org.junit.Before;
import org.junit.Test;
import utils.JDBCUtilsByDruid;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

import ServerSide.dao.bookDAO;
import ServerSide.dao.bookVersionDAO;
import ServerSide.dao.borrowRecordDAO;
import ServerSide.domain.book;
import ServerSide.domain.bookVersion;
import ServerSide.domain.borrowRecord;


import java.sql.Connection;

/*
员工用于管理书籍相关事务
 */
public class BookManagementService {

    private final bookDAO bookDAO = new bookDAO();
    private final bookVersionDAO versionDAO = new bookVersionDAO();
    private final borrowRecordDAO recordDAO = new borrowRecordDAO();

    /**
     * 1. 查询所有图书及其版本信息
     */
    public List<bookVersion> getAllBooksWithVersions() {
        String sql = """
                SELECT bv.version_id, b.book_id, b.title, b.isbn, bv.book_code, bv.authors, bv.edition, 
                       bv.publish_date, bv.language, bv.page_count, bv.price, bv.stock_quantity
                FROM books b
                JOIN book_versions bv ON b.book_id = bv.book_id
                """;

        return versionDAO.queryMulti(sql, bookVersion.class);
    }

    /**
     * 2. 添加图书及版本信息（带前置验证）
     */
    public boolean addBookWithVersion(book book, bookVersion version) {
        Connection conn = null;
        try {
            conn = JDBCUtilsByDruid.getConnection();
            conn.setAutoCommit(false);

            // 1. 查 ISBN 是否存在
            book existing = bookDAO.querySingle(conn,
                    "SELECT * FROM books WHERE isbn = ?",
                    book.class, book.getIsbn());
            int bookId;

            if (existing == null) {
                // 插入新书
                int inserted = bookDAO.insert(conn,
                        "INSERT INTO books (isbn, title, original_title) VALUES (?, ?, ?)",
                        book.getIsbn(), book.getTitle(), book.getOriginalTitle());
                if (inserted <= 0) {
                    conn.rollback();
                    System.out.println("添加图书失败。");
                    return false;
                }

                // 查回 book_id
                book fetched = bookDAO.querySingle(conn,
                        "SELECT * FROM books WHERE isbn = ?",
                        book.class, book.getIsbn());
                bookId = fetched.getBookId();
            } else {
                bookId = existing.getBookId();
            }

            // 2. book_code 是否冲突
            if (versionDAO.querySingle(conn,
                    "SELECT * FROM book_versions WHERE book_code = ?",
                    bookVersion.class, version.getBookCode()) != null) {
                conn.rollback();
                System.out.println("该版本编码已存在。");
                return false;
            }

            // 3. 插入版本
            int versionInserted = versionDAO.insert(conn,
                    "INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    bookId, version.getBookCode(), version.getAuthors(), version.getEdition(),
                    version.getPublishDate(), version.getLanguage(),
                    version.getPageCount(), version.getPrice(), version.getStockQuantity());

            if (versionInserted <= 0) {
                conn.rollback();
                System.out.println("添加图书版本失败。");
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ignore) {
            }

            // SQLState 23000: 唯一约束冲突（如 isbn/book_code 重复）
            if ("23000".equals(e.getSQLState())) {
                System.out.println("唯一约束冲突，请检查 ISBN 或版本编码是否已存在。");
                return false;
            }

            throw new RuntimeException("添加图书版本失败：", e);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
            JDBCUtilsByDruid.close(null, null, conn);
        }
    }

    /**
     * 3. 删除图书版本（前置条件：该版本未被借出）
     */
    public boolean deleteBookVersion(String bookCode) {
        // 查询是否存在借阅记录
        String checkBorrowSql = "SELECT * FROM borrow_records WHERE book_code = ? AND status != '已归还'";
        if (!recordDAO.queryMulti(checkBorrowSql, borrowRecord.class, bookCode).isEmpty()) {
            System.out.println("该书版本正在被借阅或未归还，不能删除。");
            return false;
        }

        String deleteSql = "DELETE FROM book_versions WHERE book_code = ?";
        return versionDAO.delete(deleteSql, bookCode) > 0;
    }

    /**
     * 4. 修改图书版本信息（如库存、价格、语言等）
     */
    public boolean updateBookVersion(bookVersion version) {
        // 校验价格、库存等字段
        if (version.getPrice() != null && version.getPrice().doubleValue() < 0) {
            System.out.println("价格不能为负！");
            return false;
        }

        if (version.getStockQuantity() != null && version.getStockQuantity() < 0) {
            System.out.println("库存不能为负！");
            return false;
        }

        String updateSql = """
                    UPDATE book_versions
                    SET authors = ?, edition = ?, publish_date = ?, language = ?, 
                        page_count = ?, price = ?, stock_quantity = ?
                    WHERE book_code = ?
                """;

        return versionDAO.update(updateSql,
                version.getAuthors(),
                version.getEdition(),
                version.getPublishDate(),
                version.getLanguage(),
                version.getPageCount(),
                version.getPrice(),
                version.getStockQuantity(),
                version.getBookCode()) > 0;
    }
    //===================================================

//
//    private BookManagementService service;
//
//    @Before
//    public void setUp() {
//        service = new BookManagementService();
//    }
//
//    @Test
//    public void testGetAllBooksWithVersions() {
//        List<bookVersion> versions = service.getAllBooksWithVersions();
//        assertNotNull(versions);
//        System.out.println("图书版本数量：" + versions.size());
//    }
//
//    @Test
//    public void testAddBookWithVersion() {
//        book newBook = new book();
//        newBook.setIsbn("9781234567890");
//        newBook.setTitle("测试图书");
//        newBook.setOriginalTitle("Test Book");
//
//        bookVersion version = new bookVersion();
//        version.setBookCode("TEST-001");
//        version.setAuthors("作者A, 作者B");
//        version.setEdition("第一版");
//        version.setPublishDate(Date.valueOf(LocalDate.now()));
//        version.setLanguage("中文");
//        version.setPageCount(320);
//        version.setPrice(new BigDecimal("59.90"));
//        version.setStockQuantity(10);
//
//        boolean result = service.addBookWithVersion(newBook, version);
//        assertTrue(result);
//    }
//
//    @Test
//    public void testDeleteBookVersion() {
//        String bookCode = "TEST-001"; // 替换为实际存在且未借出的 book_code
//        boolean result = service.deleteBookVersion(bookCode);
//        System.out.println("删除结果：" + result);
//        assertTrue(result || !result); // 测试时可适当调整，主要观察控制台输出
//    }
//
//    @Test
//    public void testUpdateBookVersion() {
//        bookVersion version = new bookVersion();
//        version.setBookCode("TEST-001"); // 确保数据库中存在该 book_code
//        version.setAuthors("作者C");
//        version.setEdition("第二版");
//        version.setPublishDate(Date.valueOf(LocalDate.now()));
//        version.setLanguage("英文");
//        version.setPageCount(350);
//        version.setPrice(new BigDecimal("49.99"));
//        version.setStockQuantity(5);
//
//        boolean result = service.updateBookVersion(version);
//        assertTrue(result);
//    }
//
//    @Test
//    public void testAddBookWithDuplicateIsbnAndBookCode() {
//        // 构造一本书，使用已存在的 ISBN 和 book_code
//        book b = new book(null, "9787020034636", "红楼梦", "Dream of the Red Chamber"); // 已存在 ISBN
//        bookVersion v = new bookVersion();
//        v.setBookCode("HL2010PB001"); // 已存在版本编码
//        v.setAuthors("曹雪芹, 高鹗");
//        v.setEdition("纪念版");
//        v.setPublishDate(Date.valueOf("2025-05-27"));
//        v.setLanguage("中文");
//        v.setPageCount(1200);
//        v.setPrice(BigDecimal.valueOf(150.00));
//        v.setStockQuantity(5);
//
//        boolean result = service.addBookWithVersion(b, v);
//
//        // 应该添加失败
//        assertFalse("应因唯一键冲突而失败", result);
//    }
//
//    @Test
//    public void testAddBookWithNewVersionSameIsbn() {
//        // 同一本书，但换一个 book_code（允许）
//        book b = new book(null, "9787020034636", "红楼梦", "Dream of the Red Chamber");
//        bookVersion v = new bookVersion();
//        v.setBookCode("HL2025PB999"); // 新版本编码
//        v.setAuthors("曹雪芹");
//        v.setEdition("特别增订版");
//        v.setPublishDate(Date.valueOf("2025-05-27"));
//        v.setLanguage("中文");
//        v.setPageCount(1280);
//        v.setPrice(BigDecimal.valueOf(188.00));
//        v.setStockQuantity(3);
//
//        boolean result = service.addBookWithVersion(b, v);
//
//        // 应成功添加
//        assertTrue("应成功添加一个新版本", result);
//    }

}

