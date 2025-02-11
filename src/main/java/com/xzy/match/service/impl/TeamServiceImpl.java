package com.xzy.match.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzy.match.common.ErrorCode;
import com.xzy.match.exception.BusinessException;
import com.xzy.match.mapper.TeamMapper;
import com.xzy.match.mapper.UserMapper;
import com.xzy.match.model.entity.Team;
import com.xzy.match.model.entity.User;
import com.xzy.match.model.entity.UserTeam;
import com.xzy.match.model.enums.TeamStatusEnum;
import com.xzy.match.service.TeamService;
import com.xzy.match.service.UserTeamService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {


    @Resource
    private UserTeamService userTeamService;

    @Override
    public long addTeam(Team team, User loginUser) {
        //1.请求参数是否唯恐
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.登录才可创建队伍
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //3.校验信息
        Long userId = loginUser.getId();
        //队伍人数大于1且小于20
        Integer maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum < 1 || maxNum > 10){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍标题小于20
        String teamName = team.getName();
        if (StringUtils.isNotBlank(teamName) && teamName.length() <= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍描述<=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() <= 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍状态是否公开
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(teamStatusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果队伍为加密状态，一定要有密码
        String password = team.getPassword();
        if(teamStatusEnum.SECRET.equals(teamStatusEnum)){
            if (StringUtils.isNotBlank(password) && password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        //超时时间大于当前时间
        DateTime expireTime = team.getExpireTime();
        if(new DateTime().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验用户最多创建5个队伍
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId", userId);
        long createTeamNum = this.count();
        if(createTeamNum > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        team.setUserId(userId);
        boolean res = this.save(team);
        Long teamId = team.getId();
        if(!res || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new DateTime());

        boolean res1 = userTeamService.save(userTeam);

        if(!res1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        return teamId;
    }
}
