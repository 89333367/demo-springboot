package sunyu.example.demo;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sunyu.util.HbaseUtil;

import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoSpringbootApplicationTests {
    Log log = LogFactory.get();

    @Test
    void t001() {
        //工具类中全局只需要build一次
        HbaseUtil hbaseUtil = HbaseUtil.builder().hbaseZookeeperQuorum("kafka005:2181,kafka015:2181,kafka016:2181")
                .zookeeperZnodeParent("/hbase").build();
        List<Map<String, String>> l = hbaseUtil.select(
                "select * from can_ne#can where startRowKey='ffff7e52899bcc8a11d770af62f075ad_20221012113110' and stopRowKey='ffff7e52899bcc8a11d770af62f075ad_20221012113254' limit 10");
        log.info("查询到数量 {}", l.size());
        for (Map<String, String> m : l) {
            log.info("{}", m);
        }
        hbaseUtil.close();
    }

    @Test
    void t002() {
        //工具类中全局只需要build一次
        HbaseUtil hbaseUtil = HbaseUtil.builder().hbaseZookeeperQuorum("kafka005:2181,kafka015:2181,kafka016:2181")
                .zookeeperZnodeParent("/hbase").build();
        String sql = "select count(*) from can_ne#can where startRowKey='ffff7e52899bcc8a11d770af62f075ad_20221012113110' and stopRowKey='ffff7e52899bcc8a11d770af62f075ad_20221012113254'";
        log.info("{}", hbaseUtil.count(sql));
        hbaseUtil.close();
    }

}
