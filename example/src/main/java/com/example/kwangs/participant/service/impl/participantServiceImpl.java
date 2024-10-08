package com.example.kwangs.participant.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kwangs.approval.mapper.approvalMapper;
import com.example.kwangs.approval.service.DocumentNumberGenerator;
import com.example.kwangs.approval.service.action_log_sanc;
import com.example.kwangs.approval.service.approvalVO;
import com.example.kwangs.approval.service.opinionVO;
import com.example.kwangs.approval.service.sendVO;
import com.example.kwangs.common.paging.SearchCriteria;
import com.example.kwangs.dept.mapper.deptMapper;
import com.example.kwangs.dept.service.deptVO;
import com.example.kwangs.folder.mapper.folderMapper;
import com.example.kwangs.folder.service.fldrmbr2VO;
import com.example.kwangs.folder.service.fldrmbrVO;
import com.example.kwangs.folder.service.folderVO;
import com.example.kwangs.participant.mapper.participantMapper;
import com.example.kwangs.participant.service.participantService;
import com.example.kwangs.participant.service.participantVO;
import com.example.kwangs.user.mapper.userMapper;
import com.example.kwangs.user.service.userVO;

@Service
public class participantServiceImpl implements participantService{
	private Logger log = LoggerFactory.getLogger(participantServiceImpl.class.getName());
	
	@Autowired
	private participantMapper mapper;
	@Autowired
	private approvalMapper approvalMapper;
	@Autowired
	private DocumentNumberGenerator DocumentNumberGenerator;
	@Autowired
	private folderMapper folderMapper;
	@Autowired
	private deptMapper dMapper;
	@Autowired
	private userMapper uMapper;
	
