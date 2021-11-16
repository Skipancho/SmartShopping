package com.smartshopping.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChangePwRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_Change_PW.php";
    private Map<String,String> parameters;

    public ChangePwRequest(String userID,String newPw,  Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("newPw",newPw);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
