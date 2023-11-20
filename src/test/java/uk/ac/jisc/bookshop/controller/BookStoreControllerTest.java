package uk.ac.jisc.bookshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.jisc.bookshop.dao.BookRepository;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.domain.Category;
import uk.ac.jisc.bookshop.domain.Format;
import uk.ac.jisc.bookshop.nondomain.BookSearchArgument;
import uk.ac.jisc.bookshop.service.BookRepositoryService;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@WebMvcTest
@AutoConfigureMockMvc
public class BookStoreControllerTest {


    @MockBean
    private BookRepository repository;

    @MockBean
    private BookRepositoryService bookRepositoryService;

    @Autowired
    private BookStoreController bookStoreController;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    ArgumentCaptor<BookSearchArgument> bookSearchArgumentCaptor;
    /*
    test post method
     */
    @Test
    public void testCreateBookSuccessWithAllFields() throws Exception {
        //given a new book
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"coreJava\",\n" +
                "        \"author\": \"Cay S. Horstmann\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 50.00,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        Book bookObj = mapper.readValue(requestBody, Book.class);
        bookObj.setId(1L);
        when(repository.save(any(Book.class))).thenReturn(bookObj);
        //WHEN a restful call to post method
        //THEN the status is 201
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                content(requestBody).
                contentType(utf8type)).
                andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is(bookObj.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author", is(bookObj.getAuthor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(bookObj.getId().intValue())));

    }

