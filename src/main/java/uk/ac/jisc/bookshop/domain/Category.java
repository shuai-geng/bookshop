package uk.ac.jisc.bookshop.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Category {
    FICTION("fiction"),NON_FICTION("non-fiction");

    private String value;


    Category (String value){
        this.value = value;
    }
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Category fromValue(String value) {
        for(Category category: values()){
            if (category.value.contentEquals(value)){
                return  category;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value + ",  Allowed values are " + Arrays.toString(values())) ;
    }

    public String getValue(){
        return value;
    }

}
