from pydantic import BaseModel

# 입력 받을 데이터 모델 정의
class AnalyzeRequest(BaseModel):
    sentence: str
    id: int