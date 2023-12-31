package com.ambition.bi.controller;


import cn.hutool.core.io.FileUtil;
import com.ambition.bi.common.BaseResponse;
import com.ambition.bi.common.DeleteRequest;
import com.ambition.bi.constant.ChartStateConstant;
import com.ambition.bi.common.ErrorCode;
import com.ambition.bi.common.ResultUtils;
import com.ambition.bi.constant.CommonConstant;
import com.ambition.bi.exception.BusinessException;
import com.ambition.bi.exception.ThrowUtils;
import com.ambition.bi.manager.AiManager;
import com.ambition.bi.manager.RedisLimiterManager;
import com.ambition.bi.manager.bimq.BiMessageProducer;
import com.ambition.bi.manager.mongodb.MongoDBUtil;
import com.ambition.bi.model.dto.chart.ChartQueryRequest;
import com.ambition.bi.model.dto.chart.GenChartByAiRequest;
import com.ambition.bi.model.entity.Chart;
import com.ambition.bi.model.entity.User;
import com.ambition.bi.model.entity.mongo.AnalyzeRawData;
import com.ambition.bi.model.vo.BiResponse;
import com.ambition.bi.service.ChartService;
import com.ambition.bi.service.UserService;
import com.ambition.bi.utils.ExcelUtils;
import com.ambition.bi.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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
@Slf4j
public class ChartController {


    @Resource
    private ChartService chartService;


    @Resource
    private UserService userService;


    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;


    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private BiMessageProducer biMessageProducer;


    @Resource
    private MongoDBUtil mongoDBUtil;


    @Resource
    private MongoTemplate mongoTemplate;

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
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }


    /**
     * 分页 + 模糊查询
     *
     * @param searchRequest 查询参数
     * @param request       请求
     * @return 分页数据
     */
    @PostMapping("/list")
    public BaseResponse<Page<Chart>> list(@RequestBody ChartQueryRequest searchRequest, HttpServletRequest request) {
        System.out.println("searchRequest = " + searchRequest);
        // 判断用户是否登陆
        if (searchRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        searchRequest.setUserId(loginUser.getId());
        long current = searchRequest.getCurrent();
        long size = searchRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size), getQueryWrapper(searchRequest));
        return ResultUtils.success(chartPage);

    }


    /**
     * 删除图表信息
     *
     * @param request 请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 判断用户是否登陆
        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        boolean remove = chartService.removeById(id);
        if (!remove) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "删除失败");
        } else {
            return ResultUtils.success("删除成功");
        }
    }

    @PostMapping("/chart/gen")
    public BaseResponse<BiResponse> genChartByAI(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        // 检查用户是否登陆
        User loginUser = userService.getLoginUser(request);

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
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls", "csv");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");


        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        long biModelId = CommonConstant.BI_MODEL_ID;

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

        // TODO 将chart表进行拆分 将原始数据单独存储起来 避免造成将所有的数据都存到一张表中 造成影响mysql的性能
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
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


    /**
     * 智能分析（异步）
     *
     * @param multipartFile       文件
     * @param genChartByAiRequest 请求
     * @param request             请求
     * @return 结果
     */
    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
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
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = userService.getLoginUser(request);
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        long biModelId = CommonConstant.BI_MODEL_ID;

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

        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus(ChartStateConstant.WAIT_STATUS);
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // TODO 建议处理任务队列满了后，抛异常的情况
        CompletableFuture.runAsync(() -> {
            // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus(ChartStateConstant.RUNNING_STATUS);
            boolean b = chartService.updateById(updateChart);
            if (!b) {
                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
                return;
            }
            // 调用 AI
            String result = aiManager.doChat(biModelId, userInput.toString());
            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
                handleChartUpdateError(chart.getId(), "AI 生成错误");
                return;
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setStatus(ChartStateConstant.SUCCEED_STATUS);
            boolean updateResult = chartService.updateById(updateChartResult);
            if (!updateResult) {
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }
        }, threadPoolExecutor);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    /**
     * 智能分析（异步消息队列）
     *
     * @param multipartFile       文件
     * @param genChartByAiRequest 请求参数
     * @param request             请求
     * @return 响应
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
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
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");


        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());


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

        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        // 将csvData 也就是原始数据存入到mongoDB中 而不是chart表中
        // 避免chart表中数据过大 造成查询效率低下
        // 而将原始数据保存到mongoDB中 通过chartId进行关联 当用户查询时 通过chartId查询mongoDB中的数据
        // 分表的思想
        chart.setChartData(null);

        chart.setChartType(chartType);
        chart.setStatus(ChartStateConstant.WAIT_STATUS);
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        long newChartId = chart.getId();
        InsertOneResult insertOneResult = mongoDBUtil.getMongoCollection(AnalyzeRawData.class).insertOne(new AnalyzeRawData(newChartId, csvData));
        System.out.println(insertOneResult);
        System.out.println(chart.getChartData());
        biMessageProducer.sendMessage(String.valueOf(newChartId));
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return ResultUtils.success(biResponse);
    }


    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(ChartStateConstant.FAILED_STATUS);
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

    /**
     * 根据您的描述，当您将 getAnalyzeRawData 方法的修饰符设为 private 时，
     * 无法访问 mongoDBUtil 变量并且它的值为 null。而当您将修饰符设为 public 时，
     * 您可以正确访问到 mongoDBUtil 变量并且它具有具体的值。
     * <p>
     * 这种行为可能是由于不同的访问级别造成的。当方法被声明为 private 时，
     * 它只能在当前类中访问。这意味着，如果 mongoDBUtil 是一个类的成员变量，
     * private 方法无法直接访问它，除非在当前类的其他方法中对它进行初始化或赋值。
     * 否则，在 private 方法中访问该成员变量将得到 null 值。
     * <p>
     *
     * @param deleteRequest 请求
     * @return 响应
     */

    @PostMapping("/analyze/data")
    public BaseResponse<String> getAnalyzeRawData(@RequestBody DeleteRequest deleteRequest) {

        long id = deleteRequest.getId();
        Bson query = Filters.eq("_id", id);
        AnalyzeRawData analyzeRawData = mongoDBUtil.getMongoCollection(AnalyzeRawData.class).find(query).first();
        return ResultUtils.success(analyzeRawData.getData().toString());
    }
}

