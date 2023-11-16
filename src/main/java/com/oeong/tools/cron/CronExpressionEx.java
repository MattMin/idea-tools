package com.oeong.tools.cron;

import lombok.Getter;

import java.io.Serial;
import java.text.ParseException;
import java.util.Set;
import java.util.StringTokenizer;

@Getter
public class CronExpressionEx extends CronExpression {
  @Serial
  private static final long serialVersionUID = 1L;

  public static final int NO_SPEC_INT = 98; // '?'

  private final String secondsExp;
  private final String minutesExp;
  private final String hoursExp;
  private final String daysOfMonthExp;
  private final String monthsExp;
  private final String daysOfWeekExp;

  public CronExpressionEx(String cronExpression) throws ParseException {
    super(cronExpression);

    StringTokenizer exprTok = new StringTokenizer(cronExpression, " \t", false);
    secondsExp = exprTok.nextToken().trim();
    minutesExp = exprTok.nextToken().trim();
    hoursExp = exprTok.nextToken().trim();
    daysOfMonthExp = exprTok.nextToken().trim();
    monthsExp = exprTok.nextToken().trim();
    daysOfWeekExp = exprTok.nextToken().trim();
  }
}
