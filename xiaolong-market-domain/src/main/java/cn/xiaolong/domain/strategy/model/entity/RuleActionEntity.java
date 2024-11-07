package cn.xiaolong.domain.strategy.model.entity;

import cn.xiaolong.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 14:17
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    private String ruleModel;
    private T data;


    public static class RaffleEntity {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RaffleBeforeEntity extends RaffleEntity {
        /**
         * 策略ID
         */
        private Long strategyId;

        /**
         * 权重值Key；用于抽奖时可以选择权重抽奖。
         */
        private String ruleWeightValueKey;

        /**
         * 奖品ID；
         */
        private Integer awardId;
    }

    public static class RaffleDuringEntity extends RaffleEntity {

    }

    public static class RaffleAfterEntity extends RaffleEntity {

    }

}
