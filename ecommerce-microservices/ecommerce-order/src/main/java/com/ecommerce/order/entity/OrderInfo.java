package com.ecommerce.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体 —— 映射 order_info 表
 */
@Data
@TableName("order_info")
public class OrderInfo {

    /** 自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务订单号 */
    private String orderNo;

    /** 商品 ID */
    private Long productId;

    /** 订单金额 */
    private BigDecimal amount;

    /** 状态：0-已创建 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;
}