	/*
	 * participant 내 기능 메서드
	 */
	//기안 시 기안자 다음 결재자의 결재대기 및 전 결재자에 대해 결재진행 폴더 생성
	public void IntermediateApprFldrmbr(String appr_seq) {
		//기안자 다음 결재자 결재대기,결재진행 폴더 생성
		List<participantVO> SignerInfo = mapper.getApprovalApprseq(appr_seq);
		for(int i=0; i < SignerInfo.size(); i++) {
			participantVO IntermediateApprFldrmbr  = SignerInfo.get(i);
			
			if(IntermediateApprFldrmbr.getApprovaltype() == 4 && i+1 == SignerInfo.size()) {
				folderVO ApprFldrmbr_2010 = folderMapper.ApprFldrmbr_2010(IntermediateApprFldrmbr.getSignerid());
				
				fldrmbrVO fm_2010 = new fldrmbrVO();
				fm_2010.setFldrid(ApprFldrmbr_2010.getFldrid());
				fm_2010.setFldrmbrid(IntermediateApprFldrmbr.getAppr_seq());
				fm_2010.setRegisterid(IntermediateApprFldrmbr.getSignerid());
				folderMapper.ApprFldrmbrInsert(fm_2010);
	
			}
		}
		for(participantVO pps :SignerInfo) {				
			folderVO ApprFldrmbr_2020 = folderMapper.ApprFldrmbr_2020(pps.getSignerid());
			
			Map<String,Object> check2020 = new HashMap<>();
			check2020.put("fldrmbrid", pps.getAppr_seq());
			check2020.put("registerid", pps.getSignerid());
			check2020.put("fldrid", ApprFldrmbr_2020.getFldrid());
			
			int checkFldrmbr_2020 = folderMapper.checkFldrmbr_2020(check2020);	
			
			if(checkFldrmbr_2020 == 0) {
				fldrmbrVO fm_2020 = new fldrmbrVO();
				fm_2020.setFldrid(ApprFldrmbr_2020.getFldrid());
				fm_2020.setFldrmbrid(pps.getAppr_seq());
				fm_2020.setRegisterid(pps.getSignerid());
				folderMapper.ApprFldrmbrInsert(fm_2020);			
			}			
		}
	}
	//결재 상신 시 결재선 테이블 관련 approvalType, approvalStatus 컬럼 값 셋팅 메서드
	public void approvalTypeAndStatus(List<participantVO> participant) {
	    boolean isFirst = true;

        // 기안자와 최종 결재자가 같은 경우
	    if(participant.size() == 1) {
	    	//리스트의 첫 번쨰 요소   
	        participantVO pVO = participant.get(0);     
	        //기안자가 최종결재자인 경우 결재선상태 및 결재문서 상태 업데이트
	        Map<String,Object> res = new HashMap<>();
	        res.put("appr_seq", pVO.getAppr_seq());
	        res.put("participant_seq", pVO.getParticipant_seq());
	        
	        mapper.updateFLowType(res);
	        //결재문서 상태값 변경
	        approvalMapper.ApprovalUpdateStatus(pVO.getAppr_seq());
	        ConCludeDocRegNo(pVO.getAppr_seq());
	    }
	    //그 외
	    else {
		    for(int i = 0; i < participant.size(); i++) {
		        participantVO pVO = participant.get(i);  
		        
		        // 기안자인 경우 2(결재완료)
		        if(i == 0) {       
			        Map<String,Object> res = new HashMap<>();
			        res.put("appr_seq", pVO.getAppr_seq());
			        res.put("participant_seq", pVO.getParticipant_seq());
			        
			        mapper.updateFLowType(res);
                	DocFldrmbr2Add(pVO.getAppr_seq());
		        }            
		        // 중간 결재자이면서 마지막 결재자인 경우 4(결재진행) , 4098 미결재
	            else if(i + 1 == participant.size() && isFirst) {
	                pVO.setApprovaltype(4);
	                pVO.setApprovalstatus(4098);
	            }
		        // 중간 결재자인 경우 4(결재진행) , 4098 미결재
		        else if(i + 1 < participant.size()) {
		            // 가장 앞 번호의 중간 결재자는 4, 나머지는 8
		            if(isFirst) {
		                pVO.setApprovaltype(4);
		                pVO.setApprovalstatus(4098);
		                isFirst = false;
		            } else {
		                pVO.setApprovaltype(8);
		                pVO.setApprovalstatus(4098);
		            }
		        }
		        // 마지막 결재자인 경우 8 (결재대기) ,4098 미결재
		        else if(i == participant.size() - 1) {
			        pVO.setApprovaltype(8);
			        pVO.setApprovalstatus(4098);	
		        }
		    }
	    }
	}
	//결재 이후 결재선 순번 재지정
	public void updateNextApprovalType(String appr_seq) {
	    log.info("Updating next approval type...");
	    //appr_seq를 통해 participantVO의 정보를 반복문을 통해 가져옴
	    List<participantVO> approvalLines = mapper.getApprovalApprseq(appr_seq);
	    for (int i = 0; i < approvalLines.size(); i++) {
	        //0~ 마지막 까지 
	        participantVO currentParticipant = approvalLines.get(i);
	        int line_seq = currentParticipant.getLine_seq();
	        
	        log.info("loop.... ing..");
	        log.info("participant user line_seq: {}", line_seq);       
	        // 첫 번째 결재자는 pass
	        if (line_seq == approvalLines.get(0).getLine_seq()) {
	            continue;
	        }           
	        log.info("checked appr_seq.."+appr_seq);
	        log.info("checked participant_seq.."+currentParticipant.getParticipant_seq());
	        log.info("checked currParticipant type.."+currentParticipant.getApprovaltype());
	        log.info("check size length.."+approvalLines.size());        
	        // 현재 결재자의 approvaltype이 결재를 완료하여 2가 된다
	        if (currentParticipant.getApprovaltype() == 2) {
	        	// 다음 결재자의 값을 가져오기 위한 배열의 값 +1
	            int nextIndex = i + 1;            
	            // 현재의 결재자가 결재완료 된 이후 그 뒤로 결재자가 있다면 반복문 실행
	            while (nextIndex < approvalLines.size()) {
	            	//그다음 결재자의 시퀀스를 가져오기 위해 nextIndex를 가져오고 그게에 대해 시퀀스를 가져온다.
	                participantVO nextParticipant = approvalLines.get(nextIndex);
	                String participant_seq = nextParticipant.getParticipant_seq();
	                int nextLine_seq = nextParticipant.getLine_seq();
	                
	                log.info("Next Line_seq.."+nextLine_seq);
	                log.info("NextIndex.."+nextIndex);
	                log.info("for loop check.."+participant_seq);
	                //전 결재자가 4 -> 2로 되고 그다음 결재자가 8이였을 때 4로 변경 후 업데이트
	                if (nextParticipant.getApprovaltype() == 8 && nextLine_seq == currentParticipant.getLine_seq() +1) {
	                    nextParticipant.setApprovaltype(4);
	                    log.info("Updated next approval type: {}", nextParticipant.getApprovaltype());
	                    
	                    Map<String, Object> params = new HashMap<>();
	                    params.put("appr_seq", nextParticipant.getAppr_seq());
	                    params.put("participant_seq", participant_seq);
	                    params.put("approvaltype", nextParticipant.getApprovaltype());   
	                    mapper.updateNextApprovalType(params);    
	                    //중간 결재자 결재 이후 그다음 결재자에 대한 결재멤버 폴더 등록
	    				folderVO ApprFldrmbr_2010 = folderMapper.ApprFldrmbr_2010(nextParticipant.getSignerid());		
	    				//해당 결재자 해당 문서에 폴더값 있나 없나 중복체크
	    				Map<String,Object> check2010 = new HashMap<>();
	    				check2010.put("fldrmbrid", nextParticipant.getAppr_seq());
	    				check2010.put("registerid", nextParticipant.getSignerid());
	    				check2010.put("fldrid", ApprFldrmbr_2010.getFldrid());
	    				
	    				int checkFldrmbr_2010 = folderMapper.checkFldrmbr_2010(check2010);
	    				
	    				if(checkFldrmbr_2010 == 0) {
		    				fldrmbrVO Fldrmbr_2010 = new fldrmbrVO();
		    				Fldrmbr_2010.setFldrid(ApprFldrmbr_2010.getFldrid());
		    				Fldrmbr_2010.setFldrmbrid(nextParticipant.getAppr_seq());
		    				Fldrmbr_2010.setRegisterid(nextParticipant.getSignerid());
							folderMapper.ApprFldrmbrInsert(Fldrmbr_2010);			
	    				}		
	                    break; // 업데이트 후 루프 종료
	                }             
	                nextIndex++;
	            }//end while
	             // 현재의 결재자가 결재완료 하였고, 그 결재자가 최종결재자 일 때      	
	            if(currentParticipant.getApprovaltype() == 2 && i == approvalLines.size() -1) {
                	approvalMapper.ApprovalUpdateStatus(appr_seq); //문서 상태변경
                	approvalVO ap = approvalMapper.apprInfo(appr_seq);
					if(ap.getDraftsrctype() != null) {
						log.info("접수문서 최종결재 ok");
						log.info("draftsrctype? "+ap.getDraftsrctype());
						sendVO send = approvalMapper.SndngSttusApprInfo(ap.getSendid());
						String setRecdoc = send.getRecdocstatus();
						setRecdoc = "8";
						Map<String,Object> res = new HashMap<>();
						res.put("sendid", send.getSendid());
						res.put("recdocstatus", setRecdoc);
						approvalMapper.SendDocRecdocStatus(res);	
					}
                	ConCludeDocRegNo(appr_seq); //문서번호 채번
                	DocFldrmbr2Add(appr_seq); //기안시 편철정한 폴더와 기록물등록대장에 같이 insert
                	SendDeptSosckUserDelFldr(appr_seq);
	            }
	        }//end if (currentParticipant.getApprovaltype() == 2)
	    }//end for
	}	
	//문서번호 체번
	public void ConCludeDocRegNo(String appr_seq) {
		//결재가 완료된 문서찾기
		List<approvalVO> approval = approvalMapper.getApprStatus(appr_seq);

		approvalVO ApprDocDeptInfo = approvalMapper.ApprDocDeptInfo(appr_seq);
		String deptid = ApprDocDeptInfo.getDrafterdeptid();
		
		deptVO DocDeptInfo = approvalMapper.DocDeptInfo(deptid);
		String abbreviation = DocDeptInfo.getAbbreviation();
		String deptcode = DocDeptInfo.getDeptcode();	
		for(approvalVO ap : approval) {
			if(ap.getStatus() == 256 && deptid.equals(DocDeptInfo.getDeptid())) {
				//년도 및 부서별 문서번호 셋팅 메서드 호출
				String docno = DocumentNumberGenerator.genearteDocumentNumber(deptid);
				log.info(ap.getAppr_seq()+" -> maxCurrSeq value{} "+docno);	
				log.info("Use Info" +"/"+ deptid +"/"+ abbreviation+"/"+docno);
				
				ap.setDocregno(abbreviation +docno);
				ap.setRegno(deptcode +docno);
				//문서번호 업데이트
				approvalMapper.ConCludeDocRegNo(ap);
			}
		}
	}	
	//최종 결재 이후 문서폴더멤버 테이블 같이 insert
	public void DocFldrmbr2Add(String appr_seq) {
		List<approvalVO> approval = approvalMapper.getApprStatus(appr_seq);							
		for(approvalVO ap : approval) {
			if(ap.getStatus() == 256) {
				//해당 기안기 편철에 insert
				fldrmbr2VO fm2 = new fldrmbr2VO();
				fm2.setFldrid(ap.getFolderid());
				fm2.setFldrmbrid(appr_seq);
				fm2.setIndexdate(ap.getApprovaldate());
				fm2.setRegistdate(ap.getApprovaldate());
				fm2.setRegisterid(ap.getDrafterid());
				folderMapper.DocFldrmbr2Add(fm2);
				//기록물 등록대장에 같이 들어가기
				fldrmbr2VO fm2_r = new fldrmbr2VO();
				folderVO fdDoc = folderMapper.DocFloder(ap.getDrafterdeptid());
				fm2_r.setFldrid(fdDoc.getFldrid());
				fm2_r.setFldrmbrid(appr_seq);
				fm2_r.setIndexdate(ap.getApprovaldate());
				fm2_r.setRegistdate(ap.getApprovaldate());
				fm2_r.setRegisterid(ap.getDrafterid());
				folderMapper.DocFldrmbr2Add(fm2_r);
				if(ap.getStatus() == 256 && ap.getReceivers() != null) {
					log.info("너는 발송 문서구나?");
					folderVO ApprFldrId = folderMapper.ApprFldrmbr_4030(ap.getDrafterid());
					log.info(ApprFldrId.getApplid()+" __ "+ApprFldrId.getFldrid());
					//수신처가 있는 문서일 시 발송대기 폴더 생성
					fldrmbrVO fm = new fldrmbrVO();
					fm.setFldrid(ApprFldrId.getFldrid());
					fm.setFldrmbrid(ap.getAppr_seq());
					fm.setRegisterid(ap.getDrafterid());
					folderMapper.ApprFldrmbrInsert(fm);
					log.info("후후 발송대기 넣었소.,");
				}
			}
		}
	}	
	//중간결재자들 결재 완료 및 최종 결재 완료 시 해당 문서에 대한 결재자들의 결재멤버 테이블에서의 결재진행,결재대기 값 삭제
	public void DeleteSignerApprFldrmbr(participantVO pp) {
		folderVO ApprFldrmbr_2010 = folderMapper.ApprFldrmbr_2010(pp.getSignerid());
		folderVO ApprFldrmbr_2020 = folderMapper.ApprFldrmbr_2020(pp.getSignerid());
		
		Map<String,Object> sendData_2010 = new HashMap<>();
		sendData_2010.put("fldrmbrid", pp.getAppr_seq());
		sendData_2010.put("registerid", pp.getSignerid());
		sendData_2010.put("fldrid", ApprFldrmbr_2010.getFldrid());
		
		Map<String,Object> sendData_2020 = new HashMap<>();
		sendData_2020.put("fldrmbrid", pp.getAppr_seq());
		sendData_2020.put("registerid", pp.getSignerid());
		sendData_2020.put("fldrid", ApprFldrmbr_2020.getFldrid());
		
		List<participantVO> Lines = mapper.getApprovalApprseq(pp.getAppr_seq());
		for(int i=0; i<Lines.size(); i++) {
			participantVO Signer = Lines.get(i);
			//각 중간결재자들의 결재 완료 시 결재대기 값 삭제
			if(i+1 < Lines.size() && Signer.getApprovaltype() == 2) {
				folderMapper.deleteApprFldrmbr_2010(sendData_2010);
			}
			//최종 결재자에 대한 결재멤버폴더(결재대기,결재진행) 삭제
			if(i == Lines.size()-1 && Signer.getApprovaltype() == 2) {
				sendData_2010.put("fldrmbrid", Signer.getAppr_seq());
				sendData_2010.put("registerid", Signer.getSignerid());
				sendData_2010.put("fldrid", ApprFldrmbr_2010.getFldrid());

				sendData_2020.put("fldrmbrid", Signer.getAppr_seq());
				sendData_2020.put("registerid", Signer.getSignerid());
				sendData_2020.put("fldrid", ApprFldrmbr_2020.getFldrid());
				folderMapper.deleteApprFldrmbr_2020(sendData_2020);
				folderMapper.deleteApprFldrmbr_2010(sendData_2010);	
				for(int j=0; j < i; j++) {
					participantVO Previous_Signer = Lines.get(j);
					//최종 결재자 이후 그 이전 결재자들에 대한 결재진행폴더값 삭제
					folderVO ApprFldrmbr_2020_ = folderMapper.ApprFldrmbr_2020(Previous_Signer.getSignerid());
					sendData_2020.put("fldrmbrid", Previous_Signer.getAppr_seq());
					sendData_2020.put("registerid", Previous_Signer.getSignerid());
					sendData_2020.put("fldrid", ApprFldrmbr_2020_.getFldrid());
					folderMapper.deleteApprFldrmbr_2020(sendData_2020);
				}
			}
		}
	}
//participant 내 기능 메서드 끝

