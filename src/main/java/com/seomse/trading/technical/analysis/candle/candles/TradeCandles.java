/*
 * Copyright (C) 2020 Seomse Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seomse.trading.technical.analysis.candle.candles;

import com.seomse.commons.utils.time.DateUtil;
import com.seomse.commons.utils.time.Times;
import com.seomse.trading.Trade;
import com.seomse.trading.TradeAdd;
import com.seomse.trading.technical.analysis.candle.CandleStick;
import com.seomse.trading.technical.analysis.candle.TradeCandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 여러개의 TradeCandle 정보
 * @author macle
 */
public class TradeCandles {

    private static final Logger logger = LoggerFactory.getLogger(TradeCandles.class);

    public static final int DEFAULT_COUNT = 1000;

    private static final CandleChangeObserver[] EMPTY_OBSERVER = new CandleChangeObserver[0];

    public static final TradeCandle[] EMPTY_CANDLES = new TradeCandle[0];

    //24시간으로 나눌 수 있는 값만 설정 가능
    private final long timeGap ;

    private int count = DEFAULT_COUNT;

    TradeAdd tradeAdd ;

    List<TradeCandle> candleList = new LinkedList<>();
    TradeCandle[] candles = EMPTY_CANDLES;

    TradeCandle lastCandle = null;
    double shortGapRatio = -1.0;
    double steadyGapRatio = -1.0;

    boolean isEmptyCandleContinue = false;
    boolean isEmptyCandleMake = false;

    private final Object observerLock = new Object();
    private CandleChangeObserver[] observers = EMPTY_OBSERVER;

    private final List<CandleChangeObserver> observerList = new LinkedList<>();


    /**
     *  캔들 변화 인지 옵져버 추가
     * @param candleChangeObserver CandleChangeObserver  candle change observer
     */
    public void addChangeObserver(CandleChangeObserver candleChangeObserver){
        synchronized (observerLock) {
            if (observerList.contains(candleChangeObserver)) {
                return;
            }
            observerList.add(candleChangeObserver);
            observers = observerList.toArray(new CandleChangeObserver[0]);
        }
    }

    /**
     * 캔들 배열의 유형을 설정
     * shortGapPercent
     * steadyGapPercent
     * 설정하고 실행하여야 한다.
     */
    public void setCandleType(){
        if(steadyGapRatio == -1.0 || shortGapRatio == -1.0){
            logger.error("shortGapPercent, steadyGapPercent set: " + shortGapRatio +", " + steadyGapRatio);
            return;
        }

        TradeCandle[] candles = this.candles;
        for(TradeCandle candle : candles){
            candle.setType(shortGapRatio, steadyGapRatio);
        }

    }

    /**
     * 캔들 변화 인지 옵저버 제거
     * @param candleChangeObserver CandleChangeObserver candle change observer
     */
    public void removeObserver(CandleChangeObserver candleChangeObserver){
        synchronized (observerLock) {
            if (!observerList.contains(candleChangeObserver)) {
                return;
            }
            observerList.remove(candleChangeObserver);
            observers = observerList.toArray(new CandleChangeObserver[0]);
        }
    }

    /**
     * 생성자
     * @param timeGap long timeGap
     */
    public TradeCandles(long timeGap ){
        //24시간 이하의 값에서는 24보다 낮은 값만 구할 수 있음
        if(timeGap < Times.DAY_1 &&
                Times.DAY_1%timeGap != 0){
            throw new RuntimeException("24 hour % timeGap 0: "  +  Times.DAY_1%timeGap );
        }
        this.timeGap = timeGap;
        tradeAdd = new FirstTradeAdd(this);
    }

    /**
     * 빈켄들 정보로 이어지게 할지 여부 설정
     *@param emptyCandleContinue boolean isEmptyCandleContinue
     */
    public void setEmptyCandleContinue(boolean emptyCandleContinue) {
        isEmptyCandleContinue = emptyCandleContinue;
    }

