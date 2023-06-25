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
import cn.edu.bupt.backendfinal.services.impl.OrderServiceImpl;
import cn.edu.bupt.backendfinal.services.impl.OrderServiceImpl.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "订单系统", description = "order")
public class OrderController {
  @Autowired
  OrderServiceImpl orderService;

  @GetMapping("/orders")
  @Operation(description = "查询全部订单")
  public ResponseEntity<List<OrderResponse>> getAllOrders(
      @RequestHeader(value = "Authorization", required = false) String auth) {
    return orderService.getAllOrders(Util.decodeAuth(auth));
  }

  @GetMapping("/orders/{orderId}")
  @Operation(description = "查询订单")
  @Parameters({
      @Parameter(name = "orderId", description = "订单 ID", required = true)
  })
  public ResponseEntity<OrderResponse> getOrder(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable Integer orderId) {
    return orderService.getOrder(Util.decodeAuth(auth), orderId);
  }

  @DeleteMapping("/orders/{orderId}")
  @Operation(description = "取消订单, 仅管理员和所有者可以操作")
  @Parameters({
      @Parameter(name = "orderId", description = "订单 ID", required = true)
  })
  public ResponseEntity<OrderResponse> cancelOrder(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable Integer orderId) {
    return orderService.cancelOrder(Util.decodeAuth(auth), orderId);
  }

  @PostMapping("/orders")
  @Operation(description = "新增订单")
  public ResponseEntity<OrderResponse> createOrder(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @RequestBody OrderServiceImpl.OrderRequest order) {
    return orderService.createOrder(Util.decodeAuth(auth), order);
  }
}
