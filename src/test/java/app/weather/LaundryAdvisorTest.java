package app.weather;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LaundryAdvisorTest {

    private LaundryAdvisor laundryAdvisor;
    private WeatherApiClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = Mockito.mock(WeatherApiClient.class);
        laundryAdvisor = new LaundryAdvisor();
    }

    @Test
    void testGetDryingHours_ValidData() throws Exception {
        // モックデータを準備
        JsonNode mockData = Mockito.mock(JsonNode.class);
        JsonNode hourlyData = Mockito.mock(JsonNode.class);
        JsonNode tempData = Mockito.mock(JsonNode.class);
        JsonNode humidityData = Mockito.mock(JsonNode.class);
        JsonNode weatherCodeData = Mockito.mock(JsonNode.class);

        Mockito.when(mockClient.getWeatherData(35.6895, 139.6917)).thenReturn(mockData);
        Mockito.when(mockData.path("hourly")).thenReturn(hourlyData);
        Mockito.when(hourlyData.path("temperature_2m")).thenReturn(tempData);
        Mockito.when(hourlyData.path("relative_humidity_2m")).thenReturn(humidityData);
        Mockito.when(hourlyData.path("weathercode")).thenReturn(weatherCodeData);

        String result = laundryAdvisor.getDryingHours(35.6895, 139.6917);

        assertNotNull(result, "結果は null であってはならない");
        assertTrue(result.contains("本日"), "'本日' が含まれているべきです。");
    }
}
