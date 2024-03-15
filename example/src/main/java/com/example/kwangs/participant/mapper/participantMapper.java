package com.example.kwangs.participant.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.kwangs.participant.service.participantVO;

@Repository
public class participantMapper{
	private Logger log = LoggerFactory.getLogger(participantMapper.class.getName());
	
	@Autowired
	private SqlSession session;
	
	//결재 상신 전 결재선 지정
	public void ParticipantWrite(participantVO vo) {
		session.insert("mapper.participant.ParticipantWrite",vo);
	}
	
	//일괄 결재 
	public void  BulkAppr(Map<String, Object> params) {
		log.info("dao{} :"+params);
		session.update("mapper.participant.BulkAppr",params);
	}
	
	//일괄결재 시 결재선 정보 가져오기 위한 해당 문서의 결재선 정보 가져오는 부분
	public List<participantVO> getParticipantInfo(String appr_seq) {
		return session.selectList("mapper.participant.getParticipantInfo",appr_seq);
	}
	
	//결재 시 결재자들의 타입 값 변경
	public void updateNextApprovalType(Map<String, Object> params) {
		log.info("Length value..{} :" + params);
		session.update("mapper.participant.updateNextApprovalType",params);
	}
	
	//일괄 결재 시 해당 결재문서의 결재id 가져오기 위함
	public List<participantVO>getApprovalApprseq(String appr_seq){
		return session.selectList("mapper.participant.getApprovalApprseq",appr_seq);
	}
	
	//결재
	public void FlowAppr(Map<String,Object> res) {
		log.info("Mapper FlowAppr RecData {}"+res);
		session.update("mapper.participant.FlowAppr",res);
	}

	//일반 결재 시 상세보기에서의 결재선 정보 
	public participantVO pInfo(Map<String,Object> res) {
		return session.selectOne("mapper.participant.pInfo",res);
	}
	
	//기안자가 최종결재자인 경우 결재선상태 및 결재문서 상태 업데이트
	public void updateFLowType(Map<String,Object>res) {
		session.update("mapper.participant.updateFLowType",res);
	}
	
}
