package com.ecommerce.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper —— 继承 MyBatis-Plus BaseMapper 即具备基础 CRUD
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}
