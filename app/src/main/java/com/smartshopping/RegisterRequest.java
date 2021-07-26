package com.smartshopping;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_UserRegistration.php";
    private Map<String,String> parameters;

    public RegisterRequest(User user , Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",user.getUserID());
        parameters.put("userPW",user.getUserPW());
        parameters.put("phoneNum",user.getPhoneNum());
        parameters.put("name",user.getName());
        parameters.put("nickName",user.getNickName());
        parameters.put("pwAsk",user.getPwAsk());
        parameters.put("pwAnswer",user.getPwAnswer());
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
