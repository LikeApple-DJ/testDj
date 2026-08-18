import http from './http'
import type { WeatherResponse } from '@/types/weather'

export function get7DayForecast(city: string = '杭州') {
  return http.get<WeatherResponse>('/weather/forecast/7days', {
    params: { city }
  })
}
