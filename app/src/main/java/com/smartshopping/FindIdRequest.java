package com.smartshopping;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FindIdRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_Find_ID.php";
    private Map<String,String> parameters;

    public FindIdRequest(String name, String phoneNum, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("name",name);
        parameters.put("phoneNum",phoneNum);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
