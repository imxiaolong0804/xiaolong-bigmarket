package cn.xiaolong.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ApiTest {

    @Test
    public void test() {
        log.info("测试完成");
    }

//    @Autowired
//    private RedissonService redissonService;

//    @Test
//    public void test_ForRedisson() {
//        RMap<Object, Object> map = redissonService.getMap("strategy_id_100001");
//
//        map.put(1, 101);
//        map.put(2, 101);
//        log.info("抽奖结果：{}", redissonService.getFromMap("strategy_id_100001", 1).toString());
//    }


    @Test
    public void test_1() {
        double v = 1 % 0.001;
        System.out.println(v);
    }

    @Test
    public void test_long() {
        Long test = 8L;
        String string = test.toString() + "_";
        System.out.println(string);

    }

}
