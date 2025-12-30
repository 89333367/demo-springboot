package sunyu.example.demo.mapper.tdengine;

import org.apache.ibatis.annotations.Param;
import sunyu.example.demo.pojo.tdengine.CompactInfo;
import sunyu.example.demo.pojo.tdengine.QueriesInfo;

import java.util.List;

public interface TdengineSqlMapper {
    List<String> showDatabases();

    List<CompactInfo> showCompacts();

    List<QueriesInfo> showQueries();

    void insert(String sql);

    void executeSql(String sql);

    List<String> getLastProtocol(String did);

    List<String> getLastProtocolBy5500(String did);

    List<String> getCanData(@Param("did") String did, @Param("ts") String ts);
}
