package com.ambition.bi.model.dto.search;

import lombok.Data;

/**
 * @author Ambition
 * @date 2023/10/26 15:29
 */
@Data
public class SearchRequest {

    /**
     * 搜索内容
     */
    private String searchText;


    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页显示条数
     */
    private Integer pageSize;



    private static final long serialVersionUID = 1L;
}
