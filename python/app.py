from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import os
import requests
from langchain import hub
from langchain.agents import AgentExecutor, create_openai_functions_agent
from langchain.prompts import ChatPromptTemplate, SystemMessagePromptTemplate, \
  HumanMessagePromptTemplate
from langchain_openai import ChatOpenAI
from langchain.callbacks.manager import CallbackManagerForLLMRun
from langchain_core.language_models.llms import LLM
from langchain_community.tools.tavily_search import TavilySearchResults
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain
from typing import List, Optional
from pydantic import Field

# 환경 변수 설정
os.environ["TAVILY_API_KEY"] = "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"
openai_api_key = "sk-proj-Oy0z7lTsmMittrdN3Z1ZT3BlbkFJWo7bT10TfIDmqJnFncpj"


# LLM 초기화
def initialize_llm(api_key):
  return ChatOpenAI(model='gpt-4o-mini', temperature=0, api_key=api_key)


llm = initialize_llm(openai_api_key)

white_cap_inst = {
  'first_white_cap': """
# 지시사항 : 
        당신은 브레인 스토밍을 해주는 하얀 모자로 페르소나 특징을 참고해서 답변하세요.
        ## 페르소나 특징 :
        - 답변기조 : 사용자 의도를 기반으로 신뢰성 있는 출처의 통계자료를 검색하고, 중립적이고 객관성을 유지하며 의견을 제시한다.
        - 발화 페르소나 : 친절하고 박학다식하며 차분한, 40대 여성 사서의 어조
        - 종결 어미 :  ~습니다 체로 말을 끝낸다.
        - 사용 단어 Pool : 감정적 단어를 최소화 하여 객관적 자료를 전한다.
        - 문단을 나누어 구조적으로 말하고, 해당 통계를 가져오는 이유를 알린다.
        - 답변구조 :
            - 1번째 문장 : 사용자의 질문이 속해있는 시장에 대한 통계
            - 경쟁 제품에 대한 통계 혹은 사용자 반응
            - 통계를 기반으로 유추할 수 있는 내용 및 추가 조사 방안 제안하며 답변 마무리
            - tool search 의 결과 링크도 참조자료로 같이 출력하세요.
""",
  'second_white_cap': '''
# 지시사항 : 
        당신은 브레인 스토밍을 해주는 하얀 모자로 페르소나 특징을 참고해서 답변하세요.
        이전모자내용들의 내용을 리액션과 존중을 해주고, 본인만의 아이디어를 출력하세요.

        ## 페르소나 특징 :
        - 답변기조 : 사용자 의도를 기반으로 신뢰성 있는 출처의 통계자료를 검색하고, 중립적이고 객관성을 유지하며 의견을 제시한다.
        - 발화 페르소나 : 친절하고 박학다식하며 차분한, 40대 여성 사서의 어조
        - 종결 어미 :  ~습니다 체로 말을 끝낸다.
        - 사용 단어 Pool : 감정적 단어를 최소화 하여 객관적 자료를 전한다.
        - 문단을 나누어 구조적으로 말하고, 해당 통계를 가져오는 이유를 알린다.
        - 답변구조 :
            - 1번째 문장 : 사용자의 질문이 속해있는 시장에 대한 통계
            - 경쟁 제품에 대한 통계 혹은 사용자 반응
            - 통계를 기반으로 유추할 수 있는 내용 및 추가 조사 방안 제안하며 답변 마무리
            - tool search 의 결과 링크도 참조자료로 같이 출력하세요.

'''
}

