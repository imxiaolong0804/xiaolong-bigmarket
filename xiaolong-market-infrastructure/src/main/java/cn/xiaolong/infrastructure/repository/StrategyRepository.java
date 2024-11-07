package cn.xiaolong.infrastructure.repository;

import cn.xiaolong.domain.strategy.model.entity.StrategyAwardEntity;
import cn.xiaolong.domain.strategy.model.entity.StrategyEntity;
import cn.xiaolong.domain.strategy.model.entity.StrategyRuleEntity;
import cn.xiaolong.domain.strategy.repository.IStrategyRepository;
import cn.xiaolong.infrastructure.dao.IStrategyAwardDao;
import cn.xiaolong.infrastructure.dao.IStrategyDao;
import cn.xiaolong.infrastructure.dao.IStrategyRuleDao;
import cn.xiaolong.infrastructure.dao.po.Strategy;
import cn.xiaolong.infrastructure.dao.po.StrategyAward;
import cn.xiaolong.infrastructure.dao.po.StrategyRule;
import cn.xiaolong.infrastructure.redis.IRedisService;
import cn.xiaolong.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/5 21:22
 * @Description:
 */
@Repository
@Slf4j
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IRedisService iRedisService;
    @Resource
    private IStrategyAwardDao strategyAwardDao;
    @Resource
    private IStrategyDao strategyDao;
    @Resource
    private IStrategyRuleDao strategyRuleDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 首先是去redis中去查询库存
        String key = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> result = iRedisService.getValue(key);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        // 如果没有才去数据库中去查询
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        result = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity awardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            result.add(awardEntity);
        }
        // 再向redis中存储起来
        iRedisService.setValue(key, result);
        return result;
    }

    @Override
    public void storeStrategyAwardSearchRateTables(String strategyId, int rateRange, HashMap<Integer, Integer> map) {
        // 1、存储需要随机的值的范围raterange
        iRedisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);
        // 2、保存map值
        RMap<Integer, Integer> strategyMap = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        strategyMap.putAll(map);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(strategyId.toString());
    }

    @Override
    public int getRateRange(String key) {
        Integer value = iRedisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
        if (value == null) {
            log.info("获取raterange个数失败！请检查，key是否正确");
            return 0;
        }else {
            return value;
        }
    }

    @Override
    public Integer getStrategyAwardAssembel(Long strategyId, int i) {
        return getStrategyAwardAssembel(strategyId.toString(), i);
    }

    @Override
    public Integer getStrategyAwardAssembel(String key, int i) {
        return iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, i);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        String key = Constants.RedisKey.STRATEGY_KEY + strategyId;
        // 这里优化一下，如果缓存中有直接去缓存中获取
        StrategyEntity strategyEntity = iRedisService.getValue(key);
        if (strategyEntity != null) {
            return strategyEntity;
        }
        // 否则去数据库中获取
        Strategy strategy = strategyDao.queryStrategyEntityByStrategyId(strategyId);
        strategyEntity = new StrategyEntity();
        strategyEntity.setStrategyId(strategy.getStrategyId());
        strategyEntity.setStrategyDesc(strategy.getStrategyDesc());
        strategyEntity.setRuleModels(strategy.getRuleModels());

        // 最后再保存到redis里面
        iRedisService.setValue(key, strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRuleValueByIdAndWeight(Long strategyId, String ruleWeight) {
        // 根据策略ID和规则权重查询策略规则
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(ruleWeight);
        // 查询策略规则
        StrategyRule strategyRuleRes = strategyRuleDao.queryStrategyRule(strategyRule);
        // 构建策略规则实体
        return StrategyRuleEntity.builder()
                .strategyId(strategyRuleRes.getStrategyId())
                .awardId(strategyRuleRes.getAwardId())
                .ruleType(strategyRuleRes.getRuleType())
                .ruleModel(strategyRuleRes.getRuleModel())
                .ruleValue(strategyRuleRes.getRuleValue())
                .ruleDesc(strategyRuleRes.getRuleDesc())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        return strategyRuleDao.queryStrategyRuleValue(strategyId, awardId, ruleModel);
    }
}
