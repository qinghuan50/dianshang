package com.atguigu.gmall.model.enums;

public enum OrderStatus {
    UNPAID("未支付"),
    PAID("已支付"),
    TIMEOUT_CANCELLATION("超时订单取消"),
    ACTIVE_CANCELLATION("用户取消订单"),
    WAITING_DELEVER("待发货"),
    DELEVERED("已发货"),
    CLOSED("已关闭"),
    FINISHED("已完结"),
    SPLIT("订单已拆分");

    private String comment;

    public static String getStatusNameByStatus(String status) {
        OrderStatus arrObj[] = OrderStatus.values();
        for (OrderStatus obj : arrObj) {
            if (obj.name().equals(status)) {
                return obj.getComment();
            }
        }
        return "";
    }

    OrderStatus(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
