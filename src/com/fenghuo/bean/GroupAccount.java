package com.fenghuo.bean;

/**分组数据model
 * @author Administrator
 *
 */
public class GroupAccount {
	private int id;
	private String name;
	private String sumin;
	private String sumout;
	public GroupAccount(int id, String name, String sumin, String sumout) {
		super();
		this.id = id;
		this.name = name;
		this.sumin = sumin;
		this.sumout = sumout;
	}
	public GroupAccount() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSumin() {
		return sumin;
	}
	public void setSumin(String sumin) {
		this.sumin = sumin;
	}
	public String getSumout() {
		return sumout;
	}
	public void setSumout(String sumout) {
		this.sumout = sumout;
	}
	@Override
	public String toString() {
		return "GroupAccount [id=" + id + ", name=" + name + ", sumin=" + sumin
				+ ", sumout=" + sumout + "]";
	}

}
