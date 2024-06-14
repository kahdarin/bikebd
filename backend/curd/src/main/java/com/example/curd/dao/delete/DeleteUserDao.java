package com.example.curd.dao.delete;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeleteUserDao {
    // user_id(int)：用户ID
    void delete(@Param("user_id") Integer user_id);
}
