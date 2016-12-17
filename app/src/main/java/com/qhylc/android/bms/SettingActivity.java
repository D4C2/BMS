package com.qhylc.android.bms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by qhylc on {2016/11/23.}
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,DialogInterface.OnClickListener {

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
    private TextView login_user;
    private TextView login_role;

    private Button login_button;
    private Button about_button;
    private Button exit_button;
    private Button btAddBook;
    private Button btSetAddress;

    private View view;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private final UserData userData = new UserData();

    private final BlockingQueue<Object> result = new ArrayBlockingQueue<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.setting_activity);
        getWindow().setNavigationBarColor(Color.rgb(0,191,255));
        initView();
        initEvent();

        SharedPreferences getUserData = getSharedPreferences("userData",MODE_PRIVATE);
        if("LOGIN".equals(getUserData.getString("State","OFFLINE"))) {
            login_button.setVisibility(View.INVISIBLE);
            exit_button.setVisibility(View.VISIBLE);
            if((getUserData.getInt("UserRole",2) == 0)||(getUserData.getInt("UserRole",2) == 1))
                btAddBook.setVisibility(View.VISIBLE);
            login_user.setText("当前登录用户："+ getUserData.getString("UserName","NO_USER"));
            login_role.setText("当前角色："+ String.valueOf(getUserData.getInt("UserRole",0)));
        }else if("OFFLINE".equals(getUserData.getString("State","OFFLINE"))) {
            login_button.setVisibility(View.VISIBLE);
            exit_button.setVisibility(View.INVISIBLE);
            login_user.setText("当前登录用户：NO_USER");
            login_role.setText("当前角色：NULL");
        }
    }

    private void saveUserData() {
        SharedPreferences.Editor editor = getSharedPreferences("userData",MODE_PRIVATE).edit();
        editor.putString("State",userData.getUserState());
        editor.putString("UserName",userData.getUserName());
        editor.putString("UserPwd",userData.getUserPwd());
        editor.putInt("UserRole",userData.getUserRole());
        editor.apply();
    }
    private void removeUserData(){
        SharedPreferences.Editor editor = getSharedPreferences("userData",MODE_PRIVATE).edit();
        editor.remove("UserName");
        editor.remove("UserPwd");
        editor.remove("UserRole");
        editor.putString("State",userData.getUserState());
        editor.apply();
    }

    private void initEvent() {
        ll_home.setOnClickListener(this);
        ll_aboutMe.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        login_button.setOnClickListener(this);
        about_button.setOnClickListener(this);
        exit_button.setOnClickListener(this);
        btAddBook.setOnClickListener(this);
        btSetAddress.setOnClickListener(this);
    }
    private void initView() {
        this.ll_home = (LinearLayout) findViewById(R.id.ll_home);
        this.ll_aboutMe = (LinearLayout) findViewById(R.id.ll_aboutMe);
        this.ll_setting = (LinearLayout) findViewById(R.id.ll_setting);

        this.iv_home = (ImageView) findViewById(R.id.iv_home);
        this.iv_aboutMe = (ImageView) findViewById(R.id.iv_aboutMe);
        this.iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_setting.setImageResource(R.drawable.emo_im_tongue_sticking_out);
        iv_home.setImageResource(R.drawable.emo_im_laughing_nor);

        this.tv_home = (TextView) findViewById(R.id.tv_home);
        this.tv_aboutMe = (TextView) findViewById(R.id.tv_aboutMe);
        this.tv_setting = (TextView) findViewById(R.id.tv_setting);
        this.title = (TextView) findViewById(R.id.title);
        title.setText(R.string.barText_setting);
        tv_home.setTextColor(0xffffffff);
        tv_setting.setTextColor(0xff1b940a);

        this.login_user = (TextView) findViewById(R.id.login_user);
        this.login_role = (TextView) findViewById(R.id.login_role);

        this.login_button = (Button) findViewById(R.id.login_button);
        this.about_button = (Button) findViewById(R.id.about_button);
        this.exit_button = (Button) findViewById(R.id.exit_button);
        this.btAddBook = (Button) findViewById(R.id.bt_addBook);
        this.btSetAddress =(Button) findViewById(R.id.set_address);
    }

    @Override
    public void onClick(View view) {
        restartButton();
        switch(view.getId()) {
            case R.id.ll_home:
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_aboutMe:
                Intent intent1 = new Intent(SettingActivity.this,MyBookActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_setting:
                iv_setting.setImageResource(R.drawable.emo_im_tongue_sticking_out);
                tv_setting.setTextColor(0xff1b940a);
                title.setText(R.string.barText_setting);
                break;
            case R.id.login_button:
                login();
                break;
            case R.id.about_button:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("图书管理系统");
                dialog.setMessage("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t版本1.0 "+"\n"+
                        "\t\t\t\t\t\t\t\t\t\t\t版权所有© 2016 D4C2"+"\n"+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t保留所有权利");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                break;
            case R.id.exit_button:
                userData.setUserState("OFFLINE");
                removeUserData();
                login_button.setVisibility(View.VISIBLE);
                exit_button.setVisibility(View.INVISIBLE);
                btAddBook.setVisibility(View.INVISIBLE);
                login_user.setText("当前登录用户：NO_USER");
                login_role.setText("当前角色：NULL");
                break;
            case R.id.bt_addBook:
                AddBooksDialog();
                break;
            case R.id.set_address:
                SetAddressDialog();
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
    private void login() {
        LayoutInflater layoutInflater = LayoutInflater.from(SettingActivity.this);
        view = layoutInflater.inflate(R.layout.login,null);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("用户登录");
        builder.setIcon(R.drawable.ic_menu_cc_am);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("登录",this);
        builder.setNegativeButton("取消",this);
        alertDialog = builder.create();
        alertDialog.show();

        TextView register = (TextView)view.findViewById(R.id.txt_toRegister);
        register.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v){
                CreateRegisterAlert();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog , int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                EditText user = (EditText) view.findViewById(R.id.txt_username);
                EditText pwd = (EditText) view.findViewById(R.id.txt_userPwd);
                String userStr = user.getText().toString().trim();
                String pwdStr = pwd.getText().toString().trim();
                String address = ConnectionApiConfig.getLoginUrl();
                String jsonStr = "";
                try{
                    org.json.JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_name",userStr);
                    jsonObject.put("user_password",pwdStr);
                    jsonStr = String.valueOf(jsonObject);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try{
                            JSONObject jsonObj = new JSONObject(response);
                            if((jsonObj.getString("STATUS")).equals("CHECK_OK")) {
                                result.put(0);
                            }else if((jsonObj.getString("STATUS")).equals("CHECK_FAIL")){
                                result.put(1);
                            }else if((jsonObj.getString("STATUS")).equals("CHECK_NULL")){
                                result.put(2);
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        try{
                            result.put(3);
                        } catch(Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                try{
                    switch ((int)result.take()) {
                        case 0:
                            Toast.makeText(MyApplication.getContext(),"登录成功",Toast.LENGTH_SHORT).show();
                            userData.setUserName(userStr);
                            userData.setUserPwd(pwdStr);
                            userData.setUserRole(userStr);
                            userData.setUserState("LOGIN");
                            saveUserData();
                            if((userData.getUserRole() == 0) || (userData.getUserRole() == 1))
                                btAddBook.setVisibility(View.VISIBLE);
                            login_button.setVisibility(View.INVISIBLE);
                            exit_button.setVisibility(View.VISIBLE);
                            login_user.setText("当前登录用户："+ userData.getUserName());
                            login_role.setText("当前角色："+ String.valueOf(userData.getUserRole()));
                            setMShowing(dialog,true);
                            break;
                        case 1:
                            view.findViewById(R.id.txt_login_err).setVisibility(View.VISIBLE);
                            setMShowing(dialog,false);
                            Toast.makeText(MyApplication.getContext(),"登录失败",Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(MyApplication.getContext(),"用户名不存在，请点击下方“没有账号？快速注册”",
                                    Toast.LENGTH_SHORT).show();
                            setMShowing(dialog,false);
                            break;
                        case 3:
                            Toast.makeText(MyApplication.getContext(),"网络错误",Toast.LENGTH_SHORT).show();
                            setMShowing(dialog,false);
                            break;
                        default:
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                setMShowing(dialog,true);
                dialog.dismiss();
                break;
            default:
                break;
        }
    }
    private void setMShowing(DialogInterface dialogInterface, boolean mShowing) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface,mShowing);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void CreateRegisterAlert() {
        Toast.makeText(MyApplication.getContext(),"RegisterDialog",Toast.LENGTH_SHORT).show();
        LayoutInflater layoutInflater = LayoutInflater.from(SettingActivity.this);
        view = layoutInflater.inflate(R.layout.register,null);
        AlertDialog.Builder registerBuilder = new AlertDialog.Builder(this);
        registerBuilder.setTitle("注册账号");
        registerBuilder.setIcon(R.drawable.ic_menu_cc_am);
        registerBuilder.setView(view);
        //AlertDialog registerDialog = registerBuilder.create();
        registerBuilder.setPositiveButton("注册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText username = (EditText) view.findViewById(R.id.txt_username);
                EditText userPassword = (EditText) view.findViewById(R.id.txt_userPwd);
                String userName = username.getText().toString().trim();
                String userPwd = userPassword.getText().toString().trim();
                String address = ConnectionApiConfig.getRegisteredUrl();
                String jsonStr = "";
                try{
                    org.json.JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_name",userName);
                    jsonObject.put("user_password",userPwd);
                    jsonObject.put("user_role",2);
                    jsonStr = String.valueOf(jsonObject);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if((jsonObject.getString("STATUS")).equals("Registration_SUCCESS")) {
                                result.put(0);
                            }else if ((jsonObject.getString("STATUS")).equals("UsernameAlreadyExists")) {
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
                    switch ((int)result.take()) {
                        case 0:
                            Toast.makeText(MyApplication.getContext(),"注册成功",Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            view.findViewById(R.id.txt_register_err).setVisibility(View.VISIBLE);
                            setMShowing(dialog,false);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        registerBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMShowing(dialog,true);
                dialog.dismiss();
            }
        });
        registerBuilder.show();
    }

    private void AddBooksDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SettingActivity.this);
        view = layoutInflater.inflate(R.layout.addbookdialog,null);
        AlertDialog.Builder addBookBuilder = new AlertDialog.Builder(this);
        addBookBuilder.setTitle("添加图书");
        addBookBuilder.setIcon(R.drawable.ic_menu_edit);
        addBookBuilder.setView(view);
        addBookBuilder.setCancelable(false);
        addBookBuilder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText isbn = (EditText)view.findViewById(R.id.txt_isbn);
                EditText bookName = (EditText)view.findViewById(R.id.txt_bookName);
                EditText bookAuthor = (EditText)view.findViewById(R.id.txt_bookAuthor);
                EditText amount = (EditText)view.findViewById(R.id.txt_amount);
                EditText category = (EditText)view.findViewById(R.id.txt_category);
                String address = ConnectionApiConfig.getAddBooksUrl();
                String jsonStr = "";
                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_role",1);
                    jsonObject.put("book_id",Long.parseLong(isbn.getText().toString().trim()));
                    jsonObject.put("book_name",bookName.getText().toString().trim());
                    jsonObject.put("author",bookAuthor.getText().toString().trim());
                    jsonObject.put("amount",Integer.parseInt(amount.getText().toString().trim()));
                    jsonObject.put("category",category.getText().toString().trim());
                    jsonStr = String.valueOf(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.sendHttpRequest(address, jsonStr, new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try{
                            JSONObject Obj = new JSONObject(response);
                            if("ADD_SUCCESS".equals(Obj.getString("STATUS"))){
                                result.put(0);
                            }else if("BookAlreadyExists".equals(Obj.getString("STATUS"))){
                                result.put(1);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Exception e) {
                        try{
                            result.put(2);
                        }catch(Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                try{
                    switch ((int)result.take()) {
                        case 0:
                            Toast.makeText(MyApplication.getContext(),"ADD_SUCCESS",Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(MyApplication.getContext(),"BookAlreadyExists",Toast.LENGTH_SHORT).show();
                            setMShowing(dialog,false);
                            break;
                        case 2:
                            Toast.makeText(MyApplication.getContext(),"NetWorkErr",Toast.LENGTH_SHORT).show();
                            setMShowing(dialog,false);
                            break;
                        default:
                            break;
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        addBookBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMShowing(dialog,true);
                dialog.dismiss();
            }
        });
        addBookBuilder.show();
    }

    private void SetAddressDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SettingActivity.this);
        view = layoutInflater.inflate(R.layout.set_address_dialog,null);
        AlertDialog.Builder setAddressBuilder = new AlertDialog.Builder(this);
        setAddressBuilder.setTitle("设置IP地址");
        setAddressBuilder.setIcon(R.drawable.spinner_black_20);
        setAddressBuilder.setView(view);
        setAddressBuilder.setCancelable(false);
        setAddressBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText address = (EditText)view.findViewById(R.id.txt_address);
                String addressStr = address.getText().toString().trim();
                ConnectionApiConfig.setLocalhost(addressStr);
            }
        });
        setAddressBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMShowing(dialog,true);
                dialog.dismiss();
            }
        });
        setAddressBuilder.show();
    }
}
