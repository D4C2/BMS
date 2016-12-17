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

public class ReturnBookActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etReturnBookName;
    private TextView tvState;
    private Button btReturnBook;
    private Button btReturnToMyBook;

    private final BlockingQueue<Object> result = new ArrayBlockingQueue<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookreturn_activity);
        getWindow().setNavigationBarColor(Color.rgb(0,191,255));

        initView();
        initEvent();
    }

    private void initView() {
        this.etReturnBookName = (EditText)findViewById(R.id.et_returnBookName);
        this.tvState = (TextView)findViewById(R.id.tv_returnState);
        this.btReturnBook = (Button)findViewById(R.id.bt_returnBook);
        this.btReturnToMyBook = (Button)findViewById(R.id.bt_returnMyBook);
    }
    private void initEvent() {
        btReturnBook.setOnClickListener(this);
        btReturnToMyBook.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_returnBook:
                SharedPreferences preferences = getSharedPreferences("userData",MODE_PRIVATE);
                String userName = preferences.getString("UserName","NO_USER");
                if(userName.equals("NO_USER")){
                    tvState.setText("请先登录以后再做归还图书操作！");
                    tvState.setTextColor(0xff0000);
                } else {
                    String bookName = etReturnBookName.getText().toString().trim();
                    String address = ConnectionApiConfig.getBookReturn();
                    String jsonStr = "";
                    try{
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_name",userName);
                        jsonObject.put("book_name",bookName);
                        jsonStr = String.valueOf(jsonObject);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            try{
                                JSONObject jsonObj = new JSONObject(response);
                                if("RETURN_SUCCESS".equals(jsonObj.getString("STATUS")))
                                    result.put(0);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(Exception e) {
                            try{
                                result.put(1);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                    try {
                        switch ((int)result.take()) {
                            case 0:
                                tvState.setText("图书归还成功，可前往我的图书查看。");
                                break;
                            case 1:
                                tvState.setText("网络超时，检查网络设置！");
                                tvState.setTextColor(0xff0000);
                                break;
                            default:
                                break;
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bt_returnMyBook:
                Intent intent = new Intent(ReturnBookActivity.this,MyBookActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
