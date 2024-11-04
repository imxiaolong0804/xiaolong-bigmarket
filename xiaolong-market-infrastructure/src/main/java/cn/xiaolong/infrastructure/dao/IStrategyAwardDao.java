package cn.xiaolong.infrastructure.dao;

import cn.xiaolong.infrastructure.dao.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/4 16:26
 * @Description:
 */
@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> queryStrategyAwardList();
}
