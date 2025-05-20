# 출력 데이터 모델 정의
from pydantic import BaseModel

class AnalyzeResponse(BaseModel):
    id: int
    sentence: str
    label: str
    score: float