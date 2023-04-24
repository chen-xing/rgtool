package net.gzcx.ui.pagingTable;


/*
 * Created on 2005-6-20
 */

import java.io.Serializable;

/**
 * @author chenzhiliang 说明：
 */
public class RgPageInfo implements Serializable {

    private int curPage = 1;  // 翻页信息,当前页面
    private int totalPage = 1;  // 翻页信息,总页数
    private int pageSize;  //每页显示条数
    private int totalSize;//总数目
    private int curFirst;//当前页第一条
    private int curLast;//当前页最后一条
    private int curSize;//当前显示数目
    private String oldQuerySQL;

    public String getOldQuerySQL() {
        return oldQuerySQL;
    }

    public void setOldQuerySQL(String oldQuerySQL) {
        this.oldQuerySQL = oldQuerySQL;
    }

    private String countSQL;
    private String querySQL;
    //	 翻页信息,当前查询语句
    private String curQuerySQL = "";
    private String curCountSQL = "";
    // 翻页信息,当前排序语句
    private String curSQLOrderBy = "";
    // 翻页信息,当前页面URL

    public RgPageInfo() {
        this.curPage = 1;
        this.pageSize = Constants.RECORDS_OF_PAGE;
    }

    public RgPageInfo(int curPage, int pageSize) {
        this.curPage = curPage;
        this.pageSize = pageSize;
    }

    public RgPageInfo(RgPageInfo pi) {
        this.curPage = pi.getCurPage();
        this.totalPage = pi.getTotalPage();
        this.querySQL = pi.getQuerySQL();
        this.curSQLOrderBy = pi.getCurSQLOrderBy();
    }

    /**
     * @return Returns the curPage.
     */
    public int getCurPage() {
        if (curPage > this.getTotalPage()) {
            curPage = this.getTotalPage();
        }
        if (curPage < 1) {
            curPage = 1;
        }
        return curPage;
    }

    /**
     * @param curPage The curPage to set.
     */
    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    /**
     * @return Returns the totalPage.
     */
    public int getTotalPage() {
        totalPage = ((this.getTotalSize() + this.getPageSize()) - 1) / this.getPageSize();
        return totalPage;
    }

    /**
     * @param totalPage The totalPage to set.
     */
    public void setTotalPage(int totalPage) {

        this.totalPage = totalPage;
    }

    /**
     * @return Returns the curSQLOrderBy.
     */
    public String getCurSQLOrderBy() {
        return curSQLOrderBy;
    }

    /**
     * @param curSQLOrderBy The curSQLOrderBy to set.
     */
    public void setCurSQLOrderBy(String curSQLOrderBy) {
        this.curSQLOrderBy = curSQLOrderBy;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (this.pageSize != pageSize) {
            this.setCurPage(1);
            this.pageSize = pageSize;
        }

    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getCurFirst() {
        curFirst = (this.getCurPage() - 1) * this.getPageSize() + 1;
        return curFirst;
    }

    public void setCurFirst(int curFirst) {
        this.curFirst = curFirst;
    }

    public int getCurLast() {
        curLast = this.getCurPage() * this.getPageSize();
        if (curLast > this.getTotalSize()) {
            curLast = this.getTotalSize();
        }
        return curLast;
    }

    public void setCurLast(int curLast) {
        this.curLast = curLast;
    }

    public String getQuerySQL() {
        return querySQL;
    }

    public void setQuerySQL(String querySQL) {
        if (this.querySQL == null || !this.querySQL.equals(querySQL)) {
            this.oldQuerySQL = this.getQuerySQL();
            this.querySQL = querySQL;
            this.setCurPage(1);
        }

    }

    public String getCountSQL() {
        countSQL = "select count(1) from (" + this.getQuerySQL() + ") as t";
        return countSQL;
    }

    public void setCountSQL(String countSQL) {
        this.countSQL = countSQL;
    }

    public String getCurCountSQL() {
        curCountSQL = "select count(1) from (" + this.getCurQuerySQL() + ") as t";
        return curCountSQL;
    }

    public String getCurQuerySQL() {
        curQuerySQL = this.getQuerySQL() + " LIMIT " + (this.getCurPage() - 1) * this.getPageSize() + "," + this.getPageSize();
        return curQuerySQL;
    }

    /**
     * 转到第几页
     *
     * @param page
     */
    public void go(int page) {
        this.setCurPage(page);
    }

    public void goFirst() {
        this.go(1);
    }

    public void goLast() {
        this.go(this.getTotalPage());
    }

    public void next() {
        if (!isLast()) {
            this.setCurPage(this.getCurPage() + 1);
        }
    }

    public void previous() {
        if (!isFirst()) {
            this.setCurPage(this.getCurPage() - 1);
        }
    }

    public boolean isLast() {
        return this.getCurPage() >= this.getTotalPage();
    }

    public boolean isFirst() {
        return this.getCurPage() <= 1;
    }

    public int getCurSize() {
        return curSize;
    }
//设置当前页记录数

    public void setCurSize(int curSize) {
        this.curSize = curSize;
    }
}


