package app.weather;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherApiClientTest {

    @Test
    void testGetWeatherData_ValidCoordinates() throws Exception {
        WeatherApiClient client = new WeatherApiClient();
        JsonNode data = client.getWeatherData(35.6895, 139.6917); // 東京の緯度・経度

        assertNotNull(data);
        assertTrue(data.has("hourly"), "APIレスポンスに 'hourly' フィールドが含まれているべきです。");
    }

    @Test
    void testGetWeatherData_InvalidCoordinates() {
        WeatherApiClient client = new WeatherApiClient();
        Exception exception = assertThrows(Exception.class, () -> {
            client.getWeatherData(999, 999); // 不正な緯度・経度
        });

        assertTrue(exception.getMessage().contains("天気データの取得に失敗しました"));
    }
}
