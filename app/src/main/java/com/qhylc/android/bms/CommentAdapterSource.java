package com.qhylc.android.bms;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by qhylc on {2016/12/6.}
 */

public class CommentAdapterSource {

    private ArrayList<BooksComment> booksCommentList;
    private static   CommentAdapterSource sCommentAdapterSource;
    private Context mContext;

    private CommentAdapterSource (Context context,String bookName) {
        mContext = context;
        booksCommentList = new ArrayList<>();
        String address = "http://192.168.1.103:8080/BMS/Comment.jsp";
        String jsonStr = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("commentFlag","READ_COMMENT");
            jsonStr = String.valueOf(jsonObject);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                BufferedWriter bufferedWriter = null;
                try{
                    FileOutputStream fileOutputStream = MyApplication.getContext().openFileOutput("BooksComment.txt", Context.MODE_PRIVATE);
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                    bufferedWriter.write(response);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try{
                        if (bufferedWriter != null) {
                            bufferedWriter.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        try{
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
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for(int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if(bookName.equals(jsonObj.getString("book_name"))){
                    BooksComment booksComment = new BooksComment();
                    booksComment.setCommentId(jsonObj.getInt("comment_id"));
                    booksComment.setUserName(jsonObj.getString("user_name"));
                    booksComment.setBookName(jsonObj.getString("book_name"));
                    booksComment.setBookComment(jsonObj.getString("comment_content"));
                    booksCommentList.add(booksComment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static CommentAdapterSource get(Context context,String bookName){
        if(sCommentAdapterSource == null) {
            sCommentAdapterSource = new CommentAdapterSource(context.getApplicationContext(),bookName);
        }
        return sCommentAdapterSource;
    }
    public ArrayList<BooksComment> getBooksCommentList() {
        return booksCommentList;
    }
    public BooksComment getBooksComment(int commentId) {
        for (BooksComment book : booksCommentList){
            if (book.getCommentId() == commentId)
                return book;
        }
        return null;
    }
    public void deleteBooksComment(BooksComment booksComment) {
        booksCommentList.remove(booksComment);
    }
    public void addBooksComment(BooksComment booksComment) {
        booksCommentList.add(booksComment);
    }
    public static void setNull() {
        sCommentAdapterSource = null;
    }
}
