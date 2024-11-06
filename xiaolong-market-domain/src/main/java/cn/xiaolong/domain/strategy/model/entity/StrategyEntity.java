package cn.xiaolong.domain.strategy.model.entity;

import cn.xiaolong.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/6 16:01
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {
    // 抽奖策略ID
    private Long strategyId;

    // 抽奖策略描述
    private String strategyDesc;

    // 规则模型，rule配置的模型同步到此表，便于使用
    private String ruleModels;

    // 判断ruleModels是否为空
    public String[] ruleModels() {
        if (StringUtils.isBlank(ruleModels)) {
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }

    public String getRuleWeight() {
        String[] ruledModels = ruleModels();
        for (String ruledModel : ruledModels) {
            if ("rule_weight".equals(ruledModel)) {
                return ruledModel;
            }
        }
        return null;
    }
}
