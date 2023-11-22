package com.ambition.bi.mapper;

import com.ambition.bi.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Ambition
 * @since 2023-11-12
 */
public interface ChartMapper extends BaseMapper<Chart> {

    /**
     * @param querySql 完整的查询sql
     * @return 查询结果
     */
    List<Map<String, Object>> queryChartData(@Param("querySql") String querySql);


}
