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

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 15:08
 * @Description:
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
public class RuleBackListLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-黑名单：userId：{} strategyId：{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId());


        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(),
                ruleMatterEntity.getRuleModel());
        // 100:user001,user002,user003
        String[] splitKV = ruleValue.split(Constants.COLON);
        Integer Key = Integer.parseInt(splitKV[0]);

        // 然后再看这个过滤的userid里面有没有自己
        String userId = ruleMatterEntity.getUserId();
        String[] blockUsers = ruleValue.split(Constants.SPLIT);
        for (String blockUser : blockUsers) {
            if (userId.equals(blockUser)) {
                return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                        .ruleModel(ruleMatterEntity.getRuleModel())
                        .data(RuleActionEntity.RaffleBeforeEntity.builder()
                                .strategyId(ruleMatterEntity.getStrategyId())
                                .awardId(Key)
                                .build())
                        .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                        .build();
            }
        }
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }
}
