package uk.ac.jisc.bookshop.nondomain;

import jakarta.persistence.AttributeConverter;
import uk.ac.jisc.bookshop.domain.Category;

import java.util.stream.Stream;


public class CategoryDatabaseConverter implements AttributeConverter<Category, String> {

    @Override
    public String convertToDatabaseColumn(Category category) {
        if (category == null){
            return null;
        }
        return category.getValue();
    }

    @Override
    public Category convertToEntityAttribute(String s) {
        if(s == null){
            return null;
        }
        return Stream.of(Category.values()).filter(f->f.getValue().equals(s)).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
