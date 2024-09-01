## [말모말모 배포](https://malmo.vercel.app/)

# 당신의 아이디어를 더욱 빛나게! <br />말하는 모자, 말모말모

[말모말모 최종 산출물.pdf](https://github.com/user-attachments/files/16404618/default.pdf)

회원가입 없이 서비스 이용방법 - 네이버로 계속하기 클릭 후 "Sign in with Credentials" 버튼 클릭 시 회원가입 되지 않고 테스트할 수 있습니다


<img width="1136" alt="malmo_thumbnail" src="https://github.com/user-attachments/assets/80dff216-a9fe-4153-af61-b6305c992b39">

## AI와 당신의 아이디어를 브레인스토밍으로 발전시켜주는 서비스, "말모말모"

업무중일 때, SNS 글을 쓸 때, 일상 생활을 할 때... 우리는 하루에도 몇 번씩 아이디어가 필요합니다.<br />
**아이디어가 정말 좋은지 확신이 들지 않거나**🧐 혹은 **체계적으로 발전시키고 싶은데 방법을 몰라 고민스러웠던**😣 경험이 있지 않나요?<br /><br />
**6 Thinking Hats 기법**을 기반으로 학습된 말모말모가<br />
아이디어를 다양한 관점으로 분석하여 병목상태를 해소시키고<br />
체계적인 발전을 가능하게 합니다!<br /><br />

⚪️하양이 - “전 오직 수치화된 데이터로만 말해드려요.” 통계 기반 아나운서<br />
⚫️까망이 - “KPI, BM 다 고려해서 develop 한거 맞아?” 판교어 뼈개발자<br />
🟢초록이 - “이건어때???저건어때???” 물음표살인마 8세<br />
🔵파랑이 - “회의의 시작과 끝, 인사이트는 제게만 맡기시죠.갑시다 모자제군들” 중후한 노인 사회자<br />
🟡노랑이 - “어머 이런 생각을 하다니 멋져요~너무 좋네요~” 금쪽이 칭찬전문 선생님<br />
🔴빨강이 - “😆ㅇㅣ렇게 많은 사람 앞에서 소개하다니 너무 떨리쟈나🍀🍀” 감성적인 사춘기 MZ 소녀

## 🏃 팀원

기획자 1명, 디자이너 1명, FE 1명, BE 1명, AI 1명

## 🏆 수상

<li>네클 Pick 1등 수상</li>
<li>407 포텐데이X클로바 스튜디오 3등 수상</li>
<li>407 포텐데이X클로바 스튜디오 고도화 트랙 최종 결선 3등 수상</li>
## 구현 기능 🛠

### 홈

<table>
    <tbody>
        <tr></tr>
        <tr>
            <th>시연</th>
            <th>설명</th>
        </tr>
        <tr>
            <td><img src="https://github.com/user-attachments/assets/1643b1f4-0827-42ba-9c4b-484ffadb939f"
                  width="430px"  alt="">
            </td>
                  </td>
            <td>메인 홈(로그인 전)<ul>
                    <li>비 로그인 상태에서 웹 페이지의 간단한 설명과 모자와 회의 하러가기 버튼 버튼 노출</li>
                    <li>모자와 회의 하러가기 버튼 클릭 시 네이버 로그인 버튼 노출</li>
                </ul>
            </td>
        </tr>
            <tr>
            <td>
            <img src="https://github.com/user-attachments/assets/5f67a823-d9cd-4168-ac2f-90e46b6f7fab"
            width="430px"  alt=""></td>
                  </td>
            <td>메인 홈(로그인 후)<ul>
                    <li>
                    로그인 완료 이후 Navbar와 Guide 버튼가 모자 설명 링크가 있는 Carousel UI 노출
                    </li>
                </ul>
            </td>
        </tr>
        <tr>
            <td><img src="https://github.com/user-attachments/assets/0ff43bae-4d00-4995-b189-ca77f7b29820"
                  width="430px"  alt=""></td>
            <td>회의 방 생성 모달
              <ul>
                <li>유저는 회의 방 생성 시 카테고리와 회의하고 싶은 아이디어 입력</li>
                <li>카테고리와 아이디어 모두 입력 시 모자와 회의하기 버튼 활성화</li>
              </ul>
            </td>
        </tr>
    </tbody>
</table>

<br />

### 회의 방

<table>
    <tbody>
        <tr></tr>
        <tr>
            <th>시연</th>
            <th>설명</th>
        </tr>
        <tr>
            <td><img src="https://github.com/user-attachments/assets/81a38593-6abd-413f-b18e-9268ec61dd5a"
                  width="430px"  alt=""></td>
            <td>회의 방<ul>
                    <li>Hyper CLOVA X, OpenAI 활용</li>
                    <li>회의 방 제목</li>
                    <li>유저가 작성한 내용 기반 6개의 모자 브레인 스토밍 시작</li>
                    <li>모든 모자들의 발언이 종료되면 summary 프롬프트 시작</li>
                    <li>추가 회의하기 기능</li>
                    <li>개별 메세지 북마크 기능 구현</li>
                </ul>
            </td>
        </tr>
    </tbody>
</table>

<br />

### 내역 페이지

<table>
    <tbody>
        <tr></tr>
        <tr>
            <th>시연</th>
            <th>설명</th>
        </tr>
        <tr>
            <td><img src="https://github.com/user-attachments/assets/14d1bf99-461a-4096-b771-a1d546bd5b45"
                  width="430px"  alt=""></td>
              <td>내역<ul>
                    <li>이전 회의에 대한 내역 리스트</li>
                    <li>회차 버튼 클릭 시 해당 회차에 해당하는 회의 방으로 이동</li>
                    <li>케밥 메뉴 클릭 시 삭제 버튼 노출</li>
                    <li>
                      삭제 버튼 클릭 시 해당 내역 삭제
                    </li>
                </ul>
            </td>
        </tr>
    </tbody>
</table>

### 북마크 페이지

<table>
    <tbody>
        <tr></tr>
        <tr>
            <th>시연</th>
            <th>설명</th>
        </tr>
        <tr>
            <td><img src="https://github.com/user-attachments/assets/1ed0b7da-40b5-499e-ae79-0baf9b650d4d"
                  width="430px"  alt=""></td>
              <td>북마크<ul>
                    <li>회의 방에서 Ai 발언들을 각각 북마크 가능</li>
                    <li>북마크 시 북마크 페이지에 노출</li>
                    <li>회차 버튼 클릭 시 해당 회차에 해당하는 회의 방으로 이동</li>
                    <li>케밥 메뉴 클릭 시 삭제 버튼 노출</li>
                    <li>
                      삭제 버튼 클릭 시 해당 내역 삭제
                    </li>
                </ul>
            </td>
        </tr>
    </tbody>
</table>
