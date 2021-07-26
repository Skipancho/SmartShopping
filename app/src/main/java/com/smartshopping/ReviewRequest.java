package com.smartshopping;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ReviewRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_ReviewRegistration.php";
    private Map<String,String> parameters;

    public ReviewRequest(String pCode,String userID, int score, String rText, String rDate ,int pKey, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("pCode",pCode);
        parameters.put("userID",userID);
        parameters.put("score",""+score);
        parameters.put("rText",rText);
        parameters.put("rDate",rDate);
        parameters.put("pKey",""+pKey);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
