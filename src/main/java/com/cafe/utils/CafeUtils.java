package com.cafe.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CafeUtils {

    private CafeUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String responseMsg, HttpStatus httpStatus){

        return new ResponseEntity<>(responseMsg,httpStatus);

    }
}
