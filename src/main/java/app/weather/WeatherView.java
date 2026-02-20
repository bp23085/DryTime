package app.weather;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Weather")
@Route(value = "")
public class WeatherView extends VerticalLayout {

    private final LaundryAdvisor laundryAdvisor;

    @Autowired
    public WeatherView(LaundryAdvisor laundryAdvisor) {
        this.laundryAdvisor = laundryAdvisor;
        setupLayout();
    }

    private void setupLayout() {
        setSizeFull();
        getStyle().set("background-color", "#f0ffff");
        H2 header = new H2("座標を入力して洗濯物が乾きやすい時間を確認!!");
        header.getStyle().set("text-align", "center");
        add(header);
        add(new Paragraph("例： 東京都渋谷区 緯度35.6669, 経度139.6926"));
        add(new Paragraph("　　 沖縄県那覇市 緯度26.2123, 経度127.6791"));
        TextField latitudeField = createTextField("緯度", "例: 35.6669");
        TextField longitudeField = createTextField("経度", "例: 139.6926");
        Div resultArea = createResultArea();
        add(new Paragraph("◎：とても乾きやすい　　　〇：乾きやすい"));
        Button checkButton = createCheckButton(latitudeField, longitudeField, resultArea);
        setAlignItems(Alignment.CENTER);
        add(latitudeField, longitudeField, checkButton, resultArea);
    }

    private TextField createTextField(String label, String placeholder) {
        TextField textField = new TextField(label);
        textField.setPlaceholder(placeholder);
        textField.setWidth("300px");
        return textField;
    }

    private Div createResultArea() {
        Div resultArea = new Div();
        resultArea.getStyle()
                .set("padding", "10px")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("margin-top", "20px")
                .set("max-width", "600px") // 横幅を少し広げます
                .set("background-color", "#ffffff");
        return resultArea;
    }

    private Button createCheckButton(TextField latitudeField, TextField longitudeField, Div resultArea) {
        Button checkButton = new Button("時間をチェック");
        checkButton.getStyle()
                .set("background-color", "#007bff")
                .set("color", "#ffffff")
                .set("border", "none")
                .set("padding", "10px 20px")
                .set("border-radius", "5px")
                .set("cursor", "pointer");

        checkButton.addClickListener(event -> handleCheckButtonClick(latitudeField, longitudeField, resultArea));
        return checkButton;
    }

    private void handleCheckButtonClick(TextField latitudeField, TextField longitudeField, Div resultArea) {
        try {
            double latitude = Double.parseDouble(latitudeField.getValue());
            double longitude = Double.parseDouble(longitudeField.getValue());

            // 処理中のメッセージを表示
            resultArea.removeAll();
            resultArea.add(new Paragraph("天気情報を取得中..."));

            laundryAdvisor.getDryingHoursAsync(latitude, longitude).thenAccept(result -> {
                // UIスレッドで安全にUIを更新
                getUI().ifPresent(ui -> ui.access(() -> {
                    resultArea.removeAll();
                    Paragraph resultParagraph = new Paragraph();
                    // CSSプロパティを設定して、\nで改行されるようにする
                    resultParagraph.getElement().getStyle().set("white-space", "pre-wrap");
                    resultParagraph.setText(result);
                    resultArea.add(resultParagraph);
                }));
            }).exceptionally(ex -> {
                // エラー発生時のUI更新
                getUI().ifPresent(ui -> ui.access(() -> {
                    resultArea.removeAll();
                    // getCause() を使うと、より根本的なエラーメッセージを取得できる場合があります
                    String errorMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    resultArea.add(new Paragraph("エラーが発生しました: " + errorMessage));
                }));
                return null;
            });
        } catch (NumberFormatException e) {
            resultArea.removeAll();
            resultArea.add(new Paragraph("緯度と経度には有効な数値を入力してください。"));
        }
    }
}
