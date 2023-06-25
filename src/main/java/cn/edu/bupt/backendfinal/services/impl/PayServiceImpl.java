package cn.edu.bupt.backendfinal.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cn.edu.bupt.backendfinal.services.impl.OrderServiceImpl.OrderResponse;

@Service
public class PayServiceImpl {
  @Autowired
  OrderServiceImpl orderService;

  @Autowired
  UserServiceImpl userService;

  public ResponseEntity<OrderResponse> pay(String token, Integer orderId) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new OrderResponse("You haven't login yet"));
    }
    var order = orderService.getById(orderId);
    if (order == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          new OrderResponse(String.format("Can not find order where id is %d", orderId)));
    }
    if (!order.getOwnerId().equals(user.getId())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new OrderResponse("You can't pay for other's order"));
    }
    if (order.getStatus().equals("paid")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new OrderResponse("You have already paid for this order"));
    }

    order.setStatus("paid");
    orderService.updateById(order);

    return orderService.getOrder(token, orderId);
  }
}
