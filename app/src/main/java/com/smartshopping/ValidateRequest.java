package com.smartshopping;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_Validate.php";
    private Map<String,String> parameters;

    public ValidateRequest(String find, String mode, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("find",find);
        parameters.put("mode",mode);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
