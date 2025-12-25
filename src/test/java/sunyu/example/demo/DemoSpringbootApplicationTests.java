package sunyu.example.demo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@SpringBootTest
class DemoSpringbootApplicationTests {
    Log log = LogFactory.get();

    @Test
    void 获取所有资源() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://192.168.11.131:3306/nrvp?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull&useInformationSchema=true&useSSL=false");
        config.setUsername("root");
        config.setPassword("root123QWE");
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(10);
        DataSource ds = new HikariDataSource(config);
        DbUtil.setShowSqlGlobal(true, false, true, Level.DEBUG);
        Db db = Db.use(ds);
        String sql = ResourceUtil.readUtf8Str("1.sql");
        // 1. 一次性查出所有数据
        List<Entity> allList = db.query(sql);
        // 2. 创建Map缓存和根节点列表
        Map<Object, Entity> nodeMap = new HashMap<>();
        List<Entity> rootList = new ArrayList<>();
        // 3. 第一次循环：初始化每个节点的children属性，并存入Map
        for (Entity entity : allList) {
            entity.put("children", new ArrayList<Entity>());
            nodeMap.put(entity.getLong("id"), entity);
        }
        // 4. 第二次循环：构建树状关系
        for (Entity entity : allList) {
            Long parentId = entity.getLong("parent_id");
            if (parentId == null || parentId == 0L) {
                // 根节点
                rootList.add(entity);
            } else {
                // 找到父节点并添加
                Entity parent = nodeMap.get(parentId);
                if (parent != null) {
                    List<Entity> children = (List<Entity>) parent.get("children");
                    children.add(entity);
                }
            }
        }
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.setDestFile(FileUtil.file("d:/tmp/资源树.xlsx"));
        writer.writeRow(Arrays.asList("id", "res_name", "res_url", "permission_url"));
        打印树状结构(rootList, writer);
        writer.close();
    }

    void 打印树状结构(List<Entity> rootList, ExcelWriter writer) {
        for (Entity entity : rootList) {
            log.debug("{}", StrUtil.fillBefore("", '-', entity.getInt("res_type") - 1) + entity.getStr("res_name"));
            writer.writeRow(Arrays.asList(entity.getStr("id"), entity.getStr("res_name"), entity.getStr("res_url"), entity.getStr("permission_url")));
            List<Entity> children = (List<Entity>) entity.get("children");
            if (!children.isEmpty()) {
                打印树状结构(children, writer);
            }
        }
    }

}
