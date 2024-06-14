package com.example.curd.dao.data;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataHotBikeDao {
    List<Map<String, Object>> getMost();
    List<Map<String, Object>> getLeast();
}
