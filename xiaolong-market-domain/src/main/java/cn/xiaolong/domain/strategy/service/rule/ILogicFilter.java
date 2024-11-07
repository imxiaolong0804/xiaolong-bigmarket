package cn.xiaolong.domain.strategy.service.rule;

import cn.xiaolong.domain.strategy.model.entity.RuleActionEntity;
import cn.xiaolong.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 14:18
 * @Description:
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);

}
