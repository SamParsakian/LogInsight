package com.logmonitoring.service;

import java.time.LocalDate;

/**
 * Holds current filter values (date, time, level, source, user, ip).
 */
public class FilterState {

    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String timeFrom;
    private String timeTo;
    private String level;
    private String source;
    private String user;
    private String ip;

    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
    public String getTimeFrom() { return timeFrom; }
    public void setTimeFrom(String timeFrom) { this.timeFrom = timeFrom; }
    public String getTimeTo() { return timeTo; }
    public void setTimeTo(String timeTo) { this.timeTo = timeTo; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public void clear() {
        dateFrom = null;
        dateTo = null;
        timeFrom = null;
        timeTo = null;
        level = null;
        source = null;
        user = null;
        ip = null;
    }
}
