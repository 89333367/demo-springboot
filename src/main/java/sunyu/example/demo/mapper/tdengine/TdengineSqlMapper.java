package sunyu.example.demo.mapper.tdengine;

import org.apache.ibatis.annotations.Param;
import sunyu.example.demo.pojo.tdengine.CompactInfo;
import sunyu.example.demo.pojo.tdengine.DP;
import sunyu.example.demo.pojo.tdengine.QueriesInfo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TdengineSqlMapper {
    List<String> showDatabases();
    List<DP> selectWorkPoints(@Param("did") String did, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("checkWorkStatus") Boolean checkWorkStatus);
    List<DP> selectWorkProtocol(@Param("did") String did, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("checkWorkStatus") Boolean checkWorkStatus);
}
