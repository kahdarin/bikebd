package com.example.curd.dao.delete;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeleteAreaDao {
    // area_id(int)：区域ID
    void delete(@Param("area_id") Integer area_id);
}
