package uk.ac.jisc.bookshop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.jisc.bookshop.domain.Book;


public interface BookRepository  extends JpaRepository<Book, Long> {

}
