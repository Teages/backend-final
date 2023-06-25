package cn.edu.bupt.backendfinal.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.edu.bupt.backendfinal.entity.Comment;
import cn.edu.bupt.backendfinal.entity.Live;
import cn.edu.bupt.backendfinal.entity.Order;
import cn.edu.bupt.backendfinal.entity.Product;
import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.services.impl.CommentServiceImpl.CommentResponse;
import cn.edu.bupt.backendfinal.services.impl.LiveServiceImpl.LiveResponse;
import cn.edu.bupt.backendfinal.services.impl.OrderServiceImpl.OrderResponse;
import cn.edu.bupt.backendfinal.services.impl.ProductServiceImpl.ProductResponse;
import lombok.Data;

@Service
public class ProfileServiceImpl {
  @Autowired
  UserServiceImpl userService;

  @Autowired
  OrderServiceImpl orderService;

  @Autowired
  ProductServiceImpl productService;

  @Autowired
  CommentServiceImpl commentService;

  @Autowired
  LiveServiceImpl liveService;

  public ResponseEntity<List<ProductServiceImpl.ProductResponse>> getMyProfileProduct(
      String token) {
    var user = userService.whoami(token);
    return getProfileProduct(token, user.getName());
  }

  public ResponseEntity<List<CommentServiceImpl.CommentResponse>> getMyProfileComment(
      String token) {
    var user = userService.whoami(token);
    return getProfileComment(token, user.getName());
  }

  public ResponseEntity<List<OrderServiceImpl.OrderResponse>> getMyProfileOrder(
      String token) {
    var user = userService.whoami(token);
    return getProfileOrder(token, user.getName());
  }

  public ResponseEntity<List<LiveServiceImpl.LiveResponse>> getMyProfileLive(
      String token) {
    var user = userService.whoami(token);
    return getProfileLive(token, user.getName());
  }

  public ResponseEntity<ProfileResponse> getMyProfileAll(String token) {
    var user = userService.whoami(token);
    return getProfileAll(token, user.getName());
  }

  public ResponseEntity<List<ProductServiceImpl.ProductResponse>> getProfileProduct(
      String token, String ownerUid) {
    var owner = userService.findByUid(ownerUid);
    return ResponseEntity.ok(getProducts(owner));
  }

  public ResponseEntity<List<CommentServiceImpl.CommentResponse>> getProfileComment(
      String token, String ownerUid) {
    var user = userService.whoami(token);
    var owner = userService.findByUid(ownerUid);
    return ResponseEntity.ok(getComments(user, owner));
  }

  public ResponseEntity<List<OrderServiceImpl.OrderResponse>> getProfileOrder(
      String token, String ownerUid) {
    var user = userService.whoami(token);
    var owner = userService.findByUid(ownerUid);
    return ResponseEntity.ok(getOrders(user, owner));
  }

  public ResponseEntity<List<LiveServiceImpl.LiveResponse>> getProfileLive(
      String token, String ownerUid) {
    var owner = userService.findByUid(ownerUid);
    return ResponseEntity.ok(getLives(owner));
  }

  public ResponseEntity<ProfileResponse> getProfileAll(String token, String ownerUid) {
    var user = userService.whoami(token);
    var owner = userService.findByUid(ownerUid);

    var products = getProducts(owner);
    var orders = getOrders(user, owner);
    var comments = getComments(user, owner);
    var lives = getLives(owner);

    return ResponseEntity.ok(new ProfileResponse(
        owner.getName(), products, orders, comments, lives));
  }

  private List<ProductResponse> getProducts(User owner) {
    if (owner == null || !owner.isHost(owner.getName())) {
      // 只返回主播的商品
      return null;
    }
    var productsData = productService.getBaseMapper().selectList(new QueryWrapper<Product>()
        .eq("owner_id", owner.getId()));
    var products = productsData.stream()
        .map(productData -> new ProductServiceImpl.ProductResponse(
            productData, owner))
        .toList();
    return products;
  }

  private List<CommentResponse> getComments(User user, User owner) {
    if (user == null || !user.getId().equals(owner.getId()) || !user.isAdmin()) {
      // 只能看自己的评论, 管理员可看所有评论
      return null;
    }
    var commentsData = commentService.getBaseMapper().selectList(new QueryWrapper<Comment>()
        .eq("owner_id", owner.getId()));
    var comments = commentsData.stream()
        .map(commentData -> new CommentServiceImpl.CommentResponse(
            commentData, owner))
        .toList();
    return comments;
  }

  private List<OrderResponse> getOrders(User user, User owner) {
    if (user == null || !user.getId().equals(owner.getId()) || !user.isAdmin()) {
      // 只能看自己的订单, 管理员可看所有订单
      return null;
    }
    var ordersData = orderService.getBaseMapper().selectList(new QueryWrapper<Order>()
        .eq("owner_id", owner.getId()));

    var orders = ordersData.stream()
        .map(orderData -> orderService.getOrderBuilder(orderData))
        .toList();
    return orders;
  }

  private List<LiveResponse> getLives(User owner) {
    if (owner == null || !owner.isHost(owner.getName())) {
      // 只返回主播的直播
      return null;
    }
    var livesData = liveService.getBaseMapper().selectList(new QueryWrapper<Live>()
        .eq("owner_id", owner.getId()));
    var lives = livesData.stream()
        .map(liveData -> liveService.getLiveBuilder(liveData))
        .toList();
    return lives;
  }

  @Data
  static public class ProfileResponse {
    private String name;
    private List<ProductServiceImpl.ProductResponse> products;
    private List<OrderServiceImpl.OrderResponse> orders;
    private List<CommentServiceImpl.CommentResponse> comments;
    private List<LiveServiceImpl.LiveResponse> lives;

    private String message;

    public ProfileResponse() {
    }

    public ProfileResponse(
        String name,
        List<ProductServiceImpl.ProductResponse> products,
        List<OrderServiceImpl.OrderResponse> orders,
        List<CommentServiceImpl.CommentResponse> comments,
        List<LiveServiceImpl.LiveResponse> lives) {
      this.name = name;
      this.products = products;
      this.orders = orders;
      this.comments = comments;
      this.lives = lives;
    }

    public ProfileResponse(String message) {
      this.message = message;
    }
  }

}