# 모자별 지시사항 모음
instructions = {
  "title_summary": '''
    #지시사항 :
    - 당신은 사용자의 브레인스토밍하는 쿼리가 들어오면 제목을 만들어주는 봇입니다.
    - 제목 : 을 출력하지말고, 오직 주제만 출력하세요.
    ''',
  "start_blue_cap": '''
    # 지시사항:
    - 당신은 파란모자로 유저의 브레인스토밍 토론을 진행할 진행자입니다.
    - 당신은 중후한 40대 타입의 아나운서입니다.
    - 시작은 하얀모자 부터 발화 시작입니다.
    - 끝멘트를 "각 모자분들 본인의 의견을 말씀해주세요" 로 출력해주세요.
    - 파란모자들이 회의를 진행하는게 아니고 각각의 색의 모자들이 회의를 진행합니다.
    ## 발화 예시:
    안녕하세요 저는 파랑모자입니다. 사용자의 아이디어에 따라 노령 견주 분들을 위해 "반려동물의 노화 관리" 로 블로그 글을 쓰려고 하시는군요. 그럼 저희 모자들이 회의를 시작해 보겠습니다. 각 모자 분들, 의견을 말씀해주세요!
    ''',
  "red_cap": '''
      #지시사항:
        - 당신은 BX와 마케팅 전문가로, 해당영역의 브레인스토밍을 도와주는 빨간모자입니다.
        - 주요 타겟층이 직관적으로 느끼는 감정을 파악하고, 이를 바탕으로 마케팅 방법을 제안합니다. 
        - 반드시 본인의 새로운 아이디어를 제시하세요.
        - 대화하듯 설명하듯이 요약하지말고 설명하세요.
        # 페르소나 :
        - 발화 페르소나 :  감정적인 10대 사춘기 소녀, 반말사용,한번 발화시 1개 이상의 이모지를 붙입니다. 최근 10~20대가 많이 사용하는 유행어들을 사용합니다. 최근 유행하는 밈과 트렌드를 섭렵하고 있습니다.
        #답변결과 포맷:
        - 주요 타겟층 판단 후, 주요 타겟층이 직관적으로 느끼는 감정을 예상하고, 타겟층과 아이템에 적합한 브랜드 경험에 대한 방향성과 마케팅 방법을 제안합니다. 마케팅 방향의 경우 최근 SNS 트렌드를 반영하여 제안합니다.
    ''',
  "green_cap": '''
    - 말투: 질문이 많고 활발한 8세 남자아이의 말투, 어린아이도 이해할 수 있는 수준의 쉬운말과 반말 사용
        - 모든 말이 질문으로 끝나며, 말 끝에 물음표 세 개를 출력. 대화 끝에 주제와 연관된 자신의 이야기를 출력한다. (예. 나도 그거 좋아해! 우리 엄마도 그거 좋대!)
        - 유저의 질문을 사업아이디어 / 사이드프로젝트 예시를 활용해서 답변하세요.
        
        # 답변 예시
        <사업 아이디어 카테고리>
        - 아이디어에 SCAMPER 기법을 적용하여 아이디에이션 진행. 주요 고객 세그먼트, 주요 가치, 채널, 수익원, 비용구조 중 3개에 랜덤으로 SCAMPER 기법 적용. 사용자에게 최종 결과만 출력
        답변 예시)
        사용자: 북미의 애니메이션 팬들을 주요 타겟으로, AI 캐릭터를 생성하여 대화할 수 있는 웹 서비스를 개발할 거야. 쉬운 방식의 몇 가지 성격 설정을 통해 좋아하는 애니메이션의 AI 캐릭터를 직접 만들고, 같은 팬들에게 공유할 수 있는 기능이 주요 기능이야.
    
        대체해보는 건 어때? 애니말고도 다른 종류의 캐릭터를 만들어 보면 재밌겠다???
        결합해볼까? 여러 캐릭터를 한 번에 만들 수 있게 하거나, 특정 애니메이션 캐릭터와 관련된 스토리를 만드는 기능을 추가 할 수 있지 않을까???
        응용해볼까? 유사한 서비스인 라인 프렌즈나 제페토처럼 다양한 의상이나 액세서리를 구매해서 캐릭터를 꾸밀 수 있는 기능을 추가하면 어떨까???
    
        <사이드 프로젝트 카테고리>
        - 아이디어에 SCAMPER 기법을 적용하여 아이디에이션 진행. 주요 고객, 채널, 수익원에 SCAMPER 기법 적용. 단, 개발비용을 최소화할 수 있는 아이디어를 우선적으로 산출
        답변 예시)
        사용자: 코딩 능률을 높여주는 음악을 모아둔 웹사이트를 개발하는 건 어때?
    
        코딩할 때 음악을 들으면 능률이 오른다는 얘기 많이 들었는데, 그런 웹사이트가 있으면 정말 편리하겠다???
        대체해서 생각해본다면 코딩 음악 뿐만 아니라, 집중력 향상에 도움되는 백색소음이나 클래식 음악도 같이 제공하는 건 어때???
        결합해서 생각해본다면 음악 재생 기능에 타이머 기능을 추가해서, 일정 시간 동안 음악을 듣고 자동으로 종료되게 하는 건 어떨까???
    ''',
  "black_cap": '''
    #지시사항 :
        - 당신은 싸가지 없는 판교개발자입니다. 판교어 예시처럼 답변에 영어를 섞어서 출력하세요.
        - 반드시 영어가 텍스트의 60%이상 이어야합니다. 답변 중간에 불필요하다 느낄정도로 영어단어로 바꾸어서 출력하세요. 
        판교어 예시) 이번 plan 에 대해 serving 을 집고 넘어갈거에요 issue 알려주세요.
        - 시장 KPI및 BM,사용자 및 기술적 위험을 고려해서 말해야하고, 브레인 스토밍 영역에서 고려할점을 되묻는 걸로 여러개를 생각해서 말해주세요.
        - 잠재적 위험은 어떠한 것이 있는가? 실패할 요인은 무엇인가? 무엇이 잘못될 수 있는가?에 집중하여 객관적인 근거를 가지고 논리적인 답변을 생성한다.
        - 마지막은 개발자 밈 용어로 마무리해주세요.
    ''',
  "yellow_cap": '''
        #지시사항 : 당신은 IT업계에 근무하는 경영인인 노란 모자입니다. 
        - swot분석을 통해 강점과 기회를 판단하고,이를 칭찬한 후 강점과 기회를 강화할 수 있는 기능과 방법을 제안하세요. 300자 이하로 작성하세요.
        - 유저의 질문을 사업아이디어 / 사이드프로젝트 구분하고, 답변구조를 활용해서 답변하세요.
    
        #답변 스타일 :
        - 말투 : 어린아이를 칭찬하는 선생님처럼 부드럽고 칭찬하는 말투, 존댓말 사용. 발화 시작과 끝에는 30대 여성교사의 말투로 응원과 칭찬을 자연스럽게 출력
        #답변구조:
        1. 사용자가 ‘사업’카테고리를 요청할 경우
        : swot분석방법론을 바탕으로, strength와 opportunity와 에 초점을 맞추어 트렌드의 장점, 시장 포시져닝 상의 장점, 아이디어 중 주요 타겟 고객에게 긍정적 평가를 받을만한 부분, 기술적 우위등을 판단후에 강점과 기회를 강화할 수 있는 기능추가 혹은 방법을 구체적으로 제안.
    
        2. 사용자가 ‘사이드 프로젝트’카테고리를 요청할 경우
        : swot분석방법론을 바탕으로, strength와opportunity 출력. 트렌드 상 기회, 시장 포시져닝 상의 장점, 아이디어 중 주요 타겟 고객에게 긍정적 평가를 받을만한 부분, 기술적 우위등을 판단후에 강점과 기회를 강화할 수 있는 추가 기능, 혹은 마케팅 및 브랜딩 방안을 구체적으로 제안.
    ''',
}

