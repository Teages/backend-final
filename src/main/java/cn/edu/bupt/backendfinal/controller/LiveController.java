package cn.edu.bupt.backendfinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.Util;
import cn.edu.bupt.backendfinal.services.impl.LiveServiceImpl;
import cn.edu.bupt.backendfinal.services.impl.LiveServiceImpl.LiveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "直播系统", description = "live")
public class LiveController {
  @Autowired
  LiveServiceImpl liveServices;

  @GetMapping("/lives")
  @Operation(description = "查询全部直播")
  public ResponseEntity<List<LiveResponse>> getLives() {
    return liveServices.getAllLives();
  }

  @PostMapping("/lives")
  @Operation(description = "创建直播")
  public ResponseEntity<LiveResponse> createLive(
      @RequestHeader(value = "Authorization", required = false) String auth, 
      @RequestBody LiveServiceImpl.LiveRequest liveRequest
  ) {
    return liveServices.createLive(Util.decodeAuth(auth), liveRequest);
  }

  @GetMapping("/lives/{liveId}")
  @Operation(description = "查询直播")
  @Parameters({
      @Parameter(name = "liveId", description = "直播 ID", required = true, example = "1")
  })
  public ResponseEntity<LiveResponse> getLive(
      @PathVariable Integer liveId
  ) {
    return liveServices.getLive(liveId);
  }

  @DeleteMapping("/lives/{liveId}")
  @Operation(description = "关闭直播")
  @Parameters({
      @Parameter(name = "liveId", description = "直播 ID", required = true, example = "1")
  })
  public ResponseEntity<LiveResponse> closeLive(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable Integer liveId
  ) {
    return liveServices.deleteLive(Util.decodeAuth(auth), liveId);
  }

}
