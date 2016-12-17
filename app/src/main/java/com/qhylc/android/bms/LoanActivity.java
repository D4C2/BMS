package com.qhylc.android.bms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by qhylc on {2016/12/10.}
 */

public class LoanActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etBookName;
    private EditText etBookAuthor;
    private Button btConfirm;
    private Button btReturnMyBook;
    private TextView tvState;
    String bookName;
    String authorName;
    private final BlockingQueue<Object> result = new ArrayBlockingQueue<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookloan_activity);
        getWindow().setNavigationBarColor(Color.rgb(0,191,255));

        initView();
        initEvent();

        Intent intent = getIntent();
        bookName = intent.getStringExtra("BookName");
        authorName = intent.getStringExtra("AuthorName");
        if((bookName != null)&&(authorName != null)) {
            etBookName.setText(bookName);
            etBookAuthor.setText(authorName);
        }
    }

    private void initView() {
        this.etBookName = (EditText) findViewById(R.id.et_loanBook);
        this.etBookAuthor = (EditText) findViewById(R.id.et_loanBookAuthor);
        this.btConfirm = (Button) findViewById(R.id.confirmLoan);
        this.btReturnMyBook = (Button)findViewById(R.id.bt_returnMyBook);
        this.tvState = (TextView) findViewById(R.id.tv_loanState);
    }
    private void initEvent() {
        btConfirm.setOnClickListener(this);
        btReturnMyBook.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmLoan:
                SharedPreferences preferences = getSharedPreferences("userData",MODE_PRIVATE);
                String userName = preferences.getString("UserName","NO_USER");
                if (userName.equals("NO_USER")) {
                    tvState.setText("请登录以后再来借阅书籍。");
                } else {
                    String address = ConnectionApiConfig.getLoanUrl();
                    String jsonStr = "";
                    try{
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_name",userName);
                        jsonObject.put("book_name",etBookName.getText().toString().trim());
                        jsonObject.put("author",etBookAuthor.getText().toString().trim());
                        jsonStr = String.valueOf(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            try{
                                JSONObject jsonObj = new JSONObject(response);
                                if("LOAN_SUCCESS".equals(jsonObj.getString("STATUS"))) {
                                    result.put(0);
                                }else if("NO_RESOURCE".equals(jsonObj.getString("STATUS"))){
                                    result.put(1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(Exception e) {
                            try{
                                result.put(3);
                            }catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                    try{
                        switch ((int)result.take()) {
                            case 0:
                                tvState.setText("图书已借阅成功，请前往我的图书页面查看！");
                                break;
                            case 1:
                                tvState.setText("已无该类图书资源，请联系管理员查询！");
                                break;
                            case 2:
                                tvState.setText("网络错误，请检查网络设置！");
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bt_returnMyBook:
                Intent intent = new Intent(LoanActivity.this,MyBookActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }
}
