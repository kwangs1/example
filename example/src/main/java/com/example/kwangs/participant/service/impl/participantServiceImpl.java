package com.example.kwangs.participant.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kwangs.approval.mapper.approvalMapper;
import com.example.kwangs.approval.service.DocumentNumberGenerator;
import com.example.kwangs.approval.service.approvalVO;
import com.example.kwangs.dept.service.deptVO;
import com.example.kwangs.participant.mapper.participantMapper;
import com.example.kwangs.participant.service.participantService;
import com.example.kwangs.participant.service.participantVO;
import com.example.kwangs.xmlTemp.saveXmlTemp;

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
	private saveXmlTemp saveXmlTemp;
	

	//문서 기안 시 결재선 지정
	@Override
	public void ParticipantWrite(List<participantVO> participant,String id){
		int line_seq = 1;
		
		String seqCurrval = approvalMapper.getLatestReceiptsSeq(); //결재 시퀀스 가져오기
		
	    StringBuilder xmlBuilder = new StringBuilder();
	    xmlBuilder.append("<participants>");
		
		for (participantVO pVO : participant) {
			pVO.setAppr_seq(seqCurrval);
			pVO.setLine_seq(line_seq);// 기본값 1

			// 이후 insert 된 receipts_seq 값 가져올 것.
			mapper.ParticipantWrite(pVO);
			line_seq++;// receitps_seq 별 사용자 번호 순차 증가
			approvalTypeAndStatus(participant);
			
	        //participantVO를 XML 형식으로 변환하여 StringBuilder에 추가
	        xmlBuilder.append("<participant>");
	        xmlBuilder.append("<deptid>").append(pVO.getDeptid()).append("</deptid>");
	        xmlBuilder.append("<deptname>").append(pVO.getDeptname()).append("</deptname>");
	        xmlBuilder.append("<signerid>").append(pVO.getSignerid()).append("</signerid>");
	        xmlBuilder.append("<signername>").append(pVO.getSignername()).append("</signername>");
	        xmlBuilder.append("<pos>").append(pVO.getPos()).append("</pos>");
	        xmlBuilder.append("<status>").append(pVO.getStatus()).append("</status>");
	        // 다른 필드들도 필요한 경우 추가 가능
	        xmlBuilder.append("</participant>");
			
		}
		xmlBuilder.append("</participants>");

	    // 전체 participants를 XML 문자열로 변환하여 saveXml에 전달
	    String xmlData = xmlBuilder.toString();
	    saveXmlTemp.SaveParticipantTemp(id,xmlData);
	}
	
	//일괄 결재
	@Override
	public void BulkAppr(List<participantVO> participant) {
		for(participantVO pp : participant) {
			//put을 통해 key,value를 받아 전달된 인자는 hashMap에 key-value 관계로 저장
			Map<String, Object> params = new HashMap<>();
			params.put("approvaltype", pp.getApprovaltype());
			params.put("approvalstatus", pp.getApprovalstatus());
			params.put("signerid", pp.getSignerid());
	        params.put("participant_seq", pp.getParticipant_seq());
			params.put("appr_seq", pp.getAppr_seq());
						
			log.info("service {} :"+params);
			mapper.BulkAppr(params);
			//결재선 차례 업데이트
			updateNextApprovalType(pp.getAppr_seq());
		}
	}
	
	//일괄결재 시 결재선 정보 가져오기 위한 해당 문서의 결재선 정보 가져오는 부분
	@Override
	public List<participantVO>  ApprWaitFLowInfo(String appr_seq) {
		return mapper.ApprWaitFLowInfo(appr_seq);
	}
	//회수 시 결재선 정보 가져오기 위한 해당 문서의 결재선 정보 가져오는 부분
	@Override
	public List<participantVO>  ApprProgrsFLowInfo(String appr_seq) {
		return mapper.ApprProgrsFLowInfo(appr_seq);
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
	        log.info("=======================================");
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
	        
	        // 현재 결재자의 approvaltype이 결재를 완료하여 2가 되고,
	        // 다음 결재자(중간결재자 또는 마지막 결재자)의 approvaltype이 8인 경우 approvalType를 4로 변경
	        if (currentParticipant.getApprovaltype() == 2) {
	        	// 다음 결재자의 index (첫 결재자 이후 2번쨰 부터 결재를 한 후 while문이 종료되면  1씩 증가 시켜서 다음 결재자 시퀀스를 가져오기 위해
	            int nextIndex = i + 1;
	            
	            // 모든 결재자의 participant_seq 값도 확인하여 업데이트
	            // nextIndex의 값보다 결재선의 길이가 더크다면
	            while (nextIndex < approvalLines.size()) {
	            	//그다음 결재자의 시퀀스를 가져오기 위해 nextIndex를 가져오고 그게에 대해 시퀀스를 가져온다.
	                participantVO nextParticipant = approvalLines.get(nextIndex);
	                String participant_seq = nextParticipant.getParticipant_seq();
	                int nextLine_seq = nextParticipant.getLine_seq();
	                
	                log.info("Next Line_seq.."+nextLine_seq);
	                log.info("NextIndex.."+nextIndex);
	                log.info("for loop check.."+participant_seq);
	                //결재가 되고 가져온 그다음 결재자의 값이 8이면 4로 업데이트를 침
	                if (nextParticipant.getApprovaltype() == 8 && nextLine_seq == currentParticipant.getLine_seq() +1) {
	                    nextParticipant.setApprovaltype(4);
	                    log.info("Updated next approval type: {}", nextParticipant.getApprovaltype());
	                    
	                    // Map으로 매개변수 전달
	                    Map<String, Object> params = new HashMap<>();
	                    params.put("appr_seq", nextParticipant.getAppr_seq());
	                    params.put("participant_seq", participant_seq);
	                    params.put("approvaltype", nextParticipant.getApprovaltype());
	                    
	                    // mapper를 통해 DB 업데이트 수행
	                    mapper.updateNextApprovalType(params);
	                    
	                    log.info("=======================================");
	                    break; // 업데이트 후 루프 종료
	                }
	                
	                nextIndex++;
	            }//end while
	            /*
	             * 기안자,최종결재자 둘만 있을 시 while문을 타지 않기에 while문 밖에서 기안자,최종결재자만 있는 결재문서일 경우
	             * 문서 상태값 완료로 변경            	
	            */
	            if(currentParticipant.getApprovaltype() == 2 && i == approvalLines.size() -1) {
                	approvalMapper.ApprovalUpdateStatus(appr_seq);
                	log.info("final participant and approval status update");
                	ConCludeDocRegNo(appr_seq);
	            }
	        }//end if (currentParticipant.getApprovaltype() == 2)
	    }//end for
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
		
		log.info("service FlowAppr RecData {}"+res);
		updateNextApprovalType(participant.getAppr_seq());
		
	}

	//일반 결재 시 상세보기에서의 결재선 정보 
	@Override
	public participantVO pInfo(Map<String,Object> res) {
		return mapper.pInfo(res);
	}
	
	//문서번호 체번
	public void ConCludeDocRegNo(String appr_seq) {
		log.info("=================== DOCNO UPDATE LINE ===================");
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
		log.info("=================== DOCNO UPDATE LINE ===================");
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
	public void ResubmissionParticipantWrite(List<participantVO> participant) {
		log.info("--"+participant.get(0).getAppr_seq());
		for(participantVO pvo : participant) {
			String appr_seq = pvo.getAppr_seq();
			log.info("Resubmission appr_seq "+appr_seq);
			pvo.setAppr_seq(appr_seq);
			/*
			int lastSeq = mapper.getLastSeq(appr_seq);
			log.info("Resubmission lastseq "+lastSeq);
			pvo.setLine_seq(lastSeq +1);
			*/
			log.info("new flow signer" + pvo.getSignerid());
			mapper.ResubmissionParticipantWrite(pvo);
			//lastSeq++;
			approvalTypeAndStatus(participant);
			log.info("end service ..");
		}
	}
}
