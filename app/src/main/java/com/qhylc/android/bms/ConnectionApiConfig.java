package com.qhylc.android.bms;

/**
 * Created by qhylc on {2016/12/12.}
 */

public class ConnectionApiConfig {

    private static String localhost = "192.168.1.103";

    public static void setLocalhost(String localHost) {
        ConnectionApiConfig.localhost = localHost;
    }
    public static String getLocalHost() {
        return localhost;
    }

    public static String getLoginUrl() {
        return "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/login.jsp";
    }
    public static String getRegisteredUrl() {
        return "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/registered.jsp";
    }
    public static String getAddBooksUrl() {
        return "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/addBooks.jsp";
    }
    public static String getBookQueryUrl() {
        return "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/booksQuery.jsp";
    }
    public static String getLoanUrl() {
        return "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/loan.jsp";
    }
    public static String getMyLoanUrl() {
        return "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/myLoan.jsp";
    }
    public static String getBookReturn() {
        return "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/bookReturn.jsp";
    }
    public static String getCommentUrl() {
        return  "http://"+ConnectionApiConfig.getLocalHost()+":8080/BMS/Comment.jsp";
    }
}
