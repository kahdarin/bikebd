package com.example.curd.dao.create;

import org.apache.ibatis.annotations.*;

@Mapper
public interface CreateRiderDao {
    // rider_id(int)：骑行者编号
    // rider_name(varchar(255))：骑行者姓名
    // phone_number(varchar(20))：电话号
    // age(int)：年龄
    // password(varchar(255))：密码
    // account_condition(varchar(255))：账户状态（正常；注销）
    void create(
            @Param("rider_id") int rider_id,
            @Param("rider_name") String rider_name,
            @Param("phone_number") String phone_number,
            @Param("age") int age,
            @Param("password") String password,
            @Param("account_condition") String account_condition
    );
}
