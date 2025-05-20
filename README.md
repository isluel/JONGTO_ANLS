# 네이버 종토방 감성 분석을 통한 주가 영향력 분석

## 배경
"네이버 종목 토론방의 여론이 실제 주가에 영향을 미칠까?"라는 의문에서 출발하였습니다.  
온라인 커뮤니티는 투자자들의 심리와 여론이 집약되는 공간이며,   
이러한 감정의 흐름이 실제 주가에 영향을 미칠 가능성이 있다고 판단했습니다.
## 목적
- 토론방 댓글의 감성(긍정/부정)을 분석하여 실제 주가 변동과의 상관관계를 파악합니다.  
- 특히, 전날의 감성 분석 결과가 다음 날 주가에 어떤 영향을 주는지 분석
- 감성 데이터가주가 예측의 보조 지표로 활용 가능한지 여부를 검증
## 자연어 처리 방식 (추후 확인후 변경 가능)
- 한국어 특화 언어 모델인 KoBERT를 기반으로 감성 분석 수행
- 네이버 종목 토론방 댓글을 수집하여 문장 단위로 전처리
- 긍정 / 부정 / 중립 세 가지 감정 클래스로 분류
- 감성 결과는 종목별, 날짜별로 집계하여 주가 데이터와 정렬

## Total

### 흐름도
![image](https://github.com/user-attachments/assets/15d82755-4976-401b-8b42-e3897a165b7f)

### 사용 환경

Intellij, Spring boot, Spring boot batch, Docker, MySQL, Python


## Step - NaverCrawl

### 요구사항 정의
- DB에 저장된 정보에 해당되는 주식을 Naver 종목 토론방의 데이터를 크롤링하여 DB에 저장한다.

### 상세설계 내용
- 하나의 Job 과 하나의 Step으로 구성된다.
- Job Parameter로 수집할 데이터의 기간을 전달한다.
- Item Reader 에서는 MySQL에 저장된 Naver 주식 정보를 가져와 Item Processor로 넘겨준다.
- Item Processor는 ItemReader에서 전달 받은 주식 정보 중 Naver code와 Job Parameter의 기간을 이용하여 Naver 종목 토론방 Web의 데이터를 크롤링한다.
- ItemProcessor는 가져온 결과를 Naver Post 로 만들어 Item Writer에 전달한다.
- Item Writer는 Processor로 부터 전달 받은 데이터를 저장한다.

### 흐름도
![image](https://github.com/user-attachments/assets/a766e0ca-97e4-40dd-b00b-8bd7f4123f68)

### Repository
 ![image](https://github.com/user-attachments/assets/afbd803f-da1b-4a1e-90ed-6228d5fdd536)


## Step - Sentiment

### 요구사항 정의
- 종목토론방 데이터를 Python 감정 분석 API로 전달하여 분석 후 결과를 전달받아 DB에 저장한다.

### 상세설계 내용
- Job Parameter로 종목 토론방 데이터의 기간을 전달한다.
- Item Reader 에서는 MySQL에 저장된 종목토론방 데이터를 가져와 Item Processor로 넘겨준다.
- Item Processor는 ItemReader에서 전달 받은 정보를 감정분석 API 에 전달하여 결과를 받아온다. 
- ItemProcessor는 가져온 결과를 Sentiment 로 만들어 Item Writer에 전달한다.
- Item Writer는 Processor로 부터 전달 받은 데이터를 저장한다.

### 흐름도
![image](https://github.com/user-attachments/assets/30037d50-6322-43c7-861c-11021cc3b378)

### Repository
![image](https://github.com/user-attachments/assets/d0d7c570-e148-4fd1-b222-b3561ae790f1)


## Step - Stock

### 요구사항 정의
- 공공 데이터 포탈로부터 주식의 일별 호가 정보를 가져와 저장한다.

### 상세설계 내용
- Job Parameter로 수집할 데이터의 기간을 전달한다.
- Item Reader 에서는 MySQL에 저장된 주식 정보를 가져와 Item Processor로 넘겨준다.
- Item Processor는 ItemReader에서 전달 받은 주식 정보 중 code와 Job Parameter의 기간을 이용하여 공공 데이터 포탈의 주가 정보를 조회한다.
- ItemProcessor는 가져온 결과를 Daily Stock Price 로 만들어 Item Writer에 전달한다.
- Item Writer는 Processor로 부터 전달 받은 데이터를 저장한다.

### 흐름도
![image](https://github.com/user-attachments/assets/a04565c9-8d96-4b7a-983d-c1391527ffb7)

### Repository
![image](https://github.com/user-attachments/assets/8aed3dc1-753f-4d60-8f52-d5729d66b484)

## 분석 화면

### 분석 화면 요구사항
- 조회 일자에 대해 주식의 주가를 표시한다.
- 주식과 조회 일자에 대해 감석 분석 결과, 종토방 내역을 표시한다.

### 화면 구성도
![image](https://github.com/user-attachments/assets/c22a1484-6ed8-4eb4-a94d-b324132a20d6)
![image](https://github.com/user-attachments/assets/24157938-6a86-4447-8bae-aa4497e59e5a)

