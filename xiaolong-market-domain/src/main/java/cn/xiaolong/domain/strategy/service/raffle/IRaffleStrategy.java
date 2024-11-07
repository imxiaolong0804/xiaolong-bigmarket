package cn.xiaolong.domain.strategy.service.raffle;

import cn.xiaolong.domain.strategy.model.entity.RaffleAwardEntity;
import cn.xiaolong.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 14:16
 * @Description:
 */
public interface IRaffleStrategy {
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
