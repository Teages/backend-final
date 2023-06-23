package cn.edu.bupt.backendfinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.services.impl.UserServiceImpl;

@RestController
public class SessionController {
  @Autowired
  public UserServiceImpl userService;

  @GetMapping("/session")
  public String getSession() {
    return "/session";
  }

  @PostMapping("/session/register")
  public String register() {
    return "/session";
  }

  @PostMapping("/session/login")
  public ResponseEntity<UserServiceImpl.SessionResponse> login(
      @RequestParam String user,
      @RequestParam String password) {
    return userService.login(user, password);
  }

  @DeleteMapping("/session")
  public String logout() {
    return "/session";
  }
}
