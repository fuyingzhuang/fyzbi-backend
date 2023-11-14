package com.ambition.bi.controller;


import com.ambition.bi.common.BaseResponse;
import com.ambition.bi.common.ErrorCode;
import com.ambition.bi.common.ResultUtils;
import com.ambition.bi.model.dto.search.SearchRequest;
import com.ambition.bi.model.entity.Chart;
import com.ambition.bi.model.entity.User;
import com.ambition.bi.service.ChartService;
import com.ambition.bi.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Ambition
 * @since 2023-11-12
 */
@RestController
@RequestMapping("/bi/chart")
public class ChartController {


    @Resource
    private ChartService chartService;


    @Resource
    private UserService userService;

    /**
     * 添加分析模板
     *
     * @param chart   分析模板
     * @param request 请求
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public BaseResponse add(@RequestBody Chart chart, HttpServletRequest request) {
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        // 判断分析模板是否为空
        if (chart.getGoal() == null || "".equals(chart.getGoal())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "分析模板不能为空");
        }
        // 判断图标数据是否为空
        if (chart.getChartData() == null || "".equals(chart.getChartData())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "图表数据不能为空");
        }
        // 判断图表类型是否为空
        if (chart.getChartType() == null || "".equals(chart.getChartType())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "图表类型不能为空");
        }

        boolean save = chartService.save(chart);
        return ResultUtils.success(save);
    }


    /**
     * 获取图表信息
     *
     * @param id      图表id
     * @param request 请求
     * @return 图表信息
     */
    @GetMapping("/getInfo")
    public BaseResponse get(@RequestParam("id") Integer id, HttpServletRequest request) {
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        Chart chart = chartService.getById(id);
        return ResultUtils.success(chart);
    }


    /**
     * 删除图表信息
     *
     * @param chart   图表信息
     * @param request 请求
     * @return 是否删除成功
     */
    @PostMapping("/update")
    public BaseResponse update(@RequestBody Chart chart, HttpServletRequest request) {
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        // 判断分析模板是否为空
        if (chart.getGoal() == null || "".equals(chart.getGoal())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "分析模板不能为空");
        }
        // 判断图标数据是否为空
        if (chart.getChartData() == null || "".equals(chart.getChartData())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "图表数据不能为空");
        }
        // 判断图表类型是否为空
        if (chart.getChartType() == null || "".equals(chart.getChartType())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "图表类型不能为空");
        }

        boolean save = chartService.updateById(chart);
        return ResultUtils.success(save);
    }

    /**
     * 分页 + 模糊查询
     *
     * @param searchRequest 查询参数
     * @param request       请求
     * @return 分页数据
     */
    @GetMapping("/list")
    public BaseResponse list(@RequestBody SearchRequest searchRequest,
                             HttpServletRequest request) {
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        // 获取用的的查询参数
        String searchText = searchRequest.getSearchText();
        Integer currentPage = searchRequest.getCurrentPage();
        Integer pageSize = searchRequest.getPageSize();
        // 判断当前页数是否合法
        if (currentPage == null || currentPage < 1) {
            currentPage = 1;
        }
        // 判断每页显示条数是否合法
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        QueryWrapper<Chart> chartQueryWrapper = new QueryWrapper<>();
        // 模糊搜索
        if (searchText != null && !"".equals(searchText)) {
            chartQueryWrapper.like("goal", searchText);
        }
        Page<Chart> chartPage = new Page<>();
        chartPage.setCurrent(currentPage);
        chartPage.setSize(pageSize);
        Page<Chart> page = chartService.page(chartPage, chartQueryWrapper);
        return ResultUtils.success(page);
    }


    @GetMapping("/delete")
    public BaseResponse delete(@RequestParam("id") Integer id, HttpServletRequest request) {
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        boolean remove = chartService.removeById(id);
        return ResultUtils.success(remove);
    }

}