    @Test
    public void testCreateBookSuccessWithOnlyMandatoryFields() throws Exception {
        //GIVEN a valid json post request only contains mandatory fields
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"coreJava\",\n" +
                "        \"author\": \"Cay S. Horstmann\"\n"+
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        Book bookObj = mapper.readValue(requestBody, Book.class);
        bookObj.setId(1L);
        when(repository.save(any(Book.class))).thenReturn(bookObj);
        //WHEN a restful call to post method with above request body
        //THEN the response status is 200
        //AND the book has been created with mandatory fields
        //the optional fields has been set to default values
        mockMvc.perform(MockMvcRequestBuilders.put("/book/1").content(requestBody).contentType(utf8type))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is(bookObj.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author", is(bookObj.getAuthor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publishedDate", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(bookObj.getId().intValue())));;
    }




    @Test
    public void testCreateBookFailedWithInvalidJson() throws Exception {
        //given an invalid json book request with empty format information
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"\",\n" +
                "        \"author\": \"\",\n" +
                "        \"format\": \"\",\n" +
                "        \"price\": ,\n" +
                "        \"category\": \"\",\n" +
                "        \"publishedDate\": \"\",\n" +
                "        \"isbn\": \"\",\n" +
                "        \"stockLevel\": \n" +
                "    }";
        //WHEN a restful call to post method
        //THEN the status is 400 bad request
        //AND the response body contains parse error message
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                content(requestBody).
                contentType(utf8type)).
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
                andExpect(MockMvcResultMatchers.content().string("JSON parse error: Cannot construct instance of `uk.ac.jisc.bookshop.domain.Format`, problem: Unknown enum type ,  Allowed values are [PAPER, HARDBACK, KINDLE, AUDIO]"));

    }

    /*
    * test post method end
    * */
    /*
    * test update method
    * */
    @Test
    public void testUpdateBookSuccessWithExistingBook() throws Exception {

        //GIVEN an existing book with id 2 in database
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        Long bookId = 2l;
        Book book =  new Book("Western Lane", "Chetna Maroo", Format.HARDBACK, BigDecimal.valueOf( 90.00).setScale(2),
                Category.FICTION, LocalDate.of(2023, Month.DECEMBER,30), " 978-152-909-462-6",5);
        book.setId(bookId);

        Optional<Book> bookOptional = Optional.of(book);
        Mockito.<Optional<Book>>when(repository.findById(eq(bookId))).thenReturn(bookOptional);
        Mockito.when(repository.saveAndFlush(any(Book.class))).thenReturn(book);
        String requestBody = "{" +
                "        \"title\": \"coreJava\",\n" +
                "        \"author\": \"Cay S. Horstmann\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 50.00,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        Book updatedBook = mapper.readValue(requestBody, Book.class);
        updatedBook.setId(2L);
        //WHEN a restful call to put method
        //THEN the status is 200

        mockMvc.perform(MockMvcRequestBuilders.put("/book/2").content(requestBody).contentType(utf8type))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("coreJava")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author", is("Cay S. Horstmann")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", is("paper")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", is("50.00")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category", is("non-fiction")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publishedDate", is("2023-10-21")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", is("978-161-729-045-9")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(book.getId().intValue())));;
        //AND book has been updated
        verify(repository).saveAndFlush(any(Book.class));
    }

    @Test
    public void testUpdateBookSuccessWithNonExistingBookId() throws Exception {

        //GIVEN a book id that is not existing in database
        Long bookId = 2l;
        Optional<Book> emptyBookOptional = Optional.empty();
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        Mockito.<Optional<Book>>when(repository.findById(eq(bookId))).thenReturn(emptyBookOptional);

        String requestBody = "{" +
                "        \"title\": \"coreJava\",\n" +
                "        \"author\": \"Cay S. Horstmann\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 50.00,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        Book bookObj = mapper.readValue(requestBody, Book.class);
        bookObj.setId(bookId);
        Mockito.when(repository.save(any(Book.class))).thenReturn(bookObj);
        //WHEN a restful call to put method with a book information
        //THEN response status is 201
        //AND response contains updated book
        mockMvc.perform(MockMvcRequestBuilders.put("/book/2").
                content(requestBody).
                contentType(utf8type)).
                andExpect(MockMvcResultMatchers.status().isCreated()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is(bookObj.getTitle()))).
                andExpect(MockMvcResultMatchers.jsonPath("$.id", is(bookObj.getId().intValue())));
        //AND the new book has been persisted to database
        verify(repository).save(any(Book.class));
    }

    @Test
    public void testUpdateBookSuccessWithOnlyMandatoryFields() throws Exception {
        //GIVEN an existing book with id 2 in database
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        Long bookId = 2l;
        Book book =  new Book("Western Lane", "Chetna Maroo", Format.HARDBACK, BigDecimal.valueOf( 90.00).setScale(2),
                Category.FICTION, LocalDate.of(2023, Month.DECEMBER,30), " 978-152-909-462-6",5);
        book.setId(bookId);

        Optional<Book> bookOptional = Optional.of(book);
        Mockito.<Optional<Book>>when(repository.findById(eq(bookId))).thenReturn(bookOptional);

        //AND the client will call a PUT method with mandatory fields
        String requestBody = "{" +
                "        \"title\": \"coreJava\",\n" +
                "        \"author\": \"Cay S. Horstmann\"\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        Book updatedBook = mapper.readValue(requestBody, Book.class);
        updatedBook.setId(2L);
        Mockito.when(repository.saveAndFlush(any(Book.class))).thenReturn(updatedBook);
        //WHEN a restful call to put method
        //THEN the response status is 200
        //AND book mandatory fields have been updated with JSON properties
        //AND other book fields have been set to null
        mockMvc.perform(MockMvcRequestBuilders.put("/book/2").content(requestBody).contentType(utf8type))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("coreJava")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author", is("Cay S. Horstmann")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publishedDate", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(book.getId().intValue())));
    }

    @Test
    public void testUpdateBookFailedWithInvalidJson() throws Exception {
        //given an invalid json book request with empty format information
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"\",\n" +
                "        \"author\": \"\",\n" +
                "        \"format\": \"\",\n" +
                "        \"price\": ,\n" +
                "        \"category\": \"\",\n" +
                "        \"publishedDate\": \"\",\n" +
                "        \"isbn\": \"\",\n" +
                "        \"stockLevel\": \n" +
                "    }";
        //WHEN a restful call to put method
        //THEN the status is 400 bad
        //AND the response body contains parse error message
        mockMvc.perform(MockMvcRequestBuilders.put("/book/2").
                content(requestBody).
                contentType(utf8type)).
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
                andExpect(MockMvcResultMatchers.content().string("JSON parse error: Cannot construct instance of `uk.ac.jisc.bookshop.domain.Format`, problem: Unknown enum type ,  Allowed values are [PAPER, HARDBACK, KINDLE, AUDIO]"));

    }
    /*
     * test update method end
     * */

   /*
   * test patch method
   * */
    @Test
    public void testUpdateBookPartiallySuccess() throws Exception {
        //GIVEN an existing book in database with id 2
        Long bookId = 2l;
        Book book =  new Book("Western Lane", "Chetna Maroo", Format.HARDBACK, BigDecimal.valueOf( 90.00).setScale(2),
                Category.FICTION, LocalDate.of(2023, Month.DECEMBER,30), " 978-152-909-462-6",5);

        book.setId(bookId);
        Book savedBook = (Book)book.clone();
        savedBook.setId(book.getId());
        savedBook.setStockLevel(10);
        Optional<Book> bookOptional = Optional.of(book);
        Mockito.<Optional<Book>>when(repository.findById(eq(bookId))).thenReturn(bookOptional);
        Mockito.when(repository.save(any(Book.class))).thenReturn(savedBook);

        //WHEN a restful call to patch method to update the book's stockLevel to 10
        //THEN returned response status is 200
        //AND the stockLevel has been updated
        mockMvc.perform(MockMvcRequestBuilders.patch("/book/2/10")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is(savedBook.getTitle()))).
                andExpect(MockMvcResultMatchers.jsonPath("$.id", is(savedBook.getId().intValue()))).
                andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel", is(savedBook.getStockLevel())));
    }

    @Test
    public void testUpdateBookPartiallyFailedWithNonExistingBookId() throws Exception {
        //GIVEN there is no mapping book record with book id 2
        Long bookId = 2l;
        Optional<Book> emptyBookOptional = Optional.empty();
        Mockito.<Optional<Book>>when(repository.findById(eq(bookId))).thenReturn(emptyBookOptional);
        //WHEN a restful call tries to call patch method to update the non-existing book's stockLevel to 10
        //THEN returned response status is 400
        //AND the response body contains an error message
        mockMvc.perform(MockMvcRequestBuilders.patch("/book/2/10")).
                andExpect(MockMvcResultMatchers.status().isNotFound()).
                andExpect(MockMvcResultMatchers.content().string("Could not find book 2"));
    }

    /*
     * test patch method end
     * */

    /*
    * test delete method
    * */
    @Test
    public void testDeleteBookSuccess() throws Exception {
        //GIVEN there is a book record with book id 2
        Mockito.doNothing().when(repository).deleteById(eq(2l));
        //WHEN a rest request call to delete method
        //THEN the response has status 204
        mockMvc.perform(MockMvcRequestBuilders.delete("/book/2")).
                andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /*
     * test delete method end
     * */

    /*
     * test get method
     */
    @Test
    public void testGetBookWithExistingId() throws Exception {
        //GIVEN an existing book in database with id 2
        Long bookId = 2l;
        Book book =  new Book("Western Lane", "Chetna Maroo", Format.HARDBACK, BigDecimal.valueOf( 90.00).setScale(2),
                Category.FICTION, LocalDate.of(2023, Month.DECEMBER,30), " 978-152-909-462-6",5);
        Optional<Book> bookOptional = Optional.of(book);
        book.setId(bookId);
        when(repository.findById(eq(bookId))).thenReturn(bookOptional);
        //WHEN a restful call to get method to retrieve book with id 2
        //THEN the returned response status is 200
        //AND the response body has correct book information
        System.out.println("price is:" + book.getPrice());
        mockMvc.perform(MockMvcRequestBuilders.get("/book/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is(book.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author", is(book.getAuthor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", is(book.getFormat().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", is(book.getPrice().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category", is(book.getCategory().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publishedDate", is(book.getPublishedDate().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", is(book.getIsbn())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel",is(book.getStockLevel())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(book.getId().intValue())));;
    }

    @Test
    public void testGetBookWithNonExistingId() throws Exception {
        //GIVEN there is no mapping book record with book id 2
        Long bookId = 2l;
        Optional<Book> emptyBookOptional = Optional.empty();
        Mockito.<Optional<Book>>when(repository.findById(eq(bookId))).thenReturn(emptyBookOptional);
        //WHEN a restful call to get method with a non-existing id 2
        //THEN returned response status is 400
        //AND the response body contains an error message
        mockMvc.perform(MockMvcRequestBuilders.get("/book/2")).
                andExpect(MockMvcResultMatchers.status().isNotFound()).
                andExpect(MockMvcResultMatchers.content().string("Could not find book 2"));
    }

    /*
     * test get method end
     */
    /*
        test search
     */
    @Test
    public void testSearchBookWithAllValidParameters() throws Exception {
        //GIVEN  there are some books in database
        Book book = new Book("coreJava17", "G.Cornell",Format.KINDLE, BigDecimal.valueOf( 100.00).setScale(2),
                Category.NON_FICTION,LocalDate.of(2023,Month.JANUARY,31), "978-0-195-10519-3",10);

        Mockito.when(bookRepositoryService.findBookBySearchArgument(any(BookSearchArgument.class))).thenReturn(List.of(book));
        //WHEN there is a restful request to call the search method with all valid parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                .param("title","core")
                .param("author","Cornell")
                .param("priceStart","1")
                .param("priceEnd","101")
                .param("dateStart","2000-01-01")
                .param("dateEnd","2023-01-31")
                .param("format","kindle")
                .param("category","non-fiction")
                .param("isbn","978-0-195-10519-3")
                .param("page","0")
                .param("size","2")
                .param("sort","author;asc")
                .param("sort","title;asc"));
        //THEN the findBookBySearchArgument method in bookRepositoryService class has been called
        verify(bookRepositoryService).findBookBySearchArgument(bookSearchArgumentCaptor.capture());
        BookSearchArgument value = bookSearchArgumentCaptor.getValue();
        //AND the request parameters have been set to BookBySearchArgument
        assertThat(value.getTitle(),is("core"));
        assertThat(value.getAuthor(),is("Cornell"));
        assertThat(value.getFormats().contains(Format.KINDLE),is(true));
        assertThat(value.getCategories().contains(Category.NON_FICTION),is(true));
        assertThat(value.getIsbn(),is("978-0-195-10519-3"));
        assertThat(value.getPage(),is(0));
        assertThat(value.getSorts().stream().toList(), Matchers.contains(
           new Sort.Order(Sort.Direction.ASC,"author").ignoreCase(), new Sort.Order(Sort.Direction.ASC,"title").ignoreCase()
        ));
    }

}