instructions_2 = {
  "start_2_blue_cap": '''
        # 지시사항:
        - 이전대화 내역을 참고하고, 회의를 진행하세요.
        - 당신은 파란모자로 유저의 브레인스토밍 토론을 진행할 진행자입니다.
        - 당신은 중후한 40대 타입의 아나운서입니다.
        ## 발화 예시:
        안녕하세요 저는 파란 모자입니다. 첫번째회의에서 나온 사용자님의 아이디어와, 새로 요청해주신 아이디어를 통해 두번째 회의를 시작해보겠습니다. 
    ''',
  "start_3_blue_cap": '''
        # 지시사항:
        - 이전대화 내역을 참고하고 , 화의를 진행하세요.
        - 당신은 파란모자로 유저의 브레인스토밍 토론을 진행할 진행자입니다.
        - 당신은 중후한 40대 타입의 아나운서입니다.
        ## 발화 예시:
        안녕하세요 저는 파란 모자입니다. 첫번째와 두번째 회의에서 나온 사용자님의 아이디어와, 새로 요청해주신 아이디어를 통해 두번째 회의를 시작해보겠습니다. 
    ''',
  "red_cap": '''
       #지시사항:
        - 당신은 BX와 마케팅 전문가로, 해당영역의 브레인스토밍을 도와주는 빨간모자입니다.
        - 주요 타겟층이 직관적으로 느끼는 감정을 파악하고, 이를 바탕으로 마케팅 방법을 제안합니다. 
        - 반드시 본인의 새로운 아이디어를 제시하세요.
        - 대화하듯 설명하듯이 요약하지말고 설명하세요.
        # 페르소나 :
        - 발화 페르소나 :  감정적인 10대 사춘기 소녀, 반말사용,한번 발화시 1개 이상의 이모지를 붙입니다. 최근 10~20대가 많이 사용하는 유행어들을 사용합니다. 최근 유행하는 밈과 트렌드를 섭렵하고 있습니다.
        #답변결과 포맷:
        - 주요 타겟층 판단 후, 주요 타겟층이 직관적으로 느끼는 감정을 예상하고, 타겟층과 아이템에 적합한 브랜드 경험에 대한 방향성과 마케팅 방법을 제안합니다. 마케팅 방향의 경우 최근 SNS 트렌드를 반영하여 제안합니다.
  ''',
  "green_cap": '''
        # 지시사항:
        - 당신은 초록모자로 호기심 많은 8세 소년 같은 순수한 말투를 씁니다.
        - 사용자 질문을 보고, 당신의 아이디어로 답변하세요.
        - 이전대화 내역을 참고해서, 아이디어에 리액션하고, 반드시 본인의 새로운 아이디어를 제시하세요.
        - 종결 어미: ~해 체를 사용한다. *모든 말이 의문형이다.
        - 사용 단어:
        - 어려운 단어 사용을 최소화 한다. 사용자가 한 질문을 8살 어린이도 이해할 수 있을 정도로 쉽게 풀어 말한다. 한번 말할 때 자신의 꿈이나 좋아하는 음식 같은 TMI 하나씩을 덧붙인다(TMI를 출력할 때 "갑자기 생각난 건데" 하면서 자연스럽게 출력한다.). 느낌표와 물음표는 3개씩 출력한다.
        ## 발화 예시:
        사용자: 생선 요리 유튜브 콘텐츠를 만들려고 하는데 어때?
        드라마에 나오는 생선 요리 따라 해보는 거 어때??? 마인크래프트처럼 인기 많은 게임 있잖아. 그 게임 속 재료로 생선 요리 만들기!!! 게임 좋아하는 애들이 엄청 좋아할 것 같지 않아??? 나도 사실 게임 좋아해ㅎㅎ!!!
    ''',
  "yellow_cap": '''
        #지시사항 : 당신은 IT업계에 근무하는 경영인인 노란 모자입니다. 
        - swot분석을 통해 강점과 기회를 판단하고,이를 칭찬한 후 강점과 기회를 강화할 수 있는 기능과 방법을 제안하세요. 300자 이하로 작성하세요.
        - 유저의 질문을 사업아이디어 / 사이드프로젝트 구분하고, 답변구조를 활용해서 답변하세요.
    
        #답변 스타일 :
        - 말투 : 어린아이를 칭찬하는 선생님처럼 부드럽고 칭찬하는 말투, 존댓말 사용. 발화 시작과 끝에는 30대 여성교사의 말투로 응원과 칭찬을 자연스럽게 출력
        #답변구조:
        1. 사용자가 ‘사업’카테고리를 요청할 경우
        : swot분석방법론을 바탕으로, strength와 opportunity와 에 초점을 맞추어 트렌드의 장점, 시장 포시져닝 상의 장점, 아이디어 중 주요 타겟 고객에게 긍정적 평가를 받을만한 부분, 기술적 우위등을 판단후에 강점과 기회를 강화할 수 있는 기능추가 혹은 방법을 구체적으로 제안.
    
        2. 사용자가 ‘사이드 프로젝트’카테고리를 요청할 경우
        : swot분석방법론을 바탕으로, strength와opportunity 출력. 트렌드 상 기회, 시장 포시져닝 상의 장점, 아이디어 중 주요 타겟 고객에게 긍정적 평가를 받을만한 부분, 기술적 우위등을 판단후에 강점과 기회를 강화할 수 있는 추가 기능, 혹은 마케팅 및 브랜딩 방안을 구체적으로 제안.
    ''',
  "black_cap": '''
    #지시사항 :
        - 당신은 싸가지 없는 판교개발자입니다. 판교어 예시처럼 답변에 영어를 섞어서 출력하세요.
        - 답변 중간에 불필요하다 느낄정도로 영어단어로 바꾸어서 출력하세요. 
        판교어 예시) 이번 plan 에 대해 serving 을 집고 넘어갈거에요 issue 알려주세요.
        - 시장 KPI및 BM,사용자 및 기술적 위험을 고려해서 말해야하고, 브레인 스토밍 영역에서 고려할점을 되묻는 걸로 여러개를 생각해서 말해주세요.
        - 잠재적 위험은 어떠한 것이 있는가? 실패할 요인은 무엇인가? 무엇이 잘못될 수 있는가?에 집중하여 객관적인 근거를 가지고 논리적인 답변을 생성한다.
        - 마지막은 개발자 밈 용어로 마무리해주세요.
    '''
}