    /**
     * 생성자
     * 처음부터 많은 켄들이 한번에 추가될 경우
     * @param timeGap long timeGap
     * @param candles TradeCandle ready candles
     * @param saveCount int save count
     */
    public TradeCandles(long timeGap, TradeCandle[] candles, int saveCount ){
        if(timeGap < Times.DAY_1 &&
                Times.DAY_1%timeGap != 0){
            throw new RuntimeException("24 hour % timeGap 0: "  +  Times.DAY_1%timeGap );
        }

        this.timeGap = timeGap;
        this.count = saveCount;


        if(candles == null || candles.length == 0){
            tradeAdd = new FirstTradeAdd(this);
            return;
        }

        lastCandle = candles[candles.length-1];


        tradeAdd = new NextTradeAdd(this);
        if(candles.length <= saveCount) {
            candleList.addAll(Arrays.asList(candles));
            this.candles = candles;
        }else{

            //noinspection ManualArrayToCollectionCopy
            for (int i = candles.length - saveCount ; i < candles.length; i++) {
                candleList.add(candles[i]);
            }
            this.candles = candleList.toArray(new TradeCandle[0]);
        }
    }


    /**
     * add trade candles
     * @param tradeCandles TradeCandle []
     */
    public void addCandle(TradeCandle[] tradeCandles){
        for(TradeCandle tradeCandle : tradeCandles){
            addCandle(tradeCandle, false);
        }
        this.candles = candleList.toArray(new TradeCandle[0]);
    }
    
    /**
     * add candle
     * @param tradeCandle TradeCandle add trade candle
     */
    public void addCandle(TradeCandle tradeCandle){
        addCandle(tradeCandle, true);
    }

    /**
     * add candle
     * @param tradeCandle TradeCandle add trade candle
     * @param isNewCandles boolean candles array change flag
     */
    public void addCandle(TradeCandle tradeCandle, boolean isNewCandles){
        TradeCandle lastEndCandle = null;

        if(candles.length > 0){

            lastEndCandle = candles[candles.length-1];

            //마지막 캔들이 더이상 변화가 없는상태로 변환
            lastEndCandle.setEndTrade();

            //캔들유형을
            if(shortGapRatio != -1.0 && steadyGapRatio != -1.0 && lastEndCandle.getType() == CandleStick.Type.UNDEFINED){
                lastEndCandle.setType(lastEndCandle.getOpen() * shortGapRatio, lastEndCandle.getOpen() * steadyGapRatio);
            }
            
            //새로 들어온 캔들이 변화가 없는 캔들일 경우
            if(tradeCandle.isEndTrade()){
                if(shortGapRatio != -1.0 && steadyGapRatio != -1.0 && tradeCandle.getType() == CandleStick.Type.UNDEFINED){
                    tradeCandle.setType(tradeCandle.getOpen() * shortGapRatio, tradeCandle.getOpen() * steadyGapRatio);
                }
                lastEndCandle = tradeCandle;
            }
        }

        candleList.add(tradeCandle);

        while(candleList.size() >= count) {
            candleList.remove(0);
        }
        if(isNewCandles ) {
            this.candles = candleList.toArray(new TradeCandle[0]);
        }
        lastCandle = tradeCandle;

        CandleChangeObserver[] observers = this.observers;

        for(CandleChangeObserver observer : observers){

            observer.changeCandle(lastEndCandle, tradeCandle);
        }
    }


    /**
     * 거래정보 추가
     * trade add
     * @param trade Trade trade
     */
    public void addTrade(Trade trade){
        tradeAdd.addTrade(trade);
    }

    /**
     * 타임 갭 얻기
     * timeGap get
     * @return long timeGap
     */
    public long getTimeGap() {
        return timeGap;
    }

