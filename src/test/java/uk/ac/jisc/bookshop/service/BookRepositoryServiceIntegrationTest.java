package uk.ac.jisc.bookshop.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hamcrest.CoreMatchers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.domain.Category;
import uk.ac.jisc.bookshop.domain.Format;
import uk.ac.jisc.bookshop.nondomain.BookSearchArgument;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class BookRepositoryServiceIntegrationTest {

    private static EntityManager entityManager;
    private static BookRepositoryService service;

    static Book book1 = new Book("coreJava", "Cay S. Horstmann", Format.PAPER, BigDecimal.valueOf( 50.00).setScale(2),
            Category.NON_FICTION, LocalDate.of(2000, Month.NOVEMBER,12), "978-0-596-52068-7",3);
    static Book book2 = new Book("coreJava2", "Gary Cornell",Format.HARDBACK, BigDecimal.valueOf( 1000.00).setScale(2),
            Category.NON_FICTION,LocalDate.of(2021,Month.JANUARY,31), "506-3-089-12512-0",23);
    static Book book3 = new Book("coreJava17", "G.Cornell",Format.KINDLE, BigDecimal.valueOf( 100.00).setScale(2),
            Category.NON_FICTION,LocalDate.of(2023,Month.JANUARY,31), "978-0-195-10519-3",10);
    static Book book4 = new Book("corePython", "Wesley J Chun",Format.AUDIO, BigDecimal.valueOf( 100.00).setScale(2),
            Category.NON_FICTION,LocalDate.of(2015,Month.DECEMBER,30), "978-0-132-26993-3",5);
    static Book book5 = new Book("harryPotter", "J.K.Rowling",Format.PAPER, BigDecimal.valueOf( 1000.00).setScale(2),
            Category.FICTION,LocalDate.of(2011,Month.DECEMBER,30), " 978-140-889-076-9",5);

    static Book book6 = new Book("Western Lane", "Chetna Maroo",Format.HARDBACK, BigDecimal.valueOf( 90.00).setScale(2),
            Category.FICTION,LocalDate.of(2023,Month.DECEMBER,30), " 978-152-909-462-6",5);
    @BeforeAll
    public static void setup() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("jpa-h2-queryparams");
        entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.persist(book4);
        entityManager.persist(book5);
        entityManager.persist(book6);
        entityManager.getTransaction().commit();
        entityManager.clear();

        service = new BookRepositoryService();
        ReflectionTestUtils.setField(service,"entityManager",entityManager);
    }

    @Test
    public void testFindBookBySearchArgumentWithTitle(){
        //GIVEN there are 6 valid books in database
        //AND an bookSearchArgument with title "java"
        BookSearchArgument bookSearchArgument = new BookSearchArgument();
        bookSearchArgument.setTitle("java");
        //when query the findBookBySearchArgument method
        List<Book> books = service.findBookBySearchArgument(bookSearchArgument);
        //then there are 3 matched books
        assertThat(books.size(), CoreMatchers.equalTo(3));
        assertThat(books,contains(
                hasProperty("title",is(book3.getTitle())),
                hasProperty("title",is(book2.getTitle())),
                hasProperty("title",is(book1.getTitle()))));

    }

    @Test
    public void testFindBookBySearchArgumentWithAuthor(){
        //GIVEN there are 6 valid books in database
        //AND an bookSearchArgument with author "Cornell"
        BookSearchArgument bookSearchArgument = new BookSearchArgument();
        bookSearchArgument.setAuthor("Cornell");
        //when query the findBookBySearchArgument method
        List<Book> books = service.findBookBySearchArgument(bookSearchArgument);
        //then there are 2 matched books
        assertThat(books.size(), CoreMatchers.equalTo(2));
        assertThat(books,contains(
                hasProperty("author",is(book3.getAuthor())),
                hasProperty("author",is(book2.getAuthor()))));

    }


    @Test
    public void testFindBookBySearchArgumentWithFormat(){
        //GIVEN there are 6 valid books in database
        //AND an bookSearchArgument with format "audio" and format "kindle"
        List<Format> formats = new ArrayList<>();
        formats.add(Format.KINDLE);
        formats.add(Format.AUDIO);
        BookSearchArgument bookSearchArgument = new BookSearchArgument();
        bookSearchArgument.setFormats(formats);
        //when query the findBookBySearchArgument method
        List<Book> books = service.findBookBySearchArgument(bookSearchArgument);
        //then there are 2 matched books
        assertThat(books.size(), CoreMatchers.equalTo(2));
        assertThat(books,contains(
                hasProperty("format", is(book4.getFormat())),
                hasProperty("format", is(book3.getFormat()))));
    }

    @Test
    public void testFindBookBySearchArgumentWithPriceFromAndPriceTo(){
        //GIVEN there are 6 valid books in database
        //AND an bookSearchArgument with price between 51 and 100
        BookSearchArgument bookSearchArgument = new BookSearchArgument();
        BigDecimal priceFrom = BigDecimal.valueOf(51);
        BigDecimal priceTo = BigDecimal.valueOf(100);
        bookSearchArgument.setPriceFrom(priceFrom);
        bookSearchArgument.setPriceTo(priceTo);
        //when query the findBookBySearchArgument method
        List<Book> books = service.findBookBySearchArgument(bookSearchArgument);
        //then there are 2 matched books
        assertThat(books.size(), CoreMatchers.equalTo(3));
        assertThat(books,contains(
                hasProperty("price", is(book6.getPrice())),
                hasProperty("price", is(book4.getPrice())),
                hasProperty("price", is(book3.getPrice()))
                ));
    }

    @Test
    public void testFindBookBySearchArgumentWithPublicationDateFromAndPublicationDateTo(){
        //GIVEN there are 6 valid books in database
        //AND an bookSearchArgument with publication date between 2011-12-30 and 2021-01-31
        LocalDate publicationDateFrom = LocalDate.of(2011,Month.DECEMBER,30);
        LocalDate publicationDateTo =  LocalDate.of(2021,Month.JANUARY,31);
        BookSearchArgument bookSearchArgument = new BookSearchArgument();
        bookSearchArgument.setPublicationDateStart(publicationDateFrom);
        bookSearchArgument.setPublicationDateEnd(publicationDateTo);
        //when query the findBookBySearchArgument method
        List<Book> books = service.findBookBySearchArgument(bookSearchArgument);
        //then there are 3 matched books
        assertThat(books.size(), CoreMatchers.equalTo(3));
        assertThat(books,contains(
                hasProperty("publishedDate", is(book5.getPublishedDate())),
                hasProperty("publishedDate", is(book4.getPublishedDate())),
                hasProperty("publishedDate", is(book2.getPublishedDate()))));
    }

    @Test
    public void testFindBookBySearchArgumentWithCategories(){
        //GIVEN there are 5 valid books in database
        //AND an bookSearchArgument with category FICTION
        List<Category> categories = new ArrayList<>();
        categories.add(Category.FICTION);
        BookSearchArgument bookSearchArgument = new BookSearchArgument();
        bookSearchArgument.setCategories(categories);
        //when query the findBookBySearchArgument method
        List<Book> books = service.findBookBySearchArgument(bookSearchArgument);
        //then there are 1 matched book
        assertThat(books.size(), CoreMatchers.equalTo(2));
        assertThat(books,contains(
                hasProperty("category", is(book5.getCategory())),
                hasProperty("category", is(book6.getCategory()))
                ));
    }

    @Test
    public void testFindBookBySearchArgumentPagination(){
        //GIVEN there are 6 valid books in database
        //AND user sets to display 2 books and skip first 3 books
        BookSearchArgument bookSearchArgument = new BookSearchArgument();
        bookSearchArgument.setSize(2);
        bookSearchArgument.setPage(4);
        //when query the findBookBySearchArgument method
        List<Book> books = service.findBookBySearchArgument(bookSearchArgument);
        //THEN there are 2 matched books
        assertThat(books.size(), CoreMatchers.equalTo(2));
        //AND the result is descending ordered by id
        assertThat(books,contains(book2,  book1));
    }

    @AfterAll
    public static void shutdown(){
        entityManager.close();
    }

}