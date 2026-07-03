package com.smartmushroom.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password; // BCrypt加密
    private String nickname;
    private String role;     // admin/user
    private String status;   // active/disabled
    private String createTime;
    private String updateTime;
}
