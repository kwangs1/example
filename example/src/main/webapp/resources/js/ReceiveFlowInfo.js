/**
 * 결재선 지정 유저 받아오기
 */

window.addEventListener('message',function(e){
	var data = e.data;
	var users  = data.users;
	console.log(data.users);
    
	//user list에서 선택한 값을 전달 받아 동적으로 생성
	 var inputs = $('#inputs');
	 inputs.empty();
	//동적으로 생성 시 name값이 중복되면 하나의 값으로 보기에 반복문을 통해 i의 값으로 구분
	//즉 인덱스 번호라 생각하면 됨.
		for (var i = 0; i < users.length; i++) {
			var userContainer = $('<div class="user-container">');
			userContainer.append('<input type="hidden" name="deptid_' + i + '" value="' + users[i].deptid + '" />');
			userContainer.append('<input type="hidden" name="deptname_' + i + '" value="' + users[i].deptname + '" />');
			userContainer.append('<input type="hidden" name="signerid_' + i + '" value="' + users[i].id + '" />');
			userContainer.append('<input type="text" name="signername_' + i + '" value="' + users[i].name + '" />');
			userContainer.append('<input type="hidden" name="pos_' + i + '" value="' + users[i].pos + '" />');
			
			var statusDropdown = $('<select name="status_' + i + '">');
			statusDropdown.append('<option value ="1000">기안</option>');
			statusDropdown.append('<option value ="2000">검토</option>');
			statusDropdown.append('<option value ="3000">협조</option>');
			statusDropdown.append('<option value ="4000">결재</option>');
			
			statusDropdown.val(users[i].status);
			
			userContainer.append(statusDropdown);
			inputs.append(userContainer);
		}
	
});