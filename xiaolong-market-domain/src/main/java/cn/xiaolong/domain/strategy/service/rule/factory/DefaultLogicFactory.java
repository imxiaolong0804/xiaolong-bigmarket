package cn.xiaolong.domain.strategy.service.rule.factory;


import cn.xiaolong.domain.strategy.model.entity.RuleActionEntity;
import cn.xiaolong.domain.strategy.service.annotation.LogicStrategy;
import cn.xiaolong.domain.strategy.service.rule.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 规则工厂
 */
@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>(); // 存储逻辑过滤器实例

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters) {
        logicFilters.forEach(logic -> {
            // 通过反射查找ILogicFilter类上是否标记了@LogicStrategy注解
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                // 将策略模式的Code作为Key，ILogicFilter实现类作为Value，存储到Map
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    // 返回逻辑过滤器的集合，泛型化支持规则实体类型
    public <T extends RuleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap; // 泛型强制转换
    }

    // 枚举类：定义了不同的策略逻辑模型
    @Getter
    @AllArgsConstructor
    public enum LogicModel {
        RULE_WEIGHT("rule_weight", "【抽奖前规则】根据抽奖权重返回可抽奖范围KEY"),
        RULE_BLACKLIST("rule_blacklist", "【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回");

        private final String code; // 策略代码
        private final String info; // 策略描述
    }
}
