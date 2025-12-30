package sunyu.example.demo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.BigExcelWriter;
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

import java.time.LocalDateTime;
import java.util.*;

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

    @Test
    void 获取洋马设备版本号() {
        ExcelWriter writer = ExcelUtil.getWriter("d:/tmp/洋马设备版本号.xlsx");
        String json = FileUtil.readUtf8String("d:/tmp/imei_did.json");
        List<Map<String, String>> l = new ArrayList<>();
        for (JSONObject o : JSONUtil.toList(json, JSONObject.class)) {
            Map<String, String> row = new HashMap<>();
            row.put("IMEI", o.getStr("imei"));
            row.put("DID", o.getStr("did"));
            row.put("版本号", "");
            String imei = o.getStr("imei");
            String did = o.getStr("did");
            log.debug("{} {}", imei, did);
            try {
                List<String> protocolList = tdengineSqlMapper.getLastProtocolBy5500(did);
                if (CollUtil.isNotEmpty(protocolList)) {
                    String protocol = protocolList.get(0);
                    log.debug("{}", protocol);
                    Map<String, String> map = sdk.parseProtocolString(protocol);
                    log.debug("{}", map.get("5500"));
                    row.put("版本号", map.get("5500"));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            l.add(row);
        }
        writer.write(l, true);
        writer.close();
    }

    @Test
    void 导出can数据() {
        String filePath = "d:/tmp/can数据_NJHYOPWAU0000297.xlsx";
        FileUtil.del(filePath);
        BigExcelWriter bigWriter = ExcelUtil.getBigWriter(filePath);
        List<Map<String, String>> l = new ArrayList<>();
        LocalDateTime ts = LocalDateTimeUtil.parse("2000-01-01", "yyyy-MM-dd");
        while (true) {
            List<String> canList = tdengineSqlMapper.getCanData("NJHYOPWAU0000297", LocalDateTimeUtil.format(ts, "yyyy-MM-dd HH:mm:ss"));
            if (CollUtil.isEmpty(canList)) {
                break;
            }
            for (String protocol : canList) {
                Map<String, String> map = sdk.parseProtocolString(protocol);
                ts = LocalDateTimeUtil.parse(map.get("3014"), "yyyyMMddHHmmss");
                TreeMap<String, String> mv = sdk.parseValue(map);
                Map<String, String> newMap = new HashMap<>();
                for (String key : mv.keySet()) {
                    if (key.equals("3014")) {
                        newMap.put(key + "(" + sdk.getCn(key) + ")", LocalDateTimeUtil.format(ts, "yyyy-MM-dd HH:mm:ss"));
                        continue;
                    }
                    newMap.put(key + "(" + sdk.getCn(key) + ")", mv.get(key));
                }
                l.add(newMap);
            }
        }
        Set<String> keySet = new HashSet<>();
        for (Map<String, String> m : l) {
            keySet.addAll(m.keySet());
        }
        for (Map<String, String> m : l) {
            for (String key : keySet) {
                if (!m.containsKey(key)) {
                    m.put(key, "");
                }
            }
        }
        bigWriter.write(l, true);
        bigWriter.close();
    }
}