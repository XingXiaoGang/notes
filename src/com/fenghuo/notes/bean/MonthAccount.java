package com.fenghuo.notes.bean;

/**
 * 账单的月统计
 *
 * @author Administrator
 */
public class MonthAccount extends Bean {

    private int id;
    private String things;
    private String date;
    private String sumin;
    private String sumout;

    public MonthAccount(int id, String things, String date, String sumin,
                        String sumout) {
        super();
        this.id = id;
        this.things = things;
        this.date = date;
        this.sumin = sumin;
        this.sumout = sumout;
    }

    public MonthAccount() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThings() {
        return things;
    }

    public void setThings(String things) {
        this.things = things;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
        return "MonthAccount [id=" + id + ", things=" + things + ", date="
                + date + ", sumin=" + sumin + ", sumout=" + sumout + "]";
    }

}
