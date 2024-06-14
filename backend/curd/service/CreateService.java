package com.example.curd.service;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.dao.CreateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateService {

    @Autowired
    CreateDao createDao;

    public String create(String postJson) {
        JSONObject jsonObject = JSONObject.parseObject(postJson);
        String data = jsonObject.getString("data");

        try {
            createDao.create(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "添加失败(Dao层)";
        }

        return "添加成功(Dao层)";
    }
}
