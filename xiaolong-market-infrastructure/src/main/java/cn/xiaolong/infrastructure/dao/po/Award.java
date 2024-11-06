package cn.xiaolong.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/4 16:19
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Award {
    // 自增ID
    private Integer id;

    // 抽奖奖品ID - 内部流转使用
    private Long awardId;

    // 奖品对接标识 - 每一个都是一个对应的发奖策略
    private String awardKey;

    // 奖品配置信息
    private String awardConfig;

    // 奖品内容描述
    private String awardDesc;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
