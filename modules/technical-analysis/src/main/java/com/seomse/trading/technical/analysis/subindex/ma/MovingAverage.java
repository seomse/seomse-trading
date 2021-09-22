/*
 * Copyright (C) 2021 Seomse Inc.
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

package com.seomse.trading.technical.analysis.subindex.ma;

import com.seomse.trading.technical.analysis.CandleBigDecimalChange;
import com.seomse.trading.technical.analysis.candle.Candle;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 이평선
 * @author macle
 */
public class MovingAverage {

    /**
     * 평균 배열 얻기
     * @param candles 캔들 배여
     * @param n 평균을 구하기위한 개수 N
     * @param averageCount 평균 배열 카운드 (얻고자 하는 수)
     * @return 평균 배열
     */
    public static BigDecimal[] getArray(Candle[] candles, int n, int averageCount) {
        return getArray(CandleBigDecimalChange.getCloseArray(candles), n, averageCount);
    }

    /**
     * 평균 배열 얻기
     *
     * @param array      보통 종가 배열을 많이사용 함
     * @param n            평균을 구하기위한 개수 N
     * @param averageCount 평균 배열 카운드 (얻고자 하는 수)
     * @return 평균 배열
     */
    public static BigDecimal[] getArray(BigDecimal[] array, int n, int averageCount) {

        if (averageCount > array.length) {
            averageCount = array.length;
        }

        BigDecimal[] averages = new BigDecimal[averageCount];

        //i를 포함해야 하기때문에 + 1을한다
        int arrayGap = array.length - averageCount + 1;

        for (int i = 0; i < averageCount; i++) {
            int end = arrayGap + i;
            int start = end - n;

            double length = n;
            if (start < 0) {
                start = 0;
                length = end;
            }
            BigDecimal sum = BigDecimal.ZERO;

            for (int j = start; j < end; j++) {
                sum = sum.add(array[j]);
            }
            averages[i] = sum.divide(new BigDecimal(length), MathContext.DECIMAL128);
        }
        return averages;
    }
}
