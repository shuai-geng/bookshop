package uk.ac.jisc.bookshop;

import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.nondomain.BookSearchArgument;

import java.util.List;

public interface BookRepositoryCustom {
    List<Book> findBookBySearchArgument(BookSearchArgument argument);
}
