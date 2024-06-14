package com.example.curd.dao.read;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReadUserDao {
    // user_id(int)：用户ID
    // user_name(varchar(255))：用户名
    // authority(varchar(255)): 权限 = {admin, super, manager, staff} 之一
    // phone_number(varchar(20))：手机号

    List<Map<String, Object>> readAll();

    List<Map<String, Object>> readByKey(
            @Param("user_id") Integer user_id,
            @Param("user_name") String user_name,
            @Param("password") String password,
            @Param("phone_number") String phone_number,
            @Param("authority") String authority);

    Map<String, Object> readIdPrc(@Param("user_id") Integer user_id);
}