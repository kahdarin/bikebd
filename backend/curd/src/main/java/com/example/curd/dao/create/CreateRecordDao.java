package com.example.curd.dao.create;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Time;
import java.sql.Timestamp;

@Mapper
public interface CreateRecordDao {
    // order_id(int): 订单编号
    // bike_id(int): 车辆编号
    // rider_id(int): 骑行者编号
    // start_time(date): 开始时间
    // start_location_x(double): 起始位置经度
    // start_location_y(double): 起始位置纬度
    // end_time(date): 结束时间
    // end_location_x(double): 终点位置经度
    // end_location_y(double): 终点位置纬度
    // duration(time): 骑行时长

    void create(
            @Param("order_id") int order_id,
            @Param("bike_id") int bike_id,
            @Param("rider_id") int rider_id,
            @Param("start_time") Timestamp start_time,
            @Param("start_location_x") double start_location_x,
            @Param("start_location_y") double start_location_y,
            @Param("end_time") Timestamp end_time,
            @Param("end_location_x") double end_location_x,
            @Param("end_location_y") double end_location_y,
            @Param("duration") Time duration
    );
}