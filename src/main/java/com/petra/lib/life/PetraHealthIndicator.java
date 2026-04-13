package com.petra.lib.life;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class PetraHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
//        // 1. Ваша логика проверки (например, количество живых потоков)
//        boolean isEverythingFine = checkMyThreads();
//
//        if (isEverythingFine) {
//            return Health.up()
//                    .withDetail("status", "Работаем штатно")
//                    .build();
//        } else {
            return Health.down()
                    .withDetail("error", "Слишком много задач в очереди!")
                    .build();
//        }
    }
}
