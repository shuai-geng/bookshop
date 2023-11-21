package uk.ac.jisc.bookshop.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.ISBN;

import uk.ac.jisc.bookshop.serialiser.PriceSerializer;
import uk.ac.jisc.bookshop.validation.constraint.ZeroIfAnotherFieldIsNull;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@ZeroIfAnotherFieldIsNull(fieldName="publishedDate",dependFieldName="stockLevel", message = "stockLevel should be zero if publishedDate is null")
@ZeroIfAnotherFieldIsNull(fieldName="price",dependFieldName="stockLevel", message = "stockLevel should be zero if price is empty")
@Entity
public class Book {
    private @Id @GeneratedValue Long id;

    @NotBlank(message = "title is mandatory")
    private String title;
    @NotBlank(message = "author is mandatory")
    private String author;

    private Format format;
    @DecimalMin(value = "0.00", inclusive = false,message = "the price should great than 0")
    @Digits(integer=10, fraction=2, message= "invalid price format")
    @JsonSerialize(using = PriceSerializer.class)
    private BigDecimal price;

    private Category category;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate publishedDate;
    @ISBN(message = "invalid isbn")
    private String isbn;
    @PositiveOrZero(message = "stockLevel should not be less than 0")
    private int stockLevel;

    public Book(String title, String author, Format format, BigDecimal price, Category category, LocalDate publishedDate, String isbn, int stockLevel) {
        this.title = title;
        this.author = author;
        this.format = format;
        this.price = price;
        this.category = category;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.stockLevel = stockLevel;
    }

    public Book() {

    }

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

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return stockLevel == book.stockLevel && Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && format == book.format && Objects.equals(price, book.price) && category == book.category && Objects.equals(publishedDate, book.publishedDate) && Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, format, price, category, publishedDate, isbn, stockLevel);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", format='" + format + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", publishedDate=" + publishedDate +
                ", isbn='" + isbn + '\'' +
                ", stockLevel=" + stockLevel +
                '}';
    }

    @Override
    public Object clone() {
        Book book = null;
        try{
            book = (Book) super.clone();
        } catch (CloneNotSupportedException e){
            book  = new Book(this.getTitle(),this.getAuthor(),this.getFormat(),this.getPrice(),this.getCategory(),this.getPublishedDate(),this.getIsbn(),this.getStockLevel());
        }
        return book;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
