package com.smartshopping;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ReviewDeleteRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_ReviewDelete.php";
    private Map<String,String> parameters;

    public ReviewDeleteRequest(int rID, Response.Listener<String> listener){
        super(Request.Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("rID",""+ rID);
    }
    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
