package com.example.curd.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.service.ReadSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ReadCtrl {
    @Autowired
    ReadSvc readSvc;

    @RequestMapping("read")
    public JSONObject read(@RequestBody String request) {
        JSONObject res = readSvc.read(request);
        return res;
    }
}
