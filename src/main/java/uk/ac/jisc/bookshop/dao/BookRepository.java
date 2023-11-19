package uk.ac.jisc.bookshop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.nondomain.BookSearchArgument;

import java.util.List;

public interface BookRepository  extends JpaRepository<Book, Long> {

}
