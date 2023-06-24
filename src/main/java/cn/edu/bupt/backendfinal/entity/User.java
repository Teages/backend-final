package cn.edu.bupt.backendfinal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.hutool.crypto.digest.DigestUtil;
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
    this.password = DigestUtil.sha256Hex(password);
    this.role = "user";
  }

  public boolean verify(String password) {
    return this.password.equals(DigestUtil.sha256Hex(password));
  }

  public void upgradeHost() {
    this.role = "host";
  }
  public void upgradeAdmin() {
    this.role = "admin";
  }
  public void downgrade() {
    this.role = "user";
  }

  public boolean isAdmin() {
    return this.role.equals("admin");
  }
  public boolean isHost(String expectName) {
    return (
      this.role.equals("host") && this.name.equals(expectName)
      || this.isAdmin()
    );
  }
}
