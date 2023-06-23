package cn.edu.bupt.backendfinal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class User {
  private Integer id;
  private String name;
  private String password;
  private String role;
}
