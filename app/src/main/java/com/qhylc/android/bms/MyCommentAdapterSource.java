package com.qhylc.android.bms;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by qhylc on {2016/12/7.}
 */

public class MyCommentAdapterSource {
    private ArrayList<BooksComment> mBooksCommentArrayList;
    private static MyCommentAdapterSource sMyCommentAdapterSource;
    private Context mContext;
    private MyCommentAdapterSource(Context context,String userName) {
        mContext = context;
        mBooksCommentArrayList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try{
            FileInputStream fileInputStream = MyApplication.getContext().openFileInput("BooksComment.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String stringData;
            while((stringData = bufferedReader.readLine()) != null) {
                stringBuilder.append(stringData);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(userName.equals(jsonObject.getString("user_name"))) {
                    BooksComment booksComment = new BooksComment();
                    booksComment.setCommentId(jsonObject.getInt("comment_id"));
                    booksComment.setUserName(jsonObject.getString("user_name"));
                    booksComment.setBookName(jsonObject.getString("book_name"));
                    booksComment.setBookComment(jsonObject.getString("comment_content"));
                    mBooksCommentArrayList.add(booksComment);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static MyCommentAdapterSource get(Context context,String userName) {
        if (sMyCommentAdapterSource == null) {
            sMyCommentAdapterSource = new MyCommentAdapterSource(context.getApplicationContext(),userName);
        }
        return sMyCommentAdapterSource;
    }
    public ArrayList<BooksComment> getmBooksCommentArrayList() {
        return mBooksCommentArrayList;
    }
    public void deleteMyBooksComment(BooksComment booksComment) {
        mBooksCommentArrayList.remove(booksComment);
    }
    public void addMyBooksComment(BooksComment booksComment) {
        mBooksCommentArrayList.add(booksComment);
    }
    public void setMyBooksComment(int index,BooksComment booksComment) {
        mBooksCommentArrayList.set(index,booksComment);
    }
    public static void setNull() {
        sMyCommentAdapterSource = null;
    }
}
