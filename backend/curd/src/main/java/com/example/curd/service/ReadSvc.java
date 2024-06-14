package com.example.curd.service;

import com.alibaba.fastjson.*;
import com.example.curd.Crypto;
import com.example.curd.dao.read.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReadSvc {

    @Autowired
    ReadRiderDao readRiderDao; // 查询骑行者

    @Autowired
    ReadUserDao readUserDao; // 查询用户

    @Autowired
    ReadBikeDao readBikeDao; // 查询单车

    @Autowired
    ReadRecordDao readRecordDao; // 查询记录

    @Autowired
    ReadAreaDao readAreaDao; // 查询禁停区

    public JSONObject read(String request) {
        JSONObject json = JSON.parseObject(request);

        // 返回结果
        JSONObject response = new JSONObject();

        // 得到功能类型
        String task = json.getString("task");
        response.put("task", task);

        switch (task) {
            // 查询骑行者
            case "ReadRider": {
                // 提取查询参数
                Integer rider_id = json.getInteger("rider_id");
                String rider_name = json.getString("rider_name");
                String phone_number = json.getString("phone_number");
                Integer age = json.getInteger("age");
                String password = json.getString("password");
                String account_condition = json.getString("account_condition");
                String sortBy = json.getString("sort");

                // 将空字符串转换为 null
                rider_name = (rider_name != null && rider_name.isEmpty()) ? null : rider_name;
                phone_number = (phone_number != null && phone_number.isEmpty()) ? null : phone_number;
                password = (password != null && password.isEmpty()) ? null : password;
                account_condition = (account_condition != null && account_condition.isEmpty()) ? null : account_condition;

                // 根据查询参数获取骑手信息
                List<Map<String, Object>> riders = (rider_id == null && rider_name == null && phone_number == null && age == null && account_condition == null && password == null) ? readRiderDao.readAll() : readRiderDao.readByKey(rider_id, rider_name, phone_number, age, password, account_condition);

                // 如果指定了排序字段，则对骑手信息进行排序
                if (sortBy != null) {
                    Comparator<Map<String, Object>> comparator = getRiderComparator(sortBy);
                    if (comparator != null) {
                        riders.sort(comparator);
                    }
                }

                // 将骑手信息转换为 JSON 数组
                JSONArray ridersArray = new JSONArray();
                for (Map<String, Object> rider : riders) {
                    ridersArray.add(new JSONObject(rider));
                }

                if (ridersArray.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有符合要求的骑行者！");
                } else {
                    response.put("type", "Ok");
                    response.put("msg", ridersArray);
                }

                return response;
            }

            // 查询用户
            case "ReadUser": {
                // 提取查询参数
                Integer user_id = json.getInteger("user_id");
                String user_name = json.getString("user_name");
                String password = json.getString("password");
                String safe_pwd = (password != null && !password.isEmpty()) ? Crypto.encode(password) : null;
                String phone_number = json.getString("phone_number");
                String authority = json.getString("authority");
                String sortBy = json.getString("sort");

                // 将空字符串转换为 null
                user_name = (user_name != null && user_name.isEmpty()) ? null : user_name;
                phone_number = (phone_number != null && phone_number.isEmpty()) ? null : phone_number;
                authority = (authority != null && authority.isEmpty()) ? null : authority;

                // 根据查询参数获取用户信息
                List<Map<String, Object>> users = (user_id == null && user_name == null && phone_number == null && authority == null && password == null) ? readUserDao.readAll() : readUserDao.readByKey(user_id, user_name, safe_pwd, phone_number, authority);

                // 如果指定了排序字段，则对用户信息进行排序
                if (sortBy != null) {
                    Comparator<Map<String, Object>> comparator = getUserComparator(sortBy);
                    if (comparator != null) {
                        users.sort(comparator);
                    }
                }

                // 将用户信息转换为 JSON 数组
                JSONArray usersArray = new JSONArray();
                for (Map<String, Object> user : users) {
                    usersArray.add(new JSONObject(user));
                }

                if (usersArray.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有符合要求的用户！");
                } else {
                    response.put("type", "Ok");
                    response.put("msg", usersArray);
                }

                return response;
            }

            // 查询单车
            case "ReadBike": {
                // 提取查询参数
                Integer bike_id = json.getInteger("bike_id");
                String production_date = json.getString("production_date");
                String market_date = json.getString("market_date");
                String warranty_date = json.getString("warranty_date");
                String using_condition = json.getString("using_condition");
                String current_location = json.getString("current_location");
                String usage_record = json.getString("usage_record");
                String find_in = json.getString("find_in");
                String sortBy = json.getString("sort");

                // 按日期范围查询
                String production_start = json.getString("production_start");
                String production_end = json.getString("production_end");

                production_start = (production_start != null && production_start.isEmpty()) ? null : production_start;
                production_end = (production_end != null && production_end.isEmpty()) ? null : production_end;

                String market_start = json.getString("market_start");
                String market_end = json.getString("market_end");

                market_start = (market_start != null && market_start.isEmpty()) ? null : market_start;
                market_end = (market_end != null && market_end.isEmpty()) ? null : market_end;

                String warranty_start = json.getString("warranty_start");
                String warranty_end = json.getString("warranty_end");

                warranty_start = (warranty_start != null && warranty_start.isEmpty()) ? null : warranty_start;
                warranty_end = (warranty_end != null && warranty_end.isEmpty()) ? null : warranty_end;

                // 将空字符串转换为 null
                using_condition = (using_condition != null && using_condition.isEmpty()) ? null : using_condition;
                current_location = (current_location != null && current_location.isEmpty()) ? null : current_location;
                usage_record = (usage_record != null && usage_record.isEmpty()) ? null : usage_record;
                find_in = (find_in != null && find_in.isEmpty()) ? null : find_in;

                List<Map<String, Object>> bikes = (bike_id == null && production_date == null && market_date == null && warranty_date == null && using_condition == null && current_location == null && usage_record == null && find_in == null && production_start == null && market_start == null && warranty_start == null) ? readBikeDao.readAll() : readBikeDao.readByKey(bike_id, production_date, market_date, warranty_date, using_condition, current_location, usage_record, production_start, production_end, market_start, market_end, warranty_start, warranty_end);

                // 如果指定了排序字段，则对单车信息进行排序
                if (sortBy != null) {
                    Comparator<Map<String, Object>> comparator = getBikeComparator(sortBy);
                    if (comparator != null) {
                        bikes.sort(comparator);
                    }
                }

                // 如果 find_in 非空，则解析出坐标并检测 current_location
                if (find_in != null) {
                    filterBikesByLocation(bikes, find_in);
                }

                // 将单车信息转换为 JSON 数组
                JSONArray bikesArray = new JSONArray();
                for (Map<String, Object> bike : bikes) {
                    bikesArray.add(new JSONObject(bike));
                }

                if (bikesArray.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有符合要求的单车信息！");
                } else {
                    response.put("type", "Ok");
                    response.put("msg", bikesArray);
                }

                return response;
            }

            // 查询记录
            case "ReadRecord": {
                // 提取查询参数
                Integer order_id = json.getInteger("order_id");
                Integer bike_id = json.getInteger("bike_id");
                Integer rider_id = json.getInteger("rider_id");
                String start_time = json.getString("start_time");
                String end_time = json.getString("end_time");
                String sortBy = json.getString("sort");

                // 根据查询参数获取记录信息
                List<Map<String, Object>> records = (order_id == null && bike_id == null && rider_id == null && start_time == null && end_time == null) ? readRecordDao.readAll() : readRecordDao.readByKey(order_id, bike_id, rider_id, start_time, end_time);

                // 如果指定了排序字段，则对记录信息进行排序
                if (sortBy != null) {
                    Comparator<Map<String, Object>> comparator = getRecordComparator(sortBy);
                    if (comparator != null) {
                        records.sort(comparator);
                    }
                }

                // 将记录信息转换为 JSON 数组，并格式化日期时间字段
                JSONArray recordsArray = new JSONArray();
                for (Map<String, Object> record : records) {
                    if (record.get("start_time") != null) {
                        record.put("start_time", record.get("start_time").toString().replace("T", " "));
                    }
                    if (record.get("end_time") != null) {
                        record.put("end_time", record.get("end_time").toString().replace("T", " "));
                    }
                    recordsArray.add(new JSONObject(record));
                }

                if (recordsArray.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有符合要求的记录！");
                } else {
                    response.put("type", "Ok");
                    response.put("msg", recordsArray);
                }

                return response;
            }

            case "ReadArea": {
                // 提取查询参数
                Integer area_id = json.getInteger("area_id");
                Boolean showBike = json.getBoolean("show_bike");
                String sortBy = json.getString("sort");

                // 根据查询参数获取禁停区信息
                List<Map<String, Object>> areas;
                if (area_id == null) {
                    areas = readAreaDao.readAll();
                } else {
                    Map<String, Object> area = readAreaDao.readByKey(area_id);
                    areas = new ArrayList<>();// 如果 readByKey 返回 null，则创建一个空列表
                    if (area != null) {
                        areas.add(area);
                    }
                }

                // 将禁停区信息转换为 JSON 数组，并为每个禁停区检索单车信息
                JSONArray areasArray = new JSONArray();

                if (showBike != null && showBike.equals(true)) {
                    for (Map<String, Object> area : areas) {
                        String vertex = (String) area.get("vertex");
                        if (vertex != null) {
                            List<Map<String, Object>> allBikes = readBikeDao.readAll();
                            filterBikesByLocation(allBikes, vertex);

                            // 将单车信息与禁停区信息一起打包
                            JSONArray bikesArray = new JSONArray();
                            for (Map<String, Object> bike : allBikes) {
                                bikesArray.add(new JSONObject(bike));
                            }

                            JSONObject areaObject = new JSONObject(area);
                            if (!bikesArray.isEmpty()) {
                                areaObject.put("bikes", bikesArray);
                            }
                            areasArray.add(areaObject);
                        }
                    }

                    response.put("type", "Ok");
                    response.put("msg", areasArray);

                    return response;
                }

                if (areas.isEmpty()) {
                    response.put("type", "Err");
                    response.put("msg", "没有符合要求的禁停区");
                } else {
                    response.put("type", "Ok");
                    response.put("msg", areas);
                }

                return response;
            }

            default:
                throw new IllegalArgumentException("未知任务: " + task);
        }
    }

    private void filterBikesByLocation(List<Map<String, Object>> bikes, String find_in) {
        String[] points = find_in.split("#");
        if (points.length == 2) { // 确保只有两个点
            String[] point1 = points[0].split(",");
            String[] point2 = points[1].split(",");

            double minX = Math.min(Double.parseDouble(point1[0]), Double.parseDouble(point2[0]));
            double maxX = Math.max(Double.parseDouble(point1[0]), Double.parseDouble(point2[0]));
            double minY = Math.min(Double.parseDouble(point1[1]), Double.parseDouble(point2[1]));
            double maxY = Math.max(Double.parseDouble(point1[1]), Double.parseDouble(point2[1]));

            // 移除不在矩形范围内的单车信息
            Iterator<Map<String, Object>> iterator = bikes.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> bike = iterator.next();
                String bikeLocationStr = (String) bike.get("current_location");
                if (bikeLocationStr == null || Double.parseDouble(bikeLocationStr.split(",")[0]) < minX || Double.parseDouble(bikeLocationStr.split(",")[0]) > maxX || Double.parseDouble(bikeLocationStr.split(",")[1]) < minY || Double.parseDouble(bikeLocationStr.split(",")[1]) > maxY) {
                    iterator.remove();
                }
            }
        }
    }

    // 根据排序字段获取比较器
    private Comparator<Map<String, Object>> getRiderComparator(String sortBy) {
        return switch (sortBy) {
            case "id" ->
                    Comparator.comparing(rider -> (Integer) rider.get("rider_id"));
            case "name" ->
                    Comparator.comparing(rider -> (String) rider.get("rider_name"));
            case "phone" ->
                    Comparator.comparing(rider -> (String) rider.get("phone_number"));
            case "age" ->
                    Comparator.comparing(rider -> (Integer) rider.get("age"));
            default -> null;
        };
    }

    // 根据排序字段获取比较器
    private Comparator<Map<String, Object>> getUserComparator(String sortBy) {
        return switch (sortBy) {
            case "id" ->
                    Comparator.comparing(user -> (Integer) user.get("user_id"));
            case "name" ->
                    Comparator.comparing(user -> (String) user.get("user_name"));
            case "phone" ->
                    Comparator.comparing(user -> (String) user.get("phone_number"));
            case "authority" ->
                    Comparator.comparing(user -> (String) user.get("authority"));
            default -> null;
        };
    }

    // 根据排序字段获取比较器
    private Comparator<Map<String, Object>> getBikeComparator(String sortBy) {
        return switch (sortBy) {
            case "id" ->
                    Comparator.comparing(bike -> (Integer) bike.get("bike_id"));
            case "production" ->
                    Comparator.comparing(bike -> (Date) bike.get("production_date"));
            case "market" ->
                    Comparator.comparing(bike -> (Date) bike.get("market_date"));
            case "warranty" ->
                    Comparator.comparing(bike -> (Date) bike.get("warranty_date"));
            default -> null;
        };
    }

    // 根据排序字段获取比较器
    private Comparator<Map<String, Object>> getRecordComparator(String sortBy) {
        return switch (sortBy) {
            case "order" ->
                    Comparator.comparing(record -> (Integer) record.get("order_id"));
            case "bike" ->
                    Comparator.comparing(record -> (Integer) record.get("bike_id"));
            case "rider" ->
                    Comparator.comparing(record -> (Integer) record.get("rider_id"));
            case "start" ->
                    Comparator.comparing(bike -> (LocalDateTime) bike.get("start_time"));
            case "duration" ->
                    Comparator.comparing(bike -> (String) bike.get("duration"));
            default -> null;
        };
    }
}