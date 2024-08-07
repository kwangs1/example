package com.example.kwangs.memo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.kwangs.common.paging.PageMaker;
import com.example.kwangs.common.paging.SearchCriteria;
import com.example.kwangs.memo.service.memoService;
import com.example.kwangs.memo.service.memoVO;

@Controller
@RequestMapping("/memo")
public class memoController {
	private static Logger log = Logger.getLogger(memoController.class.getName());
	@Autowired
	private memoService service;
	
	@GetMapping("/read")
	public void read(Model model,int mno) {
		model.addAttribute("read",service.read(mno));
	}
	//list 화면
	@GetMapping("/list")
	public void list() {
		
	}
	//list ajax
	@ResponseBody
	@GetMapping("ajaxList")
	public ResponseEntity<Map<String, Object>> ajaxList(SearchCriteria scri) {
    PageMaker pageMaker = new PageMaker();
    pageMaker.setCri(scri);
    pageMaker.setTotalCount(service.countList(scri));

    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("pageMaker", pageMaker);
    resultMap.put("List", service.ajaxList(scri));

    return new ResponseEntity<>(resultMap, HttpStatus.OK);
}
	
	@GetMapping("/write")
	public void write() {}
	
	/**
	 * @RequestBody : 클라이언트에서 전송한 JSON데이터를 자바 객체로 변환 하는데 사용.
	 * @ResponseBody :자바 객체를 JSON 형식으로 응답 하는데 사용.
	 * @return ResponseEntity.ok("") : 클라이언트에서는 응답을 받아오기 위해 success 콜백에서 인자 추가 처리.
	 */
	@ResponseBody
	@PostMapping("/write")
	public ResponseEntity<String> write(@RequestBody memoVO memo) {
		service.write(memo);
		return ResponseEntity.ok("Memo inserted successfully");
	}
	
	@ResponseBody
	@PostMapping("/TitleUpdate")
	public void TitleUpdate(@RequestBody memoVO memo){
		service.TitleUpdate(memo);
	}
	@ResponseBody
	@PostMapping("/update")
	public void update(@RequestBody memoVO memo) {
		service.update(memo);
	}
	
	
	@GetMapping("/searchStrForm")
	public void searchStrGet() {
		
	}
	//상세보기 문자 찾기 검색
	@ResponseBody
	@GetMapping("/searchStr")
	public ResponseEntity<Map<String, Object>> searchStr(Model model, SearchCriteria scri) {
		Map<String, Object>result = new HashMap<>();
		result.put("search", service.searchStr(scri));
	    result.put("keyword", scri.getKeyword()); // 검색어 추가
		
		return ResponseEntity.ok(result);
	}
}
