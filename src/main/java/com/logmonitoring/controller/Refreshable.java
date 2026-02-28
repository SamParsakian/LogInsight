package com.logmonitoring.controller;

/**
 * Controllers that can refresh their data when the view is shown (e.g. after navigation).
 */
public interface Refreshable {
    void refreshData();
}
