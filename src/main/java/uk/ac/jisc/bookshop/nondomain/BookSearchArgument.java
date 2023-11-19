package uk.ac.jisc.bookshop.nondomain;

import org.springframework.data.domain.Sort;
import uk.ac.jisc.bookshop.domain.Category;
import uk.ac.jisc.bookshop.domain.Format;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class BookSearchArgument {
    private String title;
    private String author;
    private List<Format> formats;
    private BigDecimal price; //TODO not used
    private String category;

    private List<Category> categories;
    private Date publishedDate;
    private String isbn;
    private int stockLevel;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private LocalDate publicationDateStart;
    private LocalDate publicationDateEnd;

    private int page;

    private int size;

    private Sort sorts;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public BigDecimal getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(BigDecimal priceFrom) {
        this.priceFrom = priceFrom;
    }

    public BigDecimal getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(BigDecimal priceTo) {
        this.priceTo = priceTo;
    }

    public LocalDate getPublicationDateStart() {
        return publicationDateStart;
    }

    public void setPublicationDateStart(LocalDate publicationDateStart) {
        this.publicationDateStart = publicationDateStart;
    }

    public LocalDate getPublicationDateEnd() {
        return publicationDateEnd;
    }

    public void setPublicationDateEnd(LocalDate publicationDateEnd) {
        this.publicationDateEnd = publicationDateEnd;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Sort getSorts() {
        return sorts;
    }

    public void setSorts(Sort sorts) {
        this.sorts = sorts;
    }
}
