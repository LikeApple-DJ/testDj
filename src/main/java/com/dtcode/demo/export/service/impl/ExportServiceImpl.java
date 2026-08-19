package com.dtcode.demo.export.service.impl;

import com.dtcode.demo.common.exception.BusinessException;
import com.dtcode.demo.demo.model.dto.BubbleSortDTO;
import com.dtcode.demo.demo.model.dto.HashDTO;
import com.dtcode.demo.demo.model.dto.HelloWorldDTO;
import com.dtcode.demo.demo.service.impl.DemoServiceImpl;
import com.dtcode.demo.export.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 导出服务实现
 *
 * @author DTCoder
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    private final DemoServiceImpl demoServiceImpl;

    public ExportServiceImpl(DemoServiceImpl demoServiceImpl) {
        this.demoServiceImpl = demoServiceImpl;
    }

    @Override
    public byte[] exportHelloWorld() {
        Object cached = demoServiceImpl.getCachedResult(DemoServiceImpl.CACHE_KEY_HELLOWORLD);
        if (cached == null) {
            throw new BusinessException("EXPORT_001", "无可导出的数据，请先执行 HelloWorld 接口");
        }
        HelloWorldDTO dto = (HelloWorldDTO) cached;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            writer.println("name,result,timestamp");
            writer.println("\"World\",\"" + escapeCsv(dto.getResult()) + "\",\"" + dto.getTimestamp() + "\"");
            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("导出 HelloWorld 结果失败: {}", e.getMessage(), e);
            throw new BusinessException("EXPORT_002", "文件生成异常", e);
        }
    }

    @Override
    public byte[] exportHash() {
        Object cached = demoServiceImpl.getCachedResult(DemoServiceImpl.CACHE_KEY_HASH);
        if (cached == null) {
            throw new BusinessException("EXPORT_001", "无可导出的数据，请先执行哈希算法接口");
        }
        HashDTO dto = (HashDTO) cached;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            writer.println("input,algorithm,hash_value,timestamp");
            writer.println("\"" + escapeCsv(dto.getInput()) + "\",\"" + dto.getAlgorithm()
                    + "\",\"" + dto.getHashValue() + "\",\"" + dto.getTimestamp() + "\"");
            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("导出哈希结果失败: {}", e.getMessage(), e);
            throw new BusinessException("EXPORT_002", "文件生成异常", e);
        }
    }

    @Override
    public byte[] exportBubbleSort() {
        Object cached = demoServiceImpl.getCachedResult(DemoServiceImpl.CACHE_KEY_BUBBLE_SORT);
        if (cached == null) {
            throw new BusinessException("EXPORT_001", "无可导出的数据，请先执行冒泡排序接口");
        }
        BubbleSortDTO dto = (BubbleSortDTO) cached;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            writer.println("original,sorted,timestamp");
            writer.println("\"" + dto.getOriginal() + "\",\"" + dto.getSorted()
                    + "\",\"" + dto.getTimestamp() + "\"");
            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("导出排序结果失败: {}", e.getMessage(), e);
            throw new BusinessException("EXPORT_002", "文件生成异常", e);
        }
    }

    /**
     * CSV 转义：将双引号转义为两个双引号
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
}
