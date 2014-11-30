package com.rjx.gogu02;

public class Comments {
	
	private String id;
	private String msg;
	private String user_id;
	
	public Comments(String id,String msg,String user_id) {
		this.id=id;
		this.msg=msg;
		this.user_id=user_id;
	}
	
	@Override
	public String toString() {
		return this.getMsg();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