	//문서 기안 시 결재선 등록
	@Override
	public void ParticipantWrite(List<participantVO> participants,String opinioncontent,Date credate){
		int line_seq = 1;
		
		String seqCurrval = approvalMapper.getLatestReceiptsSeq(); //결재 시퀀스 가져오기
		log.info("participant service appr_seq? "+seqCurrval);
		
		for (participantVO pVO : participants) {
			pVO.setAppr_seq(seqCurrval);
			pVO.setLine_seq(line_seq);// 기본값 1

			if(pVO.getStatus().equals("1000")) {
				pVO.setStatusname("기안");
			}else if(pVO.getStatus().equals("2000")){
				pVO.setStatusname("검토");
			}else if(pVO.getStatus().equals("3000")) {
				pVO.setStatusname("협조");
			}else if(pVO.getStatus().equals("4224")) {
				pVO.setStatusname("확인");
			}else if(pVO.getStatus().equals("4112")) {
				pVO.setStatusname("참조");
			}else {
				pVO.setStatusname("결재");
			}
			mapper.ParticipantWrite(pVO);
			line_seq++;// receitps_seq 별 사용자 번호 순차 증가
			approvalTypeAndStatus(participants);
			
			IntermediateApprFldrmbr(pVO.getAppr_seq());		
			if((pVO.getStatus().equals("1000") && opinioncontent != "") || (participants.size() == 1 && opinioncontent != "")) {
				log.info("gian User: "+pVO.getSignerid());
				log.info("gian User ParticipantID: "+pVO.getParticipant_seq());
				opinionVO op = new opinionVO();
				op.setOpinionid(pVO.getParticipant_seq());
				op.setOpiniontype("P1");
				op.setRegisterid(pVO.getSignerid());
				op.setCredate(credate);
				op.setOpinioncontent(opinioncontent);
				approvalMapper.DocOpinionAdd(op);
				log.info("DocOpinion Method OpinionId: "+op.getOpinionid());
				log.info("DocOpinion Method OpinionType: "+op.getOpiniontype());
				log.info("DocOpinion Method RegisterId: "+op.getRegisterid());
				log.info("DocOpinion Method RegistDate: "+op.getCredate());
				log.info("DocOpinion Method OpinionContent: "+op.getOpinioncontent());
			}	
		}
	}	
	//일괄 결재
	@Override
	public void BulkAppr(List<participantVO> participant) {
		for(participantVO pp : participant) {
			Map<String, Object> params = new HashMap<>();
			params.put("approvaltype", pp.getApprovaltype());
			params.put("approvalstatus", pp.getApprovalstatus());
			params.put("signerid", pp.getSignerid());
	        params.put("participant_seq", pp.getParticipant_seq());
			params.put("appr_seq", pp.getAppr_seq());
			mapper.BulkAppr(params);
			//결재선 차례 업데이트
			updateNextApprovalType(pp.getAppr_seq());
			
			//결재한 문서 & 기안한 문서 폴더 값 가져오기
			folderVO ApprFldrmbr_6021 = folderMapper.ApprFldrmbr_6021(pp.getSignerid()); //기안한 문서
			folderVO ApprFldrmbr_6022 = folderMapper.ApprFldrmbr_6022(pp.getSignerid()); //결재한문서
			//기안한문서
			Map<String,Object> check6021 = new HashMap<>();
			check6021.put("fldrid", ApprFldrmbr_6021.getFldrid());
			check6021.put("fldrmbrid", pp.getAppr_seq());
			check6021.put("registerid", pp.getSignerid());
			//결재한문서
			Map<String,Object> check6022 = new HashMap<>();
			check6022.put("fldrid", ApprFldrmbr_6022.getFldrid());
			check6022.put("fldrmbrid", pp.getAppr_seq());
			check6022.put("registerid", pp.getSignerid());
			//체크
			int checkAppr_6021 = folderMapper.checkFldrmbr_6021(check6021);
			int checkAppr_6022 = folderMapper.checkFldrmbr_6022(check6022);
			//접수문서 인지 아닌지
			approvalVO ap = approvalMapper.apprInfo(pp.getAppr_seq());
			if(ap.getSendid() == null) {
				log.info("접수 문서 아니네여..");
				if(checkAppr_6021 == 0 && checkAppr_6022 == 0) {
					//결재시 결재한문서 폴더 등록
					fldrmbrVO fm_6022 = new fldrmbrVO();
					fm_6022.setFldrid(ApprFldrmbr_6022.getFldrid());
					fm_6022.setFldrmbrid(pp.getAppr_seq());
					fm_6022.setRegisterid(pp.getSignerid());
					folderMapper.ApprFldrmbrInsert(fm_6022);
				}else if(checkAppr_6022 > 0) {
					log.info("해당 사용자는 이미 이전에 결재를 하였기에 폴더를 생성하지 않습니다.");
				}				
			}
			//최종 결재자 결재 시 해당 문서에 대한 결재멤버 테이블에서의 모든 결재자 결재진행,결재대기 삭제
			DeleteSignerApprFldrmbr(pp);	
		}
	}	
	//일괄결재 시 결재선 정보 가져오기 위한 해당 문서의 결재선 정보 가져오는 부분
	@Override
	public List<participantVO>  ApprWaitFLowInfo(SearchCriteria scri) {
		return mapper.ApprWaitFLowInfo(scri);
	}
	//회수 시 결재선 정보 가져오기 위한 해당 문서의 결재선 정보 가져오는 부분
	@Override
	public List<participantVO>  ApprProgrsFLowInfo(SearchCriteria scri) {
		return mapper.ApprProgrsFLowInfo(scri);
	}
	//발송대기 리스트에서 결재선 정보 가져오기
	@Override
	public List<participantVO> SndngWaitflowInfo(String appr_seq){
		return mapper.SndngWaitflowInfo(appr_seq);
	}
	//결재
	@Override
	public void FlowAppr(participantVO participant) {
		Map<String,Object> res = new HashMap<>();
		res.put("appr_seq", participant.getAppr_seq());
		res.put("participant_seq", participant.getParticipant_seq());
		res.put("approvaltype", participant.getApprovaltype());
		res.put("approvalstatus", participant.getApprovalstatus());
		res.put("signerid", participant.getSignerid());
		mapper.FlowAppr(res);
		
		updateNextApprovalType(participant.getAppr_seq());		
		//결재한 문서 & 기안한 문서 폴더 값 가져오기
		folderVO ApprFldrmbr_6021 = folderMapper.ApprFldrmbr_6021(participant.getSignerid()); //기안한 문서
		folderVO ApprFldrmbr_6022 = folderMapper.ApprFldrmbr_6022(participant.getSignerid()); //결재한문서
		//기안한문서
		Map<String,Object> check6021 = new HashMap<>();
		check6021.put("fldrid", ApprFldrmbr_6021.getFldrid());
		check6021.put("fldrmbrid", participant.getAppr_seq());
		check6021.put("registerid", participant.getSignerid());
		//결재한문서
		Map<String,Object> check6022 = new HashMap<>();
		check6022.put("fldrid", ApprFldrmbr_6022.getFldrid());
		check6022.put("fldrmbrid", participant.getAppr_seq());
		check6022.put("registerid", participant.getSignerid());
		//체크
		int checkAppr_6021 = folderMapper.checkFldrmbr_6021(check6021);
		int checkAppr_6022 = folderMapper.checkFldrmbr_6022(check6022);
		//접수문서 인지 아닌지
		approvalVO ap = approvalMapper.apprInfo(participant.getAppr_seq());
		if(ap.getSendid() == null) {
			log.info("접수 문서 아니네여..");
			if(checkAppr_6021 == 0 && checkAppr_6022 == 0) {
				//결재시 결재한문서 폴더 등록
				fldrmbrVO fm_6022 = new fldrmbrVO();
				fm_6022.setFldrid(ApprFldrmbr_6022.getFldrid());
				fm_6022.setFldrmbrid(participant.getAppr_seq());
				fm_6022.setRegisterid(participant.getSignerid());
				folderMapper.ApprFldrmbrInsert(fm_6022);
			}else if(checkAppr_6022 > 0) {
				log.info("해당 사용자는 이미 이전에 결재를 하였기에 폴더를 생성하지 않습니다.");
			}			
		}
		//최종 결재자 결재 시 해당 문서에 대한 결재멤버 테이블에서의 모든 결재자 결재진행,결재대기 삭제
		DeleteSignerApprFldrmbr(participant);	
	}
	//일반 결재 시 상세보기에서의 결재선 정보 
	@Override
	public participantVO pInfo(Map<String,Object> res) {
		return mapper.pInfo(res);
	}
	//회수
	@Override
	public void RetireAppr(Map<String,Object> res) {
		mapper.RetireAppr(res);
	}
	//재기안 시 해당 문서에 대한 결재자 정보 가져오기
	@Override
	public List<participantVO> getRe_pInfo(String appr_seq){
		return mapper.getRe_pInfo(appr_seq);
	}
	//접수문서에 대한 결재자 정보 가져오기
	@Override
	public List<participantVO> getRcept_pInfo(String appr_seq){
		return mapper.getRcept_pInfo(appr_seq);
	}
	//재기안 시 결재자 상태값 업데이트
	@Override
	public void ResubmissionFlowStatusUpd(List<participantVO> participant) {
		for(participantVO pp : participant) {
			if(pp.getApprovaltype() == 1024) {
				pp.setApprovaltype(2);
			}else if(pp.getApprovaltype() != 1024){
				pp.setApprovaltype(8);
			}
			mapper.ResubmissionFlowStatusUpd(pp);
		}
	}
	//재기안 시 결재선 새로 추가
	@Override
	public void ResubmissionParticipantWrite(List<participantVO> participant,String id) {
		int seq = 1;
		
		for(participantVO pvo : participant) {
			String appr_seq = pvo.getAppr_seq();
			pvo.setAppr_seq(appr_seq);
			pvo.setLine_seq(seq);
			
			if(pvo.getStatus().equals("1000")) {
				pvo.setStatusname("기안");
			}else if(pvo.getStatus().equals("2000")){
				pvo.setStatusname("검토");
			}else if(pvo.getStatus().equals("3000")) {
				pvo.setStatusname("협조");
			}else if(pvo.getStatus().equals("4224")) {
				pvo.setStatusname("확인");
			}else if(pvo.getStatus().equals("4112")) {
				pvo.setStatusname("참조");
			}else {
				pvo.setStatusname("결재");
			}
			log.info("ReGian flow Data Apprid "+pvo.getAppr_seq());
			log.info("ReGian flow Data Signerid "+pvo.getSignerid());
			log.info("ReGian flow Data ApprovalStatus "+pvo.getApprovalstatus());
			log.info("ReGian flow Data ApprovalType "+pvo.getApprovaltype());
			mapper.ResubmissionParticipantWrite(pvo);
			seq++;
			approvalTypeAndStatus(participant);
		}
	}
	//문서 삭제 시 결재선 삭제
	@Override
	public void deleteFlowInfo(String appr_seq) {
		mapper.deleteFlowInfo(appr_seq);
		log.info("flow data... delete");
	}
	
