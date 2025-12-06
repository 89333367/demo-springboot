package sunyu.example.demo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sunyu.example.demo.mapper.tdengine.TdengineSqlMapper;
import sunyu.example.demo.pojo.tdengine.CompactInfo;
import sunyu.example.demo.pojo.tdengine.QueriesInfo;
import uml.tech.bigdata.sdkconfig.ProtocolSdk;

import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoSpringbootApplicationTests {
    Log log = LogFactory.get();

    JSONConfig jsonConfig = JSONConfig.create().setIgnoreNullValue(true).setDateFormat("yyyy-MM-dd HH:mm:ss");

    ProtocolSdk sdk = new ProtocolSdk("http://192.168.11.8/config.xml");

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
        String startDay = "2024-03-16";
        String endDay = "2025-01-01";
        DateTime startTime = DateUtil.parse(startDay);
        while (true) {
            try {
                List<CompactInfo> compacts = tdengineSqlMapper.showCompacts();
                if (!compacts.isEmpty()) {
                    log.warn("compacts {}", JSONUtil.toJsonStr(compacts, jsonConfig));
                    log.warn("compacting {}", startTime.toString("yyyy-MM-dd"));
                    ThreadUtil.sleep(1000 * 60);
                    continue;
                }
                List<QueriesInfo> queries = tdengineSqlMapper.showQueries();
                if (!queries.isEmpty()) {
                    log.warn("queries {}", JSONUtil.toJsonStr(queries, jsonConfig));
                    log.warn("compacting {}", startTime.toString("yyyy-MM-dd"));
                    ThreadUtil.sleep(1000);
                    continue;
                }
                String sql = StrUtil.format("COMPACT DATABASE frequent start with '{}' end with '{}'", startTime.toString("yyyy-MM-dd"), startTime.offsetNew(DateField.DAY_OF_MONTH, 1).toString("yyyy-MM-dd"));
                log.info(sql);
                tdengineSqlMapper.executeSql(sql);
                if (startTime.toString("yyyy-MM-dd").equals(endDay)) {
                    break;
                }
                startTime = startTime.offset(DateField.DAY_OF_MONTH, 1);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Test
    void 获取最后已定位的经纬度() {
        ExcelReader reader = ExcelUtil.getReader("d:/tmp/常州东风经纬度.xlsx");
        List<Map<String, Object>> rows = reader.readAll();
        reader.close();
        for (Map<String, Object> row : rows) {
            row.put("经度", "");
            row.put("纬度", "");
            String did = row.get("终端编号").toString();
            String imei = row.get("IMEI").toString();
            List<String> protocolList = tdengineSqlMapper.getLastProtocol(did);
            if (CollUtil.isNotEmpty(protocolList)) {
                String protocol = protocolList.get(0);
                log.debug("{}", protocol);
                Map<String, String> map = sdk.parseProtocolString(protocol);
                log.debug("{} {}", map.get("2602"), map.get("2603"));
                row.put("经度", map.get("2602"));
                row.put("纬度", map.get("2603"));
            }
            log.debug("{} {} ", did, imei);
        }
        ExcelWriter writer = ExcelUtil.getWriter("d:/tmp/常州东风经纬度-已定位.xlsx");
        writer.write(rows, true);
        writer.close();
    }
}