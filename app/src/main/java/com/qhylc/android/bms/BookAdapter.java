package com.qhylc.android.bms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by qhylc on {2016/11/22.}
 */

public class BookAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Book> bookList;

    public BookAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        bookList = new ArrayList<>();
        String address = ConnectionApiConfig.getBookQueryUrl();
        String jsonStr = "{\"queryMethod\":\"3\",\"book_id\":\"9787302430018\",\"book_name\":\"Servlet和JSP编程基础\"," +
                "\"category\":\"计算机类\"}";
        HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0;i < jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Book book = new Book();
                        book.setBookId(jsonObject.getLong("book_id"));
                        book.setBookName(jsonObject.getString("book_name"));
                        book.setAuthor(jsonObject.getString("author"));
                        book.setAmount(jsonObject.getInt("amount"));
                        book.setCategory(jsonObject.getString("category"));
                        bookList.add(book);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    @Override
    public int getCount() {
        return bookList.size();
    }
    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return bookList.get(position).getBookId();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView ==null) {
            convertView = layoutInflater.inflate(R.layout.listview_item,parent,false);
        }
        Book book = bookList.get(position);
        TextView tv_bookName = (TextView) convertView.findViewById(R.id.tv_bookName);
        tv_bookName.setText(book.getBookName());

        TextView tv_author = (TextView) convertView.findViewById(R.id.tv_author);
        String author = "作者:" + book.getAuthor();
        tv_author.setText(author);

        TextView tv_bookId = (TextView) convertView.findViewById(R.id.tv_bookId);
        String isbn = "ISBN:" + String.valueOf(book.getBookId());
        tv_bookId.setText(isbn);

        return convertView;
    }
}
