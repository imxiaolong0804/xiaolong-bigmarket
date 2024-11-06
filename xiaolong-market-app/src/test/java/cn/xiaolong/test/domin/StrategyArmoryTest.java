package cn.xiaolong.test.domin;

import cn.xiaolong.domain.strategy.service.armory.IStrategyArmory;
import cn.xiaolong.domain.strategy.service.armory.IStrategyDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/6 9:29
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class StrategyArmoryTest {


    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IStrategyDispatch strategyDispatch;

    @Before
    public void test_Assembel() {
        strategyArmory.assembleLotteryStrategy(100001L);
    }


    @Test
    public void test_getRandomVal() {
        log.info("获取抽奖结果：{}", strategyDispatch.getRandomAwardId(100001L));
        log.info("获取抽奖结果：{}", strategyDispatch.getRandomAwardId(100001L));
        log.info("获取抽奖结果：{}", strategyDispatch.getRandomAwardId(100001L));
        log.info("获取抽奖结果：{}", strategyDispatch.getRandomAwardId(100001L));
        log.info("获取抽奖结果：{}", strategyDispatch.getRandomAwardId(100001L));
    }

    @Test
    public void test_withRuleWeight() {
//        log.info("获取抽奖结果 4000策略第一次：{}", strategyDispatch.getRandomAwardId(100001L, "4000:102,103,104,105"));
//        log.info("获取抽奖结果 4000策略第二次：{}", strategyDispatch.getRandomAwardId(100001L, "4000:102,103,104,105"));
//        log.info("获取抽奖结果 6000策略第一次：{}", strategyDispatch.getRandomAwardId(100001L, "6000:102,103,104,105,106,107,108,109"));
//        log.info("获取抽奖结果 6000策略第二次：{}", strategyDispatch.getRandomAwardId(100001L, "6000:102,103,104,105,106,107,108,109"));
        log.info("获取抽奖结果 4000策略第二次：{}", strategyDispatch.getRandomAwardId(100001L, "4000"));
        log.info("获取抽奖结果 4000策略第二次：{}", strategyDispatch.getRandomAwardId(100001L, "4000"));
        log.info("获取抽奖结果 6000策略第一次：{}", strategyDispatch.getRandomAwardId(100001L, "6000"));
        log.info("获取抽奖结果 6000策略第二次：{}", strategyDispatch.getRandomAwardId(100001L, "6000"));
//        log.info("获取抽奖结果：{}", strategyDispatch.getRandomAwardId(100001L));
    }

}
