package com.smartshopping.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_UserLogin.php";
    private Map<String,String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("userPW",userPassword);
    }

    public LoginRequest(String userID, String userPassword, String key, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("userPW",userPassword);
        parameters.put("key",key);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
