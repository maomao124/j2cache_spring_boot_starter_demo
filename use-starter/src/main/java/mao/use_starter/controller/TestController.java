package mao.use_starter.controller;

import mao.tools_j2cache.utils.RedisUtils;
import mao.use_starter.entity.Student;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Project name(项目名称)：j2cache_demo
 * Package(包名): mao.j2cache_demo.controller
 * Class(类名): TestController
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/11/5
 * Time(创建时间)： 13:22
 * Version(版本): 1.0
 * Description(描述)： 无
 */

@RestController
public class TestController
{

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private CacheChannel cacheChannel;

    private final String key = "myKey";
    private final String region = "rx";


    @GetMapping("/getInfos")
    public List<String> getInfos()
    {
        CacheObject cacheObject = cacheChannel.get(region, key);
        if (cacheObject.getValue() == null)
        {
            log.info("查询数据库");
            //缓存中没有找到，查询数据库获得
            List<String> data = new ArrayList<>();
            data.add("info1");
            data.add("info2");
            try
            {
                Thread.sleep(9);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            //放入缓存
            cacheChannel.set(region, key, data);
            return data;
        }
        return (List<String>) cacheObject.getValue();
    }

    private String cache = null;

    @GetMapping("/getInfos2")
    public String getInfos2()
    {
        if (cache == null)
        {
            log.info("查询数据库2");
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            cache = "hello";
        }
        else
        {
            return cache;
        }
        return cache;
    }


    @GetMapping("/getInfos3")
    public List<String> getInfos3()
    {
        CacheObject cacheObject = cacheChannel.get(region, key);
        if (cacheObject.getValue() == null)
        {
            log.info("查询数据库3");
            //缓存中没有找到，查询数据库获得
            try
            {
                Thread.sleep(9);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            //放入缓存
            cacheChannel.set(region, key, null);
            return null;
        }
        return null;
    }

    /**
     * 清理指定缓存
     *
     * @return {@link String}
     */
    @GetMapping("/evict")
    public String evict()
    {
        cacheChannel.evict(region, key);
        return "evict success";
    }

    /**
     * 检测存在哪级缓存
     *
     * @return {@link String}
     */
    @GetMapping("/check")
    public String check()
    {
        int check = cacheChannel.check(region, key);
        return "level:" + check;
    }

    /**
     * 检测缓存数据是否存在
     *
     * @return {@link String}
     */
    @GetMapping("/exists")
    public String exists()
    {
        boolean exists = cacheChannel.exists(region, key);
        return "exists:" + exists;
    }

    /**
     * 清理指定区域的缓存
     *
     * @return {@link String}
     */
    @GetMapping("/clear")
    public String clear()
    {
        cache = null;
        cacheChannel.clear(region);
        return "clear success";
    }


    @Autowired
    private RedisUtils redisUtils;

    private Student queryMysqlById(long id)
    {
        log.info("查询Mysql数据库");
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Student student = new Student();
        student.setId(id);
        student.setName("张三");
        return student;
    }

    /**
     * 查询mysql ,测试缓存穿透
     *
     * @param id id
     * @return {@link Student}
     */
    private Student queryMysqlById2(long id)
    {
        log.info("查询Mysql数据库2");
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean updateMysqlById(Student student, long id)
    {
        log.info("更新Mysql数据库");
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return true;
    }




    @GetMapping("/query")
    public Student query()
    {
        Student result = redisUtils.query("tools:", "tools:lock:", 1L, Student.class,
                this::queryMysqlById, 30L, TimeUnit.MINUTES, 60);
        return result;
    }

    @GetMapping("/query2")
    public Student query2()
    {
        Student result = redisUtils.query("tools:", "tools:lock:", 2L, Student.class,
                this::queryMysqlById2, 30L, TimeUnit.MINUTES, 60);
        return result;
    }

    @GetMapping("/update")
    public boolean update()
    {
        Student student = new Student();
        student.setId(1L);
        student.setName("张三2");
        boolean update = redisUtils.update(1L, student, "tools:", s -> updateMysqlById(student, 1L));
        return update;
    }



}
