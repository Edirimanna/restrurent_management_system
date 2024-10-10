package com.cafe.rest;

import com.cafe.model.Category;
import com.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewProduct(@RequestBody(required = true)Map<String,String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<ProductWrapper>> getAllCategory();

    @PostMapping(path = "/update")
    public ResponseEntity<String>  updateProduct(@RequestBody(required = true)Map<String,String> requestMap);


}
