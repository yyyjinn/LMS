<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@page import = "java.util.ArrayList" %>
<%@page import = "mvc.modeldto.StudentDTO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<%
String subject_name = (String) request.getAttribute("subject_name");
ArrayList<StudentDTO> stuArr = (ArrayList<StudentDTO>) request.getAttribute("stuArr");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;700&family=Noto+Sans:wght@200;400;600;700&display=swap" rel="stylesheet">
	<script src="https://kit.fontawesome.com/10dae3550b.js" crossorigin="anonymous"></script>
	<link rel="stylesheet" href="<c:url value='/resource/CSS/p_popup_lecture.css'/>">
</head>
<body>
	<div class = "container">
		<p><%= subject_name %> 학생 명단</p>
		<%
		for (int i = 0; i < stuArr.size(); i++){
			StudentDTO stuDTO = stuArr.get(i);
		%>
			<div class = "div_container">
				<div><%= i+1 %>번</div>
				<div>학번 : <%= stuDTO.getS_id() %></div>
				<div>이름 : <%= stuDTO.getS_name() %></div>
				<div>학과 : <%= stuDTO.getD_name() %></div>
				<div>학년 : <%= stuDTO.getS_grade() %></div>
			</div>
		<%
			}
		%>
	</div>
</body>
</html>