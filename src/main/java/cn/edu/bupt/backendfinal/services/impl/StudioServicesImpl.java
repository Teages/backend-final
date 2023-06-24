package cn.edu.bupt.backendfinal.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Service
public class StudioServicesImpl {
  @Autowired
  UserServiceImpl userService;

  public ResponseEntity<StudioResponse> upgradeHost(String token, String userId, HttpServletResponse response) {
    var admin = userService.whoami(token);
    if (admin == null || !admin.isAdmin()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new StudioResponse("You are not admin"));
    }

    var user = userService.findByUid(userId);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new StudioResponse(String.format("User %s not found", userId)));
    }

    if (user.isAdmin()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new StudioResponse(String.format("User %s is admin, can not downgrade", userId)));
    }

    user.upgradeHost();
    userService.updateById(user);
    return ResponseEntity.ok()
        .body(new StudioResponse(String.format("Upgraded user %s to host", userId)));
  }

  public ResponseEntity<StudioResponse> upgradeAdmin(String token, String userId, HttpServletResponse response) {
    var admin = userService.whoami(token);
    if (admin == null || !admin.isAdmin()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new StudioResponse("You are not admin"));
    }

    var user = userService.findByUid(userId);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new StudioResponse(String.format("User %s not found", userId)));
    }

    user.upgradeAdmin();
    userService.updateById(user);
    return ResponseEntity.ok()
        .body(new StudioResponse(String.format("Upgraded user %s to admin", userId)));
  }

  public ResponseEntity<StudioResponse> downgrade(String token, String userId, HttpServletResponse response) {
    var admin = userService.whoami(token);
    if (admin == null || !admin.isAdmin()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new StudioResponse("You are not admin"));
    }

    var user = userService.findByUid(userId);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new StudioResponse(String.format("User %s not found", userId)));
    }

    if (user.getName().equals(admin.getName())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new StudioResponse("You can not downgrade your self"));
    }

    user.downgrade();
    userService.updateById(user);
    return ResponseEntity.ok()
        .body(new StudioResponse(String.format("Downgraded user %s", userId)));

  }

  @Data
  static public class StudioResponse {
    private String message;

    public StudioResponse(String message) {
      this.message = message;
    }
  }
}
