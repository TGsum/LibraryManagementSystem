package ServerSide.service.tableService;

import ServerSide.dao.*;
import ServerSide.domain.*;
import org.junit.Before;
import org.junit.Test;
import utils.JDBCUtilsByDruid;

import org.apache.commons.dbutils.QueryRunner;

import java.awt.print.Book;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static junit.framework.TestCase.*;

public class BorrowingReturningService {

    private final borrowerDAO borrowerDAO = new borrowerDAO();
    private final bookVersionDAO versionDAO = new bookVersionDAO();
    private final bookDAO bookDAO = new bookDAO();
    private final borrowRecordDAO recordDAO = new borrowRecordDAO();

    /**
     * 借书（带事务与行锁）
     */
    public boolean borrowBook(String borrowerNumber, String bookCode) {
        Connection conn = null;
        try {
            conn = JDBCUtilsByDruid.getConnection();
            conn.setAutoCommit(false);

            // 1. 查借阅者并加锁
            borrower borrower = borrowerDAO.querySingle(conn,
                    "SELECT * FROM borrowers WHERE borrower_number = ? FOR UPDATE",
                    borrower.class, borrowerNumber);
            if (borrower == null || !"正常".equals(borrower.getStatus())) {
                conn.rollback();
                System.out.println("借阅者不存在或状态异常。");
                return false;
            }

            // 2. 查版本并加锁
            bookVersion version = versionDAO.querySingle(conn,
                    "SELECT * FROM book_versions WHERE book_code = ? FOR UPDATE",
                    bookVersion.class, bookCode);
            if (version == null || version.getStockQuantity() == null || version.getStockQuantity() <= 0) {
                conn.rollback();
                System.out.println("图书库存不足或版本不存在。");
                return false;
            }

            // 3. 查主书信息
            book book = bookDAO.querySingle(conn,
                    "SELECT * FROM books WHERE book_id = ? FOR UPDATE",
                    book.class, version.getBookId());
            if (book == null) {
                conn.rollback();
                System.out.println("图书主信息缺失。");
                return false;
            }

            // 4. 插入借阅记录
            LocalDate today = LocalDate.now();
            LocalDate due = today.plusDays(30);
            BigInteger txn = BigInteger.valueOf(System.currentTimeMillis());

            int inserted = recordDAO.insert(conn,
                    "INSERT INTO borrow_records (borrower_id, borrower_number, transaction_number, " +
                            "book_title, isbn, book_code, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    borrower.getBorrowerId(), borrower.getBorrowerNumber(), txn,
                    book.getTitle(), book.getIsbn(), bookCode,
                    Date.valueOf(today), Date.valueOf(due), "借阅中");
            if (inserted <= 0) {
                conn.rollback();
                System.out.println("插入借阅记录失败。");
                return false;
            }

            // 5. 减库存
            int updated = versionDAO.update(conn,
                    "UPDATE book_versions SET stock_quantity = stock_quantity - 1 WHERE book_code = ?",
                    bookCode);
            if (updated <= 0) {
                conn.rollback();
                System.out.println("库存更新失败。");
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException("借书失败，事务回滚", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            JDBCUtilsByDruid.close(null, null, conn);
        }
    }

    /**
     * 还书（带事务与行锁）
     */
    public boolean returnBook(BigInteger transactionNumber) {
        Connection conn = null;
        try {
            conn = JDBCUtilsByDruid.getConnection();
            conn.setAutoCommit(false);

            // 1. 查借阅记录并加锁
            borrowRecord record = recordDAO.querySingle(conn,
                    "SELECT * FROM borrow_records WHERE transaction_number = ? FOR UPDATE",
                    borrowRecord.class, transactionNumber);
            if (record == null || "已归还".equals(record.getStatus())) {
                conn.rollback();
                System.out.println("找不到借阅记录或已归还。");
                return false;
            }

            // 2. 查版本并加锁
            bookVersion version = versionDAO.querySingle(conn,
                    "SELECT * FROM book_versions WHERE book_code = ? FOR UPDATE",
                    bookVersion.class, record.getBookCode());
            if (version == null) {
                conn.rollback();
                System.out.println("找不到图书版本。");
                return false;
            }

            // 3. 更新借阅记录状态
            int updRecord = recordDAO.update(conn,
                    "UPDATE borrow_records SET status = '已归还' WHERE transaction_number = ?",
                    transactionNumber);
            if (updRecord <= 0) {
                conn.rollback();
                System.out.println("借阅记录状态更新失败。");
                return false;
            }

            // 4. 加库存
            int updStock = versionDAO.update(conn,
                    "UPDATE book_versions SET stock_quantity = stock_quantity + 1 WHERE book_code = ?",
                    record.getBookCode());
            if (updStock <= 0) {
                conn.rollback();
                System.out.println("库存增加失败。");
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException("还书失败，事务回滚", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            JDBCUtilsByDruid.close(null, null, conn);
        }
    }

    public List<borrowRecord> getBorrowRecordsByBorrowerNumber(String borrowerNumber) {
        String sql = "SELECT * FROM borrow_records WHERE borrower_number = ?";
        return recordDAO.queryMulti(sql, borrowRecord.class, borrowerNumber);
    }


    //========================================================================
//    private BorrowingReturningService service;
//    private borrowRecordDAO recordDAOTest;
//
//    @Before
//    public void setUp() {
//        service = new BorrowingReturningService();
//        recordDAOTest = new borrowRecordDAO();
//    }
//
//    @Test
//    public void testBorrowBookSuccess() {
//        // 使用数据库中存在的借阅者编号 + book_code，确保库存充足
//        String borrowerNumber = "240015000015"; // 借阅者必须存在并状态为“正常”
//        String bookCode = "TS2010PB001";       // 存在的图书版本，库存 > 0
//
//        boolean result = service.borrowBook(borrowerNumber, bookCode);
//        assertTrue("借书应成功", result);
//    }
//
//    @Test
//    public void testBorrowBookFailDueToNoStock() {
//        String borrowerNumber = "240015000015";
//        String bookCode = "DL2016PB001"; // 这本库存是 1，可多次测试用完
//
//        // 多次借用直到失败
//        boolean result1 = service.borrowBook(borrowerNumber, bookCode);
//        boolean result2 = service.borrowBook(borrowerNumber, bookCode);
//
//        assertTrue("第一次借书可能成功", result1);
//        assertFalse("第二次库存不足应失败", result2);
//    }
//
//    @Test
//    public void testReturnBookSuccess() {
//        // 查询一个“借阅中”的记录（确保存在）
//        List<borrowRecord> records = recordDAOTest.queryMulti(
//                "SELECT * FROM borrow_records WHERE status = '借阅中'", borrowRecord.class);
//
//        if (records.isEmpty()) {
//            fail("当前没有可归还的借阅记录，请先运行借书测试");
//        }
//
//        borrowRecord record = records.get(0);
//        BigInteger transactionNumber = record.getTransactionNumber(); // ✅ 使用 BigInteger
//
//        boolean result = service.returnBook(transactionNumber); // ✅ 传入正确类型
//        assertTrue("归还应成功", result);
//    }
//
//    @Test
//    public void testReturnBookFailAlreadyReturned() {
//        // 查询一个“已归还”的记录
//        List<borrowRecord> records = recordDAOTest.queryMulti(
//                "SELECT * FROM borrow_records WHERE status = '已归还'", borrowRecord.class);
//
//        if (records.isEmpty()) {
//            fail("当前没有已归还记录，请先归还一次图书");
//        }
//
//        borrowRecord record = records.get(0);
//        BigInteger transactionNumber = record.getTransactionNumber();
//
//        boolean result = service.returnBook(transactionNumber);
//        assertFalse("重复归还应失败", result);
//    }

}






