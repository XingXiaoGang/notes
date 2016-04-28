package com.fenghuo.notes.bean;

public class Note {
	private int id;
	private int img;
	private String content;
	private String date;
	private int alarm;
	public Note(int id, int img, String content, String date, int alarm) {
		super();
		this.id = id;
		this.img = img;
		this.content = content;
		this.date = date;
		this.alarm = alarm;
	}
	public Note() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getImg() {
		return img;
	}
	public void setImg(int img) {
		this.img = img;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getAlarm() {
		return alarm;
	}
	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}
	@Override
	public String toString() {
		return "Note [id=" + id + ", img=" + img + ", content=" + content
				+ ", date=" + date + ", alarm=" + alarm + "]";
	}

}
