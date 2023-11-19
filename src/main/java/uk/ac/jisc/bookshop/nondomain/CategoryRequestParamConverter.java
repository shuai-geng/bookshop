package uk.ac.jisc.bookshop.nondomain;

import uk.ac.jisc.bookshop.domain.Category;

import java.beans.PropertyEditorSupport;

public class CategoryRequestParamConverter extends PropertyEditorSupport {
    public void setAsText(final String text) throws IllegalArgumentException {
        setValue(Category.fromValue(text));
    }
}
