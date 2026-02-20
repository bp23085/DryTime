package app.weather;

import org.springframework.stereotype.Component; // importを追加
import java.time.LocalDate;
import java.time.Month;

@Component // このクラスをSpringのコンポーネントとして登録
public class ConditionEvaluator {

    // evaluateメソッドがLocalDateを引数に取るように変更
    public String evaluate(double temperature, int humidity, int weatherCode, int time, LocalDate date) {
        if (time < 6 || time > 16) {
            return "×"; // 乾きやすい時間帯は6時～16時のみ
        }

        if (weatherCode > 4) {
            return "×";
        }
        boolean isSunny = weatherCode == 0;

        // 引数で渡された日付から月を取得
        Month month = date.getMonth();

        switch (month) {
            case MARCH:
            case APRIL:
            case MAY:
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                // 春・秋の条件
                return evaluateSeasonConditions(temperature, humidity, isSunny, 15, 50, 60);
            case JUNE:
            case JULY:
            case AUGUST:
                // 夏の条件
                return evaluateSeasonConditions(temperature, humidity, isSunny, 25, 60, 80);
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
                // 冬の条件
                return evaluateSeasonConditions(temperature, humidity, isSunny, 0, 30, 50);
            default:
                return "×";
        }
    }

    private String evaluateSeasonConditions(double temperature, int humidity, boolean isSunny,
                                            double minTemp, int minHumid, int maxHumid) {
        if (temperature >= minTemp && humidity >= minHumid && humidity <= maxHumid) {
            // isSunny == true は isSunny と簡潔に書けます
            if (isSunny) {
                return "◎";
            }
            return "〇";
        }
        // 乾きにくい場合は"△"を返す
        return "△";
    }
}
