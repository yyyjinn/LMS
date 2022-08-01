<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    
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
	<meta charset="EUC-KR">
	<title>Insert title here</title>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;700&family=Noto+Sans:wght@200;400;600;700&display=swap" rel="stylesheet">
	<script src="https://kit.fontawesome.com/10dae3550b.js" crossorigin="anonymous"></script>
	<link rel="stylesheet" href="<c:url value='/resource/CSS/p_popup_lecture.css'/>">
</head>
<body>
	<div class = "container">
		<p><%= subject_name %> �л� ���</p>
		<%
		for (int i = 0; i < stuArr.size(); i++)
			{
			StudentDTO stuDTO = stuArr.get(i);
		%>
			<div class = "div_container">
				<div><%= i+1 %>��</div>
				<div>�й� : <%= stuDTO.getS_id() %></div>
				<div>�̸� : <%= stuDTO.getS_name() %></div>
				<div>�а� : <%= stuDTO.getD_name() %></div>
				<div>�г� : <%= stuDTO.getS_grade() %></div>
			</div>
		<%
			}
		%>
	</div>
</body>
</html>