package cn.xiaolong.domain.strategy.service.annotation;

import cn.xiaolong.domain.strategy.service.rule.factory.DefaultLogicFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: imxiaolong
 * @Date: 2024/11/7 15:00
 * @Description:
 */
@Target({ElementType.TYPE}) // 限定此注解只能用于类或接口
@Retention(RetentionPolicy.RUNTIME) // 指定注解的生命周期是运行时（供反射使用）
public @interface LogicStrategy {

    DefaultLogicFactory.LogicModel logicMode(); // 定义注解的一个属性，类型是LogicModel枚举
}