    /**
     * 캔들 저장 count 설정
     * candle count set
     * @param count int candle  count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 캔들을 추가로 생성하여 트레이드 정보 입력
     * @param trade Trade trade
     * @param startTime long startTime
     * @param endTime long endTime
     */
    void addTradeNewCandle(Trade trade, long startTime, long endTime){
        TradeCandle tradeCandle = new TradeCandle();
        tradeCandle.setOpenTime(startTime);
        tradeCandle.setCloseTime(endTime);
        tradeCandle.addTrade(trade);
        addCandle(tradeCandle);
    }

    /**
     * 설정된 캔들 저장 건수 얻기
     * candle count get
     * @return int candle count
     */
    public int getCount() {
        return count;
    }

    /**
     * 길어얻기
     * candles length
     * @return int candles length
     */
    public int length(){
        return candles.length;
    }

    /**
     * 캔들 배열 얻기
     * candles get
     * @return TradeCandle candles
     */
    public TradeCandle[] getCandles() {
        return candles;
    }

    /**
     * 짧은캔들 기준 변화률 설정
     * 시작기 기준의 비율
     * @param shortGapRatio double 짧은 캔들 기준 변화률
     */
    public void setShortGapRatio(double shortGapRatio) {
        this.shortGapRatio = shortGapRatio;
    }

    /**
     * 보합 기준 변화률 설정
     * 시작가 기준의 비율
     * @param steadyGapRatio double 보합 기준 변화률
     */
    public void setSteadyGapRatio(double steadyGapRatio) {
        this.steadyGapRatio = steadyGapRatio;
    }

    /**
     * 짧은캔들 gap percent
     * @return double shot gap percent
     */
    public double getShortGapRatio() {
        return shortGapRatio;
    }

    /**
     * 보합 gap percent
     * @return double steady gap percent
     */
    public double getSteadyGapRatio() {
        return steadyGapRatio;
    }

    /**
     * 빈 캔들을 채운다.
     * @param businessDayList 정렬 된 영업일 목록 yyyyMMdd
     */
    public void makeEmptyCandle(List<String> businessDayList){
        boolean lessThenDay = timeGap < Times.DAY_1;
        int candleSize = candleList.size();

        for (int i = 0; i < candleSize-1; i++) {
            TradeCandle tradeCandle = candleList.get(i);
            TradeCandle nextTradeCandle = candleList.get(i+1);
            long tradeOpenTime = tradeCandle.getOpenTime();
            long nextTradeOpenTime = nextTradeCandle.getOpenTime();

            String tradeOpenYmd = DateUtil.getDateYmd(tradeOpenTime,"yyyyMMdd");
            String nextTradeOpenYmd = getNextOpenDay(tradeOpenYmd,businessDayList);

            if(tradeOpenTime + timeGap == nextTradeOpenTime){
                continue;
            }

            long nextOpenTime = getNextOpenTime(tradeOpenTime,nextTradeOpenYmd,lessThenDay);
            double close = tradeCandle.getClose();

            TradeCandle newCandle = new TradeCandle();
            newCandle.setLow(close);
            newCandle.setHigh(close);
            newCandle.setOpen(close);
            newCandle.setHigh(close);
            newCandle.setOpenTime(nextOpenTime);
            newCandle.setCloseTime(nextOpenTime + timeGap);

            candleList.add(i+1,newCandle);
            candleSize++;
        }
        candles = (TradeCandle[])candleList.toArray();
        isEmptyCandleMake = true;
    }

    private String getNextOpenDay(String tradeOpenYmd , List<String> businessDayList) {
        int businessDaySize = businessDayList.size();
        int findIndex = -1;
        String result = null;
        for (int i = 0; i <businessDaySize-1; i++) {
            String businessDay = businessDayList.get(i);
            String nextBusinessDay = businessDayList.get(i+1);
            if(!businessDay.equals(tradeOpenYmd)){
                continue;
            } else {
                findIndex = i;
                result = nextBusinessDay;
                break;
            }
        }
        for (int i = 0; i < findIndex; i++) {
            businessDayList.remove(0);
        }
        return result;
    }

