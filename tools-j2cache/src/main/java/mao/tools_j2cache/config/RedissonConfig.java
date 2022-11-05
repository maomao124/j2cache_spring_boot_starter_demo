package mao.tools_j2cache.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project name(项目名称)：j2cache_spring_boot_starter_demo
 * Package(包名): mao.tools_j2cache.config
 * Class(类名): RedissonConfig
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/11/5
 * Time(创建时间)： 22:47
 * Version(版本): 1.0
 * Description(描述)： 无
 */

@Configuration
public class RedissonConfig
{
    private static final Logger log = LoggerFactory.getLogger(RedissonConfig.class);

    /**
     * Redisson配置
     *
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient(@Value("redis.hosts") String hosts)
    {

        //配置类
        Config config = new Config();
        //添加redis地址，用config.useClusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        //创建客户端
        return Redisson.create(config);
    }
}
