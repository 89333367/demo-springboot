package sunyu.example.demo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoSpringbootApplicationTests {
    Log log = LogFactory.get();

    @Test
    void 获取爱科设备小版本() {
        List<String> l = new ArrayList<>();
        String url = "https://agconnectgw.agcocorp.cn/dapr-service-vehicle-realtime-data/info/query";
        String queryTmp = ResourceUtil.readUtf8Str("queryTmp.json");
        log.info("queryTmp: {}", queryTmp);
        JSONObject queryObj = JSONUtil.parseObj(queryTmp);
        for (String did : ResourceUtil.readUtf8Str("dids.txt").split("\n")) {
            log.info("did: {}", did);
            queryObj.set("did", did);
            log.info("queryObj: {}", queryObj);
            HttpRequest post = HttpUtil.createPost(url);
            post.body(queryObj.toString());
            String resp = post.execute().body();
            log.info("resp: {}", resp);
            JSONObject respObj = JSONUtil.parseObj(resp);
            JSONObject data = respObj.getJSONObject("data");
            String p3014 = data.getStr("3014");
            String p3006 = data.getStr("3006");
            log.info("3014：{} 3006: {}", p3014, p3006);
            l.add(StrUtil.format("{},{},{}", did, p3014 == null ? null : DateUtil.parse(p3014).toString("yyyy-MM-dd HH:mm:ss"), p3006));
        }
        FileUtil.writeUtf8Lines(l, "d:/tmp/3014_3006.txt");
    }

}
