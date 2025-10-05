package com.example.newsAggregator.newsFetcher.Entity;

import com.fasterxml.jackson.annotation.JsonTypeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    private String articleId;
    private String title;
    private String link;
    private String description;
    private Date publicationDate;
    private String source;

}
