package com.qhylc.android.bms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qhylc on {2016/12/9.}
 */

public class QueryActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btQuery;
    private Spinner spCondition;
    private EditText etCondition;
    private ListView llQueryResult;

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.bookquery_activity);
        getWindow().setNavigationBarColor(Color.rgb(0,191,255));

        initView();
        initEvent();
        //llQueryResult.setAdapter(new QueryAdapter(QueryActivity.this));
        llQueryResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = (Book)parent.getItemAtPosition(position);
                SharedPreferences.Editor editor = getSharedPreferences("OnClickBook",MODE_PRIVATE).edit();
                editor.putString("onBook",book.getBookName());
                editor.putString("onAuthor",book.getAuthor());
                editor.apply();
                //Toast.makeText(MyApplication.getContext(),String.valueOf(flag),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        this.btQuery = (Button)findViewById(R.id.bt_queryNow);
        this.etCondition = (EditText)findViewById(R.id.et_condition);
        this.spCondition = (Spinner) findViewById(R.id.sp_Condition);
        this.llQueryResult = (ListView)findViewById(R.id.ll_queryResult);
        spCondition.setSelection(3,true);
    }
    private void initEvent() {
        btQuery.setOnClickListener(this);
        spCondition.setOnItemSelectedListener(listener);
    }

    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent,View view,int pos,long id) {
            flag = pos;
            //Toast.makeText(MyApplication.getContext(),String.valueOf(flag),Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_queryNow:
                llQueryResult.setAdapter(new QueryAdapter(QueryActivity.this));
                break;
            default:
                break;
        }
    }

    private class QueryAdapter extends BaseAdapter implements View.OnClickListener {
        private LayoutInflater layoutInflater;
        private List<Book> booksList;

        public QueryAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
            booksList = new ArrayList<>();
            String address = ConnectionApiConfig.getBookQueryUrl();
            String jsonStr = "";
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("queryMethod",flag);
                switch (flag) {
                    case 0:
                        jsonObject.put("book_id",Long.parseLong(etCondition.getText().toString().trim()));
                        break;
                    case 1:
                        jsonObject.put("book_name",etCondition.getText().toString().trim());
                        break;
                    case 2:
                        jsonObject.put("category",etCondition.getText().toString().trim());
                        break;
                    default:
                        break;
                }
                jsonStr = String.valueOf(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            Book book = new Book();
                            book.setBookId(jsonObj.getLong("book_id"));
                            book.setBookName(jsonObj.getString("book_name"));
                            book.setAuthor(jsonObj.getString("author"));
                            book.setAmount(jsonObj.getInt("amount"));
                            book.setCategory(jsonObj.getString("category"));
                            booksList.add(book);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public int getCount() {
            return booksList.size();
        }
        @Override
        public Object getItem(int position) {
            return booksList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return booksList.get(position).getBookId();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.listview_query,parent,false);

            Book book = booksList.get(position);

            TextView tvBookName = (TextView)convertView.findViewById(R.id.tv_bookName);
            tvBookName.setText("书名："+book.getBookName());
            TextView tvAuthor = (TextView)convertView.findViewById(R.id.tv_author);
            tvAuthor.setText("作者："+book.getAuthor());
            TextView tvCount = (TextView)convertView.findViewById(R.id.tv_count);
            tvCount.setText("可借图书数量："+String.valueOf(book.getAmount()));

            Button btLoan = (Button)convertView.findViewById(R.id.bt_loanNow);
            Button btComment = (Button)convertView.findViewById(R.id.bt_commentNow);
            btLoan.setOnClickListener(this);
            btComment.setOnClickListener(this);

            return convertView;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_commentNow:
                    Intent intentComment = new Intent(QueryActivity.this,CommentActivity.class);
                    SharedPreferences preferences = getSharedPreferences("OnClickBook",MODE_PRIVATE);
                    intentComment.putExtra("BookName",preferences.getString("onBook","NULL"));
                    startActivity(intentComment);
                    break;
                case R.id.bt_loanNow:
                    Intent intentLoan = new Intent(QueryActivity.this,LoanActivity.class);
                    SharedPreferences preferences1 = getSharedPreferences("OnClickBook",MODE_PRIVATE);
                    intentLoan.putExtra("BookName",preferences1.getString("onBook","NULL"));
                    intentLoan.putExtra("AuthorName",preferences1.getString("onAuthor","NULL"));
                    startActivity(intentLoan);
                    break;
            }
        }
    }
}
