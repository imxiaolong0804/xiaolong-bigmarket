package cn.xiaolong.domain.strategy.repository;

import cn.xiaolong.domain.strategy.model.entity.StrategyAwardEntity;
import cn.xiaolong.domain.strategy.model.entity.StrategyEntity;
import cn.xiaolong.domain.strategy.model.entity.StrategyRuleEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/5 21:20
 * @Description:
 */
public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTables(String strategyId, int rateRange, HashMap<Integer, Integer> map);

    int getRateRange(Long strategyId);

    int getRateRange(String key);

    Integer getStrategyAwardAssembel(Long strategyId, int i);

    Integer getStrategyAwardAssembel(String key, int i);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRuleValueByIdAndWeight(Long strategyId, String ruleWeight);
}
