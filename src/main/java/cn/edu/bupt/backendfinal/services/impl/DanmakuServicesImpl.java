package cn.edu.bupt.backendfinal.services.impl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cn.hutool.http.HttpStatus;
import jakarta.annotation.Resource;
import lombok.Data;

// 用于给用户发送文字信息 (弹幕生产者)
@Service
public class DanmakuServicesImpl {
  @Autowired
  UserServiceImpl userService;
  
  @Autowired
  LiveServiceImpl liveService;

  @Resource
  MessageServiceImpl messageService;
  
  /**
   * @param token
   * @param liveId
   * @param content
   * @return
   */
  public ResponseEntity<DanmakuResponse> addDanmaku(String token, Integer liveId, String content) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.HTTP_UNAUTHORIZED).body(
        new DanmakuResponse("You haven't logged in yet")
      );
    }
    var live = liveService.getById(liveId);
    if (live == null) {
      return ResponseEntity.status(HttpStatus.HTTP_BAD_REQUEST).body(
        new DanmakuResponse("Live not found")
      );
    }
    if (live.getDeleted()) {
      return ResponseEntity.status(HttpStatus.HTTP_BAD_REQUEST).body(
        new DanmakuResponse("Live has been deleted")
      );
    }

    // 将弹幕发送到 Redis
    messageService.produce(
      String.format("live:%s", liveId),
      new DanmakuData(content, user.getName()).toJson()
    );

    return ResponseEntity.ok(new DanmakuResponse(content, user.getName()));
  }

  @Data
  public static class DanmakuRequest {
    private String content;

    public DanmakuRequest(String content) {
      this.content = content;
    }

    public DanmakuRequest() {}
  }

  @Data
  public static class DanmakuResponse {
    private String message;
    private String content;
    private String owner;

    public DanmakuResponse(String message) {
      this.message = message;
    }

    public DanmakuResponse(String content, String owner) {
      this.content = content;
      this.owner = owner;
    }

    public DanmakuResponse() {}
  }

  @Data
  public static class DanmakuData implements Serializable {
    private String content;
    private String owner;
    private Date time;

    public DanmakuData(String content, String owner) {
      this.content = content;
      this.owner = owner;
      this.time = new Date();
    }

    public DanmakuData() {}

    public String toJson() {
      return String.format(
        "{\"content\": \"%s\", \"owner\": \"%s\", \"time\": \"%s\"}",
        content, owner, time
      );
    }
  }
}
