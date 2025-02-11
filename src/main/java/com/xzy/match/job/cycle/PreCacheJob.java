package com.xzy.match.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzy.match.model.entity.User;
import com.xzy.match.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    private List<Long> mainUserList = Arrays.asList(1L);

    @Scheduled(cron = "0 12 1 * * *")
    public void doCacheRecommendUsers() {

        String redisKey = String.format("match:user:recommend:%s", mainUserList);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        Page<User> userPage= userService.page(new Page<User>(1,10), userQueryWrapper);
        ValueOperations operations = redisTemplate.opsForValue();
        try{
            operations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error",e);
        }
    }
}
