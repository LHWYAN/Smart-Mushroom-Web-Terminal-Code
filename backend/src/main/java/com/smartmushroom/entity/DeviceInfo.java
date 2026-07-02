package com.smartmushroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("device_info")
public class DeviceInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("device_id")
    @JsonProperty("device_id")
    private String deviceId;

    @TableField("device_name")
    @JsonProperty("device_name")
    private String deviceName;

    @TableField("device_type")
    @JsonProperty("device_type")
    private String deviceType;

    private String location;
    private String status;
    private String remarks;

    @TableField("create_time")
    @JsonProperty("create_time")
    private String createTime;

    @TableField("update_time")
    @JsonProperty("update_time")
    private String updateTime;
}
