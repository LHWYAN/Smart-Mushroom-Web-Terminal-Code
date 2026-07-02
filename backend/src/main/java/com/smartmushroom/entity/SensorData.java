package com.smartmushroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("sensor_data")
public class SensorData {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("device_id")
    @JsonProperty("device_id")
    private String deviceId;

    @TableField("Temp")
    @JsonProperty("Temp")
    private String temp;

    @TableField("Humi")
    @JsonProperty("Humi")
    private String humi;

    @TableField("Lumi")
    @JsonProperty("Lumi")
    private String lumi;

    @TableField("LampST")
    @JsonProperty("LampST")
    private String lampST;

    @TableField("CondST")
    @JsonProperty("CondST")
    private String condST;

    @TableField("VentST")
    @JsonProperty("VentST")
    private String ventST;

    @TableField("BuzzerST")
    @JsonProperty("BuzzerST")
    private String buzzerST;

    @TableField("Smoke")
    @JsonProperty("Smoke")
    private String smoke;

    @TableField("CO2")
    @JsonProperty("CO2")
    private String co2;

    @TableField("create_time")
    @JsonProperty("create_time")
    private String createTime;
}
