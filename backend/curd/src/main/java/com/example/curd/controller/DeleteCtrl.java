package com.example.curd.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.service.DeleteSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class DeleteCtrl {
    @Autowired
    DeleteSvc deleteSvc;

    @RequestMapping("delete")
    public JSONObject delete(@RequestBody String request) {
        JSONObject res = deleteSvc.delete(request);
        return res;
    }
}
