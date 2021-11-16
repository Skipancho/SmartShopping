package com.smartshopping.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TokenRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_Token_Check.php";
    private Map<String,String> parameters;

    public TokenRequest(String userID, String token, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("token",token);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
