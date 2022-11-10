package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.member.model.service.AjaxService;

@Controller // 요청 -> 알맞은 서비스 -> 결과 반환 -> 알맞은 view 응답 제어 + bean 등록 (###중간 제어 역할)
public class AjaxController {
	
	@Autowired
	private AjaxService service;
	
	// 이메일 중복 검사
	@GetMapping("/emailDupCheck")
	
	@ResponseBody // 반환된 값울 jsp 경로가 아닌 값 자체로 인식
				  // -> 
	public int emailDupCheck(String memberEmail) {
			//  data: {"memberEmail" : memberEmail.value}
		
		//System.out.println(memberEmail);
		
		// 이메일 중복검사 서비스 호출
		int result = service.emailDupcheck(memberEmail);
		
		
		// 0
		// @ResponseBody 어노테이션 덕분에
		// result가 View Resolbver로 전달되지 않고
		// 호출됐던 ajax 함수로 반환됨
		return result;
		
	}
	
}
