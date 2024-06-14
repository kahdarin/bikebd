package com.example.curd.dao.data;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface DataDayAvgDao {
    List<Map<String, Object>> getByDate(@Param("date") Date date);
}
