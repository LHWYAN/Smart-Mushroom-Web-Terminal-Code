package com.smartmushroom.test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;

/**
 * MyBatis-Plus 单元测试辅助工具。
 * 在纯 Mockito 测试中（不启动 Spring 容器），LambdaQueryWrapper / LambdaUpdateWrapper
 * 需要依赖 TableInfoHelper 的实体类缓存来解析 lambda → 列名映射。
 * 本工具在 @BeforeAll 中手动初始化该缓存。
 */
public final class MybatisPlusTestSupport {

    private MybatisPlusTestSupport() {
    }

    /**
     * 初始化指定实体类的 TableInfo 缓存，幂等操作可重复调用。
     *
     * @param entityClasses 需要初始化的实体类
     */
    public static void initTableInfo(Class<?>... entityClasses) {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        for (Class<?> entityClass : entityClasses) {
            TableInfoHelper.initTableInfo(assistant, entityClass);
        }
    }
}
