package org.openrdf.model;

import org.openrdf.model.impl.URIImpl;

import java.beans.PropertyEditorSupport;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class URIEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            text = URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        URI value = new URIImpl(text);
        setValue(value);
    }

    @Override
    public String getAsText() {
        URI value = (URI) getValue();
        return value.stringValue();
    }
}
