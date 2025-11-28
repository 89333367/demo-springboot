package sunyu.example.demo;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sunyu.example.demo.mapper.tdengine.TdengineSqlMapper;

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
}