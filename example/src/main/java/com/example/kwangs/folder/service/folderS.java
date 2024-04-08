package com.example.kwangs.folder.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kwangs.apprfolder.mapper.apprfolderD;
import com.example.kwangs.apprfolder.service.apprfolderVO;
import com.example.kwangs.bizunit.mapper.bizunitMapper;
import com.example.kwangs.bizunit.service.bizunitVO;
import com.example.kwangs.dept.mapper.deptMapper;
import com.example.kwangs.dept.service.deptVO;
import com.example.kwangs.folder.mapper.folderD;
import com.example.kwangs.user.mapper.userMapper;
import com.example.kwangs.user.service.userVO;

@Service
public class folderS implements IfolderS{

	@Autowired
	private folderD mapper;
	@Autowired
	private deptMapper deptMapper;
	@Autowired
	private apprfolderD apprfolderMapper;
	@Autowired
	private userMapper userMapper;
	@Autowired
	private bizunitMapper bizMapper;
	
	//부서 별 폴더 생성(단위과제 작성 시 폴더 테이블 인서트 부분도 포함]
	@Override
	public void deptAllFolderAdd(folderVO fd) {
		List<deptVO> departments = deptMapper.findAll();
		for(deptVO dept : departments) {
			String ownerid = dept.getDeptid();
			
			fd.setOwnerid(ownerid);
			
			
		}
		mapper.deptAllFolderAdd(fd);
	}
	//문서함 사이드 메뉴 부서 폴더 가져올 거 
	@Override
	public List<folderVO>docfldrSidebar(String ownerid){
		return mapper.docfldrSidebar(ownerid);
	}
	//결재선 지정 시 폴더목록 불러올거
	@Override
	public List<folderVO> DeptFolderList(String ownerid){
		List<folderVO> DeptApprFolderList = mapper.DeptFolderList(ownerid);
		for(folderVO fd : DeptApprFolderList) {
			
			List<apprfolderVO> fds = apprfolderMapper.DeptApprFolderList(ownerid);
			fd.setApprfolders(fds);
		}
		return DeptApprFolderList;
	}
	//문서 목록
	@Override
	public List<folderVO> list(){
		return mapper.list();
	}
	//문서 상세보기
	@Override
	public folderVO info(String fldrid) {
		return mapper.info(fldrid);
	}
	//기록물철 작성
	@Override
	public void apprfolderAdd(folderVO fd,String userid) throws Exception {
		fd.setApplid(7020);
		mapper.apprfolderAdd(fd);
		
		userVO folderUseInfo = userMapper.folderUseInfo(userid);
		bizunitVO biz = bizMapper.bInfo(fd.getFldrname());
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		String strYear = Integer.toString(year);
		
		apprfolderVO af = new apprfolderVO();
		af.setFldrid(fd.getFldrid());
		af.setFldrinfoyear(strYear);
		af.setBizunitcd(biz.getBizunitcd());
		af.setProcdeptid(biz.getProcdeptid());
		af.setKeepingperiod(biz.getKeepperiod());
		af.setProdyear(fd.getYear());
		af.setEndyear(fd.getEndyear());
		af.setFldrmanagerid(folderUseInfo.getId());
		af.setFldrmanagername(folderUseInfo.getName());
		
		apprfolderMapper.write(af);
	}
}
