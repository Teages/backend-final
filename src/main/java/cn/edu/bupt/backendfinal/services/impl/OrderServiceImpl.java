package cn.edu.bupt.backendfinal.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.edu.bupt.backendfinal.entity.CartItem;
import cn.edu.bupt.backendfinal.entity.Order;
import cn.edu.bupt.backendfinal.mapper.OrderMapper;
import cn.edu.bupt.backendfinal.services.OrderService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
  @Autowired
  OrderMapper orderMapper;

  @Autowired
  UserServiceImpl userService;

  @Autowired
  CartItemServiceImpl cartItemServices;

  @Autowired
  ProductServiceImpl productService;

  public ResponseEntity<List<OrderResponse>> getAllOrders(String token) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
    }
    var ordersData = orderMapper.selectList(
        new QueryWrapper<Order>()
        .eq("owner_id", user.getId())
        .orderByDesc("create_date")
    );
    var orders = new ArrayList<OrderResponse>();
    for (var orderData : ordersData) {
      var order = getOrderBuilder(orderData);
      orders.add(order);
    }
    return ResponseEntity.ok(orders);
  }

  public ResponseEntity<OrderResponse> createOrder(String token, OrderRequest orderData) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new OrderResponse("You haven't login yet"));
    }
    var order = new Order(
      user.getId(),
      new Date(),
      "pending",
      0,
      new ArrayList<Integer>()
    );

    // 检查库存
    var cartTemp = new ArrayList<CartItem>();
    for (var cartRequest : orderData.cart) {
      var product = productService.getById(cartRequest.getProductId());
      if (product.getStock() < cartRequest.getCount()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new OrderResponse(String.format("Product %s is out of stock", product.getTitle())));
      }
      var cart = new CartItem(
        cartRequest.productId,
        product.getTitle(),
        product.getPrice(),
        product.getDescription(),
        cartRequest.getCount()
      );
      cartTemp.add(cart);
    }

    // 扣除库存, 加入订单
    for (var cart : cartTemp) {
      cartItemServices.save(cart);
      var product = productService.getById(cart.getProductId());
      order.getCart().add(cart.getId());
      product.setStock(product.getStock() - cart.getCount());
      productService.updateById(product);
      order.setTotalPrice(order.getTotalPrice() + cart.getPrice() * cart.getCount());
    }
    
    save(order);
    return ResponseEntity.ok(getOrderBuilder(order));
  }

  public ResponseEntity<OrderResponse> getOrder(String token, Integer orderId) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new OrderResponse("You haven't login yet"));
    }
    var orderData = getById(orderId);
    if (orderData == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          new OrderResponse(String.format("Can not find order where id is %d", orderId)));
    }
    if (!user.isAdmin() && !(orderData.getOwnerId().equals(user.getId()))) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new OrderResponse("You don't have permission to access this order"));
    }
    return ResponseEntity.ok(getOrderBuilder(orderData));
  }

  public ResponseEntity<OrderResponse> cancelOrder(String token, Integer orderId) {
    var user = userService.whoami(token);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new OrderResponse("You haven't login yet"));
    }
    var orderData = getById(orderId);
    if (orderData == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          new OrderResponse(String.format("Can not find order where id is %d", orderId)));
    }
    if (!user.isAdmin() && !(orderData.getOwnerId().equals(user.getId()))) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          new OrderResponse("You don't have permission to access this order"));
    }
    if (orderData.getStatus().equals("canceled")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new OrderResponse("This order has been canceled"));
    }
    
    if (orderData.getStatus().equals("paid")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new OrderResponse("This order has been paid"));
    }

    orderData.setStatus("canceled");
    updateById(orderData);

    return getOrder(token, orderId);
  }

  private OrderResponse getOrderBuilder(Order order) {
    var orderResponse = new OrderResponse(order);
    var cartsId = order.getCart();
    for (var cartId : cartsId) {
      var cartData = cartItemServices.getById(cartId);
      var cart = new OrderResponse.CartResponse(cartData);
      orderResponse.getCart().add(cart);
    }
    return orderResponse;
  }

  @Data
  @Schema(description = "订单请求")
  public static class OrderRequest{
    @Schema(description = "购物车", required = true, example = "[{\"productId\": 1, \"count\": 10}]")
    private List<CartRequest> cart;

    public OrderRequest() { }

    public OrderRequest(List<CartRequest> cart) {
      this.cart = cart;
    }

    @Data
    public static class CartRequest {
      @Schema(description = "商品 ID", required = true, example = "1")
      private Integer productId;
      @Schema(description = "购买数量", required = true, example = "10")
      private Integer count;

      public CartRequest() { }

      public CartRequest(Integer productId, Integer count) {
        this.productId = productId;
        this.count = count;
      }
    }
  }

  @Data
  public static class OrderResponse {
    private Integer id;
    private Date createDate;
    private String status;
    private Integer totalPrice;
    private List<CartResponse> cart;

    private String message;

    public OrderResponse() { }

    public OrderResponse(String message) {
      this.message = message;
    }

    public OrderResponse(Order order) {
      this.id = order.getId();
      this.createDate = order.getCreateDate();
      this.status = order.getStatus();
      this.totalPrice = order.getTotalPrice();
      this.cart = new ArrayList<CartResponse>();
    }

    public OrderResponse(Integer id, Date createDate, String status, Integer totalPrice, List<CartResponse> cart) {
      this.id = id;
      this.createDate = createDate;
      this.status = status;
      this.totalPrice = totalPrice;
      this.cart = cart;
    }

    @Data
    public static class CartResponse {
      private Integer id;
      private Integer productId;
      private String title;
      private Integer price;
      private String description;
      private Integer count;

      public CartResponse() { }

      public CartResponse(CartItem cart) {
        this.id = cart.getId();
        this.productId = cart.getProductId();
        this.title = cart.getTitle();
        this.price = cart.getPrice();
        this.description = cart.getDescription();
        this.count = cart.getCount();
      }

      public CartResponse(Integer id, Integer productId, String title, Integer price, String description, Integer count) {
        this.id = id;
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.count = count;
      }
    }
  }
}
