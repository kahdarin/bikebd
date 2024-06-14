package com.example.curd.dao.read;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReadAreaDao {
    //    area_id(int)：区域编号
    //    vertex(longtext)：经纬坐标形成矩形区域。形如："121.521,31.315#121.521,31.325"
    List<Map<String, Object>> readAll();

    Map<String, Object> readByKey(@Param("area_id") Integer area_id);
}
