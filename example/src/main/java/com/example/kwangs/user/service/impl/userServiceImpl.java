package com.example.kwangs.user.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kwangs.folder.mapper.folderMapper;
import com.example.kwangs.folder.service.folderVO;
import com.example.kwangs.user.mapper.userMapper;
import com.example.kwangs.user.service.userService;
import com.example.kwangs.user.service.userVO;

@Service
public class userServiceImpl implements userService{
	private static Logger log = LoggerFactory.getLogger(userService.class);
	@Autowired
	private userMapper mapper;
	@Autowired
	private folderMapper fMapper;
	
	@Override
	public void write(userVO user) {
		mapper.write(user);
	}
	
	@Override
	public int idcheck(String id) {
		return mapper.idcheck(id);
	}
	
	@Override
	public userVO login(userVO user) {
		log.debug("login success={}",user);
		return mapper.login(user);
	}
	
	public void JoinUseFolder(userVO user) {
		folderVO in1000 = new folderVO();
		in1000.setFldrname("결재");
		in1000.setOwnertype("2");
		in1000.setOwnerid(user.getId());
		in1000.setAppltype("2");
		in1000.setApplid(1000);
		in1000.setYear("0000");
		in1000.setEndyear("9999");
		fMapper.deptAllFolderAdd(in1000);
		
		folderVO in2010 = new folderVO();
		in2010.setFldrname("결재대기");
		in2010.setParfldrid(in1000.getFldrid());
		in2010.setParfldrname(in1000.getFldrname());
		in2010.setFldrdepth(in1000.getFldrdepth()+1);
		in2010.setOwnertype("2");
		in2010.setOwnerid(user.getId());
		in2010.setAppltype("2");
		in2010.setApplid(2010);
		in2010.setYear("0000");
		in2010.setEndyear("9999");
		fMapper.subFolderAdd(in2010);
		
		folderVO in2020 = new folderVO();
		in2020.setFldrname("결재진행");
		in2020.setParfldrid(in1000.getFldrid());
		in2020.setParfldrname(in1000.getFldrname());
		in2020.setFldrdepth(in1000.getFldrdepth()+1);
		in2020.setOwnertype("2");
		in2020.setOwnerid(user.getId());
		in2020.setAppltype("2");
		in2020.setApplid(2020);
		in2020.setYear("0000");
		in2020.setEndyear("9999");
		fMapper.subFolderAdd(in2020);
		
		folderVO in6021 = new folderVO();
		in6021.setFldrname("기안한문서");
		in6021.setParfldrid(in1000.getFldrid());
		in6021.setParfldrname(in1000.getFldrname());
		in6021.setFldrdepth(in1000.getFldrdepth()+1);
		in6021.setOwnertype("2");
		in6021.setOwnerid(user.getId());
		in6021.setAppltype("2");
		in6021.setApplid(6021);
		in6021.setYear("0000");
		in6021.setEndyear("9999");
		fMapper.subFolderAdd(in6021);
		
		folderVO in6022 = new folderVO();
		in6022.setFldrname("결재한문서");
		in6022.setParfldrid(in1000.getFldrid());
		in6022.setParfldrname(in1000.getFldrname());
		in6022.setFldrdepth(in1000.getFldrdepth()+1);
		in6022.setOwnertype("2");
		in6022.setOwnerid(user.getId());
		in6022.setAppltype("2");
		in6022.setApplid(6022);
		in6022.setYear("0000");
		in6022.setEndyear("9999");
		fMapper.subFolderAdd(in6022);
	}
	
}
