package cn.edu.bupt.backendfinal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.edu.bupt.backendfinal.services.impl.MessageServiceImpl;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

// 用于给用户接收文字信息 (弹幕消费者)
@Component
@ServerEndpoint("/live/{liveId}/danmaku")
public class DanmakuController {
  
  private static MessageServiceImpl messageService;
  @Autowired
  public void getMessageService(MessageServiceImpl messageService) {
    DanmakuController.messageService = messageService;
  }

  private static AtomicInteger onlineCount = new AtomicInteger(0);
  private static Map<Integer, List<DanmakuController>> danmakuControllerMap = new HashMap<>();
  private Session session;
  private Integer liveId;

  @OnOpen
  public void onOpen(Session session,
      @PathParam("liveId") Integer liveId) {
    this.session = session;
    this.liveId = liveId;
    if (danmakuControllerMap.containsKey(liveId)) {
      danmakuControllerMap.get(liveId).add(this);
    } else {
      danmakuControllerMap.put(liveId, List.of(this));
    }
    addOnlineCount();
    new Thread(() -> {
      while (true) {
        try {
          sendMessage(messageService.consume(String.format("live:%s", liveId)));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  @OnClose
  public void onClose() {
    subOnlineCount();
    danmakuControllerMap.get(liveId).remove(this);
  }

  @OnMessage
  public void onMessage(String message, Session session) {
    // Ignore
  }

  @OnError
  public void onError(Session session, Throwable error) {
    error.printStackTrace();
  }

  public void sendMessage(String message) throws IOException {
    this.session.getBasicRemote().sendText(message);
  }

  public static synchronized void sendDanmaku(Integer liveId, String message) {
    if (danmakuControllerMap.containsKey(liveId)) {
      danmakuControllerMap.get(liveId).forEach(controller -> {
        try {
          controller.sendMessage(message);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
  }

  public static synchronized AtomicInteger getOnlineCount() {
    return onlineCount;
  }

  public static synchronized void addOnlineCount() {
    DanmakuController.onlineCount.getAndIncrement();
  }

  public static synchronized void subOnlineCount() {
    DanmakuController.onlineCount.getAndDecrement();
  }
}