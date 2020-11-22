package uk.sky.model;

import java.time.Instant;

public class LogRecord {

    private Instant requestTimeStamp;
    private String countryCode;
    private long responseTime;


    public Instant getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public void setRequestTimeStamp(Instant requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
