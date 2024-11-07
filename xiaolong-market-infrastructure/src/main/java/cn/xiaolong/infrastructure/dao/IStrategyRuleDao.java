package cn.xiaolong.infrastructure.dao;

import cn.xiaolong.infrastructure.dao.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/4 16:26
 * @Description:
 */
@Mapper
public interface IStrategyRuleDao {

    List<StrategyRule> queryStrategyRuleList();

    StrategyRule queryStrategyRule(StrategyRule strategyRule);

    String queryStrategyRuleValue(@Param("strategyId") Long strategyId, @Param("awardId") Integer awardId, @Param("ruleModel") String ruleModel);
}
