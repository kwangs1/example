package com.example.kwangs.dept.mapper;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.kwangs.dept.service.deptVO;
import com.example.kwangs.user.service.userVO;

@Repository
public class deptMapper {

	@Autowired
	private SqlSession session;
	
	//목록
	public List<deptVO> list(){
		return session.selectList("mapper.dept.list");
	}
	//부서생성
	public void write(deptVO dept) {
	  session.insert("mapper.dept.write",dept);
	}
	//하위부서생성
	public void subDept(deptVO dept) {
		session.insert("mapper.dept.subDept",dept);
	}
	//상세보기
	public deptVO info(String deptid) {
		return session.selectOne("mapper.dept.info",deptid);
	}
	//회원가입시 불러올 부서목록
	public List<deptVO>joinUseDept(){
		return session.selectList("mapper.dept.joinUseDept");
	}
	//결재선 정보 가져올 부서 및 유저목록
	public List<deptVO> flowUseInfo(){
		return session.selectList("mapper.dept.flowUseInfo");
	}
	public List<userVO> userList(String deptid){
		return session.selectList("mapper.dept.userList",deptid);
	}
}
