# [Backend] School Helper
2023학년도 제천고등학교 코딩 동아리인 코딩연구소에서 제작한 제천고등학교 도우미 백엔드 서버

## 기술 스택
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)

## 엔드포인트

### 시간표
해당 학교에 있는 교사의 시간표를 조회합니다.
- /timetable
- POST
- ```json
  {
    "grade": (학년)
    "class": (반)
    "id": (번호)
  }
  ```

### 급식
해당 학교의 급식을 조회합니다.
- /meal
- POST
