<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%
   String abc = request.getParameter("IdPwNum");
   String num = request.getParameter("num");
   String text ="";
   if(num!=null){
      if(num.equals("1")){ text ="alert('회원가입이 완료되었습니다.');";}
      else if(num.equals("2")){ text = "alert('비밀번호가 변경되어 로그아웃 되었습니다.');"; }
      else if(num.equals("3")){ text = "alert('로그아웃 되었습니다.');"; }
   }
   if (abc == null){ }
   else if(abc.equals("3")) { text ="alert('아이디/비밀번호를 확인해주세요.');"; }
%>
<html>
<script type="text/javascript">
   <%=text%>
</script>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인</title>
    <!-- 
    	c:url을 사용하는 이유 : context path를 자동으로 포함하므로 소스를 수정함에 따른 변화를 자동으로 적용
    	${pageContext.servletContext.contextPath} or ${pageContext.request.contextPath} 로도 많이씀 ==>pageContext : 상대경로, 내 현재 위치를 기준으로 경로 인식
    	context path : root경로 (자바 웹 프로그램에서 현재 프로젝트 경로)
    	
    	==>contextPath가 변경되어도 소스 수정없이 적용하기 위해서 사용(유지보수에 용이)
     -->
    <link rel="stylesheet" href="<c:url value='/resource/CSS/login.css'/>">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;700&family=Noto+Sans:wght@200;400;600;700&display=swap" rel="stylesheet">
    <script src="https://kit.fontawesome.com/10dae3550b.js" crossorigin="anonymous"></script>
	<!-- 아이디, 패스워드 입력 유효성검사 : getElementById -->
	<script type="text/javascript">
		function idpwcheck(){
			if(document.getElementById("id").value == ""){
				alert("아이디를 입력해주세요.");
				document.login_form.id.focus();
				return false;
			}
			if(document.getElementById("passwd").value == ""){
				alert("비밀번호를 입력해주세요.");
				document.login_form.passwd.focus();
				return false;
			}
			document.login_form.submit();
		}
	</script>
</head>
<body>
	<div class="section">
        <div class="container">
            <div class="left-box">
                <img src="<c:url value='/resource/images/login.jpg'/>" alt="" class="banner">
            </div>
            <div class="right-box">
               <div class="service">
                    <div class="image">
                        <img src="<c:url value='/resource/images/logo.png'/>" class="logo">
                    </div>
                    <div class="integrated"><a>통합서비스 </a><span>로그인</span></div>
                    <div class="login">
                        <form action="login_process.do" class="form" name = "login_form" method="post">
                            <div class="input">                           
                                <input type="text" name="id" id="id" placeholder="아이디/직번">                        
                                <input type="password" name="passwd" id="passwd" placeholder="비밀번호">
                            </div>
                            <div class="submit">                                         
                                <input type="button" onclick="idpwcheck()" name = "button" class="btn-login" value="로그인"></input>
                            </div>
                        </form>
                    </div>
                    <!-- contextPath보다 더 상위에서 root를 잡기때문에 /를 붙이면 안됨 -->
                    <button class="signin"><a href="join.do">회원가입</a></button>         
                </div>    
                <div class="text">
                    <p>주소 : (11111) 경남 창원시 마산합포구 영덕동 15-27번지 경민인터빌 407호</p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>