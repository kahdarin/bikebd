package com.example.curd.dao.read;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReadRiderDao {
//    rider_id(int)：骑行者编号(唯一)
//    rider_name(varchar(255))：骑行者姓名
//    phone_number(varchar(20))：电话号
//    age(int)：年龄
//    password(varchar(255))：密码
//    account_condition(varchar(255))：账户状态（正常/注销）

    List<Map<String, Object>> readAll();

    List<Map<String, Object>> readByKey(
            @Param("rider_id") Integer rider_id,
            @Param("rider_name") String rider_name,
            @Param("phone_number") String phone_number,
            @Param("age") Integer age,
            @Param("password") String password,
            @Param("account_condition") String account_condition);
}