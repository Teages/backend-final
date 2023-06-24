package cn.edu.bupt.backendfinal.services.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.edu.bupt.backendfinal.entity.Product;
import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.mapper.ProductMapper;
import cn.edu.bupt.backendfinal.services.ProductServices;
import lombok.Data;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductServices {
  @Autowired
  ProductMapper productMapper;

  @Autowired
  UserServiceImpl userService;

  public List<ProductResponse> getAllProducts() {
    var products = productMapper.selectList(
        new QueryWrapper<Product>());
    var list = new ArrayList<ProductResponse>();
    for (var product : products) {
      var owner = userService.getById(product.getOwnerId());
      var res = new ProductResponse(product, owner);
      list.add(res);
    }
    return list;
  }

  public ResponseEntity<ProductResponse> createProducts(
      String token,
      ProductRequest productData) {
    var owner = userService.whoami(token);
    var product = new Product(
        productData.getTitle(),
        owner.getId(),
        productData.getPrice(),
        productData.getDescription(),
        productData.getStock());
    productMapper.insert(product);
    return getProduct(product.getId());
  }

  public ResponseEntity<ProductResponse> getProduct(
      @PathVariable Integer productId) {
    var product = getById(productId);
    if (product == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          ProductResponse.message(String.format("Can not find product where id is %d", productId)));
    }
    var owner = userService.getById(product.getOwnerId());
    return ResponseEntity.ok().body(new ProductResponse(product, owner));
  }

  public ResponseEntity<ProductResponse> updateProduct(
      @CookieValue(name = "token", required = false) String token,
      @PathVariable Integer productId,
      ProductRequest productData) {
    var user = userService.whoami(token);
    var product = getById(productId);
    if (product == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          ProductResponse.message(String.format("Can not find product where id is %d", productId)));
    }
    var owner = userService.getById(product.getOwnerId());
    if (!user.isHost(owner.getName())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          ProductResponse.message("You have no permission to edit"));
    }

    // 更新传入的字段, 忽略传入的空值
    BeanUtils.copyProperties(productData, product, getNullPropertyNames(productData));
    updateById(product);

    return getProduct(productId);
  }

  public ResponseEntity<ProductResponse> removeProduct(
      @CookieValue(name = "token", required = false) String token,
      @PathVariable Integer productId
  ) {
    var user = userService.whoami(token);
    var product = getById(productId);
    if (product == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          ProductResponse.message(String.format("Can not find product where id is %d", productId)));
    }
    var owner = userService.getById(product.getOwnerId());
    if (!user.isHost(owner.getName())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          ProductResponse.message("You have no permission to remove"));
    }

    productMapper.deleteById(product);

    return ResponseEntity.ok().body(new ProductResponse(product, owner, "Successfully removed"));
  }

  private static String[] getNullPropertyNames(Object source) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(source);
    PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
    return Stream.of(propertyDescriptors)
        .map(PropertyDescriptor::getName)
        .filter(propertyName -> beanWrapper.getPropertyValue(propertyName) == null)
        .toArray(String[]::new);
  }

  @Data
  static public class ProductResponse {
    private Integer id;
    private String title;
    private OwnerResponse owner;
    private Integer price; // 以分为单位
    private String description;
    private Integer stock;

    private String message;

    public ProductResponse() {
    }

    public ProductResponse(Product product, User owner) {
      this.id = product.getId();
      this.title = product.getTitle();
      this.owner = new OwnerResponse(owner);
      this.price = product.getPrice();
      this.description = product.getDescription();
      this.stock = product.getStock();
    }

    public ProductResponse(Product product, User owner, String message) {
      this.id = product.getId();
      this.title = product.getTitle();
      this.owner = new OwnerResponse(owner);
      this.price = product.getPrice();
      this.description = product.getDescription();
      this.stock = product.getStock();
      this.message = message;
    }

    static public ProductResponse message(String message) {
      var ans = new ProductResponse();
      ans.message = message;
      return ans;
    }

    @Data
    static public class OwnerResponse {
      private Integer id;
      private String name;

      public OwnerResponse(User owner) {
        this.id = owner.getId();
        this.name = owner.getName();
      }
    }
  }

  @Data
  static public class ProductRequest {
    private String title;
    private Integer price; // 以分为单位
    private String description;
    private Integer stock;
  }
}
