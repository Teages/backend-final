package cn.edu.bupt.backendfinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.Util;
import cn.edu.bupt.backendfinal.services.impl.ProfileServiceImpl;
import cn.edu.bupt.backendfinal.services.impl.CommentServiceImpl.CommentResponse;
import cn.edu.bupt.backendfinal.services.impl.LiveServiceImpl.LiveResponse;
import cn.edu.bupt.backendfinal.services.impl.OrderServiceImpl.OrderResponse;
import cn.edu.bupt.backendfinal.services.impl.ProductServiceImpl.ProductResponse;
import cn.edu.bupt.backendfinal.services.impl.ProfileServiceImpl.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "用户信息", description = "profile")
public class ProfileController {
  @Autowired
  ProfileServiceImpl profileServices;

  @GetMapping("/profiles/{userUid}/products")
  @Operation(description = "获取用户的商品列表")
  @Parameters({
      @Parameter(name = "userUid", description = "用户 ID", required = true, example = "Teages")
  })
  public ResponseEntity<List<ProductResponse>> getProfileProducts(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable String userUid) {
    return profileServices.getProfileProduct(Util.decodeAuth(auth), userUid);
  }

  @GetMapping("/profiles/{userUid}/comments")
  @Operation(description = "获取用户的评论列表")
  @Parameters({
      @Parameter(name = "userUid", description = "用户 ID", required = true, example = "Teages")
  })
  public ResponseEntity<List<CommentResponse>> getProfileComments(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable String userUid) {
    return profileServices.getProfileComment(Util.decodeAuth(auth), userUid);
  }

  @GetMapping("/profiles/{userUid}/orders")
  @Operation(description = "获取用户的订单列表")
  @Parameters({
      @Parameter(name = "userUid", description = "用户 ID", required = true, example = "Teages")
  })
  public ResponseEntity<List<OrderResponse>> getProfileOrders(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable String userUid) {
    return profileServices.getProfileOrder(Util.decodeAuth(auth), userUid);
  }

  @GetMapping("/profiles/{userUid}/Live")
  @Operation(description = "获取用户的直播间列表")
  @Parameters({
      @Parameter(name = "userUid", description = "用户 ID", required = true, example = "Teages")
  })
  public ResponseEntity<List<LiveResponse>> getProfileLives(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable String userUid) {
    return profileServices.getProfileLive(Util.decodeAuth(auth), userUid);
  }

  @GetMapping("/profiles/{userUid}")
  @Operation(description = "获取用户的信息")
  @Parameters({
      @Parameter(name = "userUid", description = "用户 ID", required = true, example = "Teages")
  })
  public ResponseEntity<ProfileResponse> getProfileAll(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable String userUid) {
    return profileServices.getProfileAll(Util.decodeAuth(auth), userUid);
  }

  @GetMapping("/profile/products")
  @Operation(description = "获取自己的商品列表")
  public ResponseEntity<List<ProductResponse>> getMyProfileProducts(
      @RequestHeader(value = "Authorization", required = false) String auth) {
    return profileServices.getMyProfileProduct(Util.decodeAuth(auth));
  }

  @GetMapping("/profile/comments")
  @Operation(description = "获取自己的评论列表")
  public ResponseEntity<List<CommentResponse>> getMyProfileComments(
      @RequestHeader(value = "Authorization", required = false) String auth) {
    return profileServices.getMyProfileComment(Util.decodeAuth(auth));
  }

  @GetMapping("/profile/orders")
  @Operation(description = "获取自己的订单列表")
  public ResponseEntity<List<OrderResponse>> getMyProfileOrders(
      @RequestHeader(value = "Authorization", required = false) String auth) {
    return profileServices.getMyProfileOrder(Util.decodeAuth(auth));
  }

  @GetMapping("/profile/Live")
  @Operation(description = "获取自己的直播间列表")
  public ResponseEntity<List<LiveResponse>> getMyProfileLives(
      @RequestHeader(value = "Authorization", required = false) String auth) {
    return profileServices.getMyProfileLive(Util.decodeAuth(auth));
  }

  @GetMapping("/profile")
  @Operation(description = "获取自己的信息")
  public ResponseEntity<ProfileResponse> getMyProfileAll(
      @RequestHeader(value = "Authorization", required = false) String auth) {
    return profileServices.getMyProfileAll(Util.decodeAuth(auth));
  }

}
