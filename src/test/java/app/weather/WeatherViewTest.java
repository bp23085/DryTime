package app.weather;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeatherViewTest {

    private WeatherView weatherView;
    private LaundryAdvisor mockLaundryAdvisor;

    @BeforeEach
    void setup() {
        mockLaundryAdvisor = Mockito.mock(LaundryAdvisor.class);
        weatherView = new WeatherView(mockLaundryAdvisor);
    }

    @Test
    void testInitialLayout() {
        VerticalLayout layout = weatherView;

        // ヘッダーが正しく配置されているかを確認
        H2 header = (H2) layout.getChildren()
                .filter(component -> component instanceof H2)
                .findFirst()
                .orElse(null);

        assertNotNull(header, "ヘッダーが見つかりませんでした。");
        assertEquals("座標を入力して洗濯物が乾きやすい時間を確認!!", header.getText());
    }

    @Test
    void testInputFieldsPresence() {
        VerticalLayout layout = weatherView;

        // 緯度と経度の入力フィールドが存在するか確認
        TextField latitudeField = (TextField) layout.getChildren()
                .filter(component -> component instanceof TextField && ((TextField) component).getLabel().equals("緯度"))
                .findFirst()
                .orElse(null);

        TextField longitudeField = (TextField) layout.getChildren()
                .filter(component -> component instanceof TextField && ((TextField) component).getLabel().equals("経度"))
                .findFirst()
                .orElse(null);

        assertNotNull(latitudeField, "緯度フィールドが見つかりませんでした。");
        assertNotNull(longitudeField, "経度フィールドが見つかりませんでした。");
    }

    @Test
    void testButtonClick_ValidInput() {
        // モック結果を準備
        Mockito.when(mockLaundryAdvisor.getDryingHoursAsync(35.6669, 139.6926))
                .thenReturn(CompletableFuture.completedFuture("本日: 12時: ◎"));

        // 入力フィールドとボタンを取得
        TextField latitudeField = (TextField) weatherView.getChildren()
                .filter(component -> component instanceof TextField && ((TextField) component).getLabel().equals("緯度"))
                .findFirst()
                .orElse(null);

        TextField longitudeField = (TextField) weatherView.getChildren()
                .filter(component -> component instanceof TextField && ((TextField) component).getLabel().equals("経度"))
                .findFirst()
                .orElse(null);

        Button checkButton = (Button) weatherView.getChildren()
                .filter(component -> component instanceof Button && ((Button) component).getText().equals("時間をチェック"))
                .findFirst()
                .orElse(null);

        assertNotNull(latitudeField);
        assertNotNull(longitudeField);
        assertNotNull(checkButton);

        // 入力フィールドに値を設定
        latitudeField.setValue("35.6669");
        longitudeField.setValue("139.6926");

        // ボタンをクリック
        checkButton.click();

        // 結果が表示されるかを確認
        Div resultArea = (Div) weatherView.getChildren()
                .filter(component -> component instanceof Div)
                .findFirst()
                .orElse(null);

        assertNotNull(resultArea);
        assertTrue(resultArea.getElement().getText().contains("本日: 12時: ◎"), "結果が正しく表示されませんでした。");
    }

    @Test
    void testButtonClick_InvalidInput() {
        // 入力フィールドとボタンを取得
        TextField latitudeField = (TextField) weatherView.getChildren()
                .filter(component -> component instanceof TextField && ((TextField) component).getLabel().equals("緯度"))
                .findFirst()
                .orElse(null);

        TextField longitudeField = (TextField) weatherView.getChildren()
                .filter(component -> component instanceof TextField && ((TextField) component).getLabel().equals("経度"))
                .findFirst()
                .orElse(null);

        Button checkButton = (Button) weatherView.getChildren()
                .filter(component -> component instanceof Button && ((Button) component).getText().equals("時間をチェック"))
                .findFirst()
                .orElse(null);

        assertNotNull(latitudeField);
        assertNotNull(longitudeField);
        assertNotNull(checkButton);

        // 入力フィールドを空のままにしてボタンをクリック
        latitudeField.setValue("");
        longitudeField.setValue("");

        checkButton.click();

        // 結果エリアが適切なエラーメッセージを表示するか確認
        Div resultArea = (Div) weatherView.getChildren()
                .filter(component -> component instanceof Div)
                .findFirst()
                .orElse(null);

        assertNotNull(resultArea);
        assertTrue(resultArea.getElement().getText().contains("有効な数値を入力してください。"), "エラーメッセージが表示されませんでした。");
    }
}

