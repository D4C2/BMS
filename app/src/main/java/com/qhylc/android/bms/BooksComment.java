package com.qhylc.android.bms;

/**
 * Created by qhylc on {2016/11/30.}
 */

public class BooksComment {

    private String bookName;
    private String userName;
    private String bookComment;
    private int commentId;

    public int getCommentId() {
        return commentId;
    }
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getBookName() {
        return bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookComment() {
        return bookComment;
    }
    public void setBookComment(String bookComment) {
        this.bookComment = bookComment;
    }

    public BooksComment(){
    }
}
