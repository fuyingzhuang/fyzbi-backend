package com.ambition.bi.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.ambition.bi.common.ErrorCode;
import com.ambition.bi.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ambition
 * @date 2023/11/15 22:57
 */
@Slf4j
public class ExcelUtils {

    public static String excelToCsv(MultipartFile file) {

        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(file.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件读取失败");
        }


        StringBuilder dataStringBuilder = new StringBuilder();
        // 获取表头
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap) list.get(0);
        // 过滤空值
        List<String> headList = headMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());

//        System.out.println(StringUtils.join(headList, ","));
        dataStringBuilder.append(StringUtils.join(headList, ",")).append("\n");
        for (int i = 1; i < list.size(); i++) {
            // 过滤空值

            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
//            System.out.println(StringUtils.join(dataList, ","));
            dataStringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }

        return dataStringBuilder.toString();
    }
}
