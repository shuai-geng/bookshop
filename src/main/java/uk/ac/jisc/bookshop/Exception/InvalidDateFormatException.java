package uk.ac.jisc.bookshop.Exception;

public class InvalidDateFormatException extends RuntimeException{
    public InvalidDateFormatException(String value, String format){
        super("invalid date value:" + value + ". please provide date with given format:" + format);
    }
}
