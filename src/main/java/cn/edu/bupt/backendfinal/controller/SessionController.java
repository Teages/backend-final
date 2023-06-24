package cn.edu.bupt.backendfinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.services.impl.UserServiceImpl;
import cn.edu.bupt.backendfinal.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Tag(name = "登录管理", description = "session")
public class SessionController {
  @Autowired
  UserServiceImpl userService;

  @GetMapping("/session")
  @Operation(description = "登录状态查询")
  public ResponseEntity<UserServiceImpl.SessionResponse> getSession(
    @RequestHeader(value = "Authorization", required = false) String auth
  ) {
    return userService.getSession(Util.decodeAuth(auth));
  }

  @PostMapping("/session/register")
  @Operation(description = "注册新用户")
  @Parameters({
    @Parameter(name = "user", description = "新用户 ID", required = true),
    @Parameter(name = "password", description = "新用户密码", required = true)
  })
  public ResponseEntity<UserServiceImpl.SessionResponse> register(
      @RequestParam String user,
      @RequestParam String password,
      HttpServletResponse response) {
    return userService.register(user, password, response);
  }

  @PostMapping("/session/login")
  @Operation(description = "用户登录")
  @Parameters({
    @Parameter(name = "user", description = "用户 ID", required = true),
    @Parameter(name = "password", description = "用户密码", required = true)
  })
  public ResponseEntity<UserServiceImpl.SessionResponse> login(
      @RequestParam String user,
      @RequestParam String password,
      HttpServletResponse response) {
    return userService.login(user, password, response);
  }

  @PostMapping("/session/logout")
  @Operation(description = "登出")
  public ResponseEntity<UserServiceImpl.SessionResponse> logout(HttpServletResponse response) {
    return userService.logout(response);
  }
}
