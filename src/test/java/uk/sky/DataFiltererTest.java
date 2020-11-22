package uk.sky;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.sky.model.LogRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.sky.util.Constants.EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE;
import static uk.sky.util.Constants.READER_NULL_ERROR_MESSAGE;

class DataFiltererTest {

    private DataFilterer<LogRecord> dataFilterer;
    private Reader reader = null;
    private Reader onlyHeaderFileReader = null;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        reader = new FileReader("src/test/resources/sample-extract");
        onlyHeaderFileReader = new FileReader("src/test/resources/sample-extract-only-header");
        dataFilterer = new DataFiltererImpl();
    }
    @ParameterizedTest(name = "filter country code for ''countrycode:{0} and expectedMatchedResults:{1}''")
    @CsvSource({
            "GB, 1",
            "US, 3",
            "DE, 1",
    })
    public void testFilterByCountryWithCountryCodeShouldYieldTheExpectedOutput(String inputCountryCode, int expectedResultsSize) {
        assertEquals(expectedResultsSize, dataFilterer.filterByCountry(reader,inputCountryCode).size());
    }

    @Test
    public void testFilterByCountryWhenInvalidCountryCode() {
        assertEquals(0, dataFilterer.filterByCountry(reader,"INVALID_COUNTRY_CODE").size());
    }

    @Test
    public void testFilterByCountryWhenCountryCodeNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> dataFilterer.filterByCountry(reader,null));
        assertEquals(exceptionThatWasThrown.getMessage(), EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE);
    }

    @Test
    public void testFilterByCountryWithCountryCodeWhenFileWithOnlyHeadersSent() {
        assertEquals(0, dataFilterer.filterByCountry(onlyHeaderFileReader,"US").size());
    }

    @Test
    public void testFilterByCountryWhenReaderNull() {

        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> dataFilterer.filterByCountry(reader,null));
        assertEquals(exceptionThatWasThrown.getMessage(), EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE);

    }

    @Test
    public void testFilterByCountryWhenCountryCodeEmptyString() {
        assertEquals(0, dataFilterer.filterByCountry(reader,"").size());
    }

    @Test
    public void testFilterByCountryWithResponseTimeAboveLimitForPositiveScenario1() {
        final Collection<LogRecord> logRecords = dataFilterer.filterByCountryWithResponseTimeAboveLimit(reader, "US", 500);

        assertEquals(3, logRecords.size());
    }

    @Test
    public void testFilterByCountryWithResponseTimeAboveLimitForPositiveScenario2() {
        final Collection<LogRecord> logRecords = dataFilterer.filterByCountryWithResponseTimeAboveLimit(reader, "US", 700);

        assertEquals(2, logRecords.size());
    }

    @Test
    public void testFilterByCountryWithResponseTimeAboveLimitWhenFileReaderWithEmptyLinesSent() {
        final Collection<LogRecord> logRecords = dataFilterer.filterByCountryWithResponseTimeAboveLimit(onlyHeaderFileReader, "US", 500);

        assertEquals(0, logRecords.size());
    }

    @Test
    public void testFilterByCountryWithResponseTimeAboveLimitWhenNoMatch() {
        final Collection<LogRecord> logRecords = dataFilterer.filterByCountryWithResponseTimeAboveLimit(reader, "US", 1000);

        assertEquals(0, logRecords.size());
    }

    @Test
    public void testFilterByCountryWithResponseTimeAboveLimitWhenReaderNull() {

        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> dataFilterer.filterByCountryWithResponseTimeAboveLimit(null, "GB", 22));
        assertEquals(exceptionThatWasThrown.getMessage(), EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE);

    }

    @Test
    public void testFilterByCountryWithResponseTimeAboveLimitWhenCountryCodeNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> dataFilterer.filterByCountryWithResponseTimeAboveLimit(reader, null, 22));
        assertEquals(exceptionThatWasThrown.getMessage(), EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE);

    }

    @Test
    public void testFilterByResponseTimeAboveAverage() {
        final Collection<LogRecord> logRecords = dataFilterer.filterByResponseTimeAboveAverage(reader);
        assertEquals(3, logRecords.size());
    }

    @Test
    public void testFilterByResponseTimeAboveAverageWhenReaderNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> dataFilterer.filterByResponseTimeAboveAverage(null));
        assertEquals(exceptionThatWasThrown.getMessage(), READER_NULL_ERROR_MESSAGE);

    }

    @Test
    public void testFilterByResponseTimeAboveAverageWhenReaderWithOnlyHeaderSent() {
        final Collection<LogRecord> logRecords = dataFilterer.filterByResponseTimeAboveAverage(onlyHeaderFileReader);
        assertEquals(0, logRecords.size());
    }
}