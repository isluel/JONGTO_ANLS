from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline
from typing import List

from AnalyzeRequest import AnalyzeRequest

# Label 매핑 (숫자 -> 텍스트)
label_mapping = {
    "0": "기쁨(행복한)",
    "1": "고마운",
    "2": "설레는(기대하는)",
    "3": "사랑하는",
    "4": "즐거운(신나는)",
    "5": "일상적인",
    "6": "생각이 많은",
    "7": "슬픔(우울한)",
    "8": "힘듦(지침)",
    "9": "짜증남",
    "10": "걱정스러운(불안한)"
}

class SentimentAnalyzer:
    def __init__(self):
        model_name = "nlp04/korean_sentiment_analysis_kcelectra"
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModelForSequenceClassification.from_pretrained(model_name)
        self.pipeline = pipeline("sentiment-analysis", model=self.model, tokenizer=self.tokenizer)

    def analyze_sentences(self, sentences: List[AnalyzeRequest]) -> List[dict]:
        results = []
        for sentence in sentences:
            prediction = self.pipeline(sentence.sentence)[0]
            raw_label = prediction['label']
            score = prediction['score']
            mapped_label = label_mapping.get(raw_label, raw_label)
            results.append({
                "id": sentence.id,
                "sentence": sentence.sentence,
                "label": mapped_label,
                "score": round(score, 4)
            })
        return results
