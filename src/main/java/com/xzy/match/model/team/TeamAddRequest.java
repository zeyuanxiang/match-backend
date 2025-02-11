package com.xzy.match.model.team;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.io.Serializable;

@Data
public class TeamAddRequest implements Serializable {

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private int maxNum;

    /**
     * 队伍过期时间
     */
    private DateTime expireTime;

    /**
     * 创建用户
     */
    private int userId;

    /**
     * 密码
     */
    private String password;
}
