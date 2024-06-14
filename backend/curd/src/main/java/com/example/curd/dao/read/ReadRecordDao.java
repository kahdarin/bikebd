package com.example.curd.dao.read;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReadRecordDao {
    // order_id(int)：订单编号
    // bike_id(int)：车辆编号
    // rider_id(int)：骑行者编号
    // start_time(datetime)：开始时间
    // end_time(datetime)：结束时间

    List<Map<String, Object>> readAll();

    List<Map<String, Object>> readByKey(
            @Param("order_id") Integer order_id,
            @Param("bike_id") Integer bike_id,
            @Param("rider_id") Integer rider_id,
            @Param("start_time") String start_time,
            @Param("end_time") String end_time);
}