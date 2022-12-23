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
    public String id;

    public String name;
    public int runtimeInMinutes;
    public int budgetInMillions;
    public int boxOfficeRevenueInMillions;
    public int academyAwardNominations;
    public int academyAwardWins;
    public int rottenTomatoesScore;
  }

  public int total;
  public int limit;
  public int offset;
  public int page;
  public int pages;

  @JsonProperty("docs")
  public List<Movie> movies;
}
