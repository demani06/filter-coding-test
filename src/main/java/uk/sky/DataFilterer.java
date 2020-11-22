package uk.sky;

import java.io.Reader;
import java.util.Collection;

public interface DataFilterer<T> {
    Collection<T> filterByCountry(Reader source, String country);

    Collection<T> filterByCountryWithResponseTimeAboveLimit(Reader source, String country, long limit);

    Collection<T> filterByResponseTimeAboveAverage(Reader source);
}
