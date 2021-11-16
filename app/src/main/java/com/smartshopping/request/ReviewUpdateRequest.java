package com.smartshopping.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ReviewUpdateRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_ReviewUpdate.php";
    private Map<String,String> parameters;

    public ReviewUpdateRequest(int rID,int score,String rText, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("rID",""+ rID);
        parameters.put("score",""+score);
        parameters.put("rText",rText);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
