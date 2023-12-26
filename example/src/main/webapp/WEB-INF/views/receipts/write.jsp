<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
<body>

	<button onclick="pop()">유저 목록</button>
<form method="post" id="frmObj">
	<input type="hidden" name="paticipantVO.receipts_seq" value="${receipts_seq}"/>
	<input type="hidden" id="id" name="paticipantVO.id" />
	<input type="hidden" id="pos" name="paticipantVO.pos" />
	<input type="text" id="name" name="paticipantVO.name" />
	<button type="submit">상신</button>	
</form>

<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script>
window.addEventListener('message',function(e){
	var data = e.data;
	var id = data.id;
	var name = data.name;
	var pos = data.pos;
	
	$('#id').val(id);
	$('#name').val(name);
	$('#pos').val(pos);
	
});

function pop(){
	window.open("${path}/user/list","pop","width=300, height=300");
}
</script>
</body>
</html>