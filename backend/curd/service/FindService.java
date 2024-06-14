package com.example.curd.service;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.dao.FindDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindService {
    @Autowired
    FindDao findDao;

    public JSONObject find(String postJson) {
        JSONObject jsonObject = JSONObject.parseObject(postJson);
        String id = jsonObject.getString("id");

        String data = findDao.find(Integer.parseInt(id));

        JSONObject response = new JSONObject();
        response.put("id", id);
        response.put("data", data);
        return response;
    }
}