package cn.xiaolong.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/4 15:53
 * @Description:
 */
@Data
public class Strategy {

    // 自增ID
    private Long id;

    // 抽奖策略ID
    private Long strategyId;

    // 抽奖策略描述
    private String strategyDesc;

    // 规则模型，rule配置的模型同步到此表，便于使用
    private String ruleModels;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
