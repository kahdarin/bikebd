package com.example.curd.service;

import com.alibaba.fastjson.JSONObject;
import com.example.curd.Crypto;
import com.example.curd.dao.create.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

@Service
public class CreateSvc {

    @Autowired
    CreateRiderDao createRiderDao; // 添加骑行者

    @Autowired
    CreateBikeDao createBikeDao; // 添加单车

    @Autowired
    CreateUserDao createUserDao; // 添加用户

    @Autowired
    CreateRecordDao createRecordDao; // 添加骑行记录

    @Autowired
    CreateAreaDao createAreaDao; // 添加禁停区

    public JSONObject create(String request) {
        JSONObject json = JSONObject.parseObject(request);

        // 得到功能类型
        String task = json.getString("task");

        // 返回结果
        JSONObject response = new JSONObject();
        response.put("task", task);

        // 判断功能类型
        switch (task) {
            // 添加骑行者
            case "CreateRider": {
                int rider_id = json.getIntValue("rider_id");
                String rider_name = json.getString("rider_name");
                String phone_number = json.getString("phone_number");
                int age = json.getIntValue("age");
                String password = json.getString("password");
                String account_condition = json.getString("account_condition");

                response.put("type", "Ok");
                response.put("msg", "成功添加骑行者：id=" + rider_id + ", name=" + rider_name);

                try {
                    createRiderDao.create(rider_id, rider_name, phone_number, age, Crypto.encode(password), account_condition);
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);
                    response.put("msg", "错误原因：rider_id 重复！"); // 解析错误原因
                }

                return response;
            }

            // 添加单车
            case "CreateBike": {
                int bike_id = json.getIntValue("bike_id");
                Date production_date = Date.valueOf(json.getString("production_date"));
                Date market_date = Date.valueOf(json.getString("market_date"));
                Date warranty_date = Date.valueOf(json.getString("warranty_date"));
                String using_condition = json.getString("using_condition");
                String current_location = json.getString("current_location");
                String usage_record = json.getString("usage_record");

                // 检查使用情况是否为预定义的值之一
                if (!Arrays.asList("待投放", "使用中", "已关锁", "已损坏", "维修中").contains(using_condition)) {
                    response.put("type", "Err");
                    response.put("msg", "错误原因：使用情况 = " + using_condition + "无效！");
                    return response;
                }

                // 如果使用情况不是 "已关锁" 或 "已损坏"，则设置 current_location 为空
                if (!"已关锁".equals(using_condition) && !"已损坏".equals(using_condition)) {
                    current_location = null;
                }

                response.put("type", "Ok");
                response.put("msg", "成功添加单车：id=" + bike_id);

                try {
                    createBikeDao.create(bike_id, production_date, market_date, warranty_date, using_condition, current_location, usage_record);
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);
                    response.put("msg", "错误原因：bike_id 重复！"); // 解析错误原因
                }

                return response;
            }

            // 添加用户
            case "CreateUser": {
                int user_id = json.getIntValue("user_id");
                String user_name = json.getString("user_name");
                String password = json.getString("password");
                String authority = json.getString("authority");
                String phone_number = json.getString("phone_number");

                String safe_pwd = Crypto.encode(password);

                response.put("type", "Ok");
                response.put("msg", "成功添加用户：id=" + user_id + ", name=" + user_name);

                try {
                    createUserDao.create(user_id, user_name, safe_pwd, authority, phone_number);
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);
                    response.put("msg", "错误原因：user_id 重复！"); // 解析错误原因
                }

                return response;
            }

            // 添加骑行记录
            case "CreateRecord": {
                int order_id = json.getIntValue("order_id");
                int bike_id = json.getIntValue("bike_id");
                int rider_id = json.getIntValue("rider_id");
                Timestamp start_time = Timestamp.valueOf(json.getString("start_time"));
                double start_location_x = json.getDoubleValue("start_location_x");
                double start_location_y = json.getDoubleValue("start_location_y");
                Timestamp end_time = Timestamp.valueOf(json.getString("end_time"));
                double end_location_x = json.getDoubleValue("end_location_x");
                double end_location_y = json.getDoubleValue("end_location_y");
                Time duration = Time.valueOf(json.getString("duration"));

                response.put("type", "Ok");
                response.put("msg", "成功添加骑行记录：order_id=" + order_id);

                try {
                    createRecordDao.create(order_id, bike_id, rider_id, start_time, start_location_x, start_location_y, end_time, end_location_x, end_location_y, duration);
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);

                    // 解析错误类型(不完善的方式，不能与数据库解耦)
                    if (err.contains("Duplicate entry")) {
                        response.put("msg", "错误原因：order_id 重复！");
                    } else if (err.contains("a foreign key constraint fails")) {
                        response.put("msg", "错误原因：bike_id 或 rider_id 不存在！");
                    } else {
                        response.put("msg", "错误原因：未知错误！");
                    }
                }

                return response;
            }

            // 添加禁停区
            case "CreateArea": {
                int area_id = json.getIntValue("area_id");
                String vertex = json.getString("vertex");

                response.put("type", "Ok");
                response.put("msg", "成功新增禁停区：area_id=" + area_id);

                try {
                    createAreaDao.create(area_id, vertex);
                } catch (Exception e) {
                    response.put("type", "Err");
                    String err = e.getMessage();
                    System.out.println(err);

                    // 解析错误类型(不完善)
                    if (err.contains("Duplicate entry")) {
                        response.put("msg", "错误原因：area_id 重复！");
                    } else {
                        response.put("msg", "错误原因：未知错误！");
                    }
                }

                return response;
            }

            default:
                throw new IllegalArgumentException("未知任务: " + task);
        }
    }
}
