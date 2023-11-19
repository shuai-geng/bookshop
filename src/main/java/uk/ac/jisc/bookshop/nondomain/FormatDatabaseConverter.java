package uk.ac.jisc.bookshop.nondomain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.jisc.bookshop.domain.Format;

import java.util.stream.Stream;
@Converter(autoApply = true)
public class FormatDatabaseConverter implements AttributeConverter<Format, String> {
    @Override
    public String convertToDatabaseColumn(Format format) {
        if (format == null){
            return null;
        }
        return format.getValue();
    }

    @Override
    public Format convertToEntityAttribute(String s) {
        if(s == null){
            return null;
        }
        return Stream.of(Format.values()).filter(f -> f.getValue().equals(s)).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
