#!/usr/bin/env python3
"""Travel weather advisor — 7-day forecast with clothing advice, activity
recommendations, and risk warnings (all in Chinese with Emoji)."""

import json
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


# ---------------------------------------------------------------------------
# Web server
# ---------------------------------------------------------------------------

WEB_HTML = r"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>🌤️ 旅行气象顾问</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;background:#f0f4f8;color:#333;min-height:100vh}
.header{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;padding:24px;text-align:center}
.header h1{font-size:1.8em;margin-bottom:4px}
.header p{opacity:.85;font-size:.95em}
.container{max-width:800px;margin:0 auto;padding:20px}
.form-card{background:#fff;border-radius:12px;padding:24px;box-shadow:0 2px 12px rgba(0,0,0,.08);margin-bottom:20px}
.form-row{display:flex;gap:12px;flex-wrap:wrap;align-items:flex-end}
.form-group{flex:1;min-width:140px}
.form-group label{display:block;font-weight:600;margin-bottom:4px;font-size:.9em;color:#555}
.form-group select,.form-group input{width:100%;padding:10px 12px;border:1px solid #ddd;border-radius:8px;font-size:1em;transition:border-color .2s}
.form-group select:focus,.form-group input:focus{outline:none;border-color:#667eea;box-shadow:0 0 0 3px rgba(102,126,234,.15)}
.btn{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border:none;padding:11px 28px;border-radius:8px;font-size:1em;cursor:pointer;font-weight:600;transition:opacity .2s,transform .1s;white-space:nowrap}
.btn:hover{opacity:.9;transform:translateY(-1px)}
.btn:active{transform:translateY(0)}
.error{background:#fff3f3;color:#c0392b;padding:12px 16px;border-radius:8px;margin-bottom:20px;border:1px solid #f5c6cb;display:none}
.result-card{background:#fff;border-radius:12px;padding:24px;box-shadow:0 2px 12px rgba(0,0,0,.08);margin-bottom:20px;display:none}
.result-card h2{font-size:1.2em;margin-bottom:12px;padding-bottom:8px;border-bottom:2px solid #667eea;color:#444}
.day-row{display:flex;align-items:center;padding:8px 0;border-bottom:1px solid #f0f0f0;gap:12px;flex-wrap:wrap}
.day-row:last-child{border-bottom:none}
.day-label{font-weight:600;min-width:100px;color:#555}
.day-detail{flex:1;color:#666}
.spinner{display:none;text-align:center;padding:20px}
.spinner::after{content:'';display:inline-block;width:32px;height:32px;border:3px solid #ddd;border-top-color:#667eea;border-radius:50%;animation:spin .6s linear infinite}
@keyframes spin{to{transform:rotate(360deg)}}
</style>
</head>
<body>
<div class="header"><h1>🌤️ 旅行气象顾问</h1><p>7日天气预报 · 穿衣指南 · 活动推荐 · 风险提示</p></div>
<div class="container">
<div class="form-card">
<div class="form-row">
<div class="form-group"><label for="city">城市</label><select id="city"><option value="北京">北京</option><option value="上海">上海</option><option value="三亚">三亚</option></select></div>
<div class="form-group"><label for="start">开始日期</label><input type="date" id="start" value="2025-06-01"></div>
<div class="form-group"><label for="end">结束日期</label><input type="date" id="end" value="2025-06-07"></div>
<button class="btn" onclick="fetchForecast()">🔍 查询</button>
</div>
</div>
<div class="error" id="error"></div>
<div class="spinner" id="spinner"></div>
<div class="result-card" id="result"></div>
</div>
<script>
async function fetchForecast(){
 const city=document.getElementById('city').value;
 const start=document.getElementById('start').value;
 const end=document.getElementById('end').value;
 const err=document.getElementById('error');
 const res=document.getElementById('result');
 const spin=document.getElementById('spinner');
 err.style.display='none';res.style.display='none';spin.style.display='block';
 try{
  const r=await fetch('/forecast?city='+encodeURIComponent(city)+'&start='+encodeURIComponent(start)+'&end='+encodeURIComponent(end));
  if(!r.ok){const t=await r.text();err.textContent=t;err.style.display='block';spin.style.display='none';return}
  const html=await r.text();
  res.innerHTML=html;res.style.display='block';spin.style.display='none';
 }catch(e){err.textContent='网络错误，请稍后重试';err.style.display='block';spin.style.display='none'}
}
</script>
</body>
</html>"""


def _forecast_to_html(forecast):
    """Convert a forecast list to an HTML string."""
    parts = []
    city = forecast[0]["city"]
    start_str = forecast[0]["date"].strftime("%Y-%m-%d")
    end_str = forecast[-1]["date"].strftime("%Y-%m-%d")

    parts.append(f'<h2>🌤️ {city} 7日天气预报</h2>')
    parts.append(f'<p style="color:#888;margin-bottom:16px">📅 {start_str} ~ {end_str}</p>')

    # Section 1: 每日天气摘要
    parts.append('<h2>📋 每日天气摘要</h2>')
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        emoji = _weather_emoji(day["temp"], day["is_rainy"])
        parts.append(
            f'<div class="day-row"><span class="day-label">📆 {d.month}月{d.day}日 ({wd})</span>'
            f'<span class="day-detail">{emoji} {day["temp"]}°C | 💧 {day["humidity"]}% | '
            f'🌧️ {day["rain_prob"]}% | 💨 {day["wind"]}级</span></div>'
        )

    # Section 2: 穿衣指南
    parts.append('<h2>👔 穿衣指南</h2>')
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        advice = _clothing_advice(day["temp"], day["is_rainy"])
        parts.append(
            f'<div class="day-row"><span class="day-label">📆 {d.month}月{d.day}日 ({wd})</span>'
            f'<span class="day-detail">{advice}</span></div>'
        )

    # Section 3: 活动推荐
    parts.append('<h2>🎯 活动推荐</h2>')
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        activity = _activity_recommendation(day["temp"], day["is_rainy"], day["wind"])
        parts.append(
            f'<div class="day-row"><span class="day-label">📆 {d.month}月{d.day}日 ({wd})</span>'
            f'<span class="day-detail">{activity}</span></div>'
        )

    # Section 4: 风险提示
    parts.append('<h2>⚠️ 风险提示</h2>')
    for day in forecast:
        d = day["date"]
        wd = WEEKDAY_NAMES[d.weekday()]
        warnings = _risk_warnings(
            day["temp"], day["is_rainy"], day["wind"], day["rain_prob"], day["humidity"]
        )
        for w in warnings:
            parts.append(
                f'<div class="day-row"><span class="day-label">📆 {d.month}月{d.day}日 ({wd})</span>'
                f'<span class="day-detail">{w}</span></div>'
            )

    return "\n".join(parts)


def main():
    """Parse CLI args, validate, generate forecast, and print output."""
    if len(sys.argv) == 2 and sys.argv[1] == "--web":
        _run_web_server()
        return
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


def _run_web_server():
    """Start a simple HTTP server with the weather advisor web UI."""
    import urllib.parse
    from http.server import HTTPServer, BaseHTTPRequestHandler

    class Handler(BaseHTTPRequestHandler):
        def do_GET(self):
            parsed = urllib.parse.urlparse(self.path)
            path = parsed.path
            params = urllib.parse.parse_qs(parsed.query)

            if path == "/":
                self._serve_html(WEB_HTML)
            elif path == "/forecast":
                self._handle_forecast(params)
            else:
                self.send_response(404)
                self.end_headers()
                self.wfile.write(b"Not Found")

        def _serve_html(self, html):
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.end_headers()
            self.wfile.write(html.encode("utf-8"))

        def _handle_forecast(self, params):
            try:
                city = params.get("city", [None])[0]
                start_str = params.get("start", [None])[0]
                end_str = params.get("end", [None])[0]
                if not city or not start_str or not end_str:
                    raise ValueError("缺少参数")
                if city not in CLIMATE_DATA:
                    raise ValueError(
                        f"未知城市 '{city}'。支持: {'、'.join(CLIMATE_DATA.keys())}"
                    )
                start_date = datetime.strptime(start_str, "%Y-%m-%d").date()
                end_date = datetime.strptime(end_str, "%Y-%m-%d").date()
                if start_date > end_date:
                    raise ValueError("开始日期不能晚于结束日期")
                delta = (end_date - start_date).days + 1
                if delta != 7:
                    raise ValueError(f"日期范围必须为7天（当前{delta}天）")

                forecast = generate_forecast(city, start_date, end_date)
                html = _forecast_to_html(forecast)
                self._serve_html(html)
            except ValueError as e:
                self.send_response(400)
                self.send_header("Content-Type", "text/plain; charset=utf-8")
                self.end_headers()
                self.wfile.write(str(e).encode("utf-8"))

        def log_message(self, format, *args):
            pass  # suppress logs

    host = "127.0.0.1"
    port = 8080
    server = HTTPServer((host, port), Handler)
    print(f"🌤️  旅行气象顾问 Web 服务已启动: http://{host}:{port}")
    print("按 Ctrl+C 停止服务")
    server.serve_forever()


if __name__ == "__main__":
    main()
