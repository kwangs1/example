package com.example.kwangs.approval.service;

import java.sql.Date;

import org.springframework.stereotype.Component;

@Component
public class approvalVO {
	private String appr_seq;
	private String name;
	private String id;
	private String regdate;
	private String title;
	private String content;
	private Date startdate;
	private Date enddate;
	private int status;
	private String docregno;
	private String drafterdeptid;
	private String drafterdeptname;
	private int regno;

	

	public int getRegno() {
		return regno;
	}
	public void setRegno(int regno) {
		this.regno = regno;
	}
	public String getDocregno() {
		return docregno;
	}
	public void setDocregno(String docregno) {
		this.docregno = docregno;
	}

	public String getDrafterdeptid() {
		return drafterdeptid;
	}
	public void setDrafterdeptid(String drafterdeptid) {
		this.drafterdeptid = drafterdeptid;
	}

	public String getDrafterdeptname() {
		return drafterdeptname;
	}
	public void setDrafterdeptname(String drafterdeptname) {
		this.drafterdeptname = drafterdeptname;
	}
	public String getAppr_seq() {
		return appr_seq;
	}
	public void setAppr_seq(String appr_seq) {
		this.appr_seq = appr_seq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Date getEnddate() {
		return enddate;
	}
	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
