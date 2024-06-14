package com.example.curd.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class CreateCtrl {
    @Autowired
    CreateSvc createSvc;

    @RequestMapping("create")
    public JSONObject create(@RequestBody String request) {
        JSONObject res = createSvc.create(request);
        return res;
    }
}
