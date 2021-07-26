package com.smartshopping;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_UserUpdate.php";
    private Map<String,String> parameters;

    public UpdateRequest(String userID, String name,String phoneNum, String nickName , Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("name",name);
        parameters.put("phoneNum",phoneNum);
        parameters.put("nickName",nickName);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }

}
