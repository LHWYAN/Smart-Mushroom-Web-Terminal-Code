package com.smartmushroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("device_commands")
public class DeviceCommand {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("device_id")
    @JsonProperty("device_id")
    private String deviceId;

    private String command;

    @TableField("param_key")
    @JsonProperty("param_key")
    private String paramKey;

    @TableField("param_value")
    @JsonProperty("param_value")
    private String paramValue;

    private String status;

    @TableField("create_time")
    @JsonProperty("create_time")
    private String createTime;

    @TableField("update_time")
    @JsonProperty("update_time")
    private String updateTime;
}
