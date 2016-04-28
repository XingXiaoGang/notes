package com.fenghuo.notes.bean;

/**数据库账单 model
 * @author Administrator
 *
 */
public class Account {
	private int id;
	private int kind;
	private String kinds;
	private float money;
	private String date;
	public Account(int id, int kind, String kinds, float money, String date) {
		super();
		this.id = id;
		this.kind = kind;
		this.kinds = kinds;
		this.money = money;
		this.date = date;
	}
	public Account() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	public String getKinds() {
		return kinds;
	}
	public void setKinds(String kinds) {
		this.kinds = kinds;
	}
	public float getMoney() {
		return money;
	}
	public void setMoney(float money) {
		this.money = money;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "Account [id=" + id + ", kind=" + kind + ", kinds=" + kinds
				+ ", money=" + money + ", date=" + date + "]";
	}

}
