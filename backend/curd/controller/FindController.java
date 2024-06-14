package com.example.curd.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.service.FindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FindController {
    @Autowired
    FindService findService;

    @RequestMapping("find")
    public JSONObject create(@RequestBody String request) {
        return findService.find(request);
    }
}
