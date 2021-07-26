package com.smartshopping;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class WithdrawalRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_UserWithdrawal.php";
    private Map<String,String> parameters;

    public WithdrawalRequest(String userID, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
