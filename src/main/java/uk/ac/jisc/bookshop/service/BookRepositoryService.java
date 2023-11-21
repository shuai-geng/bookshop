package uk.ac.jisc.bookshop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.ac.jisc.bookshop.BookRepositoryCustom;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.domain.Category;
import uk.ac.jisc.bookshop.domain.Format;
import uk.ac.jisc.bookshop.nondomain.BookSearchArgument;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class BookRepositoryService implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<Book> findBookBySearchArgument(BookSearchArgument argument) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query =  cb.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.hasText(argument.getTitle())){
            Path<String> titlePath = root.get("title");
            predicates.add(cb.like(cb.upper(titlePath),"%"+argument.getTitle().toUpperCase() + "%"));
        }

        if(StringUtils.hasText(argument.getAuthor())){
            Path<String> authorPath = root.get("author");
            predicates.add(cb.like(cb.upper(authorPath),"%"+argument.getAuthor().toUpperCase() + "%"));
        }

        if(argument.getPriceFrom()!=null && argument.getPriceFrom().compareTo(BigDecimal.ZERO)> 0 ){
            Path<BigDecimal> pricePath = root.get("price");
            Predicate predicate = cb.greaterThanOrEqualTo(pricePath, argument.getPriceFrom());
            predicates.add(predicate);
        }

        if (argument.getPriceTo() != null && argument.getPriceTo().compareTo(BigDecimal.ZERO)> 0 ){
            Path<BigDecimal> pricePath = root.get("price");
            Predicate predicate = cb.lessThanOrEqualTo(pricePath, argument.getPriceTo());
            predicates.add(predicate);
        }

        if(argument.getPublicationDateStart() != null) {
            Path<LocalDate> publishedDatePath = root.get("publishedDate");
            Predicate predicate = cb.greaterThanOrEqualTo(publishedDatePath, argument.getPublicationDateStart());
            predicates.add(predicate);
        }

        if(argument.getPublicationDateEnd() != null) {
            Path<LocalDate> publishedDatePath = root.get("publishedDate");
            Predicate predicate = cb.lessThanOrEqualTo(publishedDatePath, argument.getPublicationDateEnd());
            predicates.add(predicate);
        }

        if(CollectionUtils.isNotEmpty(argument.getFormats())){
            List<Predicate>formatPredicates = new ArrayList<>();
            Path<Format> formatPath = root.get("format");
            for(Format format: argument.getFormats()){
                Predicate predicate =  cb.equal(formatPath, format);
                formatPredicates.add(predicate);
            }

            predicates.add(cb.or(formatPredicates.toArray(Predicate[]::new)));
        }

        if(CollectionUtils.isNotEmpty(argument.getCategories())){
            List<Predicate>categoryPredicates = new ArrayList<>();
            Path<Format> categoryPath = root.get("category");
            for(Category category:argument.getCategories()){
                Predicate predicate = cb.equal(categoryPath, category);
                categoryPredicates.add(predicate);
            }
            predicates.add(cb.or(categoryPredicates.toArray(Predicate[]::new)));
        }

        if(argument.getIsbn() != null){
            Path<String> isbnPath = root.get("isbn");
            predicates.add(cb.like(isbnPath,"%"+argument.getIsbn() + "%"));
        }

        query.select(root).where(predicates.toArray(Predicate[]::new));
        query.orderBy(covertSortToOrder(cb, root, argument.getSorts()));

        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((argument.getPage()) * argument.getSize());
        if(argument.getSize()>0) typedQuery.setMaxResults(argument.getSize());

        List<Book> book = typedQuery.getResultList();
        return book;
    }

    private List<Order> covertSortToOrder(CriteriaBuilder cb,Root<Book> root, Sort sorts) {
        List<Order> orderList = new ArrayList();
        Optional.ofNullable(sorts).orElse(Sort.by(Collections.singletonList(new Sort.Order(Sort.Direction.DESC,"id")))).forEach(order->{
            if(order.isAscending()){
                orderList.add(cb.asc(root.get(order.getProperty())));
            } else{
                orderList.add(cb.desc(root.get(order.getProperty())));
            }
        });
        return orderList;
    }
}
