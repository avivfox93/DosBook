package com.aei.dosbook.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpManager {

    private static HttpManager httpManager;

    private RequestQueue requestQueue;

    public static HttpManager getInstance(Context cntx){
        if(httpManager == null)
            httpManager = new HttpManager(cntx);
        return httpManager;
    }

    public<T> void sendRequest(Request<T> request){
        requestQueue.add(request);
        requestQueue.start();
    }

    private HttpManager(Context cntx) {
        requestQueue = Volley.newRequestQueue(cntx);
    }
}
