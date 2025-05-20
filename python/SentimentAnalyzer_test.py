from SentimentAnalyzer import SentimentAnalyzer
from AnalyzeRequest import AnalyzeRequest

# Sentiment Analyzer 로드
analyzer = SentimentAnalyzer()

# 분석할 문장 리스트
sentences = [
    AnalyzeRequest(id=1, sentence="오늘은 기분이 좋다."),
    AnalyzeRequest(id=2, sentence="이 제품은 최악이다."),
    AnalyzeRequest(id=3, sentence="조금 아쉽지만 괜찮은 경험이었다."),
    AnalyzeRequest(id=4, sentence="어려움을 이겨냈는데 오름 주주분들도 이겨낼까라 생각합니다"),
    AnalyzeRequest(id=5, sentence="공포분위기 조성하느라 바쁜데 얘처럼 재무양호하고 저평가된 바이오주 하나 찾아서 가져와봐!"),
    AnalyzeRequest(id=6, sentence="특전사 출신이다 ㅡㅡㅡ \n 사장놈\n 죽인다 ㅡㅡㅡ \n 반드시"),
    AnalyzeRequest(id=7, sentence="오늘 로또 맞았다, 하한가에 줍줍"),
    AnalyzeRequest(id=8, sentence="내일 시가 2만원때 출발~ \n 털린사람 손"),
    AnalyzeRequest(id=9, sentence="죽고싶네 ㅠㅡㅠ \n 몰빵했는데"),
    AnalyzeRequest(id=10,
                   sentence="도대체 얼마만에 수익을 볼라 했길래... \n 이리들 난리고~~~?\n 그럴거면 단타매매를 배워서 하든지..ㅉㅉㅉ..... \n 개미를 모욕하는 짓거리 좀 절제하자!!! \n 이미 다 알도록 상장전부터 뿌려진 정보를.....뭔 새로운 악재라고...?? \n 회사가 실속있게 하겠다고 정리한 걸 주주들에게 공시했는데...뭐 어쩌라고???!!!!!! \n 더 신뢰 가는 호재 아니여??!!!!"),
    AnalyzeRequest(id=11,
                   sentence="내일 쭉 빼면 기회 \n 냉정히 보자 \n 그간 유방암 파이프라인 중단된거 모른사람 없는거고 \n 그냥 혹시 혹시 기대가 역시나 된거 뿐 이 회사가 그 기대로 지금 시총만들어진거 아니니 낼도 빼면 그냥 사라 \n 총알이나 만들어야 겠다"),
    AnalyzeRequest(id=12,
                   sentence="냉정히 좋게 보려해도 수급이 \n 개인 수급이 빠져나가고 기관 연기금 수급이 들어왔으면 좋게 보겠는데 개인수급 들어고 기관 연기금 수급이 빠져나갔으니 좋게 못보겠네"),
    AnalyzeRequest(id=13, sentence="일단 내일 하한가 확정 \n 3연하냐 4연하냐 그것이 문제로다")
]

results = analyzer.analyze_sentences(sentences)

for result in results:
    id = result['id']
    sentence = result['sentence']
    label = result['label']
    score = result['score']
    print(f"문장: '{sentence}' -> 감정: {label} (확신도: {score:.4f})")