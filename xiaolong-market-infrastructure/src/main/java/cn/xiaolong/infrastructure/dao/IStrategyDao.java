package cn.xiaolong.infrastructure.dao;

import cn.xiaolong.infrastructure.dao.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/4 16:25
 * @Description:
 */
@Mapper
public interface IStrategyDao {

    List<Strategy> queryStrategyList();

}
