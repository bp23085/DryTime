package app.weather;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired; // importを追加
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LaundryAdvisor {

    // finalフィールドとして宣言
    private final WeatherApiClient weatherApiClient;
    private final ConditionEvaluator conditionEvaluator;

    // コンストラクタインジェクションを使用
    @Autowired
    public LaundryAdvisor(WeatherApiClient weatherApiClient, ConditionEvaluator conditionEvaluator) {
        this.weatherApiClient = weatherApiClient;
        this.conditionEvaluator = conditionEvaluator;
    }

    @Async
    public CompletableFuture<String> getDryingHoursAsync(double latitude, double longitude) {
        return CompletableFuture.supplyAsync(() -> getDryingHours(latitude, longitude));
    }

    public String getDryingHours(double latitude, double longitude) {
        try {
            JsonNode weatherData = weatherApiClient.getWeatherData(latitude, longitude);

            JsonNode temperatures = weatherData.path("hourly").path("temperature_2m");
            JsonNode humidities = weatherData.path("hourly").path("relative_humidity_2m");
            JsonNode weatherCodes = weatherData.path("hourly").path("weathercode");

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            List<String> todayResults = new ArrayList<>();
            List<String> tomorrowResults = new ArrayList<>();

            for (int i = 0; i < 48; i++) {
                int time = i % 24;
                // 48時間のループの中で、現在処理しているのが今日か明日かを判断
                LocalDate currentDate = (i < 24) ? today : tomorrow;

                double temperature = temperatures.get(i).asDouble();
                int humidity = humidities.get(i).asInt();
                int weatherCode = weatherCodes.get(i).asInt();

                // 変更したevaluateメソッドを呼び出す
                String evaluation = conditionEvaluator.evaluate(temperature, humidity, weatherCode, time, currentDate);
                if ("〇".equals(evaluation) || "◎".equals(evaluation)) {
                    String resultText = String.format("%d時：%s", time, evaluation);
                    if (currentDate.equals(today)) {
                        todayResults.add(resultText);
                    } else {
                        tomorrowResults.add(resultText);
                    }
                }
            }

            // 結果文字列の生成ロジックを改善
            StringBuilder result = new StringBuilder();
            result.append("緯度: ").append(latitude).append(", 経度: ").append(longitude).append("\n\n");

            result.append("＜本日＞\n");
            if (todayResults.isEmpty()) {
                result.append("乾きやすい時間帯はありません。");
            } else {
                // 区切り文字をカンマ(,)から改行(\n)に変更
                result.append(String.join("\n", todayResults));
            }

            result.append("\n\n＜明日＞\n");
            if (tomorrowResults.isEmpty()) {
                result.append("乾きやすい時間帯はありません。");
            } else {
                // 区切り文字をカンマ(,)から改行(\n)に変更
                result.append(String.join("\n", tomorrowResults));
            }

            return result.toString();
        } catch (Exception e) {
            // サーバーログにスタックトレースを出力するとデバッグに役立ちます
            e.printStackTrace();
            return "エラーが発生しました: " + e.getMessage();
        }
    }
}
