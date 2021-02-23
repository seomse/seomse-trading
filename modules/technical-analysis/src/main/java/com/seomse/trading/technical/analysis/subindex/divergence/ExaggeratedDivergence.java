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

package com.seomse.trading.technical.analysis.subindex.divergence;

import com.seomse.trading.technical.analysis.candle.Candle;

/**
 * 3. 과장된 다이버전스
 *  - 과장된 하락 다이버전스
 *  주가가 비슷한 높이의 고점을 형성하는 동안, 보조지표 고점이 하락하는 현상
 *  하락추세로 전환가능성이 높음
 *
 *  - 과장된 상승 다이버전스
 *   주가가 비슷한 높이의 저점을 형성하는 동안,, 보조지표 저점이 상승하는 현상
 *   상승추세로 전환 가능성이 높음
 *
 * @author macle
 */
public class ExaggeratedDivergence implements DivergenceSignalSearch {


    @Override
    public DivergenceSignal rise(Candle[] priceCandles, Candle [] subIndexCandles, double steadyRate, int candleCount) {
        return null;
    }

    @Override
    public DivergenceSignal fall(Candle[] priceCandles, Candle [] subIndexCandles, double steadyRate, int candleCount) {
        return null;
    }
}
