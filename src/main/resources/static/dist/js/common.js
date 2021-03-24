/**
 * 자료형에 상관없이 값이 비어있는지 확인한다.
 * 
 * @param value - 타겟 밸류
 * @returns true or false
 */
function isEmpty(value) {
	if (value == null || value == "" || value == undefined || (value != null && typeof value == "object" && !Object.keys(value).length)) {
		return true;
	}

	return false;
}

/**
 * 문자열의 마지막 문자의 종성을 반환한다.
 * 
 * @param str - 타겟 문자열
 * @returns 문자열의 마지막 문자의 종성
 */
function charToUnicode(str) {
	return (str.charCodeAt(str.length - 1) - 0xAC00) % 28;
}

/**
 * 필드1, 필드2의 값이 다르면 해당 필드2로 focus한 다음, 메시지 출력
 * 
 * @param field1 - 타겟 필드1
 * @param field2 - 타겟 필드2
 * @param fieldName - 필드 이름
 * @returns 메시지
 */
function equals(field1, field2, fieldName) {
	if (field1.value === field2.value) {
		return true;
	} else {
		/* alert 메시지 */
		var message = "";
		/* 종성으로 을 / 를 구분 */
		if (charToUnicode(fieldName) > 0) {
			message = fieldName + "이 일치하지 않습니다.";
		} else {
			message = fieldName + "가 일치하지 않습니다.";
		}
		field2.focus();
		alert(message);
		// Swal.fire(message);
		return false;
	}
}

/**
 * field의 값이 올바른 형식인지 체크 (정규표현식 사용)
 * 
 * @param field - 타겟 필드
 * @param fieldName - 필드 이름 (null 허용)
 * @param focusField - 포커스할 필드 (null 허용)
 * @param type - 유형 구분 (이메일, 전화번호 등 / null 허용)
 * @returns 메시지
 */
function isValid(field, fieldName, focusField, type) {
	/* type에 해당하는 정규식 */
	var regExp = "";
	/* alert 메시지 */
	var message = "";

	/* 일반 필드의 경우 */
	if (isEmpty(type)) {
		if (isEmpty(field.value)) {
			/* 종성으로 을 / 를 구분 */
			if (charToUnicode(fieldName) > 0) {
				message = fieldName + "을 확인해 주세요.";
			} else {
				message = fieldName + "를 확인해 주세요.";
			}
			field.focus();
			alert(message);
			return false;
		}
	} else {
		/* 타입을 지정해야 하는 경우 */
		switch (type) {
		case "email":
			regExp = /^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,20}$/;
			message = "올바르지 않은 형식의 이메일입니다.";
			break;

		case "password":
			regExp = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{6,20}/;
			message = "올바르지 않은 형식의 비밀번호입니다.";
			break;

		case "phone":
			regExp = /^\d{3}\d{3,4}\d{4}$/;
			message = "올바르지 않은 형식의 연락처입니다.";
			break;

		default:
			break;
		}
		// end of switch

		if (regExp.test(field.value) == false) {
			if (isEmpty($(focusField))) {
				field.focus();
			} else {
				focusField.focus();
			}
			alert(message);
			return false;
		}
	}
	// end of else

	return true;
}

/**
 * 둘 중 비어있지 않은 value를 반환한다.
 * 
 * @param value1 - 타겟 밸류1
 * @param value2 - 타겟 밸류2
 * @returns 비어잇지 않은 vlaue
 */
function nvl(value1, value2) {
	return (isEmpty(value1) == false ? value1 : value2);
}



function num2han(num) {
	num = parseInt((num + '').replace(/[^0-9]/g, ''), 10) + ''; // 숫자/문자/돈 을 숫자만 있는 문자열로 변환
	if(num == '0')
		return '영';
	var number = ['영', '일', '이', '삼', '사', '오', '육', '칠', '팔', '구'];
	var unit = ['', '만', '억', '조'];
	var smallUnit = ['천', '백', '십', ''];
	var result = []; //변환된 값을 저장할 배열
	var unitCnt = Math.ceil(num.length / 4); //단위 갯수. 숫자 10000은 일단위와 만단위 2개이다.
	num = num.padStart(unitCnt * 4, '0') //4자리 값이 되도록 0을 채운다
	
	var regexp = /[\w\W]{4}/g; //4자리 단위로 숫자 분리
	var array = num.match(regexp);
	//낮은 자릿수에서 높은 자릿수 순으로 값을 만든다(그래야 자릿수 계산이 편하다)
	for(var i = array.length - 1, unitCnt = 0; i >= 0; i--, unitCnt++) {
		var hanValue = _makeHan(array[i]); //한글로 변환된 숫자
		if(hanValue == '') //값이 없을땐 해당 단위의 값이 모두 0이란 뜻.
		continue;
		result.unshift(hanValue + unit[unitCnt]); //unshift는 항상 배열의 앞에 넣는다.
	}
	//여기로 들어오는 값은 무조건 네자리이다. 1234 -> 일천이백삼십사
	function _makeHan(text) {
		var str = '';
		for(var i = 0; i < text.length; i++) {
			var num = text[i];
			if(num == '0') //0은 읽지 않는다
			continue;
			str += number[num] + smallUnit[i];
		}
		return str;
	}
	return result.join('');
}


function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
