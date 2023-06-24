package cn.edu.bupt.backendfinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.services.impl.StudioServicesImpl;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class StudioController {
  @Autowired
  StudioServicesImpl studioServices;
  
  @PostMapping("/studio/upgrade/host")
  public ResponseEntity<StudioServicesImpl.StudioResponse> upgradeHost(
    @CookieValue(name="token", required = false) String token,
    @RequestParam String user,
    HttpServletResponse response
  ) {
    return studioServices.upgradeHost(token, user, response);
  }

  @PostMapping("/studio/upgrade/admin")
  public ResponseEntity<StudioServicesImpl.StudioResponse> upgradeAdmin(
    @CookieValue(name="token", required = false) String token,
    @RequestParam String user,
    HttpServletResponse response
  ) {
    return studioServices.upgradeAdmin(token, user, response);
  }

  @PostMapping("/studio/downgrade")
  public ResponseEntity<StudioServicesImpl.StudioResponse> downgrade(
    @CookieValue(name="token", required = false) String token,
    @RequestParam String user,
    HttpServletResponse response
  ) {
    return studioServices.downgrade(token, user, response);
  }
}
