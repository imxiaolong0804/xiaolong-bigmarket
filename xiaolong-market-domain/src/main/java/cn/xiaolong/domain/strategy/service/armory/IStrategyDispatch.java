package cn.xiaolong.domain.strategy.service.armory;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/6 15:47
 * @Description:
 */
public interface IStrategyDispatch {
    Integer getRandomAwardId(Long strategyId);

    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);
}
