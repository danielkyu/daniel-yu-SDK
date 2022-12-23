package com.danielkyu.oneapi.params;

import com.danielkyu.oneapi.params.MovieParams.MovieAttribute;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MovieParamsTest {
  @Test
  void testDefaults() {
    MovieParams params = new MovieParams();
    List<String> queryList = params.toQueryList();

    Assertions.assertEquals("page=1", queryList.get(0));
    Assertions.assertEquals("offset=0", queryList.get(1));
    Assertions.assertEquals("limit=10", queryList.get(2));
  }

  @Test
  void testToQueryList() {
    MovieParams params = new MovieParams();

    params.withAttributeEquals(MovieAttribute.ID, "1000");
    params.withAttributeEquals(MovieAttribute.NAME, "/king/i");
    params.withAttributeNotEquals(MovieAttribute.ID, "9999");
    params.withAttributeNotEquals(MovieAttribute.NAME, "/series/i");
    params.withAttributeGreaterThan(MovieAttribute.RUNTIME_IN_MINUTES, 90);
    params.withAttributeGreaterThanOrEqualTo(MovieAttribute.BUDGET_IN_MILLIONS, 200);
    params.withAttributeLessThan(MovieAttribute.ACADEMY_AWARD_NOMINATIONS, 100);
    params.withAttributeLessThanOrEqualTo(MovieAttribute.ACADEMY_AWARD_WINS, 10);
    params.withAttributeNotEquals(MovieAttribute.ROTTEN_TOMATOES_SCORE, 50);

    params.withPage(3);
    params.withOffset(10);
    params.withLimit(100);

    List<String> queryList = params.toQueryList();

    Assertions.assertEquals("_id=1000", queryList.get(0));
    Assertions.assertEquals("name=/king/i", queryList.get(1));
    Assertions.assertEquals("_id!=9999", queryList.get(2));
    Assertions.assertEquals("name!=/series/i", queryList.get(3));
    Assertions.assertEquals("runtimeInMinutes>90", queryList.get(4));
    Assertions.assertEquals("budgetInMillions>=200", queryList.get(5));
    Assertions.assertEquals("academyAwardNominations<100", queryList.get(6));
    Assertions.assertEquals("academyAwardWins<=10", queryList.get(7));
    Assertions.assertEquals("rottenTomatoesScore!=50", queryList.get(8));

    Assertions.assertEquals("page=3", queryList.get(9));
    Assertions.assertEquals("offset=10", queryList.get(10));
    Assertions.assertEquals("limit=100", queryList.get(11));
  }
}
