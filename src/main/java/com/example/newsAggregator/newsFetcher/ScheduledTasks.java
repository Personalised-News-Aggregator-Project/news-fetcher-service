package com.example.newsAggregator.newsFetcher;

import com.example.newsAggregator.newsFetcher.Service.NewsProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private NewsProducerService newsProducerService;

    // This method will run automatically.
    // fixedRate = 300000 milliseconds means it runs every 5 minutes.
    // For testing, you can set it to a shorter interval like 60000 (1 minute).
    @Scheduled(fixedRate = 30000)
    public void scheduleNewsFetching() {
        System.out.println("Scheduler running at " + System.currentTimeMillis());
        newsProducerService.fetchAndSendNews();
    }
}