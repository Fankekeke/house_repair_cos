package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.RepairInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface RepairInfoMapper extends BaseMapper<RepairInfo> {

    // 分页查询维修信息
    IPage<LinkedHashMap<String, Object>> repairInfoByPage(Page page, @Param("repairInfo") RepairInfo repairInfo);

    // 查看维修详情
    LinkedHashMap<String, Object> repairInfoById(@Param("repairId") Integer repairId);

    /**
     * 房间类型销量比率统计
     *
     * @param year  年费
     * @param month 月份
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectTypeRateByMonth(@Param("year") String year, @Param("month") String month);

    /**
     * 按月统计收益统计
     *
     * @param year   年份
     * @param month  月份
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectPriceByMonth(@Param("year") String year, @Param("month") String month, @Param("checkDate") String date);

    /**
     * 按月统计工单统计
     *
     * @param year   年份
     * @param month  月份
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectOrderNumByMonth(@Param("year") String year, @Param("month") String month, @Param("checkDate") String date);
}
