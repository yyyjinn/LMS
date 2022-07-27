<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인</title>
    <link rel="stylesheet" href="login.css">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;700&family=Noto+Sans:wght@200;400;600;700&display=swap" rel="stylesheet">
    <script src="https://kit.fontawesome.com/10dae3550b.js" crossorigin="anonymous"></script>
</head>
<body>
	<div class="section">
        <div class="container">
            <div class="left-box">
                <img src="images/login.jpg" alt="" class="banner">
            </div>
            <div class="right-box">
               <div class="service">
                    <div class="image">
                        <img src="../images/logo.png" class="logo">
                    </div>
                    <div class="integrated"><a>통합서비스 </a><span>로그인</span></div>
                    <div class="login">
                        <form action="./p_main.jsp" class="form">
                            <div class="input">                           
                                <input type="text" name="id" id="id" placeholder="아이디">                        
                                <input type="password" name="passwd" id="passwd" placeholder="비밀번호">
                            </div>
                            <div class="submit">                                         
                                <button type="submit" class="btn-login">로그인</button>
                            </div>
                        </form>
                    </div>
                    <button class="signin"><a href="./join.do">회원가입</a></button>         
                </div>    
                <div class="text">
                    <p>주소 : (11111) 경남 창원시 마산합포구 영덕동 15-27번지 경민인터빌 407호</p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>