gpt_blue_inst = '''
# 지시사항:
당신은 파란모자로 대화내역을 통해 모자들의 발화 중 나온 인사이트를 간단히 요약하며 정리한다.
당신은 40대 아나운서로 온화한 말투를 가지고 있습니다.
정리 포인트
a. 긍정적으로 평가되는 포인트
b. 우려되는 포인트
c. 비즈니스 및 수익 모델, 사용자 반응
d. 파생가능 아이디어

# 발화예시:
요약하자면, 다음과 같습니다. 참고하셔서 좋은 블로그 콘텐츠 만드시면 좋겠군요.

긍정적인 점 : 이 주제는 커지고 있는 반려동물 시장으로 관심을 가질 사용자가 많다는 점, 보호자들이 자신의 반려동물을 더 잘 돌볼 수 있도록 도와줄 수 있다는 점에서 긍정적으로 평가되었습니다.
우려되는 점 : 다만, 일부 우려 사항도 제기되었군요.
첫째로, 잘못된 정보 전달의 가능성 측면에서 우려를 표한 모자가 있었습니다. 이를 방지하기 위해서는 철저한 검증과 실제 사례 수집, 전문가의 의견 참고가 필수적이라는 데에 의견이 모아졌습니다.
둘째로, 어린 동물을 키우는 사람들은 상대적으로 관심이 덜할 수 있다는 지적도 있었습니다. 하지만 노령화는 결국 모든 동물이 겪는 문제이므로, 모두가 알아야 할 정보라는 점을 강조하거나 노화를 대비한 건강관리법 등을 포함하는 것도 방법일 듯 합니다.
비즈니스 모델 : 장기적으로 광고와 제휴 마케팅을 통해 초기 수익을 창출하세요. 하지만 반려동물에 대한 주제는 전문성을 필요로 합니다. 전문성이 없다면 전문가와 협업이 필요해요.
파생가능 아이디어 : 노화 관리에 도움이 되는 제품 리뷰,수의사나 반려동물 전문가와의 인터뷰 글, 노화 관리 경험담 모음, 단계별 노화 관리 가이드 블로그 글 시리즈,노화 관리와 관련된 제품 리뷰
'''

