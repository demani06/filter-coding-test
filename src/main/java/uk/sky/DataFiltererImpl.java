package uk.sky;

import uk.sky.model.LogRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static uk.sky.util.Constants.EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE;
import static uk.sky.util.Constants.READER_NULL_ERROR_MESSAGE;

public class DataFiltererImpl implements DataFilterer<LogRecord> {


    @Override
    public Collection<LogRecord> filterByCountry(final Reader source, final String country) {

        validateCountryAndReaderInputs(source, country);

        //TODO validate input country, input source
        final List<LogRecord> logRecordsList = getLogRecordsList(source);

        return logRecordsList
                .stream()
                .filter(e->e.getCountryCode().trim().equalsIgnoreCase(country.trim()))
                .collect(Collectors.toList());

    }


    @Override
    public Collection<LogRecord> filterByCountryWithResponseTimeAboveLimit(final Reader source,final String country,final long limit) {

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

        if(Objects.isNull(source)){
            throw new IllegalArgumentException(READER_NULL_ERROR_MESSAGE);
        }

        final List<LogRecord> logRecordsList = getLogRecordsList(source);

        final double average = logRecordsList
                .stream()
                .mapToLong(LogRecord::getResponseTime)
                .average().getAsDouble();


        return logRecordsList
                .stream()
                .filter(e->e.getResponseTime() > average)
                .collect(Collectors.toList());

    }


    private List<LogRecord> getLogRecordsList(Reader source){
        FileReader fileReader = (FileReader) source;
        List<LogRecord> logRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(fileReader)) {

            logRecords = br.lines()
                    .skip(1) //Skip the headers
                    .map(this::parseLineAndGetLogRecord)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return logRecords;
    }

    private LogRecord parseLineAndGetLogRecord(String line) {

        final String[] logRecordStringArray = line.split(",");

        LogRecord logRecord = new LogRecord();

       //TODO handle if not able to parse
        logRecord.setRequestTimeStamp(Instant.ofEpochSecond(Long.parseLong(logRecordStringArray[0])));
        logRecord.setCountryCode(logRecordStringArray[1]);
        logRecord.setResponseTime(Long.parseLong(logRecordStringArray[2]));

        return logRecord;
    }

    private void validateCountryAndReaderInputs(Reader source, String country) {
        if(Objects.isNull(source) || Objects.isNull(country)){
            throw new IllegalArgumentException(EITHER_READER_OR_COUNTRY_IS_NULL_ERROR_MESSAGE);
        }
    }


}
