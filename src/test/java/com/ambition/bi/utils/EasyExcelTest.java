package com.ambition.bi.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * EasyExcel 测试
 *
 * @author ambition
 */
@SpringBootTest
public class EasyExcelTest {

    @Test
    public void doImport() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:test_excel.xlsx");
        List<Map<Integer, String>> list = EasyExcel.read(file)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();


        StringBuilder dataStringBuilder = new StringBuilder();
        // 获取表头
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap)list.get(0);
        // 过滤空值
        List<String> headList = headMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());

//        System.out.println(StringUtils.join(headList, ","));
        dataStringBuilder.append(StringUtils.join(headList, ",")).append("\n");
        for (int i = 1; i < list.size(); i++) {
            // 过滤空值

            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap)list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
//            System.out.println(StringUtils.join(dataList, ","));
            dataStringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
    }

}
