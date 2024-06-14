package com.example.curd.service;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.dao.DelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DelService {

    @Autowired
    DelDao delDao;

    public String delete(String postJson) {
        JSONObject jsonObject = JSONObject.parseObject(postJson);
        String id = jsonObject.getString("id");

        try {
            delDao.delete(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "删除失败(Dao层)";
        }

        return "删除成功(Dao层)";
    }
}