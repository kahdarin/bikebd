DELIMITER //

CREATE PROCEDURE update_random_data_for_each_rider()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE a_rider_id INT;
    DECLARE cur CURSOR FOR SELECT rider_id FROM rider;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop:
    LOOP
        FETCH cur INTO a_rider_id;

        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 更新随机生成的数据
        UPDATE rider
        SET rider_name        = CONCAT(
                CHAR(FLOOR(RAND() * 26) + IF(RAND() > 0.5, 65, 97)), -- 随机生成一个大写或小写字母
                CHAR(FLOOR(RAND() * 26) + IF(RAND() > 0.5, 65, 97)),
                CHAR(FLOOR(RAND() * 26) + IF(RAND() > 0.5, 65, 97)),
                CHAR(FLOOR(RAND() * 26) + IF(RAND() > 0.5, 65, 97)),
                CHAR(FLOOR(RAND() * 26) + IF(RAND() > 0.5, 65, 97)),
                CHAR(FLOOR(RAND() * 26) + IF(RAND() > 0.5, 65, 97))
                                ),
            phone_number      = CONCAT('1', LPAD(FLOOR(RAND() * 9999999999), 10, '0')), -- 随机生成电话号码
            age               = FLOOR(14 + RAND() * (60 - 14 + 1)),                     -- 随机年龄14-60
            password          = CONCAT('pass', LPAD(FLOOR(RAND() * 999999), 6, '0')),   -- 随机生成密码
            account_condition = IF(RAND() > 0.1, '正常', '注销')                        -- 随机账户状态
        WHERE rider_id = a_rider_id;
    END LOOP;

    CLOSE cur;
END//

DELIMITER ;

CALL update_random_data_for_each_rider();