# seomse-trading
- 최근에 선물시장 외국인 수급에 따른 영향도 분석 및 매수 매도 포인트 관련 연구개발 의뢰가 들어와서 오픈소스쪽 작업이 잠시 미루어지고 있습니다. 조만간에 다시 시작될 예정입니다.
- 
# 개발환경
- open jdk 15

# 개요
- 기술적 분석을 활용한 다양한 매매기법
- 아직 초기 단계로 개발된 부분이 많지 않습니다..
- 주식, 선물, 비트코인 에서 사용할 수 있는 기술적 분석을 만들고자 시작 하였으며, 아직 많은 부분이 개발되지는 않은 상태입니다.
- 다양한 알고리즘을 만들고 만들어진 알고리즘을 지수화 하여 머신런닝의 임베딩 모델도 활용 하고자 합니다.
- 이와 별개로 다양한 패턴 조합 자동 시뮬레이터 기능을 제공 하려고 합니다.
- 다양한 기술적 분석을 공부하면서 구현중이므로 아직 구현되지 않은 부분이 많습니다.
- seomse-stock, seomse-bitcoin 프로젝트에서 사용됩니다.
- 최근 주식 자동매매에 사용되기 위해 많은 기능이 업데이트 예정입니다
- 위 패키지를 사용한 내용들이 기술블로그에 올라가고 있습니다.
- technical-analysis 모듈에서 알고즘을 개발하고 chart-view 를 통해 테스트 합니다.
- chart-view 는 트레이딩뷰에서 제공하는 lightweight-charts 를 사용합니다.

# 구성
- technical-analysis 
  - 기술적 분석에 이용되는 다양한 기능
- chart-view 
  - 시각화

# gradle
- 기존에 seomse-trading 하나의 모듈이었던 부분이 세분화 됩니다.

implementation 'com.seomse.trading:technical-analysis:0.1.0'
- etc
    - https://mvnrepository.com/artifact/com.seomse.trading/technical-analysis/0.1.0

implementation 'com.seomse.trading:chart-view:0.1.0'
- etc
    - https://mvnrepository.com/artifact/com.seomse.trading/chart-view/0.1.0


# communication
### blog, homepage
- [www.seomse.com](https://www.seomse.com/)
- [github.com/seomse](https://github.com/seomse)
- [seomse.tistory.com](https://seomse.tistory.com/)
- [seomse.github.io](https://seomse.github.io/)
- seomse.com

### 카카오톡 오픈톡
 - https://open.kakao.com/o/g6vzOKqb
   - 참여코드: seomse

### 슬랙 slack
- https://seomse.slack.com/

### email
 - comseomse@gmail.com
 
 
# main developer
 - macle
    -  [github.com/macle86](https://github.com/macle86)
