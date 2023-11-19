package uk.ac.jisc.bookshop.nondomain;

import uk.ac.jisc.bookshop.domain.Format;

import java.beans.PropertyEditorSupport;

public class FormatRequestParamConverter extends PropertyEditorSupport {
    public void setAsText(final String text) throws IllegalArgumentException {
        setValue(Format.fromValue(text));
    }
}
