package com.ting.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfile {

    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private List<String> roles;
}
