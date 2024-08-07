package com.example.kwangs.bizunit.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface bizunitService {

	//csv upload
	void uploadCSV(MultipartFile file);
	//csv export 시 다운 받을 목록
	List<bizunitVO> getbizList();
	//단위과제 목록
	List<bizunitVO> list();
	//단위과제 작성
	void write(bizunitVO biz,String id, String name);
	//기록물철 작성 시 단위과제 정보 가져오기
	bizunitVO bInfo(Map<String,Object>res);
}
