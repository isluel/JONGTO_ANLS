from fastapi import FastAPI
from typing import List

from SentimentAnalyzer import SentimentAnalyzer
from AnalyzeRequest import AnalyzeRequest
from AnalyzeResponse import AnalyzeResponse

# FastAPI 앱 생성
app = FastAPI()

# Sentiment Analyzer 로드
analyzer = SentimentAnalyzer()

# 출력 데이터 모델 정의


# API 엔드포인트
@app.post("/analyze", response_model=List[AnalyzeResponse])
async def analyze_sentiment(requests: List[AnalyzeRequest]):
    results = analyzer.analyze_sentences(requests)
    return results