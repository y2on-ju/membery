package com.example.demo.controller;

import java.util.*;

import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.domain.Comment;
import com.example.demo.service.CommentService;

@Controller
@RequestMapping("comment")
public class CommentController {
	
	
	  @Autowired 
	  private CommentService service;
	
	
	//댓글 리스트 불러오기
	@GetMapping("list")
	@ResponseBody
	public List<Comment> list(@RequestParam("myFeed") Integer feedId, Authentication authentication) {
		return service.list(feedId, authentication);
	}
	
	//댓글 추가
	@PostMapping("add")
	public ResponseEntity<Map<String, Object>> add(
			@RequestBody Comment comment, 
			Authentication authentication) {
		if(authentication == null) { 
			Map<String, Object> res = Map.of("message","로그인 후 댓글 작성 가능합니다.");
			return ResponseEntity.status(401).body(null);
		} else {
			Map<String, Object> res = service.add(comment, authentication); 
			return ResponseEntity.ok().body(res); 
		}
				 
	 }
	
	//댓글 삭제
	@DeleteMapping("id/{id}")
	@PreAuthorize("authenticated and @customSecurityChecker.checkCommentWriter(authentication, #id)")
	public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Integer id) { 
		Map<String, Object> res = service.delete(id);
		 return ResponseEntity.ok().body(res);
	}
	
	//댓글 수정시 수정폼에 기존 값 가져오기
	@GetMapping("id/{id}")
	@ResponseBody
	public Comment update(@PathVariable("id") Integer id) {
		return service.get(id);
	 }
	
	//댓글 수정
	@PutMapping("update")
	@PreAuthorize("authenticated and @customSecurityChecker.checkCommentWriter(authentication, #comment.id)")
	public ResponseEntity<Map<String, Object>> update(@RequestBody Comment comment) {
		Map<String, Object> res = service.update(comment);
		 return ResponseEntity.ok().body(res);
	 }
	
		
	
}
