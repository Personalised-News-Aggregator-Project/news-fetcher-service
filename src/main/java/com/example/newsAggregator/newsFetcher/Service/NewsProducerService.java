package com.example.newsAggregator.newsFetcher.Service;

import com.example.newsAggregator.newsFetcher.Entity.Article;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.UUID;

@Service
public class NewsProducerService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    private static final String TOPIC = "raw-articles";
    private static final String RSS_URL = "https://feeds.bbci.co.uk/news/technology/rss.xml";

    public void fetchAndSendNews() {
        try {
            System.out.println("Fetching news from BBC RSS feed...");
            URL feedUrl = new URL(RSS_URL);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            for (SyndEntry entry : feed.getEntries()) {

                Article article = new Article();
                article.setArticleId("article:" + UUID.randomUUID());
                article.setTitle(entry.getTitle());
                article.setLink(entry.getLink());
                if (entry.getDescription() != null) {
                    article.setDescription(entry.getDescription().getValue());
                }
                article.setPublicationDate(entry.getPublishedDate());
                if (entry.getSource() != null) {
                    article.setSource(entry.getSource().getTitle());
                }

                try {
                    String jsonArticle = objectMapper.writeValueAsString(article);

                    kafkaTemplate.send(TOPIC, jsonArticle);

                    System.out.println("Sent article: " + article.getTitle());
                } catch (JsonProcessingException e) {
                    System.err.println("Error converting Article object to JSON: " + e.getMessage());
                }
            }
            System.out.println("Finished fetching news.");
        } catch (Exception e) {
            System.err.println("Error fetching or sending news: " + e.getMessage());
            e.printStackTrace();
        }
    }
}