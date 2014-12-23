package com.rjx.gogu02.domain;

public class Msg {

	private String from_id;
	private String to_id;
	private String msg;
	private String created_at;
	private String anonnum;
	private String anontonum;

	public Msg(String from_id, String to_id, String msg, String created_at,
			String anonnum, String anontonum) {
		this.from_id=from_id;
		this.to_id=to_id;
		this.msg=msg;
		this.created_at=created_at;
		this.anonnum=anonnum;
		this.anontonum=anontonum;
	}


	public String getFrom_id() {
		return from_id;
	}

	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}

	public String getTo_id() {
		return to_id;
	}

	public void setTo_id(String to_id) {
		this.to_id = to_id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getAnonnum() {
		return anonnum;
	}

	public void setAnonnum(String anonnum) {
		this.anonnum = anonnum;
	}

	public String getAnontonum() {
		return anontonum;
	}

	public void setAnontonum(String anontonum) {
		this.anontonum = anontonum;
	}

}
