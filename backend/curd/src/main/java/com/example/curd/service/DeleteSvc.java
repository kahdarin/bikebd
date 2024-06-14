package com.example.curd.service;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.dao.delete.DeleteAreaDao;
import com.example.curd.dao.delete.DeleteUserDao;
import com.example.curd.dao.read.ReadAreaDao;
import com.example.curd.dao.read.ReadUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeleteSvc {

    @Autowired
    ReadUserDao checkUserDao; // 检查用户是否存在

    @Autowired
    DeleteUserDao deleteUserDao; //  删除用户

    @Autowired
    ReadAreaDao checkAreaDao; // 检查禁停区是否存在

    @Autowired
    DeleteAreaDao deleteAreaDao; // 删除禁停区

    public JSONObject delete(String request) {
        JSONObject json = JSONObject.parseObject(request);

        // 得到功能类型
        String task = json.getString("task");

        // 返回结果
        JSONObject response = new JSONObject();
        response.put("task", task);

        switch (task) {

            // 删除用户
            case "DeleteUser": {
                Integer user_id = json.getInteger("user_id");

                // 检查 user_id 是否为 null
                if (user_id == null) {
                    response.put("type", "Err");
                    response.put("msg", "用户ID不能为空！");
                    return response;
                }

                Map<String, Object> user = checkUserDao.readIdPrc(user_id);

                // 检查 readIdPrc 返回的 Map 是否为空
                if (user == null || user.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "用户不存在！");
                    return response;
                }

                try {
                    deleteUserDao.delete(user_id);
                    response.put("type", "Ok");
                    response.put("msg", "删除成功");
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);

                    // 解析错误类型(不完善)
                    response.put("msg", "删除失败！");
                }

                return response;
            }

            // 删除禁停区
            case "DeleteArea": {
                Integer area_id = json.getInteger("area_id");

                // 检查 user_id 是否为 null
                if (area_id == null) {
                    response.put("type", "Err");
                    response.put("msg", "区域ID不能为空！");
                    return response;
                }

                Map<String, Object> area = checkAreaDao.readByKey(area_id);

                if (area == null) {
                    response.put("type", "Err");
                    response.put("msg", "区域不存在！");
                    return response;
                }

                try {
                    deleteAreaDao.delete(area_id);
                    response.put("type", "Ok");
                    response.put("msg", "删除成功");
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);

                    // 解析错误类型(不完善)
                    response.put("msg", "删除失败！");
                }

                return response;
            }

            default:
                throw new IllegalArgumentException("未知任务: " + task);
        }
    }
}
