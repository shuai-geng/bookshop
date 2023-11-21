package uk.ac.jisc.bookshop.dao;

import org.hibernate.usertype.LoggableUserType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.domain.Format;

import java.time.LocalDate;
import java.util.List;


public interface BookRepository  extends JpaRepository<Book, Long> {
    @Query(value="select * from Book b where " +
            "(b.title like %:title%  or :title is null or :title ='')" +
            "and (b.author like %:author% or :author is null or :author='')" +
            "and (:priceStart is null or :priceStart <= 0 or b.price>= :priceStart)" +
            "and (:priceEnd is null or :priceEnd <= 0 or b.price<= :priceEnd)" +
            "and (:dateStart is null or b.published_date>= :dateStart)" +
            "and (:dateEnd is null or b.published_date<= :dateEnd)" +
            "and (COALESCE(:formatsString) is null or b.format IN :formatsString)"+
            "and (COALESCE(:categories) is null or b.category IN :categories)" +
            "and (b.isbn =:isbn  or :isbn is null or :isbn ='')"
           // "and (COALESCE(:formats) is null or format in :#{#formats.![getValue()]})"
             ,
            countQuery = "select count(*) from Book b where (b.title like %:title%  or :title is null or :title ='')and (b.author like %:author% or :author is null or :author='')"+
                    "and (:priceStart is null or :priceStart <= 0 or b.price>= :priceStart)" +
                    "and (:priceEnd is null or :priceEnd <= 0 or b.price<= :priceEnd)" +
                    "and (:dateStart is null or b.published_date>= :dateStart)" +
                    "and (:dateEnd is null or b.published_date<= :dateEnd)" +
                   "and (COALESCE(:formatsString) is null or b.format IN :formatsString)"+
                    "and (COALESCE(:categories) is null or b.category IN :categories)" +
                    "and (b.isbn =:isbn  or :isbn is null or :isbn ='')"
            //        "and (COALESCE(:formats) is null or format IN :#{#formats.![getValue()]})"
            ,
            nativeQuery = true)
    List<Book> FindAllByTitleAndAuthor(@Param("title") String title, @Param("author") String author, @Param("priceStart") Integer priceStart,
                                       @Param("priceEnd")Integer priceEnd,
                                       @Param("dateStart") LocalDate dateStart,
                                       @Param("dateEnd") LocalDate dateEnd,
                                      // @Param("formats") List<Format> formats,
                                       @Param("formatsString")List<String>format2,
                                       @Param("categories") List<String> categories,
                                       @Param("isbn") String isbn,
                                       Pageable pageable);
}
