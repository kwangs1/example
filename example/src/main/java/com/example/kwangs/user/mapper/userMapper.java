package com.example.kwangs.user.mapper;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.kwangs.user.service.userVO;

@Repository
public class userMapper{
	@Autowired
	private SqlSession session;
	

	public void write(userVO user) {
		session.insert("mapper.user.write",user);
	}
	

	public int idcheck(String id) {
		return session.selectOne("mapper.user.idcheck",id);
	}
	

	public userVO login(userVO user) {
		return session.selectOne("mapper.user.login",user);
	}
	
	public userVO folderUseInfo(String id) {
		return session.selectOne("mapper.user.folderUseInfo",id);
	}
	
	public List<userVO> DeptUseInfo(String deptid){
		return session.selectList("mapper.user.DeptUseInfo",deptid);
	}
}
