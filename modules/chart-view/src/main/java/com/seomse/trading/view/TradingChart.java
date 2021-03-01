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
package com.seomse.trading.view;

import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.time.DateUtil;
import com.seomse.trading.technical.analysis.candle.CandleStick;

import java.io.*;
/**
 * @author ccsweets
 */
public class TradingChart {

    /* 차트 데이터 타입 */
    public enum ChartDateType {MINUTE, DAY}

    /* 캔들 데이터 */
    CandleStick[] candleStickArr;
    /* HTML chart create String */
    StringBuilder createChartStr = new StringBuilder();
    /* 차트 날짜유형 */
    ChartDateType dateType;

    /* pureJs contents */
    String pureJsContents;
    /* LightWeight Js contents */
    String lightWeightJsContents;

    /* title */
    String browserTitle = "Seomse LightWeight-Chart View";

    /**
     * 브라우저 타이틀을 설정 한다.
     * @param  browserTitle browserTitle
     */
    public void setBrowserTitle(String browserTitle) {
        this.browserTitle = browserTitle;
    }

    /**
     * Constructor
     * @param candleStickArr 캔들스틱 배열
     */
    public TradingChart(CandleStick[] candleStickArr){
        this(candleStickArr,600,300, ChartDateType.DAY);
    }
    /**
     * Constructor
     * @param candleStickArr 캔들스틱 배열
     * @param width X축 길이
     * @param height Y축 높이
     * @param dateType 날짜유형
     */
    public TradingChart(CandleStick[] candleStickArr , int width , int height , ChartDateType dateType){

        pureJsContents = FileUtil.getFileContents(new File("resources/pure.js"),"UTF-8");
        lightWeightJsContents = FileUtil.getFileContents(new File("resources/lightweight-charts.standalone.production.js"),"UTF-8");
        this.candleStickArr = candleStickArr;
        this.dateType = dateType;
        createChartStr.append( """
                var chart = LightweightCharts.createChart(document.body, {
                    width: %d,
                  height: %d,
                  rightPriceScale: {
                    visible: true,
                    borderColor: 'rgba(197, 203, 206, 1)',
                  },
                  leftPriceScale: {
                    visible: false,
                    borderColor: 'rgba(197, 203, 206, 1)',
                  },
                  layout: {
                    backgroundColor: '#ffffff',
                    textColor: 'rgba(33, 56, 77, 1)',
                  },
                  grid: {
                    horzLines: {
                      color: '#F0F3FA',
                    },
                    vertLines: {
                      color: '#F0F3FA',
                    },
                  },
                  crosshair: {
                    mode: LightweightCharts.CrosshairMode.Normal,
                  },
                  timeScale: {
                    borderColor: 'rgba(197, 203, 206, 1)',
                  },
                  handleScroll: {
                    vertTouchDrag: false,
                  },
                });
                
                const candlestickSeries = chart.addCandlestickSeries({
                  priceScaleId: 'left'
                });
                """.formatted(width,height));
        createChartStr.append("candlestickSeries.setData([");
        int candleStickArrSize = candleStickArr.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < candleStickArrSize; i++) {
            CandleStick candleStick = candleStickArr[i];
            double open = candleStick.getOpen();
            double close = candleStick.getClose();
            double low = candleStick.getLow();
            double high = candleStick.getHigh();
            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(candleStick.getOpenTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(candleStick.getOpenTime(),"yyyy-MM-dd HH:mm");
            }
            createChartStr.append("""
                    {
                        close: %.2f,
                        high: %.2f,
                        low: %.2f,
                        open: %.2f,
                        time: '%s'
                      },
                    """.formatted(
                    close,high,low,open,timeStr
            ));
        }
        createChartStr.setLength(createChartStr.length()-1);
        createChartStr.append("]);\n");
    }

    /**
     * 차트에 마커를 전부 추가 한다.
     * @param markerDataArray 마커 데이터 배열
     */
    public void addMarkerAll(MarkerData[] markerDataArray) {
        createChartStr.append("""
              var markers = [];
                """);

        for (MarkerData markerData : markerDataArray) {
            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(markerData.getTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(markerData.getTime(),"yyyy-MM-dd HH:mm");
            }
            createChartStr.append("""
                markers.push({ time: '%s', position: '%s', color: '%s', shape: '%s', text: '%s'});
                """
            .formatted(timeStr,markerData.getMarkerType().name(),markerData.getColor(),markerData.getMarkerShape().name(),markerData.getText())
            );
        }
        createChartStr.append("candlestickSeries.setMarkers(markers);");
    }

    /**
     * 거래량 데이터를 전부 추가한다.
     * @param volumeDataArr 거래량 데이터 배열
     * @param topMargin topMargin
     * @param bottomMargin bottomMargin
     */
    public void addVolumeAll(VolumeData[] volumeDataArr, double topMargin , double bottomMargin){
        createChartStr.append("""
                var volumeSeries = chart.addHistogramSeries({
                  	color: '#26a69a',
                  	priceFormat: {
                  		type: 'volume',
                  	},
                  	priceScaleId: '',
                  	scaleMargins: {
                  		top: %.1f,
                  		bottom: %.1f,
                  	},
                  });
                  volumeSeries.setData([
                """.formatted(topMargin,bottomMargin));
        int volumeDataArrSize = volumeDataArr.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < volumeDataArrSize; i++) {
            VolumeData volumeData = volumeDataArr[i];
            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(volumeData.getTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(volumeData.getTime(),"yyyy-MM-dd HH:mm");
            }

            createChartStr.append("""
                { time: '%s', value: %.2f, color: '%s' },
                """.formatted(timeStr,volumeData.getVolume() , volumeData.getColor()));
        }
        createChartStr.append("]);");
    }

    /**
     * 선형 데이터를 전부 추가한다.
     * @param lineDataArr 선형 데이터 배열
     * @param color 색깔
     * @param size 굵기
     */
    public void addLineAll(LineData[] lineDataArr , String color, int size){
        createChartStr.append("""
                chart.addLineSeries({
                  color: '%s',
                  lineWidth: %d,
                }).setData([
                """.formatted(color,size));
        int lineDataArrSize = lineDataArr.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < lineDataArrSize; i++) {
            LineData lineData = lineDataArr[i];
//            long openTime = lineData.getTime();
            double price = lineData.getPrice();


            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(lineData.getTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(lineData.getTime(),"yyyy-MM-dd HH:mm");
            }
            createChartStr.append("""
                    {
                        time: '%s',
                        value: %.2f
                      },
                    """.formatted(
                    timeStr,price
            ));

        }

        createChartStr.setLength(createChartStr.length()-1);
        createChartStr.append("]);\n");
    }

    /**
     * HTML 데이터를 전달 받는다.
     * @return HTML
     */
    public String getHtml(){
        //noinspection StringBufferReplaceableByString
        StringBuilder result = new StringBuilder("""
                <!DOCTYPE html>
                <html>
                <head>
                  <title>%s</title>
                  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                </head>
                <body>
                    <script>%s</script>
                    <script>%s</script>
                  
                """.formatted(browserTitle,pureJsContents,lightWeightJsContents)
        );

        result.append("<script>\n").append(createChartStr.toString()).append("\n</script>\n");

        return result.append("</body></html>").toString();
    }
}
