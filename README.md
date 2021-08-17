# FreePeriod (Project for 2018-2 Capstone Design)


# 프로젝트 개요
 - 시간표 UI를 통한 일정 관리
 - 밥/활동 등의 자유로운 모임 개설
 - 채팅, 지도등의 기능을 이용하여 모임 약속


# 핵심 기술 요소
-	REST API : REST 요청을 통해 사용자의 모든 정보와 모임에 대한 정보를 변경/조회하는데 사용한다.
-	Node.js & Express.js : <공강 때 뭐해?>의 서버를 개발할 때 사용한다.
-	MySQL : 사용자, 모임, 리뷰, 신고 등, 서비스에 필요한 모든 정보 저장 및 관리에 사용한다.
-	socket.io : 소켓을 이용하여 <공강 때 뭐해?>의 실시간 채팅 기능 구현에 사용한다.
-	Nodemailer : 학교이메일 인증을 위해 사용한다.
-	Firebase Cloud Messaging(FCM) : 모임 참가, 요청, 공지사항 등을 푸시 기능을 통해 사용자에게 알리기 위해 사용한다.
- SQLIte Database : 채팅 내용, 모임 목록 등 서버와의 연결이 끊어져도 기존의 데이터를 보여주기 위해 로컬 데이터베이스를 지원한다.


# 주요 기능들
- 강의 추가하기

![image](https://user-images.githubusercontent.com/35019895/128050556-ea5ebf1f-6418-4229-8c94-b78edb69056f.png)
![image](https://user-images.githubusercontent.com/35019895/128050592-9fdeb69a-6d5e-49c9-aa0f-865ff71477db.png)
![image](https://user-images.githubusercontent.com/35019895/128050626-2c0f9bf2-8ca2-4f51-bc4d-d0b9a32c7635.png)

- 모임 개설하기

![image](https://user-images.githubusercontent.com/35019895/128051404-2df1545d-2940-4d36-bb72-228e22d41ede.png)
![image](https://user-images.githubusercontent.com/35019895/128051416-3dc59c4d-820a-4908-a7bc-80a59c6378bb.png)
![image](https://user-images.githubusercontent.com/35019895/128051447-5b7564fc-dd8b-4fdd-b0fc-3567310cd1b2.png)

 - 모임 내 채팅하기

![image](https://user-images.githubusercontent.com/35019895/128051662-57e32bcb-a4d5-40ca-8d7b-331bf524ff30.png)
![image](https://user-images.githubusercontent.com/35019895/128051687-1075d519-1334-48dd-8059-680f275fd453.png)

 - 약속 장소 설정하기

![image](https://user-images.githubusercontent.com/35019895/128051839-81a34058-a408-4ee1-ba58-c79cf9860853.png)
![image](https://user-images.githubusercontent.com/35019895/128051847-2bf43fa1-6fd5-4a62-bc82-16dff0ef64c3.png)


# 프로젝트 일정 관리(product backlog를 작성하였고, burndown chart를 이용하여 개발 일정 관리)
 - Iteration 1(2018-09-20 ~ 10-19)

![image](https://user-images.githubusercontent.com/35019895/128057306-950e4962-91fc-472a-a310-ea8b41da2172.png)
![image](https://user-images.githubusercontent.com/35019895/128057315-142dcc48-b470-4024-9695-f7b710e395db.png)

 - Iteration 2(2018-10-22 ~ 11-09)

![image](https://user-images.githubusercontent.com/35019895/128057357-c52fe934-0ef0-4b55-a24e-e1d5e05b7fe4.png)
![image](https://user-images.githubusercontent.com/35019895/128057370-8d94cb23-7a4e-4d27-b3f4-4e9b84139203.png)

 - Iteration 3(2018-11-10 ~ 11-30)

![image](https://user-images.githubusercontent.com/35019895/128057415-a84985a4-5c0f-45cc-8558-8110fe1de614.png)
![image](https://user-images.githubusercontent.com/35019895/128057441-a3cc918b-7b1f-4bff-9b1b-20e180029981.png)

 - Iteration 4(2018-12-03 ~ 12-12)

![image](https://user-images.githubusercontent.com/35019895/128057492-bcff60c6-c7f4-4144-8bf4-3a11b66f6207.png)
![image](https://user-images.githubusercontent.com/35019895/128057511-f5bc6f80-6de0-4c8e-b37b-1971813bf058.png)


# DB 스키마

![db스키마(수정)](https://user-images.githubusercontent.com/35019895/128059442-e08a9065-3f85-4f9b-8fa5-3de5f24f067d.PNG)


# 소스 코드 중 내가 개발한 부분
 - FreePeriod/app/src/main/java/xyz/capsaicine/freeperiod/activities/chat
 - FreePeriod/app/src/main/java/xyz/capsaicine/freeperiod/app
 - FreePeriod/app/src/main/java/xyz/capsaicine/freeperiod/login
 - node.js 서버 코드 전부


# 특이사항
 - 네이버 클라우드 플랫폼 서버를 이용하여 호스팅 했으나 서버의 사용 기간이 만료됨.
 - Git, Trello를 이용하여 협업함
