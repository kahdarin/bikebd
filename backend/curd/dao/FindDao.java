package com.example.curd.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FindDao {
    String find(int id);
}
