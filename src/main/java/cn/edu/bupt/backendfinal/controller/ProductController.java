package cn.edu.bupt.backendfinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.services.impl.ProductServiceImpl;
import cn.edu.bupt.backendfinal.services.impl.ProductServiceImpl.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "商品系统", description = "product")
public class ProductController {
  @Autowired
  ProductServiceImpl productService;

  @GetMapping("/products")
  @Operation(description = "查询全部商品")
  public List<ProductResponse> getAllProducts() {
    return productService.getAllProducts();
  }

  @PostMapping("/products")
  @Operation(description = "新增商品, 仅管理员和主播可以操作")
  public ResponseEntity<ProductResponse> createProducts(
      @CookieValue(name = "token", required = false) String token,
      @RequestBody ProductServiceImpl.ProductRequest product
  ) {
    return productService.createProducts(token, product);
  }

  @GetMapping("/products/{productId}")
  @Operation(description = "查询商品")
  @Parameters({
    @Parameter(name = "productId", description = "商品 ID", required = true)
  })
  public ResponseEntity<ProductResponse> getProduct(
      @PathVariable Integer productId) {
    return productService.getProduct(productId);
  }

  @PutMapping("/products/{productId}")
  @Operation(description = "更新商品, 仅管理员和所有者主播可以操作")
  @Parameters({
    @Parameter(name = "productId", description = "商品 ID", required = true)
  })
  public ResponseEntity<ProductResponse> updateProduct(
      @CookieValue(name = "token", required = false) String token,
      @PathVariable Integer productId,
      @RequestBody ProductServiceImpl.ProductRequest product) {
    return productService.updateProduct(token, productId, product);
  }

  @DeleteMapping("/products/{productId}")
  @Operation(description = "删除商品, 仅管理员和所有者主播可以操作")
  @Parameters({
    @Parameter(name = "productId", description = "商品 ID", required = true)
  })
  public ResponseEntity<ProductResponse> removeProduct(
      @CookieValue(name = "token", required = false) String token,
      @PathVariable Integer productId) {
    return productService.removeProduct(token, productId);
  }

  @PostMapping("/products/{productId}/comments")
  public String getComment(
      @PathVariable Integer productId) {
    // TODO: comments
    return "Hello World";
  }

  @DeleteMapping("/products/{productId}/comments")
  public String addComment(
      @PathVariable Integer productId) {
    // TODO: comments
    return "Hello World";
  }
}
