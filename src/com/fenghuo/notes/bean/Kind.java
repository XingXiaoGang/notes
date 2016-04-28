package com.fenghuo.notes.bean;

public class Kind {
	private int id;
	private String kind;
	public Kind(int id, String kind) {
		super();
		this.id = id;
		this.kind = kind;
	}
	public Kind() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}

}
