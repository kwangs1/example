package com.example.kwangs.approval.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kwangs.approval.mapper.approvalMapper;
import com.example.kwangs.approval.service.approvalService;
import com.example.kwangs.approval.service.approvalVO;
import com.example.kwangs.user.service.userVO;


@Service
public class approvalServiceImpl implements approvalService{
	private final Logger log = LoggerFactory.getLogger(approvalServiceImpl.class);
	@Autowired
	private approvalMapper mapper;
	
	//문서 작성
	@Override
	public void apprWrite(approvalVO approval) {
		String abbr = approval.getDocregno();
		approval.setDocregno(abbr+"-@N");
		mapper.apprWrite(approval);
	}
	//결재대기
	@Override
	public List<approvalVO> apprWaitList(String id) {	
		return mapper.apprWaitList(id);
	}
	
	//결재진행
	@Override
	public List<approvalVO> SanctnProgrsList(String id) {	
		return mapper.SanctnProgrsList(id);
	}
	
	//상세보기
	@Override
	public approvalVO apprInfo(String appr_seq) {
		return mapper.apprInfo(appr_seq);
	}
	
	//유저에 대한 부서 약어
	@Override
	public userVO getUserDeptInfo(Map<String,Object>res) {
		return mapper.getUserDeptInfo(res);
	}
	
	//회수 시 문서 상태값 변경
	@Override
	public void RetireApprStatus(String appr_seq) {
		mapper.RetireApprStatus(appr_seq);
	}
	
	//재기안 시 문서 상태값 변경
	@Override
	public void Resubmission(approvalVO approval) {
		mapper.Resubmission(approval);
	}

}
