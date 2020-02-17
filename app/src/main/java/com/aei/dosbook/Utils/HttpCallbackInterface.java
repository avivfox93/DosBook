package com.aei.dosbook.Utils;

public interface HttpCallbackInterface<T>{
    void onFinish(boolean err, T result);
}