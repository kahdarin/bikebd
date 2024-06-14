package com.example.curd.controller;

import com.example.curd.service.DelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DelController {
    @Autowired
    DelService delService;

    @RequestMapping("delete")
    public String delete(@RequestBody String request) {
        return delService.delete(request);
    }
}
