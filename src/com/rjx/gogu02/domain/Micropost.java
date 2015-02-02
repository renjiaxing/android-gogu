package com.rjx.gogu02.domain;

public class Micropost {
	private String id;
	private String content;
	private String user_id;
	private String stock_id;
	private String stock_name;
	private String comment_size;
	private String good;
	private String good_number;
	private String create_time;
	private String unread;
	private String image;
	private String stock_full_name;


	public Micropost(String id, String content, String user_id,
			String stock_id, String stock_name, String comment_size,
			String good, String good_number, String create_at, String unread,String image,String stock_full_name) {
		this.id = id;
		this.content = content;
		this.user_id = user_id;
		this.stock_id = stock_id;
		this.stock_name = stock_name;
		this.comment_size = comment_size;
		this.good = good;
		this.good_number = good_number;
		this.create_time = create_at;
		this.unread = unread;
		this.image=image;
		this.stock_full_name=stock_full_name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUnread() {
		return unread;
	}

	public void setUnread(String unread) {
		this.unread = unread;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getGood_number() {
		return good_number;
	}

	public void setGood_number(String good_number) {
		this.good_number = good_number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getStock_id() {
		return stock_id;
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

	public String getComment_size() {
		return comment_size;
	}

	public void setComment_size(String comment_size) {
		this.comment_size = comment_size;
	}

	public String getGood() {
		return good;
	}

	public void setGood(String good) {
		this.good = good;
	}
	

	public String getStock_full_name() {
		return stock_full_name;
	}

	public void setStock_full_name(String stock_full_name) {
		this.stock_full_name = stock_full_name;
	}

}
