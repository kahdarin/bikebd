package com.example.curd.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.service.DataSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class DataCtrl {
    @Autowired
    DataSvc dataSvc;

    @RequestMapping("data")
    public JSONObject get(@RequestBody String request) {
        JSONObject res = dataSvc.get(request);
        return res;
    }
}
