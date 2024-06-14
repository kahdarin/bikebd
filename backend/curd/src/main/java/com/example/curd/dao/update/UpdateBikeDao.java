package com.example.curd.dao.update;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UpdateBikeDao {
    // bike_id(int)：用户ID
    // using_condition(varchar(255)): 使用情况（待投放/使用中/已关锁/已损坏/维修中）
    void update(
            @Param("bike_id") int bike_id,
            @Param("using_condition") String using_condition
    );
}
