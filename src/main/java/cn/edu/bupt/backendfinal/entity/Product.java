package cn.edu.bupt.backendfinal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("products")
public class Product {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String title;
  private Integer ownerId;
  private Integer price; // 以分为单位
  private String description;
  private Integer stock;

  // Magic Code: https://github.com/baomidou/mybatis-plus/issues/1663
  public Product() { }

  public Product(String title, Integer ownerId, Integer price, String description, Integer stock) {
    this.title = title;
    this.ownerId = ownerId;
    this.price = price;
    this.description = description;
    this.stock = stock;
  }
}
