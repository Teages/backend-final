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
import cn.edu.bupt.backendfinal.services.impl.StudioServicesImpl.StudioResponse;

@RestController
public class ProductController {
  @Autowired
  ProductServiceImpl productService;

  @GetMapping("/products")
  public List<ProductResponse> getAllProducts() {
    return productService.getAllProducts();
  }

  @PostMapping("/products")
  public ResponseEntity<ProductResponse> createProducts(
      @CookieValue(name = "token", required = false) String token,
      @RequestBody ProductServiceImpl.ProductRequest product
  ) {
    return productService.createProducts(token, product);
  }

  @GetMapping("/products/{productId}")
  public ResponseEntity<ProductResponse> getProduct(
      @PathVariable Integer productId) {
    return productService.getProduct(productId);
  }

  @PutMapping("/products/{productId}")
  public ResponseEntity<ProductResponse> updateProduct(
      @CookieValue(name = "token", required = false) String token,
      @PathVariable Integer productId,
      @RequestBody ProductServiceImpl.ProductRequest product) {
    return productService.updateProduct(token, productId, product);
  }

  @DeleteMapping("/products/{productId}")
  public ResponseEntity<ProductResponse> removeProduct(
      @CookieValue(name = "token", required = false) String token,
      @PathVariable Integer productId) {
    return productService.removeProduct(token, productId);
  }

  @PutMapping("/products/{productId}/comments")
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