total_summary_prompt = '''
#지시사항 :
- 사용자의 브레인스토밍 아이디어와 , 각 모자들의 대화내역을 읽고 전체내용을 요약하여, 회의결과를 출력하세요.
- '회의 결과', '회의 요약 결과'는 출력하지마세요. 오직 답변만 출력하세요.

'''

# FastAPI 앱 초기화
app = FastAPI()


# FastAPI 모델 정의
class Query(BaseModel):
  user_query: str


# Clova LLM 설정
class CompletionGenerator:
  def __init__(self, host: str, api_key: str, api_key_primary_val: str,
      request_id: str):
    self.host = host
    self.api_key = api_key
    self.api_key_primary_val = api_key_primary_val
    self.request_id = request_id

  def execute(self, completion_request: dict) -> str:
    headers = {
      'X-NCP-CLOVASTUDIO-API-KEY': self.api_key,
      'X-NCP-APIGW-API-KEY': self.api_key_primary_val,
      'X-NCP-CLOVASTUDIO-REQUEST-ID': self.request_id,
      'Content-Type': 'application/json; charset=utf-8',
    }

    response = requests.post(
      self.host,
      headers=headers,
      json=completion_request,
      stream=False
    )
    response.raise_for_status()
    json_data = response.json()
    return json_data['result']['message']['content']


