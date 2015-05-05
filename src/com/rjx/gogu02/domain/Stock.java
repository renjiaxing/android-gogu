package com.rjx.gogu02.domain;

public class Stock {

	private String stock_id;
	private String stock_name;
	private String follow;
	
	public Stock(String stock_id,String stock_name,String follow) {
		this.stock_id=stock_id;
		this.stock_name=stock_name;
		this.follow=follow;
	}

	public String getStock_id() {
		return stock_id;
	}

	public String getFollow() {
		return follow;
	}

	public void setFollow(String follow) {
		this.follow = follow;
	}

	public void setStock_id(String stock_id) {
		this.stock_id = stock_id;
	}

	public String getStock_name() {
		return stock_name;
	}

	public void setStock_name(String stock_name) {
		this.stock_name = stock_name;
	}
}
