package cn.edu.bupt.backendfinal.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.edu.bupt.backendfinal.entity.Live;
import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.mapper.LiveMapper;
import cn.edu.bupt.backendfinal.services.LiveService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Service
public class LiveServiceImpl extends ServiceImpl<LiveMapper, Live> implements LiveService {
  @Autowired
  LiveMapper liveMapper;

  @Autowired
  UserServiceImpl userService;

  public ResponseEntity<List<LiveResponse>> getAllLives() {
    var lives = liveMapper.selectList(new QueryWrapper<Live>()
        .eq("deleted", false));
    var liveResponses = lives.stream()
        .map(this::getLiveBuilder)
        .toList();
    return ResponseEntity.ok(liveResponses);
  }

  public ResponseEntity<LiveResponse> createLive(
      String token,
      LiveRequest liveRequest) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new LiveResponse("You are not logged in"));
    }
    var live = new Live(liveRequest.getTitle(), user.getId());
    liveMapper.insert(live);
    return ResponseEntity.ok(getLiveBuilder(live));
  }

  public ResponseEntity<LiveResponse> deleteLive(String token, Integer id) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new LiveResponse("You are not logged in"));
    }
    var live = liveMapper.selectById(id);
    if (live == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          new LiveResponse("Live not found"));
    }
    if (!live.getOwnerId().equals(user.getId()) && !user.isAdmin()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
          new LiveResponse("Permission denied"));
    }
    live.setDeleted(true);
    liveMapper.updateById(live);
    return ResponseEntity.ok(getLiveBuilder(live));
  }

  private LiveResponse getLiveBuilder(Live live) {
    var owner = userService.getById(live.getOwnerId());
    return new LiveResponse(live.getId(), live.getTitle(), owner);
  }

  @Data
  static public class LiveRequest {
    @Schema(description = "直播标题", required = true, example = "这是直播标题")
    private String title;

    public LiveRequest() {
    }

    public LiveRequest(String title) {
      this.title = title;
    }
  }

  @Data
  static public class LiveResponse {
    private Integer id;
    private String title;
    private String owner;

    private String message;

    public LiveResponse() {
    }

    public LiveResponse(Integer id, String title, User owner) {
      this.id = id;
      this.title = title;
      this.owner = owner.getName();
    }

    public LiveResponse(String message) {
      this.message = message;
    }
  }
}
