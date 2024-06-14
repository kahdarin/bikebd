package com.example.curd.dao.update;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UpdateAreaDao {
    // area_id(int)：用户ID
    // vertex(varchar(255)): 坐标
    void update(@Param("area_id") int area_id, @Param("vertex") String vertex);
}
