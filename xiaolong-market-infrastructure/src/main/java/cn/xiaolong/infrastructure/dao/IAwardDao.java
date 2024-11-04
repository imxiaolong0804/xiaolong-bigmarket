package cn.xiaolong.infrastructure.dao;

import cn.xiaolong.infrastructure.dao.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/4 16:26
 * @Description:
 */
@Mapper
public interface IAwardDao {
    List<Award> queryAwardList();
}
