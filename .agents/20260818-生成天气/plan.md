# 旅行气象顾问 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Create a Python travel weather consultant script that generates a 7-day weather forecast for a given city and date range, with clothing advice, activity recommendations, and risk warnings.

**Architecture:** Single-file Python script `weather_advisor.py` using `#!/usr/bin/env python3` shebang. The script embeds climate data for major cities, accepts command-line arguments (city, start_date, end_date), and outputs a friendly forecast with Emoji.

**Tech Stack:** Python 3 (system pre-installed), stdlib only (`sys`, `datetime`, `random` with fixed seed for reproducibility).

---

## Global Constraints

- File path: `weather_advisor.py` (root of repository)
- Must accept: `python3 weather_advisor.py <city> <start_date> <end_date>` (dates in YYYY-MM-DD format)
- Must output a 7-day forecast with four sections: 每日天气摘要, 穿衣指南, 活动推荐, 风险提示
- Must use Emoji for readability
- Must handle at least 3 cities with embedded climate data (北京, 上海, 三亚)
- Unknown cities: output a friendly error message and exit with code 1
- Zero external dependencies (stdlib only)
- Output must be in Chinese with friendly, clear tone

---

## Task 1: Create weather_advisor.py

**Files:**
- Create: `weather_advisor.py`

**Interfaces:**
- CLI: `python3 weather_advisor.py <city> <start_date> <end_date>`
- Produces: formatted stdout with 7-day weather forecast

---

- [ ] **Step 1: Write the weather advisor script**

  The script must contain:
  - Climate data dict for at least 北京, 上海, 三亚 (monthly avg temp, humidity, rain probability, wind level)
  - `generate_forecast(city, start_date, end_date)` — returns 7 days of forecast data
  - `format_output(forecast)` — prints the four sections with Emoji
  - `main()` — parses CLI args, validates, calls generate + format

- [ ] **Step 2: Verify syntax**

  Run: `python3 -m py_compile weather_advisor.py`
  Expected: exit code 0, clean compile

- [ ] **Step 3: Verify help/error for missing args**

  Run: `python3 weather_advisor.py`
  Expected: exit code 1, usage message

- [ ] **Step 4: Verify unknown city**

  Run: `python3 weather_advisor.py 火星 2025-06-01 2025-06-07`
  Expected: exit code 1, error message about unknown city

- [ ] **Step 5: Verify valid forecast (北京)**

  Run: `python3 weather_advisor.py 北京 2025-06-01 2025-06-07`
  Expected: exit code 0, 7-day forecast with all four sections, Emoji present

- [ ] **Step 6: Verify valid forecast (上海)**

  Run: `python3 weather_advisor.py 上海 2025-12-20 2025-12-26`
  Expected: exit code 0, 7-day forecast with all four sections

- [ ] **Step 7: Verify valid forecast (三亚)**

  Run: `python3 weather_advisor.py 三亚 2025-01-10 2025-01-16`
  Expected: exit code 0, 7-day forecast with all four sections

- [ ] **Step 8: Commit**

  ```bash
  git add weather_advisor.py
  git commit -m "feat: add travel weather advisor script"
  ```

---

## Self-Review

### 1. Spec Coverage

| Requirement | Covered By |
|-------------|-----------|
| CLI with city, start, end | Task 1, Step 1 + Step 5-7 |
| 7-day forecast | Task 1, Step 1, generate_forecast |
| 每日天气摘要 | Task 1, Step 1, format_output |
| 穿衣指南 | Task 1, Step 1, format_output |
| 活动推荐 | Task 1, Step 1, format_output |
| 风险提示 | Task 1, Step 1, format_output |
| Emoji | Task 1, Step 1, format_output |
| 3 cities minimum | Task 1, Steps 5-7 |
| Unknown city error | Task 1, Step 4 |
| Zero dependencies | stdlib only |
| Chinese output | Task 1, Step 1 |

### 2. Placeholder Scan

No TBD, TODO, or vague directives. All steps contain exact checks.

### 3. Type Consistency

Single task, single file — no cross-task type mismatches.