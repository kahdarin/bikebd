package com.example.curd.dao.create;
import org.apache.ibatis.annotations.*;
import java.sql.Date;

@Mapper
public interface CreateBikeDao {
    // bike_id(int): 单车唯一编号
    // production_date(date): 生产时间
    // market_date(date): 投放到市场时间
    // warranty_date(date): 保修日期
    // using_condition(varchar(255)): 使用情况（待投放；使用中；已关锁；已损坏；维修中）
    // current_location(varchar(255)): 当前位置(形如"121.517,31.275")，仅当using_condition为”已关锁“，”已损坏“时有效
    // usage_record(varchar(255)): 使用过的rider_id，用','分割（形如"12,17,52,..."）

    void create(
            @Param("bike_id") int bike_id,
            @Param("production_date") Date production_date,
            @Param("market_date") Date market_date,
            @Param("warranty_date") Date warranty_date,
            @Param("using_condition") String using_condition,
            @Param("current_location") String current_location,
            @Param("usage_record") String usage_record
    );
}