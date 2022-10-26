package edu.kh.project.member.model.dao;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.project.member.model.vo.Member;

@Repository // 퍼시스턴스 레이어, 영속성(###프로그램을 꺼도 유지 한다)을 (파일, DB) 가진 클래스 + bean 등록
public class MemberDAO {

	
	// DBCP + 마이바티스 이용 객체 DI(의존성 주입)
	@Autowired
	private SqlSessionTemplate sqlSession;
	
//	pdf 6번으로 Mybatis 세팅
	/** 로그인 DAO
	 * @param memberEmail
	 * @return loginMember
	 */
	public Member login(String memberEmail) {
		
		//	   SqlSession.selectOne("매퍼이름.태그id", SQL 작성 시 필요한 값);
		return sqlSession.selectOne("memberMapper.login", memberEmail);
	}

	/** 회원 가입 DAO
	 * @param inputMember
	 * @return result
	 */
	public int signUp(Member inputMember) {//memberMapper매퍼이름.태그id
		return sqlSession.insert("memberMapper.signUp", inputMember);
	}

}
