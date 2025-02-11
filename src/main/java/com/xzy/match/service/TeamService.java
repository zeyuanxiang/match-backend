package com.xzy.match.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzy.match.model.entity.Team;
import com.xzy.match.model.entity.User;

public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);
}
