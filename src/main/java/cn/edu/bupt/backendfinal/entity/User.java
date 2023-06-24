package cn.edu.bupt.backendfinal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class User {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String name;
  private String password;
  private String role;

  public User(String name, String password) {
    this.name = name;
    this.password = password;
    this.role = "user";
  }
}