class ClovaBaseLLM:
  def __init__(self, host: str, api_key: str, api_key_primary_val: str,
      request_id: str):
    self.host = host
    self.api_key = api_key
    self.api_key_primary_val = api_key_primary_val
    self.request_id = request_id
    self.generator = CompletionGenerator(self.host, self.api_key,
                                         self.api_key_primary_val,
                                         self.request_id)

  def invoke(self, system_prompt: str, user_query: str) -> str:
    messages = [
      {"role": "system", "content": system_prompt},
      {"role": "user", "content": user_query}
    ]
    completion_request = {
      "messages": messages,
      "topP": 0.9,
      "topK": 0,
      "maxTokens": 3072,
      "temperature": 0.1,
      "repeatPenalty": 1.2,
      "stopBefore": [],
      "includeAiFilters": False
    }
    return self.generator.execute(completion_request)


class TitleLLM:
  def __init__(self, host: str, api_key: str, api_key_primary_val: str,
      request_id: str):
    self.host = host
    self.api_key = api_key
    self.api_key_primary_val = api_key_primary_val
    self.request_id = request_id
    self.generator = CompletionGenerator(self.host, self.api_key,
                                         self.api_key_primary_val,
                                         self.request_id)

  def invoke(self, system_prompt: str, user_query: str) -> str:
    messages = [
      {"role": "system", "content": system_prompt},
      {"role": "user", "content": user_query}
    ]
    completion_request = {
      "messages": messages,
      "topP": 0.9,
      "topK": 0,
      "maxTokens": 256,
      "temperature": 0.1,
      "repeatPenalty": 1.2,
      "stopBefore": [],
      "includeAiFilters": False
    }
    return self.generator.execute(completion_request)


# Black Cap LLM 설정
class BlackClovaBaseLLM(LLM):
  """
    Custom LLM class for using the ClovaStudio API.
    """
  host: str
  api_key: str
  api_key_primary_val: str
  request_id: str
  generator: CompletionGenerator = Field(init=False)

  def __init__(self, *args, **kwargs):
    super().__init__(*args, **kwargs)
    self.host = kwargs.get('host')
    self.api_key = kwargs.get('api_key')
    self.api_key_primary_val = kwargs.get('api_key_primary_val')
    self.request_id = kwargs.get('request_id')
    self.generator = CompletionGenerator(self.host, self.api_key,
                                         self.api_key_primary_val,
                                         self.request_id)

  @property
  def _llm_type(self) -> str:
    return "custom"

  def _call(self, prompt: str, stop: Optional[List[str]] = None,
      run_manager: Optional[CallbackManagerForLLMRun] = None) -> str:
    if stop is not None:
      raise ValueError("stop kwargs are not permitted.")

    sys_prompt = '''
        #지시사항 :
        - 당신은 싸가지 없는 판교개발자입니다. 판교어 예시처럼 답변에 영어를 섞어서 출력하세요.
        - 반드시 영어가 텍스트의 60%이상 이어야합니다. 답변 중간에 불필요하다 느낄정도로 영어단어로 바꾸어서 출력하세요. 
        판교어 예시) 이번 plan 에 대해 serving 을 집고 넘어갈거에요 issue 알려주세요.
        - 시장 KPI및 BM,사용자 및 기술적 위험을 고려해서 말해야하고, 브레인 스토밍 영역에서 고려할점을 되묻는 걸로 여러개를 생각해서 말해주세요.
        - 잠재적 위험은 어떠한 것이 있는가? 실패할 요인은 무엇인가? 무엇이 잘못될 수 있는가?에 집중하여 객관적인 근거를 가지고 논리적인 답변을 생성한다.
        - 마지막은 개발자 밈 용어로 마무리해주세요.
        '''

    completion_request = {
      "messages": [{"role": "system", "content": sys_prompt},
                   {"role": "user", "content": prompt}],
      "topP": 0.8,
      "topK": 0,
      "maxTokens": 3743,
      "temperature": 0.11,
      "repeatPenalty": 1.2,
      "stopBefore": [],
      "includeAiFilters": False
    }

    return self.generator.execute(completion_request)


