package edu.kh.project.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.service.MyPageService;
import edu.kh.project.member.model.vo.Member;

// 클래스 레벨에 작성된 @RequestMapping
// -> 요청주소 중 앞에 공통된 부분을 작성하여
//    해당 유형의 요청을 모두 받아들인다고 알림
// #요청 주소가 member에 myPage의 요청이 있다면 다 여기로 가져와라
@RequestMapping("/member/myPage")

@SessionAttributes("loginMember") // 탈퇴 성공 시 로그아웃에 사용

@ControllerAdvice // bean 등록
public class MyPageController {
	
	@Autowired
	private MemberController mc;
	
	@Autowired
	private MyPageService service;
	
	// 내 정보 페이지 이동
//	@GetMapping("/member/myPage/info") 원래라면 이렇게 작성 하지만 위에 @RequestMapping에 적혀 있기 때문에 아래로 작성
	@GetMapping("/info") // 나머지 주소 작성
	public String info() {
		return "member/myPage-info";// #멤버페이지에 있는 마이페이지 인포로 이동하겠다.
	}
	// 내 정보 수정
	@PostMapping("/info")
	public String updateInfo(Member inputMember, 
							 String[] memberAddress,
							 @SessionAttribute("loginMember") Member loginMember, //어떤회원을 수정해야나?(Session에 있는 회원) 
							 // loginMember가 어더옴
							 RedirectAttributes ra
							 ) {
		
		// inputMember : 입력 받은 memberNickname / memberTel / memberAddres가 들어 있음(가공 필요)
		// memberAddress : 우편번호, 주소, 상세주소가 담긴 배열
		
		//@SessionAttribute("loginMember") Member loginMemeber 
		// -> session의 속성 중 "loginMember"를 키로 가지는 값을
		//    매개변수에 대입
		
	     // 기존 방법
	      //HttpSession session = req.getSession();
	      //Member loginMember = (Member)session.getAttribute("loginMember");
		
		// 1. 로그인된 회우너 정보에서 회원 번호를 얻어와 inputMember에 저장
		inputMember.setMemberNo(loginMember.getMemberNo());
		
		// 2. memberAddress의 값 따라 inputMember의 address 값 변경
		if(inputMember.getMemberAddress().equals(",,")){ // 주소 미작성
			inputMember.setMemberAddress(null);
		}else {
			String address = String.join(",,", memberAddress);
			inputMember.setMemberAddress(address);
		}
		
		//---여기까지 하면 값이 다담김
		// 회원 정보 수정 서비스 호출 후 결과 반환 받기
		int result = service.updateInfo(inputMember);
		
		String message = null;
		
		if(result > 0) {
			message = "회원 정보가 수정되었습니다.";
			
			// DB - session 동기화 작업
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(     inputMember.getMemberTel());
			loginMember.setMemberAddress( inputMember.getMemberAddress());
		}
		else {
			message = "회원 정보 수정 실패...";
		}
		
		ra.addFlashAttribute("message",message);
		
		return "redirect:info"; // 내 정보 페이지 재요청
	}
	
	// 비밀번호 변경 페이지 이동
	@GetMapping("/changePw")
	public String changePw() {
		return "member/myPage-changePw";
	}
	
	// 비밀번호 변경
	@PostMapping("/changePw")								// 얕은 복사
	public String changePw( @SessionAttribute("loginMember") Member loginMember,
				 		    //String currentPw, String newPw // 파라미터 각각 전달 받기
				 		     @RequestParam Map<String, Object> paramMap,// 파라미터를 다 얻어온다 -> K:V
							 RedirectAttributes ra
						 	) {
//		@RequestParam Map<String, Object> paramNap// 파라미터를 다 얻어온다 -> K:V
//		 - 모든 파라미터를 맵 형식으로 얻어와 저장
//		
//		add->list랑
		// 1. loginMember에서 회원 번호를 얻어와 paramMap에 추가
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		// 2. 서비스 호출 후 결과 반환 받기
		int result = service.changePw(paramMap);
		
		String path = null;
		String message = null;
		
		if(result > 0) { // 성공
			path = "info";
			message = "비밀번호가 변경되었습니다.";
			
		} else { // 실패
			path = "changePw";
			message = "현재 비밀번호가 일치하지 않습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:"+path;
	}
	
	
	// 회원 탈퇴 페이지 이동
	@GetMapping("/delete")
	public String memberDelete() {
		
		return "member/myPage-delete";
	}
	
	// 회원 탈퇴
	@PostMapping("/delete")
	public String memberDelete(
			@SessionAttribute("loginMember")Member loginMember,
			String memberPw,
			SessionStatus status, // 로그아웃시 사용
			RedirectAttributes ra
			) {
		
		// 서비스 호출 후 결과 반환 받기
		int result = service.memberDelete(loginMember.getMemberNo(), memberPw);
							// MEMBER_DEL_FL = 'N'으로 UPDATE
		
		String message = null;
		String path = null;
		
		if(result>0) { // 성공
			message = "탈퇴 되었습니다...";
			
			path = "/"; // 메인 페이지
			
			// 로그아웃 코드 추가
			status.setComplete();
//			mc.logout(status);
			
		}else { // 실패
			message = "비밀번호가 일치하지 않습니다.";
			
			path = "delete"; // 탈퇴 페이지
		}
		
		// message 전달 코드 작성
		ra.addFlashAttribute("message", message);
		
		return "redirect:"+path;
//		status.setComplete(); // 세션 무효화
		// -> 클래스 레벨에 작성된 
		// @SessionAttributes("Key") 에 작성된
		// key가 일치하는 값만 무효화
		
		// ex) session에서 "loginMember"를 없애야 한다.
		// == @SessionAttributes("loginMember")
		//    ...
		//    status.complete(); // "loginMember" 무효화
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
