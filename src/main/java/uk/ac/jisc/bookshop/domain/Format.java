package uk.ac.jisc.bookshop.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
public enum Format {
    PAPER("paper"),HARDBACK("hardback"),KINDLE("kindle"),AUDIO("audio");

    private String value;

    Format(String value){
        this.value = value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Format fromValue(String value){
        for (Format format : values()){
            if(format.value.contentEquals(value)){
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value + ",  Allowed values are " + Arrays.toString(values())) ;
    }

    @JsonValue
    public String getValue(){
        return value;
    }

}
