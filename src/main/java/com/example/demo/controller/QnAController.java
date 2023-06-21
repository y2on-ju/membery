package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import com.example.demo.domain.*;
import com.example.demo.service.*;

@Controller
@RequestMapping("qna")
public class QnAController {

	@Autowired
	private QnAService service;
	
	@GetMapping("")
	public String list(Model model) {
		
		List<QnA> list = service.qnaList();
		model.addAttribute("qnaList", list);
		return "qna";
	}
	
	@GetMapping("/id/{writer}")
	public String qna(@PathVariable("writer") String writer, 
					  Model model,
					  Authentication authentication) {
		QnA qna = service.getQnA(writer, authentication);
		model.addAttribute("qna", qna);
		return "qnaforwhat";
	}
	
	@GetMapping("/modify/{id}")
	@PreAuthorize("isAuthenticated() and @customSecurityChecker.checkQnAWriter(authentication, #writer)")
	public String modifyForm(
							 @PathVariable("writer") String writer, Model model) {
		model.addAttribute("qna", service.getQnA(writer));
		return "qnaModify";
	}
	
	@PostMapping("/modify/{id}")
	@PreAuthorize("isAuthenticatied() and @customSecurityChecker.checkQnAWriter(authentication, #qna.writer)")
	public String modifyProcess(QnA qna, 
								RedirectAttributes rttr) {

		boolean ok = service.modify(qna);
		
		if(ok) {
			rttr.addFlashAttribute("message", qna.getId() + "번 QnA가 수정되었습니다.");
			return "redirect:/qna/id/" + qna.getId();
		} else {
			rttr.addFlashAttribute("message", qna.getId() + "번 QnA가 수정되지 않았습니다.");
			return "redirect:/qna/modify/" + qna.getId();		
		}
	}
	
	@PostMapping("remove")
	@PreAuthorize("isAuthenticated() and @customSecurityChecker.checkQnAWriter(authentication, #id)")
	public String remove(@RequestParam Integer id, RedirectAttributes rttr) {
		boolean ok = service.remove(id);
		
		if(ok) {
			rttr.addFlashAttribute("message", id + "번 QnA가 삭제되었습니다.");
			return "redirect:/qna";
		} else {
			return "redirect:/qna/id/" + id;
		}
	}
	
	@GetMapping("add")
	@PreAuthorize("isAuthenticated()")
	public void addForm(Model model, Authentication auth) {
		model.addAttribute("auth", auth);
	}
	
	@PostMapping("add")
	@PreAuthorize("isAuthenticated()")
	public String addProcess(QnA qna, RedirectAttributes rttr,
							Authentication auth) throws Exception{
		qna.setWriter(auth.getName());		
	    boolean ok = service.addQnA(qna);
		
		if(ok) {
			rttr.addFlashAttribute("message", qna.getId() + "번 QnA가 등록되었습니다.");
			return "redirect:/qna/id/" + qna.getId();
		} else {
			rttr.addFlashAttribute("message", "QnA 등록 중 문제가 발생하였습니다.");
			rttr.addFlashAttribute("qna", qna);
			return "redirect:/qna/add";
		}
	}

}