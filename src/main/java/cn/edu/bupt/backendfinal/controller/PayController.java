package cn.edu.bupt.backendfinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.Util;
import cn.edu.bupt.backendfinal.services.impl.PayServiceImpl;
import cn.edu.bupt.backendfinal.services.impl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "支付系统", description = "pay")
public class PayController {
  @Autowired
  PayServiceImpl payService;

  @PostMapping("/pay/{orderId}")
  @Operation(description = "支付订单")
  @Parameters({
      @Parameter(name = "orderId", description = "订单 ID", required = true, example = "1")
  })
  public ResponseEntity<OrderServiceImpl.OrderResponse> pay(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable Integer orderId) {
    return payService.pay(Util.decodeAuth(auth), orderId);
  }
}
