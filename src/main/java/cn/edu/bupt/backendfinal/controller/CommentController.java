package cn.edu.bupt.backendfinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.bupt.backendfinal.Util;
import cn.edu.bupt.backendfinal.services.impl.CommentServicesImpl;
import cn.edu.bupt.backendfinal.services.impl.CommentServicesImpl.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "评论系统", description = "comment")
public class CommentController {
  @Autowired
  CommentServicesImpl commentServices;

  @GetMapping("/comments/{commentId}")
  @Operation(description = "查询评论")
  @Parameters({
    @Parameter(name = "commentId", description = "评论 ID", required = true, example = "1")
  })
  public ResponseEntity<CommentResponse> getComment(
      @PathVariable Integer commentId) {
    return commentServices.getComment(commentId);
  }

  @PostMapping("/comments/{commentId}")
  @Operation(description = "回复评论")
  @Parameters({
    @Parameter(name = "token", description = "用户 token (由 cookie 提供)", required = true, example = "header.payload.signature"),
    @Parameter(name = "commentId", description = "评论 ID", required = true, example = "1")
  })
  public ResponseEntity<CommentResponse> createComment(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable Integer commentId,
      CommentServicesImpl.CommentRequest commentRequest) {
    return commentServices.createComment(Util.decodeAuth(auth), commentId, commentRequest);
  }

  @DeleteMapping("/comments/{commentId}")
  @Operation(description = "删除评论")
  @Parameters({
    @Parameter(name = "token", description = "用户 token (由 cookie 提供)", required = true, example = "header.payload.signature"),
    @Parameter(name = "commentId", description = "评论 ID", required = true, example = "1")
  })
  public ResponseEntity<CommentResponse> deleteComment(
      @RequestHeader(value = "Authorization", required = false) String auth,
      @PathVariable Integer commentId) {
    return commentServices.deleteComment(Util.decodeAuth(auth), commentId);
  }
}
