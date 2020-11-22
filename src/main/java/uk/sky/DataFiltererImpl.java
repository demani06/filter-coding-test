package uk.sky;

import uk.sky.model.LogRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static uk.sky.util.Constants.*;

public class DataFiltererImpl implements DataFilterer<LogRecord> {

    private final static Logger LOG = Logger.getLogger(DataFiltererImpl.class.getName());

    @Override
    public Collection<LogRecord> filterByCountry(final Reader source, final String country) {

        LOG.info(String.format("Filtering by Country country:%s", country));

        validateCountryAndReaderInputs(source, country);

        final List<LogRecord> logRecordsList = getLogRecordsList(source);

        return logRecordsList
                .stream()
                .filter(e->e.getCountryCode().trim().equalsIgnoreCase(country.trim()))
                .collect(Collectors.toList());

    }


    @Override
    public Collection<LogRecord> filterByCountryWithResponseTimeAboveLimit(final Reader source,final String country,final long limit) {

        LOG.info(String.format("Filtering by CountryWithResponseTimeAboveLimit with country:%s and limit:%s", country, limit));

        validateCountryAndReaderInputs(source, country);

        final List<LogRecord> logRecordsList = getLogRecordsList(source);

        return logRecordsList
                .stream()
                .filter(e->e.getCountryCode().trim().equalsIgnoreCase(country.trim()))
                .filter(e->e.getResponseTime() > limit)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<LogRecord> filterByResponseTimeAboveAverage(final Reader source) {

        LOG.info("Filtering by ResponseTimeAboveAverage");

        if(Objects.isNull(source)){
            throw new IllegalArgumentException(READER_NULL_ERROR_MESSAGE);
        }

        final List<LogRecord> logRecordsList = getLogRecordsList(source);

        final double average = logRecordsList
                .stream()
                .mapToLong(LogRecord::getResponseTime)
                .average()
                .orElse(Double.NaN);

        return logRecordsList
                .stream()
                .filter(e->e.getResponseTime() > average)
                .collect(Collectors.toList());

    }


    private List<LogRecord> getLogRecordsList(Reader source){
        LOG.info("Getting Log Records from source reader");

        List<LogRecord> logRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(source)) {

            logRecords = br.lines()
                    .skip(1) //Skip the headers
                    .map(this::parseLineAndGetLogRecord)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            LOG.severe(String.format("Problem with reading file, exception : %s", e));
        }

        return logRecords;
    }

    private LogRecord parseLineAndGetLogRecord(String line) {

        final String[] logRecordStringArray = line.split(COMMA_DELIMITER);

        LogRecord logRecord = new LogRecord();

        logRecord.setRequestTimeStamp(Instant.ofEpochSecond(Long.parseLong(logRecordStringArray[0])));
        logRecord.setCountryCode(logRecordStringArray[1]);
        logRecord.setResponseTime(Long.parseLong(logRecordStringArray[2]));

        return logRecord;
    }

    private void validateCountryAndReaderInputs(Reader source, String country) {

        LOG.info("Validating country and Reader inputs if null");

        if(Objects.isNull(source) || Objects.isNull(country)){
            throw new IllegalArgumentException(EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE);
        }
    }


}
