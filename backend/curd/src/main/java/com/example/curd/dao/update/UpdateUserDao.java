package com.example.curd.dao.update;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UpdateUserDao {
    // user_id(int)：用户ID
    // user_name(varchar(255))：用户名
    // password(varchar(255))：密码
    // authority(varchar(255)): 权限 = {admin, super, manager, staff} 之一
    // phone_number(varchar(20))：手机号
    void update(
            @Param("user_id") int user_id,
            @Param("user_name") String user_name,
            @Param("password") String password,
            @Param("authority") String authority,
            @Param("phone_number") String phone_number
    );
}
