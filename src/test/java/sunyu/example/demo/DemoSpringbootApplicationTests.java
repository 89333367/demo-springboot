package sunyu.example.demo;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sunyu.example.demo.mapper.tdengine.TdengineSqlMapper;
import sunyu.example.demo.pojo.tdengine.CompactInfo;
import sunyu.example.demo.pojo.tdengine.QueriesInfo;

import java.util.List;

@SpringBootTest
class DemoSpringbootApplicationTests {
    Log log = LogFactory.get();

    JSONConfig jsonConfig = JSONConfig.create().setIgnoreNullValue(true).setDateFormat("yyyy-MM-dd HH:mm:ss");

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
    void showCompacts() {
        for (CompactInfo showCompact : tdengineSqlMapper.showCompacts()) {
            log.info("{}", JSONUtil.toJsonStr(showCompact, jsonConfig));
        }
    }

    @Test
    void compact() {
        String startDay = "2022-04-29";
        String endDay = "2022-01-01";
        DateTime startTime = DateUtil.parse(startDay);
        while (true) {
            try {
                List<CompactInfo> compacts = tdengineSqlMapper.showCompacts();
                if (!compacts.isEmpty()) {
                    log.warn("compacts {}", JSONUtil.toJsonStr(compacts, jsonConfig));
                    log.warn("compacting {}", startTime.toString("yyyy-MM-dd"));
                    ThreadUtil.sleep(1000 * 10);
                    continue;
                }
                List<QueriesInfo> queries = tdengineSqlMapper.showQueries();
                if (!queries.isEmpty()) {
                    log.warn("queries {}", JSONUtil.toJsonStr(queries, jsonConfig));
                    log.warn("compacting {}", startTime.toString("yyyy-MM-dd"));
                    ThreadUtil.sleep(1000);
                    continue;
                }
                String sql = StrUtil.format("COMPACT DATABASE frequent start with '{}' end with '{}'", startTime.offsetNew(DateField.DAY_OF_MONTH, -1).toString("yyyy-MM-dd"), startTime.toString("yyyy-MM-dd"));
                log.info(sql);
                tdengineSqlMapper.executeSql(sql);
                if (startTime.toString("yyyy-MM-dd").equals(endDay)) {
                    break;
                }
                startTime = startTime.offset(DateField.DAY_OF_MONTH, -1);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}