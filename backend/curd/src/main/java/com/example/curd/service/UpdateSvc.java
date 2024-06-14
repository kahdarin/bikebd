package com.example.curd.service;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.Crypto;
import com.example.curd.dao.read.ReadAreaDao;
import com.example.curd.dao.read.ReadBikeDao;
import com.example.curd.dao.read.ReadUserDao;
import com.example.curd.dao.update.UpdateAreaDao;
import com.example.curd.dao.update.UpdateBikeDao;
import com.example.curd.dao.update.UpdateUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UpdateSvc {

    @Autowired
    ReadUserDao checkUserDao; // 检查用户

    @Autowired
    UpdateUserDao updateUserDao; // 更新用户

    @Autowired
    ReadBikeDao checkBikeDao; // 检查单车

    @Autowired
    UpdateBikeDao updateBikeDao; // 更新单车

    @Autowired
    ReadAreaDao checkAreaDao; // 检查区域

    @Autowired
    UpdateAreaDao updateAreaDao; // 更新区域

    public JSONObject update(String request) {
        JSONObject json = JSONObject.parseObject(request);

        // 得到功能类型
        String task = json.getString("task");

        // 返回结果
        JSONObject response = new JSONObject();
        response.put("task", task);

        switch (task) {
            // 更新用户
            case "UpdateUser": {
                Integer user_id = json.getInteger("user_id");

                // 检查 user_id 是否为 null
                if (user_id == null) {
                    response.put("type", "Err");
                    response.put("msg", "用户ID不能为空！");
                    return response;
                }

                // 前端请求的权限
                String opr = json.getString("opr");

                String user_name = json.getString("user_name");
                String password = json.getString("password");
                String safe_pwd = Crypto.encode(password);

                String authority = json.getString("authority");
                String phone_number = json.getString("phone_number");

                Map<String, Object> user = checkUserDao.readIdPrc(user_id);

                // 检查 readIdPrc 返回的 Map 是否为空
                if (user == null || user.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "用户不存在！");
                    return response;
                }

                try {
                    updateUserDao.update(user_id, user_name, safe_pwd, authority, phone_number);
                    response.put("type", "Ok");
                    response.put("msg", "更新成功");
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);

                    // 解析错误类型(不完善)
                    response.put("msg", "更新失败！");
                }

                return response;
            }

            // 更新单车
            case "UpdateBike": {
                Integer bike_id = json.getInteger("bike_id");

                // 检查 user_id 是否为 null
                if (bike_id == null) {
                    response.put("type", "Err");
                    response.put("msg", "单车ID不能为空！");
                    return response;
                }

                // 前端请求的权限
                String opr = json.getString("opr");

                String using_condition = json.getString("using_condition");

                Map<String, Object> user = checkBikeDao.readIdPrc(bike_id);

                // 检查 readIdPrc 返回的 Map 是否为空
                if (user == null || user.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "单车不存在！");
                    return response;
                }

                try {
                    updateBikeDao.update(bike_id, using_condition);
                    response.put("type", "Ok");
                    response.put("msg", "更新成功");
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);

                    // 解析错误类型(不完善)
                    response.put("msg", "更新失败！");
                }

                return response;
            }

            // 更新区域
            case "UpdateArea": {
                Integer area_id = json.getInteger("area_id");

                // 检查 user_id 是否为 null
                if (area_id == null) {
                    response.put("type", "Err");
                    response.put("msg", "区域ID不能为空！");
                    return response;
                }

                // 前端请求的权限
                String opr = json.getString("opr");

                String vertex = json.getString("vertex");

                Map<String, Object> area = checkAreaDao.readByKey(area_id);

                // 检查 readIdPrc 返回的 Map 是否为空
                if (area == null || area.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "区域不存在！");
                    return response;
                }

                try {
                    updateAreaDao.update(area_id, vertex);
                    response.put("type", "Ok");
                    response.put("msg", "更新成功");
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);

                    // 解析错误类型(不完善)
                    response.put("msg", "更新失败！");
                }

                return response;
            }

            default:
                throw new IllegalArgumentException("未知任务: " + task);
        }
    }
}