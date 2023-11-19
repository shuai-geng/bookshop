package uk.ac.jisc.bookshop.Deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uk.ac.jisc.bookshop.Exception.InvalidDateFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

    public LocalDateDeserializer(){
        this(null);
    }

    public LocalDateDeserializer(Class<?> clazz){
        super(clazz);
    }

    private DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        LocalDate date = null;
        try{
            date = LocalDate.parse(node.asText(),formatter);
        }catch (Exception e) {
            throw new InvalidDateFormatException("invalid date",formatter.toString());
        }
        return date;
    }
}
