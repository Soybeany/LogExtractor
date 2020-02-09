package com.soybeany.logextractor.core.data;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IReportAccessor<Report> {

    Report getReport();

    void setReport(Report report);

}
