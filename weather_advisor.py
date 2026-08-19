#!/usr/bin/env python3
"""Travel weather advisor — 7-day forecast with clothing advice, activity
recommendations, and risk warnings (all in Chinese with Emoji)."""

import random
import sys
from datetime import datetime, timedelta

# ---------------------------------------------------------------------------
# Climate data: monthly averages for supported cities
# ---------------------------------------------------------------------------
CLIMATE_DATA = {
    "北京": {
        1:  {"temp": -3, "humidity": 44, "rain_prob": 5,  "wind": 3},
        2:  {"temp": 0,  "humidity": 46, "rain_prob": 8,  "wind": 3},
        3:  {"temp": 7,  "humidity": 50, "rain_prob": 12, "wind": 3},
        4:  {"temp": 15, "humidity": 48, "rain_prob": 15, "wind": 3},
        5:  {"temp": 21, "humidity": 52, "rain_prob": 20, "wind": 2},
        6:  {"temp": 26, "humidity": 58, "rain_prob": 30, "wind": 2},
        7:  {"temp": 28, "humidity": 65, "rain_prob": 40, "wind": 2},
        8:  {"temp": 27, "humidity": 68, "rain_prob": 35, "wind": 2},
        9:  {"temp": 22, "humidity": 60, "rain_prob": 25, "wind": 2},
        10: {"temp": 15, "humidity": 53, "rain_prob": 15, "wind": 2},
        11: {"temp": 5,  "humidity": 48, "rain_prob": 10, "wind": 3},
        12: {"temp": -1, "humidity": 45, "rain_prob": 5,  "wind": 3},
    },
    "上海": {
        1:  {"temp": 5,  "humidity": 72, "rain_prob": 35, "wind": 2},
        2:  {"temp": 7,  "humidity": 70, "rain_prob": 40, "wind": 2},
        3:  {"temp": 11, "humidity": 68, "rain_prob": 45, "wind": 2},
        4:  {"temp": 17, "humidity": 66, "rain_prob": 40, "wind": 2},
        5:  {"temp": 22, "humidity": 68, "rain_prob": 35, "wind": 2},
        6:  {"temp": 26, "humidity": 76, "rain_prob": 45, "wind": 2},
        7:  {"temp": 29, "humidity": 78, "rain_prob": 40, "wind": 2},
        8:  {"temp": 29, "humidity": 80, "rain_prob": 40, "wind": 2},
        9:  {"temp": 25, "humidity": 76, "rain_prob": 35, "wind": 2},
        10: {"temp": 20, "humidity": 72, "rain_prob": 30, "wind": 2},
        11: {"temp": 14, "humidity": 72, "rain_prob": 35, "wind": 2},
        12: {"temp": 8,  "humidity": 70, "rain_prob": 30, "wind": 2},
    },
    "三亚": {
        1:  {"temp": 22, "humidity": 80, "rain_prob": 20, "wind": 2},
        2:  {"temp": 23, "humidity": 80, "rain_prob": 15, "wind": 2},
        3:  {"temp": 25, "humidity": 82, "rain_prob": 20, "wind": 2},
        4:  {"temp": 27, "humidity": 82, "rain_prob": 25, "wind": 2},
        5:  {"temp": 29, "humidity": 82, "rain_prob": 30, "wind": 2},
        6:  {"temp": 30, "humidity": 82, "rain_prob": 35, "wind": 2},
        7:  {"temp": 30, "humidity": 82, "rain_prob": 35, "wind": 2},
        8:  {"temp": 30, "humidity": 82, "rain_prob": 40, "wind": 2},
        9:  {"temp": 29, "humidity": 82, "rain_prob": 40, "wind": 2},
        10: {"temp": 27, "humidity": 80, "rain_prob": 35, "wind": 2},
        11: {"temp": 25, "humidity": 78, "rain_prob": 25, "wind": 2},
        12: {"temp": 23, "humidity": 78, "rain_prob": 20, "wind": 2},
    },
}

WEEKDAY_NAMES = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]

