package com.qhylc.android.bms;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.qhylc.android.bms.MyCommentAdapterSource.setNull;

/**
 * Created by qhylc on {2016/12/2.}
 */

public class MyBookActivity extends ListActivity implements View.OnClickListener {
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

    private Button btQuery;
    private Button btLoan;
    private Button btReturn;

    //private ListView lvComment;
    private ListView lvLoan;

    private String userName;
    private final BlockingQueue<Object> result = new ArrayBlockingQueue<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.mybook_activity);
        getWindow().setNavigationBarColor(Color.rgb(0,191,255));

        initView();
        initEvent();

        lvLoan.setAdapter(new LoanAdapter(this));

        SharedPreferences preferences = getSharedPreferences("userData",MODE_PRIVATE);
        userName = preferences.getString("UserName","NO_USER");

        setListAdapter(new MyCommentAdapter(this));
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.books_mycommentlist_context,menu);
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete:
                        MyCommentAdapter myCommentAdapter = (MyCommentAdapter)getListAdapter();
                        MyCommentAdapterSource myCommentAdapterSource = MyCommentAdapterSource.get(MyApplication.getContext(),userName);
                        for (int i = myCommentAdapter.getCount()-1; i>0; i--) {
                            if (getListView().isItemChecked(i)) {
                                BooksComment booksCommentSelect = (BooksComment) myCommentAdapter.getItem(i);
                                String bookNameSelect = booksCommentSelect.getBookName();
                                String address = ConnectionApiConfig.getCommentUrl();
                                String jsonStr = "";
                                try{
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("commentFlag","DELETE_COMMENT");
                                    jsonObject.put("user_name",userName);
                                    jsonObject.put("book_name",bookNameSelect);
                                    jsonStr = String.valueOf(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                                    @Override
                                    public void onFinish(String response) {
                                        try{
                                            JSONObject jsonObj = new JSONObject(response);
                                            if((jsonObj.getString("STATUS")).equals("DELETE_SUCCESS")) {
                                                result.put(0);
                                            }
                                        }catch(Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        e.printStackTrace();
                                        try{
                                            result.put(1);
                                        }catch (Exception e1){
                                            e1.printStackTrace();
                                        }
                                    }
                                });
                                try {
                                    switch((int)result.take()){
                                        case 0:
                                            myCommentAdapterSource.deleteMyBooksComment(booksCommentSelect);
                                            Toast.makeText(MyApplication.getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                            break;
                                        case 1:
                                            Toast.makeText(MyApplication.getContext(),"删除失败，请检查网络或其他设置",Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            break;
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        mode.finish();
                        myCommentAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.menu_item_rewrite:
                        CreateRewriteCommentAlert();
                        //mode.finish();
                        return true;
                    default:
                        return false;
                }
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    private void initEvent() {
        ll_home.setOnClickListener(this);
        ll_aboutMe.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        btQuery.setOnClickListener(this);
        btLoan.setOnClickListener(this);
        btReturn.setOnClickListener(this);
    }

    private void initView() {
        this.ll_home = (LinearLayout) findViewById(R.id.ll_home);
        this.ll_aboutMe = (LinearLayout) findViewById(R.id.ll_aboutMe);
        this.ll_setting = (LinearLayout) findViewById(R.id.ll_setting);

        this.iv_home = (ImageView) findViewById(R.id.iv_home);
        this.iv_aboutMe = (ImageView) findViewById(R.id.iv_aboutMe);
        this.iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_aboutMe.setImageResource(R.drawable.emo_im_winking);
        iv_home.setImageResource(R.drawable.emo_im_laughing_nor);

        this.tv_home = (TextView) findViewById(R.id.tv_home);
        this.tv_aboutMe = (TextView) findViewById(R.id.tv_aboutMe);
        this.tv_setting = (TextView) findViewById(R.id.tv_setting);
        this.title = (TextView) findViewById(R.id.title);
        title.setText(R.string.barText_aboutMe);
        tv_home.setTextColor(0xffffffff);
        tv_aboutMe.setTextColor(0xff1b940a);

        this.btQuery = (Button) findViewById(R.id.bt_query);
        this.btLoan = (Button) findViewById(R.id.bt_loan);
        this.btReturn = (Button) findViewById(R.id.bt_return);

        //this.lvComment = (ListView) findViewById(R.id.ll_myComment);
        this.lvLoan = (ListView) findViewById(R.id.ll_myLoan);
    }

    private void restartButton() {
        iv_home.setImageResource(R.drawable.emo_im_laughing_nor);
        iv_aboutMe.setImageResource(R.drawable.emo_im_winking_nor);
        iv_setting.setImageResource(R.drawable.emo_im_tongue_sticking_out_nor);
        tv_home.setTextColor(0xffffffff);
        tv_aboutMe.setTextColor(0xffffffff);
        tv_setting.setTextColor(0xffffffff);
    }

    @Override
    public void onClick(View view) {
        restartButton();
        switch (view.getId()) {
            case R.id.ll_home:
                Intent intent = new Intent(MyBookActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_aboutMe:
                iv_aboutMe.setImageResource(R.drawable.emo_im_winking);
                tv_aboutMe.setTextColor(0xff1b940a);
                title.setText(R.string.barText_aboutMe);
                break;
            case R.id.ll_setting:
                Intent intent1 = new Intent(MyBookActivity.this,SettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.bt_query:
                Intent intentQuery = new Intent(MyBookActivity.this,QueryActivity.class);
                startActivity(intentQuery);
                break;
            case R.id.bt_loan:
                Intent intentLoan = new Intent(MyBookActivity.this,LoanActivity.class);
                startActivity(intentLoan);
                break;
            case R.id.bt_return:
                Intent intentReturn = new Intent(MyBookActivity.this,ReturnBookActivity.class);
                startActivity(intentReturn);
                break;
            default:
                break;
        }
    }

    private class MyCommentAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<BooksComment> myBooksCommentList;

        public MyCommentAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
            myBooksCommentList = new ArrayList<>();
            myBooksCommentList = MyCommentAdapterSource.get(context,userName).getmBooksCommentArrayList();
        }
        @Override
        public int getCount() {
            return myBooksCommentList.size();
        }
        @Override
        public Object getItem(int position) {
            return myBooksCommentList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return myBooksCommentList.get(position).getCommentId();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.listview_mycomment,parent,false);
            BooksComment booksComment = myBooksCommentList.get(position);
            TextView tvBookName = (TextView) convertView.findViewById(R.id.tv_commentBook);
            tvBookName.setText(booksComment.getBookName());
            TextView tvCommentContent = (TextView) convertView.findViewById(R.id.tv_myCommentContent);
            tvCommentContent.setText(booksComment.getBookComment());
            return convertView;
        }
    }

    private void CreateRewriteCommentAlert(){
        LayoutInflater layoutInflater = LayoutInflater.from(MyBookActivity.this);
        final View view = layoutInflater.inflate(R.layout.commentfromme_dialog,null);
        AlertDialog.Builder commentBuild = new AlertDialog.Builder(this);
        commentBuild.setTitle("书写评论");
        commentBuild.setIcon(R.drawable.ic_menu_edit);
        commentBuild.setView(view);
        commentBuild.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText commentContent = (EditText) view.findViewById(R.id.et_commentFromMe);
                MyCommentAdapter myCommentAdapter = (MyCommentAdapter) getListAdapter();
                MyCommentAdapterSource myCommentAdapterSource = MyCommentAdapterSource.get(MyApplication.getContext(), userName);
                for (int i = myCommentAdapter.getCount() - 1; i > 0; i--) {
                    if (getListView().isItemChecked(i)) {
                        BooksComment booksCommentSelect = (BooksComment) myCommentAdapter.getItem(i);
                        String bookNameSelect = booksCommentSelect.getBookName();
                        String address = ConnectionApiConfig.getCommentUrl();
                        String jsonStr = "";
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("commentFlag", "REWRITE_COMMENT");
                            jsonObject.put("book_name", bookNameSelect);
                            jsonObject.put("comment_content", commentContent.getText().toString());
                            jsonObject.put("user_name", userName);
                            jsonStr = String.valueOf(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if ((jsonObject.getString("STATUS")).equals("REWRITE_SUCCESS")) {
                                        result.put(0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                                try {
                                    result.put(1);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                        try {
                            switch ((int) result.take()) {
                                case 0:
                                    BooksComment mBooksComment = new BooksComment();
                                    mBooksComment.setUserName(userName);
                                    mBooksComment.setBookName(bookNameSelect);
                                    mBooksComment.setBookComment(commentContent.getText().toString());
                                    myCommentAdapterSource.setMyBooksComment(i,mBooksComment);
                                    break;
                                case 1:
                                    Toast.makeText(MyApplication.getContext(), "网络错误！请检查网络设置", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                myCommentAdapter.notifyDataSetChanged();
            }
        });
        commentBuild.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        commentBuild.show();
    }

    private class LoanAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Loan> loanList;

        public LoanAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
            loanList = new ArrayList<>();
            SharedPreferences preferences = getSharedPreferences("userData",MODE_PRIVATE);
            userName = preferences.getString("UserName","NO_USER");
            String address = ConnectionApiConfig.getMyLoanUrl();
            String jsonStr = "";
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_name",userName);
                jsonStr = String.valueOf(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Loan loan = new Loan();
                            loan.setLoanId(object.getInt("loan_id"));
                            loan.setUserName(object.getString("user_name"));
                            loan.setBookName(object.getString("book_name"));
                            loan.setBookAuthor(object.getString("author"));
                            loan.setLoanDate(object.getString("date"));
                            loanList.add(loan);
                        }
                    }catch (JSONException e) {
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
            return loanList.size();
        }
        @Override
        public Object getItem(int position) {
            return loanList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return loanList.get(position).getLoanId();
        }
        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            if(convertView == null)
                convertView = layoutInflater.inflate(R.layout.listview_myloan,parent,false);

            Loan loan = loanList.get(position);
            TextView tvBookName = (TextView)convertView.findViewById(R.id.tv_loanBookName);
            tvBookName.setText("书名："+ loan.getBookName());

            TextView tvBookAuthor = (TextView)convertView.findViewById(R.id.tv_loanBookAuthor);
            tvBookAuthor.setText("作者："+ loan.getBookAuthor());

            TextView tvLoanDate = (TextView)convertView.findViewById(R.id.tv_loanDate);
            tvLoanDate.setText("借阅时间："+ loan.getLoanDate());

            return convertView;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        setNull();
    }
}
