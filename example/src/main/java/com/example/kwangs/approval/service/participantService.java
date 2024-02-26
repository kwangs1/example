package com.example.kwangs.approval.service;

import java.util.List;

import com.example.kwangs.approval.domain.participantVO;

public interface participantService {

	//일괄 결재 시 결재선 업데이트 
	void participantCheck(List<participantVO> participant);
	
	//일괄결재 시 결재선 정보 가져오기 위한 해당 문서의 결재선 정보 가져오는 부분
	List<participantVO> getParticipantInfo(String appr_seq);
}