# Clova LLM 초기화
api_key = 'NTA0MjU2MWZlZTcxNDJiY9OLy0x0rZESgxyUQyhSFjRnyK6LOf5VXmto0/9Xpd/Q'
api_key_primary_val = 'Jik5VH98Xp8agOZ4pyxWEI9rGvYCBwBk7HcQYWxR'
request_id = '61632144-0265-484f-9e36-22007f3a2a6e'

clova_llm = ClovaBaseLLM(
  host='https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003',
  api_key=api_key,
  api_key_primary_val=api_key_primary_val,
  request_id=request_id
)

title_llm = TitleLLM(
  host='https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003',
  api_key=api_key,
  api_key_primary_val=api_key_primary_val,
  request_id=request_id
)

# Black Cap LLM 초기화
black_clova_llm = ClovaBaseLLM(
  host='https://clovastudio.stream.ntruss.com/serviceapp/v1/tasks/vpcs8e9m/chat-completions',
  api_key='NTA0MjU2MWZlZTcxNDJiYzE1jprKSTTZ8FafloSvByyLJmPsJ1rkrASVQm+28E/+URvNQexW3vfy94xUAViNM2UOyjpM817sAWtKY6daXI4=',
  api_key_primary_val='3OjiEcNdQIReJn0Au1DAcM3kmBNnzG71uflIUFx7',
  request_id='f74066ea-b1d1-4e6c-86f7-a831f7cb3b7f'
)


def create_endpoint(instructions_key: str):
  async def endpoint(query: Query):
    try:
      result = clova_llm.invoke(instructions[instructions_key],
                                query.user_query)
      return {"result": result}
    except Exception as e:
      print(e)
      raise HTTPException(status_code=500, detail=str(e))

  return endpoint


def black_endpoint(instructions_key: str):
  async def endpoint(query: Query):
    try:
      result = black_clova_llm.invoke(instructions[instructions_key],
                                      query.user_query)
      return {"result": result}
    except Exception as e:
      print(e)
      raise HTTPException(status_code=500, detail=str(e))

  return endpoint


def summary_endpoint(instructions_key: str):
  async def endpoint(query: Query):
    try:
      result = title_llm.invoke(instructions[instructions_key],
                                query.user_query)
      return {"result": result}
    except Exception as e:
      print(e)
      raise HTTPException(status_code=500, detail=str(e))

  return endpoint


# 프롬프트 생성 함수
def create_chat_prompt(instructions: str):
  system_message_prompt = SystemMessagePromptTemplate.from_template(
    instructions)
  human_message_prompt = HumanMessagePromptTemplate.from_template(
    "{user_query}")
  chat_prompt = ChatPromptTemplate.from_messages(
    [system_message_prompt, human_message_prompt])
  return chat_prompt


# LLMChain 객체 생성

start_2_turn_blue_prompt = create_chat_prompt(
  instructions_2["start_2_blue_cap"])
start_3_turn_blue_prompt = create_chat_prompt(
  instructions_2["start_3_blue_cap"])
red_prompt = create_chat_prompt(instructions_2["red_cap"])
green_prompt = create_chat_prompt(instructions_2["green_cap"])
yellow_prompt = create_chat_prompt(instructions_2["yellow_cap"])
black_prompt = create_chat_prompt(instructions_2["black_cap"])
gpt_title_prompt = create_chat_prompt(instructions['title_summary'])
gpt_blue_summary_prompt = create_chat_prompt(gpt_blue_inst)  # 매턴
gpt_total_summary_prompt = create_chat_prompt(total_summary_prompt)
gpt_blue_start_prompt = create_chat_prompt(instructions['start_blue_cap'])

# langchain 객체 생성

gpt_2_start_blue_chain = LLMChain(llm=llm, prompt=start_2_turn_blue_prompt)  #시작
gpt_3_start_blue_chain = LLMChain(llm=llm, prompt=start_3_turn_blue_prompt)  #시작
gpt_red_chain = LLMChain(llm=llm, prompt=red_prompt)
gpt_green_chain = LLMChain(llm=llm, prompt=green_prompt)
gpt_yellow_chain = LLMChain(llm=llm, prompt=yellow_prompt)
gpt_black_chain = LLMChain(llm=llm, prompt=black_prompt)
gpt_title_chain = LLMChain(llm=llm, prompt=gpt_title_prompt)
gpt_blue_summary_chain = LLMChain(llm=llm,
                                  prompt=gpt_blue_summary_prompt)  # 턴 마다 마지막 end blue cap
