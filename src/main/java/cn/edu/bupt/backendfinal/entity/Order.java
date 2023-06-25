package cn.edu.bupt.backendfinal.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("orders")
public class Order {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer ownerId;
  private Date createDate;
  private String status;
  private Integer totalPrice;
  @TableField(typeHandler = cn.edu.bupt.backendfinal.handler.ListArrayTypeHandler.class)
  private List<Integer> cart;

  public Order() { }

  public Order(Integer ownerId, Date createDate, String status, Integer totalPrice, List<Integer> cart) {
    this.ownerId = ownerId;
    this.createDate = createDate;
    this.status = status;
    this.totalPrice = totalPrice;
    this.cart = cart;
  }
}
