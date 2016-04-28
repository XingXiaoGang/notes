package com.fenghuo.notes.bean;

public class Alarm {
	private int id;
	private String date;
	private String time;
	private int repeat;
	private String week;
	private int vibration;
	private String ringUri;
	private String content;
	public Alarm(int id, String date, String time, int repeat, String week,
				 int vibration, String ringUri, String content) {
		super();
		this.id = id;
		this.date = date;
		this.time = time;
		this.repeat = repeat;
		this.week = week;
		this.vibration = vibration;
		this.ringUri = ringUri;
		this.content = content;
	}
	public Alarm() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public int getVibration() {
		return vibration;
	}
	public void setVibration(int vibration) {
		this.vibration = vibration;
	}
	public String getRingUri() {
		return ringUri;
	}
	public void setRingUri(String ringUri) {
		this.ringUri = ringUri;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "Alarm [id=" + id + ", date=" + date + ", time=" + time
				+ ", repeat=" + repeat + ", week=" + week + ", vibration="
				+ vibration + ", ringUri=" + ringUri + ", content=" + content
				+ "]";
	}


}
