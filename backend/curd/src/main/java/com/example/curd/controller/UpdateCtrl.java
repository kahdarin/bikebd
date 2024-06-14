package com.example.curd.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.service.UpdateSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class UpdateCtrl {
    @Autowired
    UpdateSvc updateSvc;

    @RequestMapping("update")
    public JSONObject update(@RequestBody String request) {
        JSONObject res = updateSvc.update(request);
        return res;
    }
}
