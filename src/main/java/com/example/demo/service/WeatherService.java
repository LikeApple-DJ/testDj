package com.example.demo.service;

import com.example.demo.dto.WeatherDay;
import com.example.demo.dto.WeatherResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private static final String DRESS_ADVICE = "短衫、短裤、T恤等清凉夏季服装。"
            + "雨天备速干面料，必备折叠伞。防滑凉鞋/网面运动鞋最佳。";

    private static final String OUTDOOR_STRATEGY = "黄金窗口：8月18-19日（今明两天）最佳户外时间，"
            + "建议把西湖、灵隐寺、龙井村等户外景点集中在这两天。"
            + "雨天策略：8月20日和22日中雨时段建议安排博物馆、书店、美食等室内活动，"
            + "利用雨停间隙快速打卡户外景点。";

    public WeatherResponse getWeather(String city) {
        if (!"hangzhou".equalsIgnoreCase(city) && !"杭州".equals(city)) {
            throw new IllegalArgumentException("暂仅支持杭州 (hangzhou) 天气查询");
        }

        List<WeatherDay> days = new ArrayList<>();
        days.add(new WeatherDay("2026-08-18", "周二", "阴", 34, 26,
                "适宜", "西湖骑行、龙井村茶文化体验"));
        days.add(new WeatherDay("2026-08-19", "周三", "阴转多云", 34, 26,
                "适宜", "灵隐寺+飞来峰、西溪湿地摇橹船"));
        days.add(new WeatherDay("2026-08-20", "周四", "中雨转小雨", 32, 26,
                "谨慎", "浙江省博物馆、清河坊+南宋御街"));
        days.add(new WeatherDay("2026-08-21", "周五", "小雨转多云", 32, 25,
                "可出行", "晓书馆/茑屋书店、中国美术学院象山校区"));
        days.add(new WeatherDay("2026-08-22", "周六", "中雨转多云", 31, 26,
                "谨慎", "杭帮菜美食之旅（楼外楼/知味观）"));
        days.add(new WeatherDay("2026-08-23", "周日", "小雨", 32, 25,
                "可出行", "杭州博物馆、南宋御街文艺小店"));
        days.add(new WeatherDay("2026-08-24", "周一", "小雨转阴", 33, 26,
                "可出行", "九溪烟树、云栖竹径雨后漫步"));

        return new WeatherResponse("杭州", Instant.now().toString(),
                days, DRESS_ADVICE, OUTDOOR_STRATEGY);
    }
}