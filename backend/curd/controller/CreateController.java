package com.example.curd.controller;

import com.example.curd.service.CreateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateController {
    @Autowired
    CreateService createService;

    @RequestMapping("create")
    public String create(@RequestBody String postJson) {
        if (createService.create(postJson).equals("添加成功(Dao层)")) {
            return "添加成功(Service层)";
        } else {
            return "添加失败(Service层)";
        }
    }
}
