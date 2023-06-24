package cn.edu.bupt.backendfinal.services.impl;

import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.mapper.UserMapper;
import cn.edu.bupt.backendfinal.services.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
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
import java.util.Date;

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
            "You haven't logged in yet",
            null,
            null));
  }

  public ResponseEntity<SessionResponse> login(String userId, String password, HttpServletResponse response) {
    var user = userMapper.selectOne(
        new QueryWrapper<User>().eq("name", userId));
    if (user != null && user.getPassword().equals(DigestUtil.sha256Hex(password))) {

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

      var cookie = new Cookie("token", token);
      // cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge(7 * 24 * 60 * 60);
      response.addCookie(cookie);

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

  public ResponseEntity<SessionResponse> logout(HttpServletResponse response) {
      var cookie = new Cookie("token", "");
      // cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge(0);
      response.addCookie(cookie);
      return ResponseEntity.ok()
        .body(new SessionResponse(
            "Cleaned token",
            null,
            null));
  }

  public ResponseEntity<SessionResponse> register(String userId, String password, HttpServletResponse response) {
    var user = userMapper.selectOne(
        new QueryWrapper<User>().eq("name", userId));
    if (user != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new SessionResponse(
              "The name have been used",
              null,
              null));
    }
    var newUser = new User(userId, DigestUtil.sha256Hex(password));
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
    JWTUtil.verify(token, signer);
    final var jwt = JWTUtil.parseToken(token);
    JWTValidator.of(jwt)
        .validateAlgorithm(signer)
        .validateDate(DateUtil.date());

    var userId = (String) jwt.getPayload("user");
    if (userId != null) {
      var user = userMapper.selectOne(
          new QueryWrapper<User>().eq("name", userId));
      return user;
    }
    return null;
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
