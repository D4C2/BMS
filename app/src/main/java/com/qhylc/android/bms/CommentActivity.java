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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.qhylc.android.bms.CommentAdapterSource.setNull;

/**
 * Created by qhylc on {2016/12/1.}
 */

public class CommentActivity extends ListActivity implements View.OnClickListener {

    private TextView tvCommentBook;
    private TextView tvCommentErr;
    private Button btComment;
    //private ListView lvBooksComment;
    String bookName;
    private final BlockingQueue<Object> result = new ArrayBlockingQueue<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.comment_activity);
        getWindow().setNavigationBarColor(Color.rgb(0,191,255));
        Intent intent = getIntent();
        bookName = intent.getStringExtra("BookName");

        initView();
        initEvent();
        setListAdapter(new CommentAdapter(this));

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.books_commentlist_context,menu);
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
                        CommentAdapter adapter = (CommentAdapter)getListAdapter();
                        CommentAdapterSource commentAdapterSource = CommentAdapterSource.get(MyApplication.getContext(),bookName);
                        for(int i = adapter.getCount()-1; i>=0; i--) {
                            if(getListView().isItemChecked(i)) {
                                BooksComment booksCommentSelect = (BooksComment) adapter.getItem(i);
                                String userNameSelect = booksCommentSelect.getUserName();
                                SharedPreferences preferences = getSharedPreferences("userData",MODE_PRIVATE);
                                String userNameLogin = preferences.getString("UserName","NO_USER");
                                if(userNameLogin.equals(userNameSelect)){
                                    String address = ConnectionApiConfig.getCommentUrl();
                                    String jsonStr = "";
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("commentFlag","DELETE_COMMENT");
                                        jsonObject.put("user_name",userNameSelect);
                                        jsonObject.put("book_name",bookName);
                                        jsonStr = String.valueOf(jsonObject);
                                    }catch (JSONException e) {
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
                                        }
                                    });
                                    try {
                                        if ((int)result.take() == 0){
                                            commentAdapterSource.deleteBooksComment(booksCommentSelect);
                                            Toast.makeText(MyApplication.getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MyApplication.getContext(),"删除失败，请检查网络或其他设置",Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else{
                                    Toast.makeText(MyApplication.getContext(),"您不是该用户，不能删除该评论！请前往设置重新登录。",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        mode.finish();
                        adapter.notifyDataSetChanged();
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

    private void initView() {
        this.tvCommentBook = (TextView) findViewById(R.id.txt_commentBook);
        this.tvCommentErr = (TextView) findViewById(R.id.comment_err);
        this.btComment = (Button) findViewById(R.id.bt_comment);
        //this.lvBooksComment = (ListView) findViewById(R.id.lv_comment);
    }

    private void initEvent() {
        tvCommentBook.setOnClickListener(this);
        tvCommentErr.setOnClickListener(this);
        btComment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_comment:
                SharedPreferences getUserData = getSharedPreferences("userData",MODE_PRIVATE);
                if("LOGIN".equals(getUserData.getString("State","OFFLINE"))) {
                    CreateWriteCommentAlert();
                    CommentAdapter adapter = (CommentAdapter)getListAdapter();
                    adapter.notifyDataSetChanged();
                } else if("OFFLINE".equals(getUserData.getString("State","OFFLINE"))) {
                    tvCommentErr.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private class CommentAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<BooksComment> booksCommentList;

        public CommentAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
            booksCommentList = new ArrayList<>();
            booksCommentList = CommentAdapterSource.get(context,bookName).getBooksCommentList();
        }
        @Override
        public int getCount() {
            return booksCommentList.size();
        }
        @Override
        public Object getItem(int position) {
            return booksCommentList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return booksCommentList.get(position).getCommentId();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = layoutInflater.inflate(R.layout.listview_comment,parent,false);
            }
            BooksComment booksComment = booksCommentList.get(position);

            TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_commentName);
            tvUserName.setText(booksComment.getUserName());

            tvCommentBook.setText(booksComment.getBookName());

            TextView tvCommentContent = (TextView) convertView.findViewById(R.id.tv_commentContent);
            tvCommentContent.setText(booksComment.getBookComment());
            return convertView;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        setNull();
    }

    private void CreateWriteCommentAlert(){
        LayoutInflater layoutInflater = LayoutInflater.from(CommentActivity.this);
        final View view = layoutInflater.inflate(R.layout.commentfromme_dialog,null);
        AlertDialog.Builder commentBuild = new AlertDialog.Builder(this);
        commentBuild.setTitle("书写评论");
        commentBuild.setIcon(R.drawable.ic_menu_edit);
        commentBuild.setView(view);
        commentBuild.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText commentContent = (EditText) view.findViewById(R.id.et_commentFromMe);
                SharedPreferences preferences = getSharedPreferences("userData",MODE_PRIVATE);
                String userName = preferences.getString("UserName","NO_USER");
                String address = ConnectionApiConfig.getCommentUrl();
                String jsonStr = "";
                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("commentFlag","WRITE_COMMENT");
                    jsonObject.put("book_name",bookName);
                    jsonObject.put("comment_content",commentContent.getText().toString());
                    jsonObject.put("user_name",userName);
                    jsonStr = String.valueOf(jsonObject);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if((jsonObject.getString("STATUS")).equals("WRITE_SUCCESS")) {
                                result.put(0);
                            }else if ((jsonObject.getString("STATUS")).equals("HAVE_COMMENT")) {
                                result.put(1);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
                try{
                    if((int)result.take() == 0){
                        CommentAdapterSource adapterSource = CommentAdapterSource.get(MyApplication.getContext(),bookName);
                        BooksComment mBooksComment = new BooksComment();
                        mBooksComment.setUserName(userName);
                        mBooksComment.setBookName(bookName);
                        mBooksComment.setBookComment(commentContent.getText().toString());
                        adapterSource.addBooksComment(mBooksComment);
                    } else {
                        Toast.makeText(MyApplication.getContext(),"您已撰写过该评论，请前往我的图书重写",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
}
