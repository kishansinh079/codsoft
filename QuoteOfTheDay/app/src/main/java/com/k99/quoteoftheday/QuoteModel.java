package com.k99.quoteoftheday;

public class QuoteModel {
    private String text;
    private String author;
    private String category;

    public QuoteModel() {} // Firebase needs this

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }
}