    private long getNextOpenTime(long tradeOpenTime,String nextTradeOpenYmd,boolean lessThenDay) {
        long nextTime = tradeOpenTime + timeGap;
        int nextTimeHm = Integer.parseInt( DateUtil.getDateYmd(nextTime,"HHmm") );
        if(lessThenDay){
            if(nextTimeHm > 1530){ // 15:30
                return DateUtil.getDateTime( nextTradeOpenYmd + "0900" , "yyyyMMddHHmm");
            } else {
                return nextTime;
            }
        } else {
            return DateUtil.getDateTime(nextTradeOpenYmd + "0900", "yyyyMMddHHmm");
        }
    }

    /**
     * 현재 정보로 더 큰 시간의 TradeCandles 를 만든다.
     * @param bigTimeGap
     * @return
     */
    public TradeCandles getBigTimeTradeCandles(long bigTimeGap){
        if(bigTimeGap <= timeGap){
            logger.error("time setting error : [" + bigTimeGap + "] is bigger than [" + timeGap + "]");
            return null;
        }
        if(!isEmptyCandleMake){
            logger.error("empty candle make first : makeEmptyCandle(List<String> businessDayList);");
            return null;
        }
        TradeCandles bigTradeCandles = new TradeCandles(bigTimeGap);
        bigTradeCandles.setShortGapRatio(this.shortGapRatio);
        bigTradeCandles.setSteadyGapRatio(this.steadyGapRatio);

        int candleSize = candleList.size();

        int bigDivisionCount = (int)(bigTimeGap / timeGap);

        boolean isMinute = bigTimeGap < Times.DAY_1;


        List<TradeCandle> bigTradeCandleList = new ArrayList<>();

        for (int i = 0; i < candleSize; i++) {

            TradeCandle bigTradeCandle = new TradeCandle();

            TradeCandle tradeCandle = candleList.get(i);
            int tradeCount = tradeCandle.getTradeCount();
            double volume = tradeCandle.getVolume();
            double low = tradeCandle.getLow();
            double high = tradeCandle.getHigh();
            double open = tradeCandle.getOpen();
            double close = tradeCandle.getClose();
            long openTime = tradeCandle.getOpenTime();
            long closeTime = tradeCandle.getCloseTime();

            String tradeDay = DateUtil.getDateYmd(openTime,"MMdd");

            for (int j = i+1; j < bigDivisionCount && i+1 < candleSize; j++) {
                TradeCandle nextTradeCandle = candleList.get(j);
                int nextTradeCount = nextTradeCandle.getTradeCount();
                double nextVolume = nextTradeCandle.getVolume();
                double nextLow = nextTradeCandle.getLow();
                double nextHigh = nextTradeCandle.getHigh();
                double nextClose = nextTradeCandle.getClose();
                long nextOpenTime = nextTradeCandle.getOpenTime();
                String nextTradeDay = DateUtil.getDateYmd(nextOpenTime,"MMdd");

                if(!nextTradeDay.equals(tradeDay)){
                    break;
                }

                volume += nextVolume;
                if(nextLow < low){
                    low = nextLow;
                }
                if(nextHigh > high){
                    high = nextHigh;
                }
                close = nextClose;
                tradeCount += nextTradeCount;

            }
            bigTradeCandle.setTradeCount(tradeCount);
            bigTradeCandle.setOpen(open);
            bigTradeCandle.setClose(close);
            bigTradeCandle.setHigh(high);
            bigTradeCandle.setLow(low);
            bigTradeCandle.setOpenTime(openTime);
            bigTradeCandle.setCloseTime(closeTime);
            bigTradeCandleList.add(bigTradeCandle);
        }

        TradeCandle[] bigTradeCandleArr = (TradeCandle[])bigTradeCandleList.toArray();


        return bigTradeCandles;
    }
}