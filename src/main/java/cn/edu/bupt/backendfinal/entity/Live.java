package cn.edu.bupt.backendfinal.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("lives")
public class Live {
  private Integer id;
  private String title;
  private Integer ownerId;
  private Boolean deleted = false;

  public Live() { }
  
  public Live(String title, Integer ownerId) {
    this.title = title;
    this.ownerId = ownerId;
  }
}
