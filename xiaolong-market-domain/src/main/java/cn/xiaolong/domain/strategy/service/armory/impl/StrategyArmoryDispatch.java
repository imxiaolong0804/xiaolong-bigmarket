package cn.xiaolong.domain.strategy.service.armory.impl;

import cn.xiaolong.domain.strategy.model.entity.StrategyAwardEntity;
import cn.xiaolong.domain.strategy.model.entity.StrategyEntity;
import cn.xiaolong.domain.strategy.model.entity.StrategyRuleEntity;
import cn.xiaolong.domain.strategy.repository.IStrategyRepository;
import cn.xiaolong.domain.strategy.service.armory.IStrategyArmory;
import cn.xiaolong.domain.strategy.service.armory.IStrategyDispatch;
import cn.xiaolong.types.enums.ResponseCode;
import cn.xiaolong.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/5 21:17
 * @Description:
 */
@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {

    @Resource
    private IStrategyRepository repository;

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 1、查询策略配置，这里查询到的就是strategyId下的所有中奖产品，101-109
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
        if (strategyAwardEntities == null || strategyAwardEntities.isEmpty()) {
            return false;
        }
        // todo 首先全部进行装配
        assembleLotteryStrategy(strategyId.toString(), strategyAwardEntities);

        // 查询权重策略配置，这里查询对应的strategy
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (ruleWeight == null) {
            return true;
        }

        // 再根据ruleModel来查询value
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRuleValueByIdAndWeight(strategyId, ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(
                    ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),
                    ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo()
            );
        }
        // key 为积分值，value为该积分值下可选的奖品awardid
        Map<String, List<Integer>> map = strategyRuleEntity.getRuleWeightValues();
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            // 这里过滤一下每个积分下的抽奖情况
            strategyAwardEntitiesClone.removeIf(entity -> !entry.getValue().contains(entity.getAwardId()));
            // todo 最后这entity里面就只剩下该积分下可选的值了，然后再进行装配，但是key要变了
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(entry.getKey()),
                    strategyAwardEntitiesClone);
        }
        return true;

    }

    private void assembleLotteryStrategy(String strategyId, List<StrategyAwardEntity> strategyAwardEntities) {
        // 1、获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 2、获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3、获取概率值范围
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        // 4、 构建出这个范围的数组，往里面存储改概率下需要的个数
        ArrayList<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            // 计算出每个概率值需要存放在表中的数量
            int scale = rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue();
            for (int i = 0; i < scale; i++) {
                // 这里面放的都是awardId
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        // 5、打乱顺序
        Collections.shuffle(strategyAwardSearchRateTables);

        // 6、将数据放入hashmap中，最后存储到redis中
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            map.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 7、最后存储到redis中
        repository.storeStrategyAwardSearchRateTables(strategyId, strategyAwardSearchRateTables.size(), map);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        return repository.getStrategyAwardAssembel(strategyId, new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = strategyId.toString() + "_" + ruleWeightValue;
        int rateRange = repository.getRateRange(key);
        return repository.getStrategyAwardAssembel(key, new SecureRandom().nextInt(rateRange));
    }


}
