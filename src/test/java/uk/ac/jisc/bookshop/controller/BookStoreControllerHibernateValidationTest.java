package uk.ac.jisc.bookshop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.jisc.bookshop.dao.BookRepository;
import uk.ac.jisc.bookshop.service.BookRepositoryService;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
@WebMvcTest
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is("title is mandatory")));
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
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
                andExpect(MockMvcResultMatchers.status().isBadRequest()).
                andExpect(MockMvcResultMatchers.jsonPath("$.title", is("title is mandatory")));
    }
    /*
        test update method end
     */
}
