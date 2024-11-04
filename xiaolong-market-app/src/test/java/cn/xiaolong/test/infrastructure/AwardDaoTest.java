package cn.xiaolong.test.infrastructure;

import cn.xiaolong.infrastructure.dao.IAwardDao;
import cn.xiaolong.infrastructure.dao.po.Award;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/4 16:52
 * @Description:
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class AwardDaoTest {


    @Resource
    private IAwardDao awardDao;

    @Test
    public void test() {
        List<Award> awards = awardDao.queryAwardList();
//        log.info("awards: {}", JSON.toJSONString(awards));
        String jsonString = JSON.toJSONString(awards);
        System.out.printf(jsonString);
    }

}
