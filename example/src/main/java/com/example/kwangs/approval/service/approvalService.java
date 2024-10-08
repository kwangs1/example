package com.example.kwangs.approval.service;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.example.kwangs.common.paging.SearchCriteria;
import com.example.kwangs.user.service.userVO;

public interface approvalService {
	//결재 작성
	void apprWrite(approvalVO approval)throws IOException;
	//결재대기
	List<approvalVO> apprWaitList(SearchCriteria scri);
	//결재진행
	List<approvalVO> SanctnProgrsList(SearchCriteria scri);
	//발송대기
	List<approvalVO>SndngWaitDocList(SearchCriteria scri);
	//발송현황
	List<approvalVO>SndngSttusDocList(SearchCriteria scri);
	//접수대기
	List<approvalVO>RceptWaitDocList(SearchCriteria scri);
	//수신반송
	List<approvalVO>ReceptReturnDocList(SearchCriteria scri);
	//문서함
	List<approvalVO>docFrame(SearchCriteria scri);
	//결재 상세보기
	approvalVO apprInfo(String appr_seq);
	//유저에 대한 부서 약어
	userVO getUserDeptInfo(Map<String, Object> res);
	//회수 시 문서 상태값 변경
	void RetireApprStatus(String appr_seq);
	//재기안 시 문서 상태값 변경
	void Resubmission(approvalVO approval) throws IOException;
	//문서함 문서 총갯수
	int totalDocCnt(SearchCriteria scri);
	//결재함[결재대기,진행] 문서 총 갯수
	int totalApprCnt(SearchCriteria scri);
	//결재함[발송대기] 문서 총 갯수
	int TotalSndngWaitCnt(SearchCriteria scri);
	//결재함[접수대기] 문서 총 갯수
	int TotalRceptWaitCnt(SearchCriteria scri);
	//결재함[수신반송] 문서 총 갯수
	int TotalRceptReturnDocCnt(SearchCriteria scri);
	//결재진행, 재기안 시 첨부파일 등록 및 삭제 시 카운트 업데이트
	void UpdAttachCnt(Map<String,Object>res);
	/*
	 * 문서발송
	 * DocSndng - 발송부서의 send테이블 insert
	 * ReceiveDeptIn = 수신부서
	 * UpdDocPostStatus = 발송부서 문서 발송상태값 수정
	*/
	void DocSend(sendVO send);
	void ReceiveDeptIn(sendVO send);
	void UpdDocPostStatus(Map<String ,Object> drafterRes);
	//상세보기에서의 접수를 해야할 문서인지 체크
	sendVO getSendInfo(Map<String,Object> send);
	//상세보기에서의 접수문서인지 체크
	sendVO getReceptInfo(Map<String,Object> send);
	//문서 접수시 기안부서 문서ID및 발송ID 가져오기
	sendVO getSendOrgApprId(String appr_seq);
	sendVO getSendId(Map<String,Object> res);
	//발송대기 문서 접수하기
	void RceptDocSang(approvalVO ap) throws IOException;
	/*
	 * 문서 삭제
	 */
	boolean DeleteDoc(String appr_seq);
	sendVO SndngSttusApprInfo(String sendid);
	void SendDocRecdocStatus(Map<String,Object> res);
	//수신반송
	void RecptDocReturn(String appr_seq,String deptid,String userid, String opinioncontent, Date regdate, String name)throws ParseException;
	//의견리스트
	List<opinionVO>DocOpinionList(String apprid);
	//의견추가
	void DocOpinionAdd(opinionVO op);
	//의견삭제
	void DocOpinionDel(Map<String,Object> res);
}
