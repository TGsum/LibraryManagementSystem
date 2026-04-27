package ServerSide.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class bookVersion  implements Serializable {
    public bookVersion(Integer versionId, Integer bookId, String bookCode, String authors,
                       String edition, Date publishDate, String language,
                       Integer pageCount, BigDecimal price, Integer stockQuantity) {
        this.versionId = versionId;
        this.bookId = bookId;
        this.bookCode = bookCode;
        this.authors = authors;
        this.edition = edition;
        this.publishDate = publishDate;
        this.language = language;
        this.pageCount = pageCount;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
public bookVersion(){}
    private Integer versionId;
    private Integer bookId;
    private String bookCode;
    private String authors;
    private String edition;
    private Date publishDate;
    private String language = "中文";
    private Integer pageCount;
    private BigDecimal price;
    private Integer stockQuantity = 0;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    private String isbn;

    @Override
    public String toString() {
        return "bookVersion{" +
                "versionId=" + versionId +
                ", bookId=" + bookId +
                ", bookCode='" + bookCode + '\'' +
                ", authors='" + authors + '\'' +
                ", edition='" + edition + '\'' +
                ", publishDate=" + publishDate +
                ", language='" + language + '\'' +
                ", pageCount=" + pageCount +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                '}';
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
