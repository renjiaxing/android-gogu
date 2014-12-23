package com.rjx.gogu02.domain;

public class Comments {

	private String id;
	private String msg;
	private String user_id;
	private String anon_id;
	private String create_time;

	public Comments(String id, String msg, String user_id, String anon_id,
			String create_time) {
		this.id = id;
		this.msg = msg;
		this.user_id = user_id;
		this.anon_id = anon_id;
		this.create_time = create_time;
	}

	@Override
	public String toString() {
		return "匿名用户" + this.getAnon_id() + ":" + this.getMsg();
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

	public String getAnon_id() {
		return anon_id;
	}

	public void setAnon_id(String anon_id) {
		this.anon_id = anon_id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

}