# ---------------------------------------------------------------------------
# Helper: generate a single day's forecast from monthly climate data
# ---------------------------------------------------------------------------


def _daily_forecast(city, date):
    """Generate one day's forecast based on the city's monthly climate data."""
    month = date.month
    base = CLIMATE_DATA[city][month]

    # Deterministic seed for reproducibility
    rng = random.Random(f"{city}-{date.isoformat()}")

    temp_variation = rng.randint(-3, 3)
    humidity_variation = rng.randint(-5, 5)
    rain_roll = rng.randint(0, 100)

    temp = base["temp"] + temp_variation
    humidity = max(0, min(100, base["humidity"] + humidity_variation))
    rain_prob = base["rain_prob"]
    # Whether it actually rains depends on the roll
    is_rainy = rain_roll < rain_prob
    wind = base["wind"] + rng.choice([-1, 0, 0, 1])
    wind = max(1, min(6, wind))

    return {
        "city": city,
        "date": date,
        "temp": temp,
        "humidity": humidity,
        "rain_prob": rain_prob,
        "is_rainy": is_rainy,
        "wind": wind,
    }


# ---------------------------------------------------------------------------
# Public API
# ---------------------------------------------------------------------------


def generate_forecast(city, start_date, end_date):
    """Return a list of 7 daily forecast dicts for *city* from *start_date*
    to *end_date* (inclusive).

    *start_date* and *end_date* are ``datetime.date`` objects.
    """
    forecasts = []
    current = start_date
    while current <= end_date:
        forecasts.append(_daily_forecast(city, current))
        current += timedelta(days=1)
    return forecasts


def _weather_emoji(temp, is_rainy):
    """Pick an appropriate weather Emoji."""
    if is_rainy:
        return "🌧️"
    if temp >= 30:
        return "☀️"
    if temp >= 20:
        return "⛅"
    if temp >= 10:
        return "🌤️"
    if temp >= 0:
        return "☁️"
    return "❄️"


def _clothing_advice(temp, is_rainy):
    """Return clothing recommendation based on temperature and rain."""
    if temp < -5:
        advice = "羽绒服、厚毛衣、围巾、手套、棉帽 🧣"
    elif temp < 0:
        advice = "羽绒服、厚毛衣、围巾手套 🧣"
    elif temp < 10:
        advice = "厚外套、毛衣、保暖裤 🧥"
    elif temp < 18:
        advice = "薄外套、长袖衬衫 👔"
    elif temp < 25:
        advice = "长袖T恤、薄裤 👕"
    elif temp < 30:
        advice = "短袖、短裤、裙子 👗"
    else:
        advice = "短袖短裤、防晒衣、帽子 🧢"
    if is_rainy:
        advice += "；带雨伞或雨衣 🌂"
    return advice


def _activity_recommendation(temp, is_rainy, wind):
    """Return activity suggestion based on weather conditions."""
    if is_rainy:
        if wind >= 4:
            return "建议室内活动，如博物馆、咖啡馆 ☕"
        return "建议室内活动，如逛商场、看电影 🎬"
    if temp >= 35:
        return "避免中午户外活动，建议清晨或傍晚散步 🌅"
    if temp >= 30:
        return "适宜游泳、水上乐园 🏊"
    if temp >= 20:
        return "适宜户外运动、骑行、野餐 🚴"
    if temp >= 10:
        return "适宜徒步、登山、公园漫步 🏞️"
    if temp >= 0:
        return "适宜短时户外活动，注意保暖 🚶"
    return "建议室内运动，如健身房、瑜伽 🧘"


