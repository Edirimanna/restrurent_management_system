package com.cafe.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CafeUtils {

    private CafeUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String responseMsg, HttpStatus httpStatus){

        return new ResponseEntity<>(responseMsg,httpStatus);

    }

    public static String getUUID(){
        Date date = new Date();
        long time = date.getTime();
        return "BILL- " + time;
    }
    public static JSONArray getJsonArrayFromString(String data) throws JSONException {

        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }
    public static Map<String,String> getMapFromJson(String data){
        if (!Strings.isNullOrEmpty(data))
            return new Gson().fromJson(data,new TypeToken<Map<String,Object>>(){}.getType());
        return new HashMap<>();
    }
}
