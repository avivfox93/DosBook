package com.aei.dosbook.Utils;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Verification {
    private final static String VERIFY_PHONE_PATH = "/api/auth/login";
    private final static String VERIFY_CODE_PATH = "/api/auth/code";
    private static String token = "";

    public static String getToken(){return token;}

    public static void verifyToken(String token, HttpCallbackInterface<JSONObject> callback){
        String address = MyApp.SERVER_ADDRESS + VERIFY_PHONE_PATH;
        JSONObject json = new JSONObject();
        try {
            json.put("token", token);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,json,
                response -> callback.onFinish(false, response),
                error -> {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error",error.getMessage());
                    }catch (JSONException ex){ex.printStackTrace();}
                    callback.onFinish(true, jsonObject);
                });
        MyApp.getHttpManager().sendRequest(request);
    }

    public static void verifyCode(String code, String phone, HttpCallbackInterface<String> callback){
        String address = MyApp.SERVER_ADDRESS + VERIFY_CODE_PATH;
        JSONObject json = new JSONObject();
        try {
            json.put("phone_number",phone);
            json.put("code", code);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {
            try {
                token = response.getString("token");
                callback.onFinish(false, response.toString());
            }catch (JSONException e){
                callback.onFinish(true,"Token Unavailable");
            }
        },
                error -> callback.onFinish(true,error.toString()));
        MyApp.getHttpManager().sendRequest(request);
    }

//    public static void authenticate(Map<String,String> map){
//        map.put("Authentication", Verification.getToken());
//    }

    public static void setToken(String token){Verification.token = token;}

    public static void loadToken(){
        token = MyApp.getPrefs().getString("token","");
    }

    public static void saveToken(){
        MyApp.getPrefs().putString("token",token);
    }
}
