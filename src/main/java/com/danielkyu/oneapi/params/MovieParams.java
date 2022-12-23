package com.danielkyu.oneapi.params;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/** Parameters for filtering movies. */
public class MovieParams {
  /** The attributes that are associated with the movie. */
  public enum MovieAttribute {
    ID("_id"),
    NAME("name"),
    RUNTIME_IN_MINUTES("runtimeInMinutes"),
    BUDGET_IN_MILLIONS("budgetInMillions"),
    BOX_OFFICE_REVENUE_IN_MILLIONS("boxOfficeRevenueInMillions"),
    ACADEMY_AWARD_NOMINATIONS("academyAwardNominations"),
    ACADEMY_AWARD_WINS("academyAwardWins"),
    ROTTEN_TOMATOES_SCORE("rottenTomatoesScore");

    private final String queryParam;

    private MovieAttribute(String queryParam) {
      this.queryParam = queryParam;
    }

    @Override
    public String toString() {
      return this.queryParam;
    }
  }

  private int page = 1;
  private int offset = 0;
  private int limit = 10;
  private final List<String> queryParams = new ArrayList<>();

  /**
   * Adds a new attribute (=) filtering criteria.
   *
   * @param attribute The attribute that is targetted by the criteria.
   * @param value The value associated with the criteria.
   * @return This instance.
   */
  public <T> MovieParams withAttributeEquals(MovieAttribute attribute, T value) {
    return withAttribute(attribute, "=", value);
  }

  /**
   * Adds a new attribute (!=) filtering criteria.
   *
   * @param attribute The attribute that is targetted by the criteria.
   * @param value The value associated with the criteria.
   * @return This instance.
   */
  public <T> MovieParams withAttributeNotEquals(MovieAttribute attribute, T value) {
    return withAttribute(attribute, "!=", value);
  }

  /**
   * Adds a new attribute (<) filtering criteria.
   *
   * @param attribute The attribute that is targetted by the criteria.
   * @param value The value associated with the criteria.
   * @return This instance.
   */
  public <T> MovieParams withAttributeLessThan(MovieAttribute attribute, T value) {
    return withAttribute(attribute, "<", value);
  }

  /**
   * Adds a new attribute (<=) filtering criteria.
   *
   * @param attribute The attribute that is targetted by the criteria.
   * @param value The value associated with the criteria.
   * @return This instance.
   */
  public <T> MovieParams withAttributeLessThanOrEqualTo(MovieAttribute attribute, T value) {
    return withAttribute(attribute, "<=", value);
  }

  /**
   * Adds a new attribute (>) filtering criteria.
   *
   * @param attribute The attribute that is targetted by the criteria.
   * @param value The value associated with the criteria.
   * @return This instance.
   */
  public <T> MovieParams withAttributeGreaterThan(MovieAttribute attribute, T value) {
    return withAttribute(attribute, ">", value);
  }

  /**
   * Adds a new attribute (>=) filtering criteria.
   *
   * @param attribute The attribute that is targetted by the criteria.
   * @param value The value associated with the criteria.
   * @return This instance.
   */
  public <T> MovieParams withAttributeGreaterThanOrEqualTo(MovieAttribute attribute, T value) {
    return withAttribute(attribute, ">=", value);
  }

  /**
   * Adds a new attribute filtering criteria.
   *
   * @param attribute The attribute that is targetted by the criteria.
   * @param conditional The condition that ties the attribute to the value.
   * @param value The value associated with the criteria.
   * @return This instance.
   */
  <T> MovieParams withAttribute(MovieAttribute attribute, String conditional, T value) {
    Validate.notNull(attribute, "Attribute must not be null.");
    Validate.notNull(conditional, "Conditional must not be null");
    Validate.notNull(value, "Value must not be null");

    this.queryParams.add(attribute.toString() + conditional + value.toString());
    return this;
  }

  /**
   * Sets the limit.
   *
   * @param limit The maximum number of movies to return in the response.
   * @return This instance.
   */
  public MovieParams withLimit(int limit) {
    Validate.isTrue(limit >= 0, "Limit must not be a negative value.");

    this.limit = limit;
    return this;
  }

  /**
   * Sets the offset.
   *
   * @param limit The offset from the start of movies to return.
   * @return This instance.
   */
  public MovieParams withOffset(int offset) {
    Validate.isTrue(offset >= 0, "Offset must not be a negative value.");

    this.offset = offset;
    return this;
  }

  /**
   * Sets the page.
   *
   * @param page The page number from which the movies should be accessed.
   * @return This instance.
   */
  public MovieParams withPage(int page) {
    Validate.isTrue(page > 0, "Page must be a positive value.");

    this.page = page;
    return this;
  }

  /**
   * Returns a list of query parameters that represent the filtering criteria represented by this
   * object.
   *
   * @return The list of query parameters.
   */
  public List<String> toQueryList() {
    // Create a shallow copy of the query parameters list so as not to expose the member list to
    // external modification.
    List<String> queryParams = new ArrayList<>(this.queryParams);

    queryParams.add(String.format("page=%d", this.page));
    queryParams.add(String.format("offset=%d", this.offset));
    queryParams.add(String.format("limit=%d", this.limit));

    return queryParams;
  }
}
