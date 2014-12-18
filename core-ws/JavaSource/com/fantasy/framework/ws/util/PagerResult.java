package com.fantasy.framework.ws.util;

import java.util.Arrays;

public abstract class PagerResult<T> extends WSResult {

    private T[] pageItems;
    /**
     * 最大数据条数
     */
    private int totalCount = 0;
    /**
     * 每页显示的数据条数
     */
    private int pageSize = 0;
    /**
     * 总页数
     */
    private int totalPage = 1;
    /**
     * 当前页码
     */
    private int currentPage = 1;
    /**
     * 排序字段
     */
    private String orderBy;
    private String order;
    private String[] orders = new String[0];

    /**
     * 返回的json数据
     */

    /**
     * 默认每页显示条数15条
     */
    public PagerResult() {
        this.pageSize = 15;
    }

    /**
     * 初始化传入每页显示条数
     *
     * @param pageSize 显示条数
     */
    public PagerResult(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取最大数据条数
     *
     * @return int
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 修改最大数据条数
     *
     * @param totalCount 总数据条数
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 获取每页显示条数
     *
     * @return int
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 修改每页显示条数
     *
     * @param pageSize 显示条数
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取总页数
     *
     * @return int
     */
    public int getTotalPage() {
        return totalPage;
    }

    /**
     * 修改总页数
     *
     * @param totalPage 总页数
     */
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    /**
     * 获取当前页码
     *
     * @return int
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 修改当前页码
     *
     * @param currentPage 当前页码
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 获取排序对象
     *
     * @return order
     */
    public String getOrder() {
        return order;
    }

    /**
     * 修改排序对象
     *
     * @param order 排序方向
     */
    public void setOrder(String order) {
        this.order = order;
    }

    public String[] getOrders() {
        return orders;
    }

    public void setOrders(String[] orders) {
        this.orders = orders;
    }

    public T[] getPageItems(){
        return pageItems;
    }

    public void setPageItems(T[] pageItems){
        this.pageItems = pageItems;
    }

    @Override
    public String toString() {
        return "PagerDTO [totalCount=" + totalCount + ", pageSize=" + pageSize + ", totalPage=" + totalPage + ", currentPage=" + currentPage + ", orderBy=" + orderBy + ", order=" + order + ", orders=" + Arrays.toString(orders) + "]";
    }

}
