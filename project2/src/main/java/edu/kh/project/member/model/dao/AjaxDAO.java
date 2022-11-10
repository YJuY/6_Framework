package edu.kh.project.member.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository // DB연결하는 역할 + bean등록(bean : spring 프레임워크가 관리하는 객체) 
public class AjaxDAO {
	
	@Autowired // 같은 자료형이 bean으로 등록되어 있으면 자동으로 DI
	private SqlSessionTemplate sqlSession;
//	SqlSessionTemplate : 커넥션 + 마이바티스 + 스프링 TX 제어

	/** 이메일 중복 검사 DAO
	 * @param memberEmail
	 * @return result
	 */
	public int emailDupcheck(String memberEmail) {
		
		return sqlSession.selectOne("ajaxMapper.emailDupCheck", memberEmail);
	}
	
}

