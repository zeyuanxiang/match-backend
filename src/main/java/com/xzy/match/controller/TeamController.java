package com.xzy.match.controller;

import com.xzy.match.common.BaseResponse;
import com.xzy.match.common.ErrorCode;
import com.xzy.match.common.ResultUtils;
import com.xzy.match.exception.BusinessException;
import com.xzy.match.model.entity.Team;
import com.xzy.match.model.entity.User;
import com.xzy.match.model.team.TeamAddRequest;
import com.xzy.match.service.TeamService;
import com.xzy.match.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if(null == teamAddRequest) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }
}
