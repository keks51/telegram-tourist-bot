package com.tourist_bot.bot.logic;


public class BotConf {

    public final int maxSearchDistanceMeters;
    public final int searchDistanceStep;
    public final int sessionTimeOutSec;
    public final int sessionCleanUpPeriodSec;

    public BotConf(int maxSearchDistanceMeters,
                   int searchDistanceStep,
                   int sessionTimeOutSec,
                   int sessionCleanUpPeriodSec) {

        this.maxSearchDistanceMeters = maxSearchDistanceMeters;
        this.searchDistanceStep = searchDistanceStep;
        this.sessionTimeOutSec = sessionTimeOutSec;
        this.sessionCleanUpPeriodSec = sessionCleanUpPeriodSec;
    }



}
