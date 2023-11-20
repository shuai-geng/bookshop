package uk.ac.jisc.bookshop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import uk.ac.jisc.bookshop.Exception.BookNotFoundException;
import uk.ac.jisc.bookshop.dao.BookRepository;
import uk.ac.jisc.bookshop.domain.Book;
import uk.ac.jisc.bookshop.domain.Category;
import uk.ac.jisc.bookshop.domain.Format;
import uk.ac.jisc.bookshop.nondomain.BookSearchArgument;
import uk.ac.jisc.bookshop.nondomain.CategoryRequestParamConverter;
import uk.ac.jisc.bookshop.nondomain.FormatRequestParamConverter;
import uk.ac.jisc.bookshop.service.BookRepositoryService;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
@Validated
@RestController
public class BookStoreController {
    private final BookRepository bookRepository;

    private BookRepositoryService bookRepositoryService;


    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public BookRepositoryService getBookRepositoryService() {
        return bookRepositoryService;
    }
    @Autowired
    public void setBookRepositoryService(BookRepositoryService bookRepositoryService) {
        this.bookRepositoryService = bookRepositoryService;
    }

    public BookStoreController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }



    @GetMapping("/books")
    public ResponseEntity<List<Book>> all(){
        return new ResponseEntity<List<Book>>(bookRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/book/{id}")
    public @ResponseBody Book getById(@PathVariable Long id){
        return bookRepository.findById(id).orElseThrow(
                ()-> new BookNotFoundException(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> getBooks(@RequestParam(required = false) String title, @RequestParam(required = false)String author,
                               @RequestParam(required = false)@Min(0) Integer priceStart, @RequestParam(required = false)@Min(0)Integer priceEnd,
                               @RequestParam(required = false,name="dateStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate publicationDateStart,
                               @RequestParam(required = false,name="dateEnd")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publicationDateEnd,
                               @RequestParam(required = false,name="format") List<Format> formats,
                               @RequestParam(required = false,name="category") List<Category> categories,
                               @RequestParam(required = false,name="isbn") String isbn,
                               @RequestParam(required = false,name="page", defaultValue = "0") int page,
                               @RequestParam(required = false,name ="size", defaultValue = "10") int size,
                               @RequestParam(required = false,value = "sort", defaultValue = "title;desc") String[] sortBy
                                ){
        Sort sorts = Sort.by(Arrays.stream(sortBy).map(sort -> sort.split(";",2)).map(array -> new Sort.Order(replaceOrderStringThroughDirection(array[1]),array[0]).ignoreCase()).collect(Collectors.toList()));
        BookSearchArgument argument = inialiseSearchArgument(title, author, priceStart, priceEnd,publicationDateStart,publicationDateEnd, formats, categories, isbn, page, size,sorts);
        return new ResponseEntity<List<Book>>(bookRepositoryService.findBookBySearchArgument(argument), HttpStatus.OK);
    }

    @PostMapping("/book")
    public  ResponseEntity<Book> addBook(@Valid @RequestBody Book book){
        return new ResponseEntity(bookRepository.save(book),HttpStatus.CREATED);
    }

    @PutMapping("/book/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody @Valid Book newBook){
        return bookRepository.findById(id).map(book ->{
            book.setTitle(newBook.getTitle());
            book.setAuthor(newBook.getAuthor());
            book.setFormat(newBook.getFormat());
            book.setPrice(newBook.getPrice());
            book.setCategory(newBook.getCategory());
            book.setPublishedDate(newBook.getPublishedDate());
            book.setIsbn(newBook.getIsbn());
            book.setStockLevel(newBook.getStockLevel());
            System.out.println("updated book:" + book);
            Book savedBook = bookRepository.saveAndFlush(book);
            return new ResponseEntity(savedBook,HttpStatus.OK);
        }).orElseGet(()-> {
            //newBook.setId(id);
            return new ResponseEntity(bookRepository.save(newBook),HttpStatus.CREATED);
        });

    }

    @PatchMapping("book/{id}/{stockLevel}")
    public @ResponseBody Book updateBookPartially(@PathVariable @Min(0) Long id, @PathVariable @Min(0) Integer stockLevel){
        return bookRepository.findById(id).map(book-> {
            book.setStockLevel(stockLevel);
            return bookRepository.save(book);
        }).orElseThrow(()->{
            return new BookNotFoundException(id);}
        );
    }


    @DeleteMapping("/book/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }

    @InitBinder
    public void initBinder(final WebDataBinder webDataBinder){
        webDataBinder.registerCustomEditor(Format.class, new FormatRequestParamConverter());
        webDataBinder.registerCustomEditor(Category.class, new CategoryRequestParamConverter());
    }

    private BookSearchArgument inialiseSearchArgument(String title,String author, Integer priceStart, Integer priceEnd,
                                                      LocalDate publicationDateStart, LocalDate publicationDateEnd, List<Format> formats, List<Category> categories, String isbn,int page,int size, Sort sorts)  {
        BookSearchArgument argument = new BookSearchArgument();
        argument.setTitle(title);
        argument.setAuthor(author);
        argument.setPriceFrom(Optional.ofNullable(priceStart).orElse(0)>0?new BigDecimal(priceStart):null);
        argument.setPriceTo(Optional.ofNullable(priceEnd).orElse(0)>0?new BigDecimal(priceEnd):null);
        argument.setPublicationDateStart(publicationDateStart);
        argument.setPublicationDateEnd(publicationDateEnd);
        argument.setFormats(formats);
        argument.setCategories(categories);
        argument.setIsbn(isbn);
        argument.setSize(size);
        argument.setPage(page);
        argument.setSorts(sorts);
        return argument;
    }

    private Sort.Direction replaceOrderStringThroughDirection(String sortDirection) {
        if (sortDirection.equalsIgnoreCase("DESC")){
            return Sort.Direction.DESC;
        } else {
            return Sort.Direction.ASC;
        }
    }

}
