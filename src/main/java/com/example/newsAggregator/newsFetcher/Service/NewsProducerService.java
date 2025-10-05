package com.example.newsAggregator.newsFetcher.Service;
import com.example.newsAggregator.kafka.KafkaProducerService;
import com.example.newsAggregator.newsFetcher.Entity.Article;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
public class NewsProducerService {

    @Autowired
    ObjectMapper objectMapper;

    // Spring Boot automatically configures this template for you to talk to Kafka.
    @Autowired
    private KafkaProducerService kafkaProducerService;

    // We'll send all raw articles to this topic.
    private static final String TOPIC = "raw-articles";
    private static final String RSS_URL = "https://feeds.bbci.co.uk/news/technology/rss.xml";

    public void fetchAndSendNews() {
        try {
            System.out.println("Fetching news from BBC RSS feed...");
            // Use the Rome library to fetch and parse the XML feed.
            URL feedUrl = new URL(RSS_URL);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            //kafkaProducerService.sendMessage("Hello I sent You a message");

            // Loop through each news item in the feed.
            for (SyndEntry entry : feed.getEntries()) {

                Article article = new Article();
                article.setArticleId("article:" + UUID.randomUUID());
                String articleTitle = entry.getTitle();
                article.setTitle(articleTitle);
                String articleLink = entry.getLink();
                article.setLink(articleLink);
                String articleDescription = String.valueOf(entry.getDescription().getValue());
                article.setDescription(articleDescription);
                Date publishingDate = entry.getPublishedDate();
                article.setPublicationDate(publishingDate);
                String articleSource = String.valueOf(entry.getSource());
                article.setSource(articleSource);

                //System.out.println(article.toString());

                try{
                    String jsonArticle = objectMapper.writeValueAsString(article);
                    kafkaProducerService.sendMessage(jsonArticle);
                    System.out.println("sent an article" + jsonArticle);
                }catch (JsonProcessingException e){
                    System.err.println("Error converting Article object to JSON: " + e.getMessage());
                }

                // Send the article title as a message to our Kafka topic.
                //kafkaProducerService.sendMessage(articleTitle);
                //System.out.println("Sent to Kafka: " + articleTitle);
            }
            System.out.println("Finished fetching news.");
        } catch (Exception e) {
            // In a real app, you'd have more robust error handling.
            System.err.println("Error fetching or sending news: " + e.getMessage());
        }
    }
}