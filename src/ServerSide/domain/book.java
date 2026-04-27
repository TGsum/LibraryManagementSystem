package ServerSide.domain;

import java.io.Serializable;

public class book implements Serializable {
    // 图书ID (主键，自增长)
    private Integer bookId;

    // 国际标准书号
    private String isbn;

    // 书名(通用名称)
    private String title;

    // 原标题(外文书籍适用)
    private String originalTitle;

    // 无参构造器
    public book() {
    }

    // 全参构造器
    public book(Integer bookId, String isbn, String title, String originalTitle) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.originalTitle = originalTitle;
    }

    // Getter 和 Setter 方法
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    // toString 方法
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                '}';
    }
}
