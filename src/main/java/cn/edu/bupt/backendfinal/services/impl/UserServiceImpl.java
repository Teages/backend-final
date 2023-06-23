package cn.edu.bupt.backendfinal.services.impl;

import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.mapper.UserMapper;
import cn.edu.bupt.backendfinal.services.UserService;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
  @Autowired
  private UserMapper userMapper;

  public ResponseEntity<SessionResponse> login(String userId, String password) {
    var user = userMapper.selectOne(
        new QueryWrapper<User>().eq("name", userId));
    if (user != null && user.getPassword().equals(DigestUtil.sha256Hex(password))) {
      return ResponseEntity.ok()
          .body(new SessionResponse(
              "Login successful",
              user.getName(),
              user.getRole()));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new SessionResponse(
              "Wrong username or password",
              null,
              null));
    }
  }

  @Data
  static public class SessionResponse {
    private String user;
    private String role;
    private String message;

    public SessionResponse(String message, String user, String role) {
      this.message = message;
      this.user = user;
      this.role = role;
    }
  }
}
