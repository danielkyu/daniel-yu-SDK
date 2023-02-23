package com.danielkyu.oneapi.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/** The movie API response from OneApi. */
@Data
public class MovieResponse {
  @Data
  public static class Movie {
    @JsonProperty("_id")
    private String id;

    private String name;
    private int runtimeInMinutes;
    private int budgetInMillions;
    private int boxOfficeRevenueInMillions;
    private int academyAwardNominations;
    private int academyAwardWins;
    private int rottenTomatoesScore;
  }

  private int total;
  private int limit;
  private int offset;
  private int page;
  private int pages;

  @JsonProperty("docs")
  private List<Movie> movies;
}
