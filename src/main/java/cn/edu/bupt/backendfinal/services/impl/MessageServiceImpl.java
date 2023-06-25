package cn.edu.bupt.backendfinal.services.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.edu.bupt.backendfinal.services.MessageService;
import jakarta.annotation.Resource;

@Service
public class MessageServiceImpl implements MessageService {
  private static Integer timeOut = 1;

  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  @Override
  public void produce(String key, String msg) {
    redisTemplate.opsForList().leftPush(key, msg);
  }

  @Override
  public String consume(String key) {
    String msg = null;
    while (msg == null) {
      msg = (String) redisTemplate.opsForList().rightPop(key, timeOut, TimeUnit.SECONDS);
    }
    return msg;
  }
}
