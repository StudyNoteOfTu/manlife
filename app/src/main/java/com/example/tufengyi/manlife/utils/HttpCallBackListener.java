package com.example.tufengyi.manlife.utils;

public interface HttpCallBackListener {

    void onFinish(String response);

    void onError(Exception e);
}
