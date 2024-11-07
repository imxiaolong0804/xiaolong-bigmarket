package cn.xiaolong.domain.strategy.service.rule.impl;

import cn.xiaolong.domain.strategy.model.entity.RuleActionEntity;
import cn.xiaolong.domain.strategy.model.entity.RuleMatterEntity;
import cn.xiaolong.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.xiaolong.domain.strategy.repository.IStrategyRepository;
import cn.xiaolong.domain.strategy.service.annotation.LogicStrategy;
import cn.xiaolong.domain.strategy.service.rule.ILogicFilter;
import cn.xiaolong.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.xiaolong.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 16:12
 * @Description:
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WEIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;

    public Long userScore = 4500L;

    /**
     * 权重规则过滤；
     * 1. 权重规则格式；4000:102,103,104,105 6000:102,103,104,105,106,107,108,109
     * 2. 解析数据格式；判断哪个范围符合用户的特定抽奖范围
     *
     * @param ruleMatterEntity 规则物料实体对象
     * @return 规则过滤结果
     */
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}",
                ruleMatterEntity.getUserId(),
                ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        Integer awardId = ruleMatterEntity.getAwardId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        String ruleValue = repository.queryStrategyRuleValue(strategyId, awardId, ruleModel);
        // TODO 这里也需要注意一下，如果数据库没有值的话直接返回allow，就不进行过滤了
        if (null == ruleValue) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        // 1. 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
        // 这里的key为 4000，value也为4000
        Map<Long, String> analyticalValue = getAnalyticalValue(ruleValue);
        if (null == analyticalValue || analyticalValue.isEmpty()) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 2. 转换Keys值，并默认排序
        ArrayList<Long> keys = new ArrayList<>(analyticalValue.keySet());
        keys.sort((o1, o2) -> (int) (o2 - o1));

        // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
        Long nextValue = keys.stream()
                .filter(score -> userScore >= score)
                .findFirst()
                .orElse(null);

        // 如果不为空的话就是走规则过滤
        if (null != nextValue) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(strategyId)
                            .ruleWeightValueKey(analyticalValue.get(nextValue))
                            .build())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode())
                    .build();
        }
        // 否则的话直接放行
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }


    public Map<Long, String> getAnalyticalValue(String ruleValue) {

        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        HashMap<Long, String> res = new HashMap<>();
        for (String ruleValueGroup : ruleValueGroups) {
            if (ruleValueGroup == null || ruleValueGroup.isEmpty()) {
                return res;
            }

            String[] partsKV = ruleValueGroup.split(Constants.COLON);
            if (partsKV.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueGroup);
            }
            res.put(Long.valueOf(partsKV[0]), partsKV[0]);
        }
        return res;
    }
}
