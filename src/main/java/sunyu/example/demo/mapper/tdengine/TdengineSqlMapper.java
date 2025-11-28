package sunyu.example.demo.mapper.tdengine;

import sunyu.example.demo.pojo.tdengine.CompactInfo;
import sunyu.example.demo.pojo.tdengine.QueriesInfo;

import java.util.List;

public interface TdengineSqlMapper {
    List<String> showDatabases();

    List<CompactInfo> showCompacts();

    List<QueriesInfo> showQueries();

    void insert(String sql);

    void executeSql(String sql);
}
