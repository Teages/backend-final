package cn.edu.bupt.backendfinal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("cart_items")
public class CartItem {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer productId;
  private String title;
  private Integer price; // 以分为单位
  private String description;
  private Integer count;

  public CartItem() { }

  public CartItem(Integer productId, String title, Integer price, String description, Integer count) {
    this.productId = productId;
    this.title = title;
    this.price = price;
    this.description = description;
    this.count = count;
  }
}
