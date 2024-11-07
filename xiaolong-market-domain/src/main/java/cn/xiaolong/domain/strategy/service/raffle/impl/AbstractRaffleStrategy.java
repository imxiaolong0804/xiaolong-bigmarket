package cn.xiaolong.domain.strategy.service.raffle.impl;

import cn.xiaolong.domain.strategy.model.entity.RaffleAwardEntity;
import cn.xiaolong.domain.strategy.model.entity.RaffleFactorEntity;
import cn.xiaolong.domain.strategy.model.entity.RuleActionEntity;
import cn.xiaolong.domain.strategy.model.entity.StrategyEntity;
import cn.xiaolong.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.xiaolong.domain.strategy.repository.IStrategyRepository;
import cn.xiaolong.domain.strategy.service.armory.IStrategyDispatch;
import cn.xiaolong.domain.strategy.service.raffle.IRaffleStrategy;
import cn.xiaolong.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.xiaolong.types.enums.ResponseCode;
import cn.xiaolong.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 14:18
 * @Description:
 */
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    @Resource
    private IStrategyRepository repository;

    // 这里注入抽奖的接口
    @Resource
    private IStrategyDispatch strategyDispatch;

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1. 参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),
                    ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 策略查询
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);


        // 3. 抽奖前 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = this.doCheckRaffleBeforeLogic(
                RaffleFactorEntity.builder().userId(userId).strategyId(strategyId).build(), strategyEntity.ruleModels()
        );

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())) {
            if (DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())) {
                // 返回黑名单固定的奖品ID
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();
            } else if (DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode().equals(ruleActionEntity.getRuleModel())) {
                // 返回权重的奖品Id
                String ruleWeightValueKey = ruleActionEntity.getData().getRuleWeightValueKey();
                Integer awardId = strategyDispatch.getRandomAwardId(strategyId, ruleWeightValueKey);
                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }

        // 4. 默认抽奖流程
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);


        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity build, String... logics);
}
