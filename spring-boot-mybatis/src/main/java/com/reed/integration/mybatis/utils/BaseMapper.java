package com.reed.integration.mybatis.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用Mapper基类
 * 特别注意，该接口不能被扫描到，否则会出错
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

}