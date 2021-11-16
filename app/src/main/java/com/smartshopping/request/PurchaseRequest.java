package com.smartshopping.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PurchaseRequest extends StringRequest {
    final static private String URL="https://ctg1770.cafe24.com/SC/S_C_AddPurchase.php";
    private Map<String,String> parameters;

    public PurchaseRequest(String userID, String pCode,int price, int amount, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("pCode",pCode);
        parameters.put("price",""+price);
        parameters.put("amount",""+amount);
        //parameters.put("bDate",bDate);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
