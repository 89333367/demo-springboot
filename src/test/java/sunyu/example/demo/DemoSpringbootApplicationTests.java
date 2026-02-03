package sunyu.example.demo;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sunyu.example.demo.mapper.tdengine.TdengineSqlMapper;
import sunyu.example.demo.pojo.tdengine.DP;

import java.util.List;

@SpringBootTest
class DemoSpringbootApplicationTests {
    Log log = LogFactory.get();

    @Autowired
    TdengineSqlMapper tdengineSqlMapper;

    @Test
    void t001() {
        log.debug("sqlMapper {}", tdengineSqlMapper);
    }

    @Test
    void t002() {
        log.debug("showDatabases {}", tdengineSqlMapper.showDatabases());
    }

    @Test
    void t003() {
        String did = "LFS1032311100736";
        String startTime = "20260116104103";
        String endTime = "20260116153830";
        List<DP> l = tdengineSqlMapper.selectWorkPoints(did, LocalDateTimeUtil.parse(startTime, "yyyyMMddHHmmss"), LocalDateTimeUtil.parse(endTime, "yyyyMMddHHmmss"), false);
        log.info("{}", l.size());
    }
}