def _risk_warnings(temp, is_rainy, wind, rain_prob, humidity):
    """Return a list of risk warning strings."""
    warnings = []
    if temp >= 35:
        warnings.append("🔥 高温预警：注意防暑降温")
    elif temp >= 33:
        warnings.append("⚠️ 气温较高，注意补充水分")
    if temp <= -5:
        warnings.append("🥶 极寒预警：注意防冻保暖")
    elif temp <= 0:
        warnings.append("⚠️ 气温较低，注意保暖")
    if rain_prob >= 50:
        warnings.append("☔ 降雨概率较高，请携带雨具")
    elif rain_prob >= 30:
        warnings.append("🌂 可能有雨，建议携带雨具")
    if wind >= 5:
        warnings.append("💨 大风预警：注意防风")
    elif wind == 4:
        warnings.append("🌬️ 风力较大，户外注意安全")
    if temp >= 30 and humidity >= 75:
        warnings.append("🥵 闷热潮湿，注意防暑")
    if not warnings:
        warnings.append("✅ 无特殊风险")
    return warnings


def format_output(forecast):
    """Print the 7-day forecast in four sections with Emoji."""
    lines = []
    city = forecast[0]["city"]

    # Header
    start_str = forecast[0]["date"].strftime("%Y-%m-%d")
    end_str = forecast[-1]["date"].strftime("%Y-%m-%d")
    lines.append(f"🌤️  {city} 7日天气预报")
    lines.append(f"📅  {start_str} ~ {end_str}")
    lines.append("")

    # Section 1: 每日天气摘要
    lines.append("📋 每日天气摘要")
    lines.append("━" * 30)
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        emoji = _weather_emoji(day["temp"], day["is_rainy"])
        lines.append(
            f"📆 {d.month}月{d.day}日 ({wd}): {emoji} "
            f"{day['temp']}°C | 💧 {day['humidity']}% | "
            f"🌧️ {day['rain_prob']}% | 💨 {day['wind']}级"
        )
    lines.append("")

    # Section 2: 穿衣指南
    lines.append("👔 穿衣指南")
    lines.append("━" * 30)
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        advice = _clothing_advice(day["temp"], day["is_rainy"])
        lines.append(f"📆 {d.month}月{d.day}日 ({wd}): {advice}")
    lines.append("")

    # Section 3: 活动推荐
    lines.append("🎯 活动推荐")
    lines.append("━" * 30)
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        activity = _activity_recommendation(
            day["temp"], day["is_rainy"], day["wind"]
        )
        lines.append(f"📆 {d.month}月{d.day}日 ({wd}): {activity}")
    lines.append("")

    # Section 4: 风险提示
    lines.append("⚠️ 风险提示")
    lines.append("━" * 30)
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        warnings = _risk_warnings(
            day["temp"], day["is_rainy"], day["wind"], day["rain_prob"], day["humidity"]
        )
        for w in warnings:
            lines.append(f"📆 {d.month}月{d.day}日 ({wd}): {w}")
    sys.stdout.write("\n".join(lines) + "\n")


def main():
    """Parse CLI args, validate, generate forecast, and print output."""
    if len(sys.argv) != 4:
        print("用法: python3 weather_advisor.py <城市> <开始日期> <结束日期>",
              file=sys.stderr)
        print("示例: python3 weather_advisor.py 北京 2025-06-01 2025-06-07",
              file=sys.stderr)
        sys.exit(1)

    city = sys.argv[1]
    start_str = sys.argv[2]
    end_str = sys.argv[3]

    if city not in CLIMATE_DATA:
        print(f"错误: 未知城市 '{city}'。支持的城市: "
              f"{'、'.join(CLIMATE_DATA.keys())}", file=sys.stderr)
        sys.exit(1)

    try:
        start_date = datetime.strptime(start_str, "%Y-%m-%d").date()
        end_date = datetime.strptime(end_str, "%Y-%m-%d").date()
    except ValueError:
        print("错误: 日期格式无效，请使用 YYYY-MM-DD 格式。", file=sys.stderr)
        sys.exit(1)

    if start_date > end_date:
        print("错误: 开始日期不能晚于结束日期。", file=sys.stderr)
        sys.exit(1)

    # Ensure exactly 7 days
    delta = (end_date - start_date).days + 1
    if delta != 7:
        print(f"错误: 日期范围必须为7天（当前{delta}天）。", file=sys.stderr)
        sys.exit(1)

    forecast = generate_forecast(city, start_date, end_date)
    format_output(forecast)


if __name__ == "__main__":
    main()
