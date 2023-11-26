package com.ambition.bi.model.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Ambition
 * @date 2023/11/26 19:48
 * 分析的原始数据
 * 保存到MongoDB中 因为数据量大 而且不是关系型数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnalyzeRawData {
    private long id;
    private String data;


}
