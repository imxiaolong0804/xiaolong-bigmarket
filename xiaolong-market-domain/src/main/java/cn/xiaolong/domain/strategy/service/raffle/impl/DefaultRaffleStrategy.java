package cn.xiaolong.domain.strategy.service.raffle.impl;

import cn.xiaolong.domain.strategy.model.entity.RaffleFactorEntity;
import cn.xiaolong.domain.strategy.model.entity.RuleActionEntity;
import cn.xiaolong.domain.strategy.model.entity.RuleMatterEntity;
import cn.xiaolong.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.xiaolong.domain.strategy.repository.IStrategyRepository;
import cn.xiaolong.domain.strategy.service.armory.IStrategyDispatch;
import cn.xiaolong.domain.strategy.service.rule.ILogicFilter;
import cn.xiaolong.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 16:50
 * @Description:
 */
@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    @Resource
    private DefaultLogicFactory logicFactory;

    @Resource
    private IStrategyRepository repository;

    @Resource
    private IStrategyDispatch strategyDispatch;

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(
            RaffleFactorEntity raffleFactorEntity, String... logics) {

        // 这里的code就为：rule_blacklist
        String ruleBlacklistCode = DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode();
        // 这里相当于就用工厂的模式去判断是否需要进行过滤
        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterMap = logicFactory.openLogicFilter();

        // 黑名单规则优先过滤, 通过logics里面的值去判断是否有黑名单的model
        String ruleBlack = Arrays.stream(logics)
                .filter(model -> model.contains(ruleBlacklistCode))
                .findFirst()
                .orElse(null);

        if (!StringUtils.isBlank(ruleBlack)) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterMap.get(ruleBlacklistCode);
            // 组装filter的参数
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleBlack);
            // 执行过滤
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }

        }

        // 其他的依次过滤
        List<String> restRuleList = Arrays.stream(logics)
                .filter(model -> !model.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        for (String ruleModel : restRuleList) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterMap.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }
        }

        return ruleActionEntity;
    }
}
