package com.qhylc.android.bms;

/**
 * Created by qhylc on {2016/11/22.}
 */

public class Book {
    private long bookId;
    private String bookName;
    private String author;
    private int amount;
    private String category;

    public Book(){
    }

    public long getBookId(){
        return bookId;
    }
    public String getBookName() {
        return bookName;
    }
    public String getAuthor() {
        return author;
    }
    public int getAmount() {
        return amount;
    }
    public String getCategory() {
        return category;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
