package cn.edu.bupt.backendfinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.services.impl.StudioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Tag(name = "权限管理", description = "Studio")
public class StudioController {
  @Autowired
  StudioServiceImpl studioServices;
  
  @PostMapping("/studio/upgrade/host")
  @Operation(description = "将用户的权限提升为主播")
  @Parameters({
    @Parameter(name = "user", description = "用户 ID", required = true)
  })
  public ResponseEntity<StudioServiceImpl.StudioResponse> upgradeHost(
    @CookieValue(name="token", required = false) String token,
    @RequestParam String user,
    HttpServletResponse response
  ) {
    return studioServices.upgradeHost(token, user, response);
  }

  @PostMapping("/studio/upgrade/admin")
  @Operation(description = "将用户的权限提升为管理员")
  @Parameters({
    @Parameter(name = "user", description = "用户 ID", required = true)
  })
  public ResponseEntity<StudioServiceImpl.StudioResponse> upgradeAdmin(
    @CookieValue(name="token", required = false) String token,
    @RequestParam String user,
    HttpServletResponse response
  ) {
    return studioServices.upgradeAdmin(token, user, response);
  }

  @PostMapping("/studio/downgrade")
  @Operation(description = "将用户的权限降级为普通用户")
  @Parameters({
    @Parameter(name = "user", description = "用户 ID", required = true)
  })
  public ResponseEntity<StudioServiceImpl.StudioResponse> downgrade(
    @CookieValue(name="token", required = false) String token,
    @RequestParam String user,
    HttpServletResponse response
  ) {
    return studioServices.downgrade(token, user, response);
  }
}
