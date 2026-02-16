# 소모임 어플리케이션

## 📌 개요
소모임을 만들고 참가할 수 있는 어플리케이션입니다.  
사용자는 회원가입 후 모임을 생성하거나 참여할 수 있으며, 마이페이지에서 개인 정보를 관리할 수 있습니다.  

---

## 🛠️ 기술 스택
- **Backend**: Java, Spring Boot, JPA  
- **Database**: H2 (테스트), MySQL (운영)
- **Auth**: JWT  
- **Infra**: AWS EC2, RDS, S3, Route 53, Nginx
- **Monitoring**: Prometheus, Grafana

---
## 🗄️ ERD
<img width="1930" height="882" alt="LEMS-PROJECT" src="https://github.com/user-attachments/assets/5f019856-e66f-43fb-8a85-6dc32b372149" />

---

## 🏗️ 서버 아키텍처
<img width="2382" height="1872" alt="아키텍처 drawio" src="https://github.com/user-attachments/assets/3b5e9433-25d2-4104-a8f8-710a7a82b5ce" />


---
## ⚙️ 문제 해결
- 모임 참가 **동시성 문제** 해결  
  참고: [heedb 블로그](https://heedb.tistory.com/65)

---

## 📱 화면 구성

### 🔑 로그인 및 회원 가입
<p align="left">
  <img src="https://github.com/user-attachments/assets/cae48d93-ff10-4986-b918-a20dedb79ab2" width="200" hspace="20" />
  <img src="https://github.com/user-attachments/assets/eae6bb11-9a79-413e-b527-5ddf287efeac" width="200" hspace="20" />
  <img src="https://github.com/user-attachments/assets/44dda79a-203d-4ffd-a7d8-a9e59ff6301e" width="200" hspace="20" />
</p>

---

### 🏠 홈 화면
<p align="left">
  <img src="https://github.com/user-attachments/assets/6bf76d23-3cdd-4eb8-a9a4-dd74a3e4fb96" width="200" hspace="20" />
  <img src="https://github.com/user-attachments/assets/2e908ee8-2a58-4af5-91cf-29bec5549b01" width="200" hspace="20" />
</p>

---

### 👥 모임 생성 및 모임 페이지
<div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; justify-items: center;">
  <img src="https://github.com/user-attachments/assets/16960769-de53-4d66-9369-56d6e4795ba9" width="200" />
  <img src="https://github.com/user-attachments/assets/d2ee6d9c-df7f-481f-8218-9c0f57e62133" width="200" />
  <img src="https://github.com/user-attachments/assets/bf433253-a1c2-402c-b756-4c2f4ede2458" width="200" />
  <img src="https://github.com/user-attachments/assets/b02255b4-2f28-4e16-aa83-c30b9ba59e97" width="200" />
</div>

---

### 🙍 마이 페이지
<p align="left">
  <img src="https://github.com/user-attachments/assets/7b58644f-4a01-4df7-a2c2-964e80c175c2" width="200" hspace="20" />
  <img src="https://github.com/user-attachments/assets/7a0ea1e8-fe5b-440d-813b-cbcf1d810958" width="200" hspace="20" />
  <img src="https://github.com/user-attachments/assets/c9f6886e-3203-4150-a540-84ba3bec17d8" width="200" hspace="20" />
</p>
