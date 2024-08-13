<template>
  <TopBar />
  <div class="login-container">
    <form class="login-form">
      <p class="heading">회원가입</p>
      <div class="include-msg">
        <div class="input-group btn_posi">
          <input @input="idChange()" v-model="id" placeholder="아이디"  name="userID" type="text" maxlength="20" />
          <button type="button" @click="isDuplicateId()">중복확인</button>
        </div>
        <text id="userID" class="infoMessage">{{ idMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group">
          <input @input="pwChange()" v-model="pw" placeholder="비밀번호(8~16글자, 영문, 숫자, 특수문자 모두 포함)" type="password" />
        </div>
        <text id="password" class="infoMessage">{{ pwMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group">
          <input @input="onchange()" v-model="pwCheck" placeholder="비밀번호 재확인"  id="confirm-Password" type="password" />
        </div>
        <text id="passwordCheck" class="infoMessage">{{ pwCheckMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group">
          <input @input="nameChange()" v-model="name" placeholder="이름"  type="text" maxlength="20" />
        </div>
        <text id="name" class="infoMessage">{{ nameMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group btn_posi">
          <input @input="nickNameChange()" v-model="nickname" placeholder="닉네임"  type="text" />
          <button type="button" @click="isDuplicateNickName()">중복확인</button>
        </div>
        <text id="nickname" class="infoMessage">{{ nickNameMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="gender-selection">
          <div class="gender-option">
            <input @input="genderChange()" type="radio" id="male" name="gender" v-model="gender" value="M">
            <label for="male">남</label>
          </div>
          <div class="gender-option">
            <input @input="genderChange()" type="radio" id="female" name="gender" v-model="gender" value="F">
            <label for="female">여</label>
          </div>
        </div>
        <text id="gender" class="infoMessage">{{ genderMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group btn_posi">
          <input @input="phoneNumChange()" v-model="phonenumber" placeholder="전화번호"  type="text" />
          <button type="button" @click="sendSMS()">인증번호 전송</button>
        </div>
        <text id="phone" class="infoMessage">{{ phoneMsg }}</text>
      </div>

      <div class="include-msg" v-show="showVerificationCode">
        <div class="input-group btn_posi">
          <input @input="checknumberChange()" v-model="checknumber" placeholder="인증번호"   type="text" maxlength="6"  />
          <button type="button" @click="numberCheck()">확인</button>
        </div>
        <text id="checknumber" class="infoMessage">{{pnCheckMsg}}</text>
      </div>

      <button type="button" @click="signUp()">Sign up</button>
      <div class="bottom-text">
      </div>
    </form>
  </div>

  <!-- 에러 모달 -->
    <div class="modal" v-if="isErrorModal">
        <div class="cookies-card2">
            <p class="cookie-heading2">{{ modalTitle }}</p>
            <p class="cookie-para2">
                {{ modalMsg }}
            </p>
            <div class="button-wrapper2">
                <button class="accept2 cookie-button2" @click="isErrorModal = false">확인</button>
            </div>
        </div>
    </div>
</template>

<script>
import TopBar from '@/components/TopBar.vue';
import axios from 'axios';

export default {
  name: "SignUpPage",
  components: {
    TopBar,
  },
  data() {
    return {
      id : '', // 아이디
      idDuplicateCheck : false, // 아이디 중복확인 진행 여부
      pw : '', // 비밀번호
      pwCheck : '', // 비밀번호 재확인
      name : '', // 이름
      nickname : '', // 닉네임
      nickNameDuplicateCheck : false, // 닉네임 중복확인 진행 여부
      gender : '', // 성별 ex) 남자 : M, 여자 : F
      phonenumber : '', // 전화번호
      isPhoneNumCheck : false, // 인증번호 진행 여부
      checknumber : '', // 인증번호

      idMsg : '', // 아이디 메세지
      pwMsg : '', // 비밀번호 메세지
      pwCheckMsg : '', // 비밀번호 재확인 메세지
      nameMsg : '', // 이름 메세지
      nickNameMsg : '', // 닉네임 메세지
      genderMsg : '', // 성별 메세지
      phoneMsg : '', // 전화번호 메세지
      pnCheckMsg : '', // 인증번호 확인 메세지

      showVerificationCode : false, // 전화번호를 입력 후 인증번호 요청 클릭시 인증번호를 입력하는 input이 나오기 위한 변수.

      isErrorModal : false, // 에러 모달 창 실행 여부
      modalTitle : '', // 에러 모달 제목
      modalMsg : '', // 에러 모달 메세지
    }
  },
  methods: {
    // 아이디 입력 감지
    idChange() {
      const userId = document.getElementById('userID');
      if(this.id.length < 5) {
        userId.style.color = "#F00";
        this.idMsg = "영어와 숫자를 사용하여 최소 5자리 이상 입력하세요."
      } else {
        this.idMsg = '';
      }
    },
    // 비밀번호 입력 감지
    pwChange() {
      const password = document.getElementById('password');
      if(this.strongPassword(this.pw) === false) {
        password.style.color = "#F00";
        this.pwMsg = "영문, 숫자, 특수문자 모두 포함 8글자 이상 입력하세요.";
      } else {
        password.style.color = "#70CA77";
        this.pwMsg = "사용 가능한 비밀번호입니다.";
      }
    },  
    // 비밀번호 재입력 입력 감지
    onchange(){
      const vm = this;
      const input_tag = document.getElementById('confirm-Password');
      if(vm.pw !== vm.pwCheck) {
        input_tag.style.borderColor = '#F00';
        document.getElementById('passwordCheck').style.color = '#F00';
        vm.pwCheckMsg = '비밀번호가 일치하지 않습니다.';
      } else {
        input_tag.style.borderColor = '#70CA77';
        document.getElementById('passwordCheck').style.color = '#70CA77';
        vm.pwCheckMsg = '비밀번호가 일치합니다.';
      }
    },
    // 이름 입력 감지
    nameChange() {
      const textTag = document.getElementById('name');
      if(this.validateName(this.name) === false) {
        textTag.style.color = "#F00";
        this.nameMsg = '실명을 입력하세요.';
      } else {
        this.nameMsg = '';
      }
    },

    // 닉네임 입력 감지
    nickNameChange() {
      const textTag = document.getElementById('nickname');
      if(this.validateNickName(this.nickname) === false) {
        textTag.style.color = '#F00';
        this.nickNameMsg = '첫글자는 영어 또는 숫자이고 그 이후는 대소문자, 숫자, 언더스코어, 띄어쓰기 포함 15글자까지 입력가능합니다.';
      } else {
        this.nickNameMsg = '';
      }
    },

    // 전화번호 입력 감지
    phoneNumChange() {
      if(this.validatePhoneNumber(this.phonenumber) === false) {
        document.getElementById('phone').style.color = '#F00';
        this.phoneMsg = '입력예시 : 01012345678';
      } else {
        this.phoneMsg = '';
      }
    },

    // 성별 선택 감지
    genderChange() {
      this.genderMsg = '';
    },

    //인증번호 입력 감지
    checknumberChange() {
      this.pnCheckMsg = '';
    },

    // 아이디 유효성 검사 (영어 또는 숫자 또는 영어+숫자만 입력 가능)
    onlyEnglishAndNumber(id) {
      return /^[A-Za-z0-9][A-Za-z0-9]{4,20}$/.test(id);
    }, 
    // 비밀번호 유효성 검사 (8글자 이상, 영문, 숫자, 특수문자 사용)
    strongPassword (password) {
      return /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,16}$/.test(password);
    },
    // 비밀번호 재확인
    isMatch(pw, pwCheck) {
      return pw === pwCheck;
    },
    // 닉네임 유효성 검사 (첫글자는 숫자또는 영어가 오고 그 이후에 영어 대소문자, 숫자, 언더스코어, 띄어쓰기 및 한글만 포함. 길이는 15자까지 가능.)
    validateNickName(nickName) {
      return /^[A-Za-z0-9가-힣][A-Za-z0-9가-힣_ ]{0,14}$/.test(nickName);
    },
    // 이름 유효성 검사( 한글만 입력 가능)
    validateName(name) {
      return /^[가-힣]{2,20}$/.test(name);
    },
    // 전화번호 유효성 검사( 숫자 11자리)
    validatePhoneNumber(phoneNumber) {
      return /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/.test(phoneNumber);
    },
    // 아이디 중복확인
    isDuplicateId() {
      const vm = this;
      if(vm.onlyEnglishAndNumber(vm.id) === false) {
        document.getElementById('userID').style.color = '#F00';
        vm.idMsg = '5글자 이상인 아이디를 입력해주세요.';
        return;
      }
      const data = {
        userid : vm.id.toLowerCase()
      };
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/isDuplicateId",
        data: data,
      })
        .then(function(response) {
          const userId = document.getElementById('userID');
          if(response.data > 0) {
            vm.idMsg = '이미 존재하는 아이디 입니다.';
            userId.style.color = "#F00";

            vm.idDuplicateCheck = false;
          } else {
            vm.idMsg = '사용가능한 아이디 입니다.';
            userId.style.color = '#70CA77'
            vm.idDuplicateCheck = true;
          }
        })
        .catch(() =>  {
          this.errorModalContent();
        })
    },

    // 닉네임 중복확인
    isDuplicateNickName() {
      const vm = this;
      if(vm.validateNickName(vm.nickname) === false) {
        vm.nickNameMsg = "닉네임은 영어 대소문자, 숫자, 언더스코어 및 한글만 포함. 길이는 15자까지 가능하오니 다시 입력해주시기 바랍니다.";
        return;
      }
      const data = {
        nickname : vm.nickname
      };
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/isDuplicateNickName",
        data: data,
      })
        .then(function(response) {
          if(response.data > 0) {
            document.getElementById('nickname').style.color = '#F00';
            vm.nickNameMsg = '이미 존재하는 닉네임 입니다.';
            vm.nickNameDuplicateCheck = false;
          } else {
            document.getElementById('nickname').style.color = '#70CA77';
            vm.nickNameMsg = '사용가능한 닉네임 입니다.';
            vm.nickNameDuplicateCheck = true;
          }
        })
        .catch(() => {
          this.errorModalContent();
        })
    },

    // 전화번호 기입 후 인증번호 전송 버튼클릭시 인증번호 요청
    sendSMS() {
      const vm = this;
      if(vm.validatePhoneNumber(vm.phonenumber) === false) {
        document.getElementById('phone').style.color = '#F00';
        vm.phoneMsg = '전화번호를 다시 입력해주세요';
        return;
      }
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/sendSMS",
      })
        .then(function(response) {
          vm.showVerificationCode = true;
          vm.pnCheckMsg = '';
          vm.phoneMsg = '';
          vm.modalTitle = '인증번호'
          vm.modalMsg = "인증 번호는 " + response.data + " 입니다."
          vm.isErrorModal = true;
        })
        .catch(() => {
          this.errorModalContent();
        })

    },

    // 인증번호 입력후 확인하는 작업
    numberCheck() {
      const vm = this;
      const data = {
        checknumber : vm.checknumber
      };
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/isNumberCheck",
        data: data,
      })
        .then(function(response){
          if(response.data > 0) {
            vm.isPhoneNumCheck = true;
            document.getElementById('checknumber').style.color = '#70CA77';
            vm.pnCheckMsg = '인증 성공';
          } else {
            vm.isPhoneNumCheck = false;
            document.getElementById('checknumber').style.color = '#F00';
            vm.pnCheckMsg = '인증번호를 다시 입력해주세요.';
          }
        })
        .catch(() => {
          this.errorModalContent();
        })
    },

    // 아무것도 입력하지 않고 회원가입 클릭했을 경우
    isEmpty() {
      var result = true;
      const vm = this;
      if(vm.id == '') {
        document.getElementById('userID').style.color = '#F00';
        vm.idMsg = '아이디를 입력해주세요.';
        result = false;
      } else if( vm.idDuplicateCheck === false) {
        document.getElementById('userID').style.color = '#F00';
        vm.idMsg = '아이디 중복여부를 확인해주세요.';
        result = false;
      }
      if(vm.pw == '') {
        document.getElementById('password').style.color = '#F00';
        vm.pwMsg = '비밀번호를 입력해주세요.';
        result = false;
      }
      if(vm.pwCheck == '') {
        document.getElementById('passwordCheck').style.color = '#F00';
        vm.pwCheckMsg = '비밀번호를 재입력 해주세요';
        result = false;
      }
      if(vm.name == '') {
        document.getElementById('name').style.color = '#F00';
        vm.nameMsg = '이름을 입력해주세요';
        result = false;
      }
      if(vm.nickname == '') {
        document.getElementById('nickname').style.color = '#F00';
        vm.nickNameMsg = '닉네임을 입력해주세요.';
        result = false;
      } else if(vm.nickNameDuplicateCheck === false) {
        document.getElementById('nickname').style.color = '#F00';
        vm.nickNameMsg = '닉네임 중복확인을 진행해주세요.';
        result = false;
      }
      if(vm.gender == '') {
        document.getElementById('gender').style.color = '#F00';
        vm.genderMsg = '성별을 선택해주세요.';
        result = false;
      }
      if(vm.phonenumber == '') {
        document.getElementById('phone').style.color = '#F00';
        vm.phoneMsg = '전화번호를 입력해주세요.';
        result = false;
      } else if(vm.showVerificationCode === false && vm.isPhoneNumCheck === false) {
        document.getElementById('phone').style.color = '#F00';
        vm.phoneMsg = '인증번호를 발급받고 인증을 진행해주세요.';
      }
      if(vm.checknumber == '') {
        document.getElementById('checknumber').style.color = '#F00';
        vm.pnCheckMsg = '인증번호를 입력하고 확인을 진행 해주세요.';
        result = false;
      }

      if(result === false) return result;
      return true;
    },

    // 회원가입
    signUp() {
      const vm = this;
      let tf = vm.isEmpty();

      if (tf === false) return;

      if( vm.idDuplicateCheck === false) {
        document.getElementById('userID').style.color = '#F00';
        vm.idMsg = '아이디 중복여부를 확인해주세요.';
        return;
      }

      if(vm.strongPassword(vm.pw) === false) {
        document.getElementById('password').style.color = '#F00';
        vm.pwMsg = '비밀번호를 입력해주세요.';
        return ;
      }

      if(vm.isMatch(vm.pw, vm.pwCheck) === false) {
        document.getElementById('passwordCheck').style.color = '#F00';
        vm.pwCheckMsg = '비밀번호가 일치하지 않습니다.';
        return ;
      }

      if(vm.validateName(vm.name) === false) {
        document.getElementById('name').style.color = '#F00';
        vm.nameMsg = '이름을 입력해주세요';
        return ;
      }

      if(vm.isDuplicateNickName(vm.nickname) === false) {
        document.getElementById('nickname').style.color = '#F00';
        vm.nickNameMsg = '닉네임 중복확인을 진행해주세요.';
        return ;
      }

      if(vm.validatePhoneNumber(vm.phonenumber) === false) {
        document.getElementById('phone').style.color = '#F00';
        vm.phoneMsg = '전화번호를 입력해주세요.';
        return;
      }
      
      if(vm.isPhoneNumCheck === false) {
        document.getElementById('checknumber').style.color = '#F00';
        vm.pnCheckMsg = '인증번호를 입력하고 확인을 진행 해주세요.';
        return ;
      }

      const data = {
        userid : vm.id.toLowerCase(),
        pw : vm.pw,
        name : vm.name,
        phonenumber : vm.phonenumber,
        nickname : vm.nickname,
        gender : vm.gender
      };

      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/signup",
        data: data,
      })
        .then(function(response) {
          if(response.data.result) {
            vm.$router.push({path : "/login"});
          } else {
            vm.modalTitle = "회원가입 실패";
            vm.modalMsg = '회원가입에 실패했습니다. 다시 시도해주세요.';
            vm.isErrorModal = true;
          }
        })
        .catch(() => {
          this.errorModalContent();
        })
    },

    // 에러 발생시 오류 모달창 활성화
    errorModalContent() {
        this.modalTitle = '에러 발생'
        this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
        this.isErrorModal = true;
    },
  },

  created() {

  },
  mounted() {
    if(this.$cookies.get('JSESSIONID') != null && this.$cookies.get('USER_ID') != null ){
      this.$router.push({ name: 'CategoryChoice' });
    }
  },
}
</script>

<style scoped>
.include-msg {
  margin-bottom: 20px;
}
.infoMessage {
  display: block;
  color: #F00;
  -webkit-text-stroke-width: 1;
  -webkit-text-stroke-color: #000;
  font-family: Inter;
  font-size: 0.7em;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  text-align: left;
}
input:focus {
  outline: none;
}
.login-container {
  position: relative;
  top: 6%;
  padding: 1em 2.5em 0em 2.5em;
  max-width: 800px;
  width: 100%;
  text-align: center;
  margin: 0 auto;
  height: 88%;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.heading {
  color: #000000;
  font-weight: 500;
  font-size: 1.7em;
  margin-bottom: 1em;
}


.input-group2 {
  margin-bottom: 20px;
  height: 2em;
}

.btn_posi {
  display: flex;
}

.btn_posi>input {
  width: 60%;
}

.btn_posi>button {
  width: 40%;
  padding: 0;
  font-size: .7em;
  margin-left: .5em;

}

.input-group2 input {
  background: none;
  border: 1px solid #d1d1d1;
  padding: 0.7em 1em;
  font-size: 0.7em;
  border-radius: 8px;
  color: #000000;
  width: 100%;
}

.input-group input {
  background: none;
  border: 1px solid #d1d1d1;
  padding: 0.7em 1em;
  font-size: 0.7em;
  border-radius: 8px;
  color: #000000;
  width: 100%;
}

button {
  padding: 15px;
  border: none;
  border-radius: 8px;
  background-color: #5271ff;
  color: #ffffff;
  font-size: 0.7em;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
  box-shadow: rgba(0, 0, 0, 0.06) 0px 1px 1px,
    rgba(0, 0, 0, 0.09) 0px 1px 1px, rgba(0, 0, 0, 0.09) 0px 1px 2px,
    rgba(0, 0, 0, 0.09) 0px 1px 4px, rgba(0, 0, 0, 0.09) 0px 1px 8px;

}


.bottom-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
  color: #000000;
  font-size: .8em;
  font-weight: 400;
}

.bottom-text a {
  color: #0173ed;
  text-decoration: none;
  transition: color 0.3s ease;
}

.gender-selection {
  display: flex;
  justify-content: space-evenly;
  background-color: #f0f8ff;
  /* margin-bottom: 20px; */
  padding: 10px;
  border-radius: 10px;
  border: .5px solid #d1d1d1;
}

.gender-option {
  display: flex;
  align-items: center;
  color: #0173ed;
  font-weight: bold;
}

.gender-option input {
  margin-right: 5px;
}

/* 에러 모달 css */
.modal {
    display: flex;
    justify-content: center;
    align-items: center;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0, 0, 0, 0.5);
}

.cookies-card2 {
    width: 70%;
    height: fit-content;
    background-color: rgb(255, 250, 250);
    border-radius: 10px;
    border: 1px solid rgb(206, 206, 206);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px;
    gap: 15px;
    position: relative;
    font-family: Arial, Helvetica, sans-serif;
    box-shadow: 0px 10px 10px rgba(0, 0, 0, 0.066);
}

.cookie-heading2 {
    color: rgb(34, 34, 34);
    font-weight: 800;
    text-align: center;
    font-size: 1.2em;
}

.cookie-para2 {
    font-size: 1em;
    font-weight: 400;
    color: rgb(51, 51, 51);
}

.button-wrapper2 {
    width: 50%;
    height: auto;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
}

.cookie-button2 {
    width: 100%;
    padding: 8px 0;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.accept2 {
    background-color: rgb(34, 34, 34);
    color: white;
    font-size: 1em;
}
</style>