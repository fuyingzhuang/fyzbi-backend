package com.ambition.bi.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.ambition.bi.common.BaseResponse;
import com.ambition.bi.common.ErrorCode;
import com.ambition.bi.common.ResultUtils;
import com.ambition.bi.constant.CommonConstant;
import com.ambition.bi.exception.BusinessException;
import com.ambition.bi.exception.ThrowUtils;
import com.ambition.bi.manager.AiManager;
import com.ambition.bi.model.dto.chart.ChartAddRequest;
import com.ambition.bi.model.dto.chart.ChartQueryRequest;
import com.ambition.bi.model.dto.chart.GenChartByAiRequest;
import com.ambition.bi.model.dto.search.SearchRequest;
import com.ambition.bi.model.entity.Chart;
import com.ambition.bi.model.entity.User;
import com.ambition.bi.model.vo.BiResponse;
import com.ambition.bi.service.ChartService;
import com.ambition.bi.service.UserService;
import com.ambition.bi.utils.ExcelUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

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


    @Resource
    private AiManager aiManager;

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
    @PostMapping("/list")
    public BaseResponse<Page<Chart>> list(@RequestBody ChartQueryRequest searchRequest,
                             HttpServletRequest request) {
        System.out.println("searchRequest = " + searchRequest);
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        // 获取用的的查询参数
        String searchText = searchRequest.getName();
        Long currentPage = searchRequest.getCurrent();
        Long pageSize = searchRequest.getPageSize();
        // 判断当前页数是否合法
        if (currentPage == null || currentPage < 1) {
            currentPage = 1L;
        }
        // 判断每页显示条数是否合法
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }
        QueryWrapper<Chart> chartQueryWrapper = new QueryWrapper<>();
        // 模糊搜索
        if (searchText != null && !"".equals(searchText)) {
            chartQueryWrapper.like("name", searchText);
        }
        Page<Chart> chartPage = new Page<>();
        chartPage.setCurrent(currentPage);
        chartPage.setSize(pageSize);
        Page<Chart> page = chartService.page(chartPage, chartQueryWrapper);
        return ResultUtils.success(page);
    }


    /**
     * 删除图表信息
     *
     * @param id      图表id
     * @param request 请求
     * @return
     */
    @GetMapping("/delete")
    public BaseResponse delete(@RequestParam("id") Integer id, HttpServletRequest request) {
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        boolean remove = chartService.removeById(id);
        return ResultUtils.success(remove);
    }

    @PostMapping("/chart/gen")
    public BaseResponse<BiResponse> genChartByAI(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
        // 校验文件后缀 aaa.png
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = userService.getLoginUser(request);
        // 限流判断，每个用户一个限流器
//        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
//        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//                "分析需求：\n" +
//                "{数据分析的需求或者目标}\n" +
//                "原始数据：\n" +
//                "{csv格式的原始数据，用,作为分隔符}\n" +
//                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//                "【【【【【\n" +
//                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
//                "【【【【【\n" +
//                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        long biModelId = CommonConstant.BI_MODEL_ID;
        // 分析需求：
        // 分析网站用户的增长情况
        // 原始数据：
        // 日期,用户数
        // 1号,10
        // 2号,20
        // 3号,30

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

        String result = aiManager.doChat(biModelId, userInput.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenResult(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);

    }


//    @PostMapping("/chart/gen")
//    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
//                                                        GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
//        String name = genChartByAiRequest.getName();
//        String goal = genChartByAiRequest.getGoal();
//        String chartType = genChartByAiRequest.getChartType();
//        // 校验
//        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
//        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
//        // 校验文件
//        long size = multipartFile.getSize();
//        String originalFilename = multipartFile.getOriginalFilename();
//        // 校验文件大小
//        final long ONE_MB = 1024 * 1024L;
//        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
//        // 校验文件后缀 aaa.png
//        String suffix = FileUtil.getSuffix(originalFilename);
//        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
//        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
//
//        User loginUser = userService.getLoginUser(request);
//        // 限流判断，每个用户一个限流器
////        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
//        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
////        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
////                "分析需求：\n" +
////                "{数据分析的需求或者目标}\n" +
////                "原始数据：\n" +
////                "{csv格式的原始数据，用,作为分隔符}\n" +
////                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
////                "【【【【【\n" +
////                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
////                "【【【【【\n" +
////                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
//        long biModelId = 1659171950288818178L;
//        // 分析需求：
//        // 分析网站用户的增长情况
//        // 原始数据：
//        // 日期,用户数
//        // 1号,10
//        // 2号,20
//        // 3号,30
//
//        // 构造用户输入
//        StringBuilder userInput = new StringBuilder();
//        userInput.append("分析需求：").append("\n");
//
//        // 拼接分析目标
//        String userGoal = goal;
//        if (StringUtils.isNotBlank(chartType)) {
//            userGoal += "，请使用" + chartType;
//        }
//        userInput.append(userGoal).append("\n");
//        userInput.append("原始数据：").append("\n");
//        // 压缩后的数据
//        String csvData = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append(csvData).append("\n");
//
//        // 插入到数据库
//        Chart chart = new Chart();
//        chart.setName(name);
//        chart.setGoal(goal);
//        chart.setChartData(csvData);
//        chart.setChartType(chartType);
////        chart.setStatus("wait");
//        chart.setUserId(loginUser.getId());
//        boolean saveResult = chartService.save(chart);
//        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
//        long newChartId = chart.getId();
////        biMessageProducer.sendMessage(String.valueOf(newChartId));
//        BiResponse biResponse = new BiResponse();
////        biResponse.setGenChart(newChartId);
//        return ResultUtils.success(biResponse);
//    }


}