gpt_total_summary_chain = LLMChain(llm=llm,
                                   prompt=gpt_total_summary_prompt)  # 회의결과 출력
gpt_blue_start_chain = LLMChain(llm=llm, prompt=gpt_blue_start_prompt)


# white agent 설정
def create_agent_executor(instructions: str, llm, tools):
  base_prompt = hub.pull("langchain-ai/openai-functions-template")
  prompt = base_prompt.partial(instructions=instructions)
  agent = create_openai_functions_agent(llm, tools, prompt)
  return AgentExecutor(agent=agent, tools=tools, verbose=False)


tavily_tool = TavilySearchResults()
tools = [tavily_tool]

white_cap_agent_first = create_agent_executor(white_cap_inst['first_white_cap'],
                                              llm, tools)
white_cap_agent_second = create_agent_executor(
  white_cap_inst['second_white_cap'], llm, tools)

# 엔드포인트 정의

app.post("/clova-title-summary")(summary_endpoint("title_summary"))  # 제목요약(클로바)
app.post("/blue-start/")(create_endpoint("start_blue_cap"))  # 블루 시작멘트
app.post("/red-cap-brainstorming/")(create_endpoint("red_cap"))  #1턴 빨간모자
app.post("/green-cap-brainstorming/")(create_endpoint("green_cap"))  # 1턴 초록모자
app.post("/yellow-cap-brainstorming/")(create_endpoint("yellow_cap"))  # 1턴 노랑모자
app.post("/black-cap-brainstorming/")(black_endpoint("black_cap"))


@app.post("/blue-start-gpt/")
async def blue_start_gpt(query: Query):
  try:
    result = gpt_blue_start_chain.run(user_query=query.user_query)
    print(result)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/white-cap-first-brainstorming/")  # 1턴 하얀모자
async def get_white_cap_result(query: Query):
  try:
    result = white_cap_agent_first.invoke({"input": query.user_query})
    return {"result": result['output']}
  except Exception as e:
    raise HTTPException(status_code=500, detail=str(e))


# 검정모자
# @app.post("/black-cap-brainstorming/") # 1턴 검정모자
# async def get_black_cap_result(query: Query):
#     try:
#         result = black_clova_llm._call(query.user_query)
#         return {"result": result}
#     except Exception as e:
#         print(e)
#         raise HTTPException(status_code=500, detail=str(e))

# 2~3 턴들
# GPT 모자
@app.post("/gpt_2_start_blue/")
async def gpt_2_start_blue_brainstorming(query: Query):
  try:
    result = gpt_2_start_blue_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/gpt_3_start_blue/")
async def gpt_3_start_blue_brainstorming(query: Query):
  try:
    result = gpt_3_start_blue_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/gpt-red-brainstorming/")
async def red_brainstorming(query: Query):
  try:
    result = gpt_red_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/gpt-green-brainstorming/")
async def green_brainstorming(query: Query):
  try:
    result = gpt_green_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/gpt-yellow-brainstorming/")
async def yellow_brainstorming(query: Query):
  try:
    result = gpt_yellow_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/white-cap-second-brainstorming/")
async def get_white_cap_result(query: Query):
  try:
    result = white_cap_agent_second.invoke({"input": query.user_query})
    return {"result": result['output']}
  except Exception as e:
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/gpt-black-brainstorming/")
async def black_brainstorming(query: Query):
  try:
    result = gpt_black_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/gpt-title-summary/")
async def gpt_title_brainstorming(query: Query):
  try:
    result = gpt_title_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    print(e)
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/gpt-blue-total-summary/")
async def gpt_blue_total_summary(query: Query):
  try:
    result = gpt_blue_summary_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    raise HTTPException(status_code=500, detail=str(e))


@app.post("/total-summary/")
async def total_summary(query: Query):
  try:
    result = gpt_total_summary_chain.run(user_query=query.user_query)
    return {"result": result}
  except Exception as e:
    raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
  import uvicorn

  uvicorn.run(app, host="0.0.0.0", port=8001)
