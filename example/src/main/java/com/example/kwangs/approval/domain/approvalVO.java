package com.example.kwangs.approval.domain;

import java.sql.Date;

import org.springframework.stereotype.Component;

@Component
public class approvalVO {
	private String appr_seq;
	private String name;
	private String id;
	private Date regdate;
	private String productname;
	private int fullstock;
	private String stock;
	private int stockquantity;
	private int subdivision;
	private int manufacturing;
	private int currentstock;
	

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
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public int getFullstock() {
		return fullstock;
	}
	public void setFullstock(int fullstock) {
		this.fullstock = fullstock;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public int getStockquantity() {
		return stockquantity;
	}
	public void setStockquantity(int stockquantity) {
		this.stockquantity = stockquantity;
	}
	public int getSubdivision() {
		return subdivision;
	}
	public void setSubdivision(int subdivision) {
		this.subdivision = subdivision;
	}
	public int getManufacturing() {
		return manufacturing;
	}
	public void setManufacturing(int manufacturing) {
		this.manufacturing = manufacturing;
	}
	public int getCurrentstock() {
		return currentstock;
	}
	public void setCurrentstock(int currentstock) {
		this.currentstock = currentstock;
	}
	
	
	
}