package com.example.curd.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DelDao {
    void delete(String id);
}
