package com.example.curd.dao.create;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CreateAreaDao {
    //    area_id(int)：区域编号
    //    vertex(longtext)：经纬坐标。形如："121.521,31.315#121.521,31.325#"
    void create(@Param("area_id") int area_id, @Param("vertex") String vertex);
}