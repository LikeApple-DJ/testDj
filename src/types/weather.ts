export interface DailyForecast {
  date: string
  maxTemp: number
  minTemp: number
  weatherCode: string
  weatherDesc: string
  weatherIcon: string
  precipitationProbability: number
  windSpeed: number
}

export interface WeatherResponse {
  city: string
  updateTime: string
  forecasts: DailyForecast[]
}
