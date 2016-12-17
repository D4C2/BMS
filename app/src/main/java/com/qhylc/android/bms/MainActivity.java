package com.qhylc.android.bms;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_home;
    private LinearLayout ll_aboutMe;
    private LinearLayout ll_setting;

    private ImageView iv_home;
    private ImageView iv_aboutMe;
    private ImageView iv_setting;

    private TextView tv_home;
    private TextView tv_aboutMe;
    private TextView tv_setting;
    private TextView title;

    private ListView lvBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setNavigationBarColor(Color.rgb(0,191,255));

        initView();
        initEvent();

        lvBook.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view1,int position,
                                    long id){
                Book book = (Book) parent.getItemAtPosition(position);
                String onBook = book.getBookName();
                Intent intent = new Intent(MainActivity.this,CommentActivity.class);
                intent.putExtra("BookName",onBook);
                startActivity(intent);
            }
        });
    }

    private void initEvent() {
        ll_home.setOnClickListener(this);
        ll_aboutMe.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
    }
    private void initView() {
        this.ll_home = (LinearLayout) findViewById(R.id.ll_home);
        this.ll_aboutMe = (LinearLayout) findViewById(R.id.ll_aboutMe);
        this.ll_setting = (LinearLayout) findViewById(R.id.ll_setting);

        this.iv_home = (ImageView) findViewById(R.id.iv_home);
        this.iv_aboutMe = (ImageView) findViewById(R.id.iv_aboutMe);
        this.iv_setting = (ImageView) findViewById(R.id.iv_setting);

        this.tv_home = (TextView) findViewById(R.id.tv_home);
        this.tv_aboutMe = (TextView) findViewById(R.id.tv_aboutMe);
        this.tv_setting = (TextView) findViewById(R.id.tv_setting);
        this.title = (TextView) findViewById(R.id.title);
        title.setText(R.string.barText_home);

        this.lvBook = (ListView) findViewById(R.id.lv_content);
        lvBook.setAdapter(new BookAdapter(this));
    }

    @Override
    public void onClick(View view) {
        restartButton();
        switch(view.getId()) {
            case R.id.ll_home:
                iv_home.setImageResource(R.drawable.emo_im_laughing);
                tv_home.setTextColor(0xff1b940a);
                title.setText(R.string.barText_home);
                break;
            case R.id.ll_aboutMe:
                Intent intent1 = new Intent(MainActivity.this,MyBookActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_setting:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private void restartButton() {
        iv_home.setImageResource(R.drawable.emo_im_laughing_nor);
        iv_aboutMe.setImageResource(R.drawable.emo_im_winking_nor);
        iv_setting.setImageResource(R.drawable.emo_im_tongue_sticking_out_nor);
        tv_home.setTextColor(0xffffffff);
        tv_aboutMe.setTextColor(0xffffffff);
        tv_setting.setTextColor(0xffffffff);
    }
}
