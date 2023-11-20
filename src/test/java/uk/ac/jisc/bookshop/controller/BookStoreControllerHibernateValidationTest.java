package uk.ac.jisc.bookshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.jisc.bookshop.dao.BookRepository;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.domain.Category;
import uk.ac.jisc.bookshop.domain.Format;
import uk.ac.jisc.bookshop.service.BookRepositoryService;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookStoreControllerHibernateValidationTest {

    @MockBean
    private BookRepository repository;

    @MockBean
    private BookRepositoryService bookRepositoryService;

    @Autowired
    private BookStoreController bookStoreController;

    @Autowired
    private MockMvc mockMvc;
    /*
     test create method
    */

    @Test
    public void testHibernateValidationEmptyFailedForCreateBook() throws Exception {
        //given a json book request with empty title, author and isbn value
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"\",\n" +
                "        \"author\": \"\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 1,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to post method
        //THEN response status is 400 bad request
        //AND response body contains error message
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is("title is mandatory"))).
                andExpect(MockMvcResultMatchers.jsonPath("$.author", is("author is mandatory"))).
                andExpect(MockMvcResultMatchers.jsonPath("$.isbn", is("invalid isbn")));
    }

    @Test
    public void testHibernateValidationPriceLessThanZeroFailedForCreateBook() throws Exception {
        //given a json book request with less than 0 price value
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": -11,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to post method
        //THEN response status is 400 bad request
        //AND response body contains error message for price
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.price", is("the price should great than 0")));
    }

    @Test
    public void testHibernateValidationPriceHasMoreThanTwoFractionsFailedForCreateBook() throws Exception {
        //given a json book request with price value has more than two fractions
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 11.8888,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to post method
        //THEN response status is 400 bad request
        //AND response body contains error message for property price
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.price", is("invalid price format")));
    }

    @Test
    public void testHibernateValidationStockLevelPositiveAndPublishedDateIsNullFailedForCreateBook() throws Exception {
        //GIVEN a json book request with positive stockLevel and null PublishedDate
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 11.88,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to post method
        //THEN response status is 400 bad request
        //AND response body contains error message for property stockLevel
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel", is("stockLevel should be zero if publishedDate is null")));
    }

    @Test
    public void testHibernateValidationStockLevelPositiveAndPriceIsEmptyFailedForCreateBook() throws Exception {
        //GIVEN a json book request with positive stockLevel and empty Price
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": \"\",\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to post method
        //THEN response status is 400 bad request
        //AND response body contains correct message
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                content(requestBody).
                contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel", is("stockLevel should be zero if price is empty")));
    }
    @Test
    public void testHibernateValidationWithNoMandatoryFieldFailedForCreateBook() throws Exception {
        //GIVEN a valid json book request doesn't contain mandatory field title
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": \"\",\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to post method
        //THEN response status is 400 bad request
        //AND response body contains error message for field title
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is("title is mandatory")));
    }


    @Test
    public void testHibernateValidationWithNegativeStockLevelForCreateBook() throws Exception {
        //GIVEN a valid json book request contains a negative stock level
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": \"11.22\",\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": -3\n" +
                "    }";
        //WHEN a restful call to post method
        //THEN response status is 400 bad request
        //AND response body contains error message for stockLevel
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel", is("stockLevel should not be less than 0")));
    }
    /*
        test create method end
      */

    /*
        test update method start
     */
    @Test
    public void testHibernateValidationEmptyFailedForUpdateBook() throws Exception {
        //given a json book request with empty title, author and isbn value
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"\",\n" +
                "        \"author\": \"\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 1,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to put method
        //THEN response status is 400 bad request
        //AND response body contains error message for property title, author and isbn
        mockMvc.perform(MockMvcRequestBuilders.put("/book/1").
                content(requestBody).
                contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is("title is mandatory"))).
                andExpect(MockMvcResultMatchers.jsonPath("$.author", is("author is mandatory"))).
                andExpect(MockMvcResultMatchers.jsonPath("$.isbn", is("invalid isbn")));
    }

    @Test
    public void testHibernateValidationPriceLessThanZeroFailedForUpdateBook() throws Exception {
        //given a json book request with less than 0 price value
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": -11,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to put method
        //THEN response status is 400 bad request
        //AND response body contains error message for property price
        mockMvc.perform(MockMvcRequestBuilders.post("/book").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.price", is("the price should great than 0")));
    }

    @Test
    public void testHibernateValidationPriceHasMoreThanTwoFractionsFailedForUpdateBook() throws Exception {
        //given a json book request with price value has more than two fractions
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 11.8888,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to put method
        //THEN response status is 400 bad request
        //AND response body contains error message for property price
        mockMvc.perform(MockMvcRequestBuilders.put("/book/1").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.price", is("invalid price format")));
    }


    @Test
    public void testHibernateValidationStockLevelPositiveAndPublishedDateIsNullFailedFoUpdateBook() throws Exception {
        //GIVEN a json book request with positive stockLevel and null PublishedDate
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": 11.88,\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to put method
        //THEN response status is 400 bad request
        //AND response body contains error message for property stockLevel
        mockMvc.perform(MockMvcRequestBuilders.put("/book/1").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel", is("stockLevel should be zero if publishedDate is null")));
    }

    @Test
    public void testHibernateValidationStockLevelPositiveAndPriceIsEmptyFailedForUpdateBook() throws Exception {
        //GIVEN a json book request with positive stockLevel and empty Price
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"title\": \"title\",\n" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": \"\",\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to put method
        //THEN response status is 400 bad request
        //AND response body contains error message for property price
        mockMvc.perform(MockMvcRequestBuilders.put("/book/1").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel", is("stockLevel should be zero if price is empty")));
    }

    @Test
    public void testHibernateValidationWithNoMandatoryFieldFailedForUpdateBook() throws Exception {
        //GIVEN a valid json book request doesn't contain mandatory field title
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": \"\",\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": 3\n" +
                "    }";
        //WHEN a restful call to put method
        //THEN response status is 400 bad request
        //AND response body contains error message for field title
        mockMvc.perform(MockMvcRequestBuilders.put("/book/1").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is("title is mandatory")));
    }
    @Test
    public void testHibernateValidationWithNegativeStockLevelForUpdateBook() throws Exception {
        //GIVEN a valid json book request contains a negative stock level
        MediaType utf8type = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String requestBody = "{" +
                "        \"author\": \"author\",\n" +
                "        \"format\": \"paper\",\n" +
                "        \"price\": \"11.22\",\n" +
                "        \"category\": \"non-fiction\",\n" +
                "        \"publishedDate\": \"2023-10-21\",\n" +
                "        \"isbn\": \"978-161-729-045-9\",\n" +
                "        \"stockLevel\": -3\n" +
                "    }";
        //WHEN a restful call to put method
        //THEN response status is 400 bad request
        //AND response body contains error message for stockLevel
        mockMvc.perform(MockMvcRequestBuilders.put("/book/1").
                        content(requestBody).
                        contentType(utf8type)).
                andExpect(status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.stockLevel", is("stockLevel should not be less than 0")));
    }
    /*
        test update method end
     */
    /*
        test patch method
    */
    @Test
    public void testHibernateValidationWithNegativeStockLevelAndNegativeBookIdForUpdateBookPartially() throws Exception {
        //WHEN a restful call to patch method with a negative stock level and negative book id
        //THEN the response status is 400
        //AND the response body contains error message for field stockLevel and field id
        mockMvc.perform(MockMvcRequestBuilders.patch("/book/-1/-3")).
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.['updateBookPartially.stockLevel']", is("must be greater than or equal to 0"))).
                andExpect(MockMvcResultMatchers.jsonPath("$.['updateBookPartially.id']",is("must be greater than or equal to 0")));
    }
}
