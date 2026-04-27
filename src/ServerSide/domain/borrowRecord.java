package ServerSide.domain;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigInteger;
import java.io.Serializable;
import java.sql.Date;

public class borrowRecord  implements Serializable {
    // 记录ID (主键，自增长)
    private Integer recordId;

    // 关联的借阅者ID
    private Integer borrowerId;

    // 借阅者编号(冗余)
    private String borrowerNumber;

    // 64位事务编号
    private BigInteger  transactionNumber;

    // 书名(冗余)
    private String bookTitle;

    // ISBN编号(冗余)
    private String isbn;

    // 24位图书编码(关联版本表)
    private String bookCode;

    // 借阅日期
    private Date borrowDate;

    // 应归还日期
    private Date dueDate;

    // 状态 (枚举: 借阅中/逾期/已归还，默认为借阅中)
    private String status;

    @Override
    public String toString() {
        return "borrowRecord{" +
                "recordId=" + recordId +
                ", borrowerId=" + borrowerId +
                ", borrowerNumber='" + borrowerNumber + '\'' +
                ", transactionNumber=" + transactionNumber +
                ", bookTitle='" + bookTitle + '\'' +
                ", isbn='" + isbn + '\'' +
                ", bookCode='" + bookCode + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                '}';
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Integer borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBorrowerNumber() {
        return borrowerNumber;
    }

    public void setBorrowerNumber(String borrowerNumber) {
        this.borrowerNumber = borrowerNumber;
    }

    public BigInteger  getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(BigInteger  transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public borrowRecord(Integer recordId, Integer borrowerId, String borrowerNumber, BigInteger  transactionNumber, String bookTitle, String isbn, String bookCode, Date borrowDate, Date dueDate, String status) {
        this.recordId = recordId;
        this.borrowerId = borrowerId;
        this.borrowerNumber = borrowerNumber;
        this.transactionNumber = transactionNumber;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.bookCode = bookCode;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }
    public borrowRecord(){}

}
