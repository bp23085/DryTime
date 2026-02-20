package app.weather;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest {

    @Test
    void testEvaluate_SunnySpringDay() {
        ConditionEvaluator evaluator = new ConditionEvaluator();
        String result = evaluator.evaluate(20, 55, 0, 10);

        assertThat(result).isEqualTo("△");
    }

    @Test
    void testEvaluate_NightTime() {
        ConditionEvaluator evaluator = new ConditionEvaluator();
        String result = evaluator.evaluate(20, 55, 0, 5);

        assertThat(result).isEqualTo("×");
    }

    @Test
    void testEvaluate_CloudySummerDay() {
        ConditionEvaluator evaluator = new ConditionEvaluator();
        String result = evaluator.evaluate(30, 70, 3, 12);

        assertThat(result).isEqualTo("△");
    }

    @Test
    void testEvaluate_SunnyDay() {
        ConditionEvaluator evaluator = new ConditionEvaluator();
        String result = evaluator.evaluate(30, 50, 0, 12);

        assertThat(result).isEqualTo("◎");
    }
}

