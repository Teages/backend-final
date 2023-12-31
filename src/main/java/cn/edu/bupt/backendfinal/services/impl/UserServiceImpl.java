package cn.edu.bupt.backendfinal.services.impl;

import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.mapper.UserMapper;
import cn.edu.bupt.backendfinal.services.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
  @Autowired
  UserMapper userMapper;

  static public String secret = "himitsu";
  JWTSigner signer = JWTSignerUtil.hs256(secret.getBytes(StandardCharsets.UTF_8));

  public ResponseEntity<SessionResponse> getSession(String token) {
    var user = whoami(token);
    if (user != null) {
      return ResponseEntity.ok()
          .body(new SessionResponse(
              String.format("You are %s", user.getName()),
              user.getName(),
              user.getRole()));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new SessionResponse(
            "You haven't logged in yet"));
  }

  public ResponseEntity<SessionResponse> login(String userId, String password, HttpServletResponse response) {
    var user = findByUid(userId);
    if (user != null && user.verify(password)) {
      var createAt = Calendar.getInstance();
      var expiresAt = Calendar.getInstance();
      expiresAt.add(Calendar.DAY_OF_MONTH, 7);

      String token = JWT.create()
          .setPayload("user", user.getName())
          .setPayload("role", user.getRole())
          .setSigner(signer)
          .setIssuedAt(createAt.getTime())
          .setExpiresAt(expiresAt.getTime())
          .sign();

      var cookieToken = new Cookie("token", token);
      // cookieToken.setSecure(true);
      cookieToken.setHttpOnly(true);
      cookieToken.setPath("/");
      cookieToken.setMaxAge(7 * 24 * 60 * 60);
      response.addCookie(cookieToken);

      var cookieRole = new Cookie("role", user.getRole());
      cookieRole.setMaxAge(7 * 24 * 60 * 60);
      cookieRole.setPath("/");
      response.addCookie(cookieRole);

      return ResponseEntity.ok()
          .body(new SessionResponse(
              "Login successful",
              user.getName(),
              user.getRole(),
              token));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new SessionResponse(
              "Wrong username or password"));
    }
  }

  public ResponseEntity<SessionResponse> logout(HttpServletResponse response) {
    var cookie = new Cookie("token", "");
    // cookie.setSecure(true);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
    return ResponseEntity.ok()
        .body(new SessionResponse(
            "Cleaned token"));
  }

  public ResponseEntity<SessionResponse> register(String userId, String password, HttpServletResponse response) {
    var user = findByUid(userId);
    if (user != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new SessionResponse(
              "The name have been used"));
    }
    var newUser = new User(userId, password);
    userMapper.insert(newUser);

    var ans = login(userId, password, response);
    if (ans.getBody().getUser() == null) {
      ans.getBody().setMessage("Unknown error");
    } else {
      ans.getBody().setMessage("Register successfully");
    }
    return ans;
  }

  public User whoami(String token) {
    if (token == null) {
      return null;
    }
    try {
      JWTUtil.verify(token, signer);
      final var jwt = JWTUtil.parseToken(token);
      JWTValidator.of(jwt)
          .validateAlgorithm(signer)
          .validateDate(DateUtil.date());

      var userId = (String) jwt.getPayload("user");
      if (userId != null) {
        var user = findByUid(userId);
        return user;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public User findByUid(String userId) {
    if (userId == null) {
      return null;
    }
    return userMapper.selectOne(
        new QueryWrapper<User>().apply(
            StringUtils.isNotEmpty((userId)),
            "LOWER(name) = {0}",
            userId.toLowerCase()));
  }

  @Data
  static public class SessionResponse {
    private String user;
    private String role;
    private String token;
    private String message;

    public SessionResponse(String message, String user, String role, String token) {
      this.message = message;
      this.user = user;
      this.role = role;
      this.token = token;
    }
    
    public SessionResponse(String message, String user, String role) {
      this.message = message;
      this.user = user;
      this.role = role;
    }

    public SessionResponse(String message) {
      this.message = message;
    }
  }
}
