package com.ecommerce.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {

    private Long id;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private LocalDateTime createTime;
}
