package cn.edu.bupt.backendfinal.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.edu.bupt.backendfinal.entity.Comment;
import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.mapper.CommentMapper;
import cn.edu.bupt.backendfinal.services.CommentServices;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Service
public class CommentServicesImpl extends ServiceImpl<CommentMapper, Comment> implements CommentServices {
  @Autowired
  CommentMapper commentMapper;

  @Autowired
  UserServiceImpl userService;

  public ResponseEntity<CommentResponse> getComment(Integer id) {
    var ans = getCommentBuilder(commentMapper.selectById(id));
    if (ans == null) {
      return ResponseEntity.status(Response.SC_NOT_FOUND).body(
          new CommentResponse("Comment not found"));
    }
    return ResponseEntity.ok(ans);
  }

  public ResponseEntity<CommentResponse> createComment(
      String token,
      Integer id,
      CommentRequest commentRequest) {
    var user = userService.whoami(token);
    return ResponseEntity.ok(
        createCommentBuilder(user, "comment", id, commentRequest.getContent()));
  }

  public ResponseEntity<CommentResponse> deleteComment(
      String token,
      Integer id) {
    var user = userService.whoami(token);
    var comment = commentMapper.selectById(id);
    if (comment == null) {
      return ResponseEntity.status(Response.SC_NOT_FOUND).body(
          new CommentResponse("Comment not found"));
    }
    if (!(comment.getOwnerId().equals(user.getId()) || user.isAdmin())) {
      return ResponseEntity.status(Response.SC_FORBIDDEN).body(
          new CommentResponse("Permission denied"));
    }

    comment.setDeleted(true);
    commentMapper.updateById(comment);

    return ResponseEntity.ok(
        getCommentBuilder(comment));
  }

  public CommentResponse createCommentBuilder(
      User owner,
      String category,
      Integer categoryId,
      String content) {
    var comment = new Comment(
        category,
        categoryId,
        owner.getId(),
        new Date(),
        content);
    commentMapper.insert(comment);
    return getCommentBuilder(comment);
  }

  public List<CommentResponse> getAllCommnetBuilder(
      String category,
      Integer categoryId) {
    var comments = commentMapper.selectList(
        new QueryWrapper<Comment>()
            .eq("category", category)
            .eq("category_id", categoryId)
            .orderByDesc("create_date"));
    var ans = new ArrayList<CommentResponse>();
    for (var comment : comments) {
      ans.add(getCommentBuilder(comment));
    }
    return ans;
  }

  private CommentResponse getCommentBuilder(Comment commentData) {
    if (commentData == null) {
      return null;
    }
    var owner = userService.getById(commentData.getOwnerId());

    var replies = new ArrayList<CommentResponse>();
    // Warn: 可能被递归攻击, 用户回复时要展开回复树为单层
    var repliesData = commentMapper.selectList(
        new QueryWrapper<Comment>()
            .eq("category", "comment")
            .eq("category_id", commentData.getId())
            .ne("id", commentData.getId())
            .orderByDesc("create_date"));
    for (var replyData : repliesData) {
      replies.add(getCommentBuilder(replyData));
    }

    return new CommentResponse(commentData, owner, replies);
  }

  @Data
  static public class CommentResponse {
    private Integer id;
    private String category;
    private Integer categoryId;
    private String owner;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;
    private String content;
    private List<CommentResponse> replies;
    private String message;

    public CommentResponse() {
    }

    public CommentResponse(Comment comment, User owner, List<CommentResponse> repliesData) {
      this.id = comment.getId();
      this.category = comment.getCategory();
      this.categoryId = comment.getCategoryId();
      this.owner = owner.getName();
      this.createDate = comment.getCreateDate();
      this.content = comment.getDeleted() ? null : comment.getContent();
      this.replies = repliesData;
    }

    public CommentResponse(String message) {
      this.message = message;
    }
  }

  @Data
  @Schema(description = "评论请求")
  static public class CommentRequest {
    
    @Schema(description = "评论内容", required = true, example = "这是一条正常的评论")
    private String content;

    public CommentRequest() {
    }

    public CommentRequest(String content) {
      this.content = content;
    }
  }
}
