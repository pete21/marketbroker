import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync // Włącza obsługę zadań asynchronicznych
class AsyncBackgroundConfig {

    @Bean(name = ["taskExecutor"]) // Spring użyje tej nazwy jako domyślnej dla @Async
    fun backgroundTaskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        
        // Konfiguracja rozmiaru puli wątków
        executor.corePoolSize = 3        // Minimalna liczba aktywnych wątków
        executor.maxPoolSize = 5        // Maksymalna liczba wątków przy dużym obciążeniu
        executor.setQueueCapacity(100)   // Pojemność kolejki na zadania oczekujące
        
        // Prefiks ułatwiający identyfikację wątków w logach aplikacji
        executor.setThreadNamePrefix("WsBgTask-") 
        
        // Eleganckie zamykanie wątków podczas zatrzymywania aplikacji
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(60)
        
        executor.initialize()
        return executor
    }
}
