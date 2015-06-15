package com.nevejans.jordan.workcalculator;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jordan on 5/21/2015.
 */
public class WorkDay extends SugarRecord<WorkDay> {

    int tips;
    int noBusboys;
    int noServers;
    int bar;



    double hoursWorked;
    Date date; //mmddyy
    double moneyMade;//=tips+hoursWorked*hourlyWage
    double restarauntSales;//=tips*25
    double serverTips;//after tipping busboys =(tips/noServers)*4

    @Ignore//not persisted in db
    final double HOURLY_WAGE = 5.40;


    //need this for SugarORM to work
    public WorkDay(){
    }


    public WorkDay(int tips,int bar, int noBusboys, int noServers, Date date, double hoursWorked){
        setTips(tips);//same as this.tips = tips;
        setNoBusboys(noBusboys);
        setNoServers(noServers);
        setDate(date);
        setBar(bar);
        setHoursWorked(hoursWorked);

        calcRestaurantSales();
        calcServerTips();
        calcMoneyMade();
    }

    //needs hoursWorked to be set
    public void calcMoneyMade(){
        moneyMade = (tips+bar)+(hoursWorked*HOURLY_WAGE);
    }

    public void calcRestaurantSales(){
        restarauntSales = tips*noBusboys*25;
    }

    public void calcServerTips(){
        serverTips = (((double) tips * noBusboys)/noServers)*4;
    }//=.8*individualServerTips because tips/noServers = .2 What the servers made total but .25 of their net pay

    public Date getDate() {
        return date;
    }

    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public int getNoServers() {
        return noServers;
    }

    public void setNoServers(int noServers) {
        this.noServers = noServers;
    }

    public int getNoBusboys() {
        return noBusboys;
    }

    public void setNoBusboys(int noBusboys) {
        this.noBusboys = noBusboys;
    }

    public int getTips() {
        return tips;
    }

    public void setTips(int tips) {
        this.tips = tips;
    }


    public double getMoneyMade() {
        return moneyMade;
    }

    public double getRestarauntSales() {
        return restarauntSales;
    }

    public double getServerTips() {
        return serverTips;
    }

    public String getDayOfWeek(){
        return new SimpleDateFormat("EEE").format(getDate());//output ie 'Fri' | to get 'Friday' arugument should be "EEEE"
    }

}
