package uk.ac.jisc.bookshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.jisc.bookshop.dao.BookRepository;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.domain.Category;
import uk.ac.jisc.bookshop.domain.Format;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Configuration
public class InitialiseBook {

    private static final Logger log = LoggerFactory.getLogger(InitialiseBook.class);
    @Bean
    CommandLineRunner initBook(BookRepository repo){
        return args -> {
            log.info("preloading :" + repo.save(new Book("coreJava", "Cay S. Horstmann", Format.PAPER, BigDecimal.valueOf( 50.00),
                    Category.NON_FICTION,LocalDate.of(2000, Month.NOVEMBER,12), " 978-161-729-045-9",3)));
            log.info("preloading :" + repo.save(new Book("coreJava2", "Gary Cornell",Format.HARDBACK, BigDecimal.valueOf( 1000.00),
                    Category.NON_FICTION,LocalDate.of(2021,Month.JANUARY,31), " 978-161-729-045-9",23)));
            log.info("preloading :" + repo.save(new Book("coreJava17", "Gary Cornell",Format.KINDLE, BigDecimal.valueOf( 100.00),
                    Category.NON_FICTION,LocalDate.of(2023,Month.JANUARY,31), " 978-161-729-045-9",10)));
            log.info("preloading :" + repo.save(new Book("corePython", "Wesley J Chun",Format.AUDIO, BigDecimal.valueOf( 100.00),
                    Category.NON_FICTION,LocalDate.of(2015,Month.DECEMBER,30), " 978-161-729-045-9",5)));
            log.info("preloading :" + repo.save(new Book("harryPotter", "J.K.Rowling",Format.PAPER, BigDecimal.valueOf( 1000.00),
                    Category.FICTION,LocalDate.of(2011,Month.DECEMBER,30), " 978-161-729-045-9",5)));
/*            for (int i=0;i<50;i++) {
                log.info("preloading :" + repo.save(new Book("testPageWith"+i, "author",Format.PAPER, BigDecimal.valueOf(i),
                        Category.FICTION,LocalDate.of(2012,Month.DECEMBER,30), "isbn5",5)));
            }*/
        };
    }
}
