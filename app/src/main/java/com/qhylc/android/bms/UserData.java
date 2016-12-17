package com.qhylc.android.bms;

import java.io.Serializable;

/**
 * Created by qhylc on {2016/11/23.}
 */

public class UserData implements Serializable {
    private String userName;
    private String userPwd;
    private int userRole;
    private String userState;

    public String getUserState() {
        return userState;
    }
    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }
    public void setUserPwd(String userPwd){
        this.userPwd = userPwd;
    }

    public int getUserRole() {
        return userRole;
    }
    public void setUserRole(String userName) {
        if(userName.equals("admin")){
            userRole = 0;
        }else if (userName.equals("LibManager_0")||userName.equals("LibManager_1")){
            userRole = 1;
        }else {
            userRole = 2;
        }
    }

    public UserData() {
    }
}