	//접수문서 최종결재 이후, 발송부서 소속인원 발송현황 테이블에서의 문서데이터 삭제
		public void SendDeptSosckUserDelFldr(String apprid) {
			approvalVO ap = approvalMapper.apprInfo(apprid);
			log.info("-SendDeptSosckUserDelFldr method start-");
			if(ap.getSendid() != null) {
				sendVO sendDocInfo= approvalMapper.SndngSttusApprInfo(ap.getSendid());
				log.info("해당문서의 SENDID? "+sendDocInfo.getSendid()+" / 기안부서 APPRID? "+sendDocInfo.getAppr_seq());
				
				List<sendVO> sttusList = approvalMapper.sttusList(sendDocInfo.getParsendid());
				Map<String ,Object> documentCounts = new HashMap<>(); //문서별 카운트 관리
				
				for(sendVO receiverDocInfo : sttusList) {			
					String currentApprid = receiverDocInfo.getAppr_seq(); //send 테이블에서 apprid값(기안부서 문서ID) 저장
					documentCounts.putIfAbsent(currentApprid, 0); //발송부서 문서id별로 맵에 담아 저장
					int currentCount = (int) documentCounts.get(currentApprid); // .putIfAbsent에 담은 value값 설정
					
					if(receiverDocInfo.getAppr_seq().equals(ap.getOrgappr_seq()) && receiverDocInfo.getRecdocstatus().equals("8") &&
							ap.getDraftsrctype().equals("1") && ap.getStatus() == 256) {
						currentCount++;
						documentCounts.put(currentApprid, currentCount); //위 조건에 합당하면, key,value별로 카운트 증가한 값 맵에 담기
						receiverDocInfo.setCount(currentCount); // 증가된 count를 SndngVO 인스턴스 변수에 저장
			            log.info("수신처 결재 완료 카운트: " + receiverDocInfo.getCount() + " / 전체 수신처 개수: " + sttusList.size());
			            
						if(receiverDocInfo.getCount() == sttusList.size()) {
							log.info("모든 수신처에서 최종 결재가 완료됨.");
							for(sendVO receiverDept : sttusList) {
								List<deptVO> SndngSttus = dMapper.UserSosck(receiverDept.getSenddeptid());
								
								for(deptVO dept : SndngSttus) {
									List<userVO> users = dMapper.userList(receiverDocInfo.getSenddeptid());
									dept.setUsers(users);
									
									if(dept.getUsers() != null) {
										for(userVO user : dept.getUsers()) {
											String userid = user.getId();
											log.info("발송 부서의 소속 멤버: "+userid);
											List<folderVO> getFolderInfo_4050 = folderMapper.ApprFldrmbr_4050(userid);		
											for(folderVO sendDeptUserFd : getFolderInfo_4050) {
												Map<String,Object> Del4050 = new HashMap<>();
												Del4050.put("fldrmbrid", receiverDocInfo.getAppr_seq());
												Del4050.put("registerid", userid);
												Del4050.put("fldrid", sendDeptUserFd.getFldrid());
												folderMapper.deleteApprFldrmbr_4050(Del4050);
									            log.info("발송부서 소속 인원의 폴더 데이터 삭제 완료");
											}//end for getFolderInfo_4050
										}
									}else {
										log.error("Dept Sosck Users Null...");
									}
								}//end for deptVO
							}//end for sttusList
						}//end if totalCount == sttusList.size();
					}//end if ..1
				}//end for sttusList
				//결재 완료 시 수신처 소속인원 접수대기 폴데테이블에서 해당 문서 데이터 삭제
				if(ap.getDraftsrctype().equals("1") && ap.getStatus() == 256) {
					log.info("----? "+ap.getAppr_seq());
					sendVO sendReceiptApprId = approvalMapper.getSendOrgApprId(ap.getSendid());
					log.info("---!? "+sendReceiptApprId.getReceiverid());
					List<userVO> DeptUseInfo = uMapper.DeptUseInfo(sendReceiptApprId.getReceiverid());
					log.info("해당 수신처 접수문서 최종결재가 났으니, 수신부서 소속 인원 접수대기폴데어서 해당 데이터 삭제");
					for(userVO use : DeptUseInfo) {
						log.info("Name? "+use.getName()+" ,"+"Sosck? "+use.getDeptname());
						//접수대기폴더 삭제
						Map<String,Object> sendData_5010 = new HashMap<>();
						folderVO fldrmbr5010 = folderMapper.ApprFldrmbr_5010(use.getId());
						sendData_5010.put("fldrmbrid", sendReceiptApprId.getSendid());
						sendData_5010.put("registerid", use.getId());
						sendData_5010.put("fldrid", fldrmbr5010.getFldrid());
						folderMapper.deleteApprFldrmbr_5010(sendData_5010);
					}
					action_log_sanc sanc = new action_log_sanc();
					sanc.setUserid(ap.getFinalapprover());
					sanc.setAction_code("S2");
					sanc.setData1(ap.getTitle());
					sanc.setData2(ap.getAppr_seq());
					log.info("수신처에서의 문서 접수 후 최종결재완료 후 action_log_sanc 테이블 INSERT[접수부서]");
					log.info(sanc.getUserid()+" / "+sanc.getAction_code()+" / "+sanc.getData1()+" / "+sanc.getData2());
					approvalMapper.ActionLogSancAdd(sanc);
				}	
			}else {//end if sendid is not null
				log.info("APPROVAL SENDID NULL");
				action_log_sanc sanc = new action_log_sanc();
				sanc.setUserid(ap.getFinalapprover());
				sanc.setAction_code("S2");
				sanc.setData1(ap.getTitle());
				sanc.setData2(ap.getAppr_seq());
				log.info("내부결재문서 접수 후 최종결재완료 후 action_log_sanc 테이블 INSERT[기안부서]");
				log.info(sanc.getUserid()+" / "+sanc.getAction_code()+" / "+sanc.getData1()+" / "+sanc.getData2());
				approvalMapper.ActionLogSancAdd(sanc);
				return;
			}
		}//end method
}
