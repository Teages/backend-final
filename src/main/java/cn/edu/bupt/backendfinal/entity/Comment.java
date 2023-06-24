package cn.edu.bupt.backendfinal.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("comments")
public class Comment {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String category;
  private Integer categoryId;
  private Integer ownerId;
  private Date createDate;
  private String content;
  private Boolean deleted = false;

  public Comment() { }

  public Comment(String category, Integer categoryId, Integer ownerId, Date createDate, String content) {
    this.category = category;
    this.categoryId = categoryId;
    this.ownerId = ownerId;
    this.createDate = createDate;
    this.content = content;
  }
}
