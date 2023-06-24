package cn.edu.bupt.backendfinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.services.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class SessionController {
  @Autowired
  UserServiceImpl userService;

  @GetMapping("/session")
  public ResponseEntity<UserServiceImpl.SessionResponse> getSession(
    @CookieValue(name="token", required = false) String token
  ) {
    return userService.getSession(token);
  }

  @PostMapping("/session/register")
  public ResponseEntity<UserServiceImpl.SessionResponse> register(
      @RequestParam String user,
      @RequestParam String password,
      HttpServletResponse response) {
    return userService.register(user, password, response);
  }

  @PostMapping("/session/login")
  public ResponseEntity<UserServiceImpl.SessionResponse> login(
      @RequestParam String user,
      @RequestParam String password,
      HttpServletResponse response) {
    return userService.login(user, password, response);
  }

  @PostMapping("/session/logout")
  public ResponseEntity<UserServiceImpl.SessionResponse> logout(HttpServletResponse response) {
    return userService.logout(response);
  }
}
