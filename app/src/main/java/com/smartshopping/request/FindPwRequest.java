package com.smartshopping.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FindPwRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_Find_PW.php";
    private Map<String,String> parameters;

    public FindPwRequest(String name, String phoneNum,String userID,String pwAsk,String pwAnswer,  Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("name",name);
        parameters.put("phoneNum",phoneNum);
        parameters.put("userID",userID);
        parameters.put("pwAsk",pwAsk);
        parameters.put("pwAnswer",pwAnswer);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
