package com.example.curd.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.example.curd.dao.data.DataDayAvgDao;
import com.example.curd.dao.data.DataHotBikeDao;
import com.example.curd.dao.data.DataHotRiderDao;
import com.example.curd.dao.data.DataHotSpotDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class DataSvc {

    @Autowired
    DataDayAvgDao dataDayAvgDao; // 每日单次骑行的平均骑行时间

    @Autowired
    DataHotSpotDao dataHotSpotDao; // 本月热点骑行区域/时间段

    @Autowired
    DataHotBikeDao dataHotBikeDao; // 使用最频繁和最不频繁的车辆排行榜

    @Autowired
    DataHotRiderDao dataHotRiderDao; // 使用单车最频繁的骑行者及其特征

    public JSONObject get(String request) {
        JSONObject json = JSONObject.parseObject(request);

        // 得到功能类型
        String task = json.getString("task");

        // 返回结果
        JSONObject response = new JSONObject();
        response.put("task", task);

        switch (task) {
            // 每日单次骑行的平均骑行时间
            case "DataDayAvg": {
                Date date = json.getSqlDate("date");
                String sortBy = json.getString("sort");

                List<Map<String, Object>> day_avg = dataDayAvgDao.getByDate(date);

                if (day_avg.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有该日期的记录！");
                    return response;
                } else {
                    response.put("type", "Ok");
                }

                // 格式化 average_duration 为时间类型
                for (Map<String, Object> entry : day_avg) {
                    if (entry.containsKey("avg_duration")) {
                        BigDecimal avgDuration = (BigDecimal) entry.get("avg_duration");
                        String formattedAvgDuration = formatDuration(avgDuration);
                        entry.put("avg_duration", formattedAvgDuration);
                    }
                }

                // 如果指定了排序字段，则对记录信息进行排序
                if (sortBy != null) {
                    day_avg.sort(Comparator.comparing(entry -> (Comparable) entry.get("date")));
                }

                response.put("msg", day_avg);

                return response;
            }

            // 本月热点骑行区域/时间段
            case "DataHotSpot": {
                String month = json.getString("month");
                String consider = json.getString("consider");

                List<Map<String, Object>> hotSpots = consider != null && consider.equals("loc") ? dataHotSpotDao.getLoc(month) : dataHotSpotDao.getPeriod(month);

                if (hotSpots.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有该月份的记录！");
                    return response;
                } else {
                    response.put("type", "Ok");
                }

                response.put("msg", hotSpots);
                return response;
            }

            // 使用最频繁和最不频繁的车辆排行榜
            case "DataHotBike": {
                String consider = json.getString("consider");

                List<Map<String, Object>> hotBikes = consider == null || consider.equals("most") ? dataHotBikeDao.getMost() : dataHotBikeDao.getLeast();

                if (hotBikes.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有单车的记录！");
                    return response;
                } else {
                    response.put("type", "Ok");
                }

                response.put("msg", hotBikes);
                return response;
            }

            // 使用单车最频繁的骑行者及其特征
            case "DataHotRider": {
                List<Map<String, Object>> hotRider = dataHotRiderDao.getMost();

                if (hotRider.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有骑行者的记录！");
                    return response;
                } else {
                    response.put("type", "Ok");
                }

                // 获取rider_id
                Integer riderId = (Integer) hotRider.get(0).get("rider_id");

                response.put("rider_id", riderId);

                // 遍历hotRider列表，删除rider_id字段
                hotRider.forEach(map -> map.remove("rider_id"));

                // 将处理后的hotRider列表放入响应消息中
                response.put("data", hotRider);
                return response;
            }

            // 根据热点骑行区域和热点骑行时间制定调度方案，如xx时刻将xx辆单车从某地运往某地
            case "DataSched": {
                String month = json.getString("month");
                List<Map<String, Object>> hotLoc = dataHotSpotDao.getLoc(month);
                List<Map<String, Object>> hotPeriod = dataHotSpotDao.getPeriod(month);

                if (hotLoc.isEmpty() || hotPeriod.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有找到相关数据！");
                } else {
                    response.put("type", "Ok");

                    // 示例调度方案
                    JSONArray schedule = generateSchedule(hotLoc, hotPeriod);
                    response.put("msg", schedule);
                }

                return response;
            }

            // 显示当前空闲(已关锁)单车分布和禁停区
            case "DataAreaMap": {

            }

            default:
                throw new IllegalArgumentException("未知任务: " + task);
        }
    }

    private JSONArray generateSchedule(List<Map<String, Object>> hotLoc, List<Map<String, Object>> hotPeriod) {
        JSONArray scheduleArray = new JSONArray();

        // 假设我们需要在热点时间段内将单车从热点区域调度到需求量大的区域
        for (Map<String, Object> period : hotPeriod) {
            String timeRange = (String) period.get("period");
            Long count = (Long) period.get("count");

            // 根据时间段的需求量，选择合适的调度数量
            Long dispatchCount = count / 10; // 假设每10辆单车需要调度1辆

            // 找到需求量大的区域
            String highDemandArea = findHighDemandArea(hotLoc);

            // 遍历热点区域，选择合适的调度地点
            for (Map<String, Object> loc : hotLoc) {
                String location = (String) loc.get("location");
                Long locCount = (Long) loc.get("count");

                // 如果该区域的骑行次数较多，考虑从这里调度单车
                if (locCount > 20) { // 假设阈值为20次骑行
                    // 创建一个调度信息的JSON对象
                    JSONObject scheduleItem = new JSONObject();
                    scheduleItem.put("period", timeRange);
                    scheduleItem.put("send", dispatchCount);
                    scheduleItem.put("from", location);
                    scheduleItem.put("to", highDemandArea);

                    // 如果dispatchCount不为0，则将调度信息添加到数组中
                    if (dispatchCount > 0) {
                        scheduleArray.add(scheduleItem);
                    }
                }
            }
        }

        return scheduleArray;
    }

    // 找到需求量最大的区域
    private String findHighDemandArea(List<Map<String, Object>> hotLoc) {
        String highDemandArea = "";
        Long maxCount = 0L;

        for (Map<String, Object> loc : hotLoc) {
            Long locCount = (Long) loc.get("count");
            if (locCount > maxCount) {
                maxCount = locCount;
                highDemandArea = (String) loc.get("location");
            }
        }

        return highDemandArea;
    }

    private String formatDuration(BigDecimal duration) {
        long seconds = duration.longValue();
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) - (hours * 60);
        long secs = seconds - (hours * 3600) - (minutes * 60);

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}