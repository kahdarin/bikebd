package com.example.curd.dao.data;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataHotSpotDao {
    List<Map<String, Object>> getLoc(@Param("month") String month);
    List<Map<String, Object>> getPeriod(@Param("month") String month);
}
