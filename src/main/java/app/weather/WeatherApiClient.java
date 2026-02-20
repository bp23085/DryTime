package app.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component; // importを追加

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration; // importを追加

@Component // このクラスをSpringのコンポーネントとして登録
public class WeatherApiClient {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    // HttpClientは再利用が推奨されているため、一度だけ生成します。
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)) // タイムアウトを設定
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getWeatherData(double latitude, double longitude) throws Exception {
        // API URLの作成
        String apiUrl = String.format(
                "%s?latitude=%.4f&longitude=%.4f&hourly=temperature_2m,relative_humidity_2m,weathercode&timezone=Asia/Tokyo",
                BASE_URL, latitude, longitude);

        // HTTPリクエストの実行
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("天気データの取得に失敗しました。HTTPステータス: " + response.statusCode());
        }

        // JSONレスポンスの解析
        return objectMapper.readTree(response.body());
    }
}
