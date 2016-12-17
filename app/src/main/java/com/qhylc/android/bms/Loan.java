package com.qhylc.android.bms;

/**
 * Created by qhylc on {2016/12/8.}
 */

public class Loan {
    public Loan() {}

    private int loanId;
    private String userName;
    private String bookName;
    private String bookAuthor;
    private String loanDate;

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }
    public int getLoanId() {
        return loanId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public String getBookName() {
        return bookName;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }
    public String getLoanDate() {
        return loanDate;
    }
}
