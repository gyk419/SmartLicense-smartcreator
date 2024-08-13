<template>
  <TopBar />
  <div class="login-container">
    <form class="login-form">
      <p class="heading">정보수정</p>
      <div class="include-msg">
        <div class="input-group btn_posi disableinput">
          <input v-model="member.member_id" placeholder="아이디" name="userID" type="text" maxlength="20" disabled />
        </div>
      </div>
      <div class="include-msg">
        <div class="input-group">
          <input @input="pwChange()" v-model="member.member_password" placeholder="비밀번호(8~16글자, 영문, 숫자, 특수문자 모두 포함)" type="password" />
        </div>
        <text id="password" class="infoMessage">{{ pwMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group">
          <input @input="onchange()" v-model="pwCheck" placeholder="비밀번호 재확인" id="confirm-Password" type="password" />
        </div>
        <text id="passwordCheck" class="infoMessage">{{ pwCheckMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group disableinput">
          <input v-model="member.member_name" placeholder="이름" type="text" maxlength="20" disabled/>
        </div>
      </div>
      <div class="include-msg">
        <div class="input-group btn_posi">
          <input @input="nickNameChange()" v-model="member.member_nickname" placeholder="닉네임" type="text" />
          <button class="button1" type="button" @click="isDuplicateNickName()">중복확인</button>
        </div>
        <text id="nickname" class="infoMessage">{{ nickNameMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="input-group btn_posi">
          <input @input="phoneNumChange()" v-model="member.member_phone_number" placeholder="전화번호" type="text" />
        </div>
        <text id="phone" class="infoMessage">{{ phoneMsg }}</text>
      </div>
      <div class="include-msg">
        <div class="gender-selection">
          <div class="gender-option">
            <input type="radio" id="male" name="gender" v-model="member.member_gender" value="M" disabled>
            <label for="male">남</label>
          </div>
          <div class="gender-option">
            <input type="radio" id="female" name="gender" v-model="member.member_gender" value="F" disabled>
            <label for="female">여</label>
          </div>
        </div>
      </div>
      <button class="button1" type="button" @click="updateUserInfo()">수정 하기</button>
    </form>
    <div class="DA-wrap">
      <p class="deleteAccount" @click="isShowModal = true">탈퇴 하기</p>
    </div>
  </div>

   <!---------알림 모달 창--------->
  <div class="modal" v-if="isUpdateShowModal">
    <div class="cookies-card">
      <p class="cookie-heading">회원 정보 수정</p>
      <p class="cookie-para">
        {{ modalMsg }}
      </p>
      <div class="button-wrapper">
        <button class="accept cookie-button" @click="this.$router.push({name: 'MyPage'})">확인</button>
        <button class="reject cookie-button" @click="isUpdateShowModal = false" >취소</button>
      </div>
      <button class="exit-button" @click="isUpdateShowModal = false">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 162 162"
          class="svgIconCross"
        >
          <path
            stroke-linecap="round"
            stroke-width="17"
            stroke="black"
            d="M9.01074 8.98926L153.021 153"
          ></path>
          <path
            stroke-linecap="round"
            stroke-width="17"
            stroke="black"
            d="M9.01074 153L153.021 8.98926"
          ></path>
        </svg>
      </button>
    </div>
  </div>

  <div class="modal" v-if="isUpdateSuccessful">
    <div class="cookies-card2" v-if="isUpdateSuccessful">
      <p class="cookie-heading2">회원 정보 수정</p>
      <p class="cookie-para2">
        {{ modalMsg }}
      </p>
      <div class="button-wrapper2">
        <button class="accept2 cookie-button2" @click="modalCheck()">확인</button>
      </div>
    </div>
  </div>
  
  <div class="modal" v-if="isShowModal">
    <div class="card">
      <div class="header">
        <div class="image"><svg aria-hidden="true" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" fill="none">
                    <path d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z" stroke-linejoin="round" stroke-linecap="round"></path>
                  </svg></div>
        <div class="content">
          <span class="title">회원 탈퇴</span>
          <p class="message">정말로 회원 탈퇴를 원하십니까? 탈퇴 시 모든 정보가 삭제되며 복구할 수 없습니다.</p>
          <br>

        </div>
        <div class="actions">
          <button class="desactivate" type="button" @click="deleteAccount()">탈퇴하기</button>
          <button class="cancel" type="button" @click="isShowModal = false">취소</button>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 에러 모달 -->
  <div class="modal" v-if="isErrorModal">
        <div class="cookies-card3">
            <p class="cookie-heading3">{{ errorModalTitle }}</p>
            <p class="cookie-para3">
                {{ errorModalMsg }}
            </p>
            <div class="button-wrapper3">
                <button class="accept3 cookie-button3" @click="errorModalClose()">확인</button>
            </div>
        </div>
    </div>

  <BottomBar />
</template>

<script>
import TopBar from '@/components/TopBar.vue';
import BottomBar from '@/components/BottomBar.vue';
import axios from 'axios';

export default {
  name: "ChangeInfo",
  components: {
    TopBar, BottomBar,
  },
  data() {
    return {
      member : {
        member_id : sessionStorage.getItem('USER_ID'),
        member_password : '',
        member_name : '',
        member_nickname : '',
        member_gender : '',
        member_phone_number : '',
      },
      nickNameDuplicateCheck : false, // 닉네임 중복확인 진행 여부
      isShowModal: false, // 회원탈퇴에 대한 모달창
      isUpdateShowModal : false, // 변경내용이 없을 때 보이는 모달창
      isUpdateSuccessful : false, // 회원정보를 수정했을 때 보이는 모달창
      pwCheck : '', // 비밀번호 재확인

      pwMsg : '', // 비밀번호 메세지
      pwCheckMsg : '', // 비밀번호 재확인 메세지
      nickNameMsg : '', // 닉네임 메세지
      phoneMsg : '', // 전화번호 메세지

      // 수정하기 전 데이터
      beforeUpdateMember : {
        member_nickname : '',
        member_phone_number : '',
      },
      
      modalStatus : 0, // 0이면 현재 페이지, 1이면 다음페이지로 이동

      isErrorModal : false, // 에러 모달 창 실행 여부
      errorModalTitle : '', // 에러 모달 제목
      errorModalMsg : '', // 에러 모달 메세지

      isAccountDelete : 0, // 탈퇴 여부
    }
  },
  methods: {
    modalCheck() {
      this.modalStatus === 1 ? this.$router.push({name: 'MyPage'}) : this.isUpdateSuccessful = false;
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
    // 전화번호 유효성 검사( 숫자 11자리)
    validatePhoneNumber(phoneNumber) {
      return /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/.test(phoneNumber);
    },
    // 비밀번호 입력 감지
    pwChange() {
      const password = document.getElementById('password');
      if(this.strongPassword(this.member.member_password) === false) {
        password.style.color = "#F00";
        this.pwMsg = "영문, 숫자, 특수문자 모두 포함 8글자 이상 입력하세요.";
      } else {
        password.style.color = "#70CA77";
        this.pwMsg = "사용 가능한 비밀번호입니다.";
      }
      if(this.member.member_password === '') {
        password.style.color = '#d1d1d1'
        this.pwMsg = '';
      }
    },  
    // 비밀번호 재입력 입력 감지
    onchange(){
      const vm = this;
      const input_tag = document.getElementById('confirm-Password');
      if(vm.member.member_password !== vm.pwCheck) {
        input_tag.style.borderColor = '#F00';
        document.getElementById('passwordCheck').style.color = '#F00';
        vm.pwCheckMsg = '비밀번호가 일치하지 않습니다.';
      } else {
        input_tag.style.borderColor = '#70CA77';
        document.getElementById('passwordCheck').style.color = '#70CA77';
        vm.pwCheckMsg = '비밀번호가 일치합니다.';
      }
      if(vm.pwCheck === '') {
        input_tag.style.borderColor = '#d1d1d1';
        vm.pwCheckMsg = '';
      }
    },
    // 닉네임 입력 감지
    nickNameChange() {
      const textTag = document.getElementById('nickname');
      if(this.validateNickName(this.member.member_nickname) === false) {
        textTag.style.color = '#F00';
        this.nickNameMsg = '첫글자는 영어 또는 숫자이고 그 이후는 대소문자, 숫자, 언더스코어, 띄어쓰기 포함 15글자까지 입력가능합니다.';
      } else {
        this.nickNameMsg = '';
      }
    },

    // 전화번호 입력 감지
    phoneNumChange() {
      if(this.validatePhoneNumber(this.member.member_phone_number) === false) {
        document.getElementById('phone').style.color = '#F00';
        this.phoneMsg = '입력예시 : 01012345678';
      } else {
        this.phoneMsg = '';
      }
    },
    // 닉네임 중복 확인
    isDuplicateNickName() {
      const vm = this;
      if(vm.validateNickName(vm.member.member_nickname) === false) {
        vm.nickNameMsg = "닉네임은 영어 대소문자, 숫자, 언더스코어 및 한글만 포함. 길이는 15자까지 가능하오니 다시 입력해주시기 바랍니다.";
        return;
      }

      if(vm.member.member_nickname.trim() === vm.beforeUpdateMember.member_nickname) {
        document.getElementById('nickname').style.color = '#F00';
        vm.nickNameMsg = '사용자의 현재 닉네임입니다.';
        return ;
      }
      const data = {
        nickname : vm.member.member_nickname
      };
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/isDuplicateNickName",
        data: data,
      })
        .then(response => {
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
        .catch(function() {
          vm.errorModalContent();
        })
    },

    // 회원정보 수정
    updateUserInfo() {
      const vm = this;
      if((vm.pwCheck === '' && vm.member.member_password === '') && (vm.member.member_nickname === vm.beforeUpdateMember.member_nickname && vm.nickNameDuplicateCheck  === false) && vm.member.member_phone_number === vm.beforeUpdateMember.member_phone_number) {
        vm.modalMsg = '변경사항이 없습니다. 마이페이지로 이동하시겠습니까?';
        vm.isUpdateShowModal = true;
        return;
      }
      if(vm.member.member_password !== vm.pwCheck) {
        return;
      }
      if(vm.member.member_nickname !== vm.beforeUpdateMember.member_nickname && vm.nickNameDuplicateCheck === false) {
        document.getElementById('nickname').style.color = '#F00';
        vm.nickNameMsg = '닉네임 중복확인을 진행해주세요.'
        return;
      }
      if(vm.phoneMsg !== '') {
        return;
      }

      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/updateUserInfo",
        data: vm.member,
      })
        .then(response => {
          if(response.data > 0) {
            vm.modalMsg = '회원님의 정보가 성공적으로 수정되었습니다.';
            vm.modalStatus = 1
            vm.isUpdateSuccessful = true;
          } else {
            vm.modalMsg = '회원정보 수정에 실패했습니다. 다시 시도해 주세요.';
            vm.modalStatus = 0
            vm.isUpdateSuccessful = true;
          }
        })
        .catch(() => {
          vm.modalMsg = '오류가 발생하였습니다. 잠시후 다시 시도해주세요.';
          vm.modalStatus = 0;
          vm.isUpdateSuccessful = true;
        })
      
    },  

    // 회원탈퇴
    deleteAccount() {
      const vm = this;
      const postData = {
        member_id : vm.member.member_id,
        member_name : vm.member.member_name
      }
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/deleteAccount",
        data: postData,
      })
        .then(response => {
          if(response.data > 0 ) {
            vm.isAccountDelete = 1;
            vm.errorModalTitle = '계정 탈퇴';
            vm.errorModalMsg = '회원님의 계정이 정상적으로 탈퇴되었습니다. 그동안 이용해주셔서 감사합니다.'
            vm.isErrorModal = true;
            sessionStorage.clear();
          }
        })
        .catch(() => {
          vm.errorModalContent();
        })
    },

    // 사용자 닉네임, 전화번호 불러오기
    loadUserInfo() {
      const vm = this;
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/api/getUserInfo",
        data: {member_id : vm.member.member_id }
      })
        .then(response => {
          if(response.data != null) {
            vm.member.member_name = response.data.member_name;
            vm.member.member_nickname = response.data.member_nickname;
            vm.member.member_phone_number = response.data.member_phone_number;
            vm.member.member_gender = response.data.member_gender;
            vm.beforeUpdateMember.member_nickname = response.data.member_nickname;
            vm.beforeUpdateMember.member_phone_number = response.data.member_phone_number;
          } else {
            vm.errorModalTitle = '알림';
            vm.errorModalMsg = '정보를 불러오는데 실패하였습니다.';
            vm.isErrorModal = true;
          }
        })
        .catch(() => {
          vm.errorModalContent();
        })
    },

    // 에러 발생시 오류 모달창 활성화
    errorModalContent() {
        this.errorModalTitle = '에러 발생'
        this.errorModalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
        this.isErrorModal = true;
    },

    // 에러 모달창이 나타났을 때는 에러 모달창 닫기, 탈퇴완료 모달창 발생시 로그인 페이지로 이동
    errorModalClose() {
      this.isErrorModal = false;
      if(this.isAccountDelete == 1) {
        this.$router.push({name : 'LoginPage'});
      }
    }
  },

  created() {

  },
  mounted() {
    this.loadUserInfo();
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
  overflow-y:auto;
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

.btn_posi {
  display: flex;
}

.btn_posi>input {
  width: 60%;
}

.btn_posi>.button1 {
  width: 40%;
  padding: 0;
  font-size: .7em;
  margin-left: .5em;

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

.button1 {
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

.disableinput {
  background-color: #e7e7e7;
  border-radius: 8px;
}

.DA-wrap {
  position: absolute;
  bottom: 1em;
  width: calc(100% - 5em);
  text-align: right;
}
.deleteAccount{
  text-decoration-line: none;
  font-size: 11px;
  color: #a8a8a8;
  cursor: pointer;
}
/* 모달 스타일 */
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

.card {
  overflow: hidden;
  position: relative;
  background-color: #ffffff;
  text-align: left;
  border-radius: 0.5rem;
  max-width: 80%;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.header {
  padding: 1.25rem 1rem 1rem 1rem;
  background-color: #ffffff;
}

.image {
  display: flex;
  margin-left: auto;
  margin-right: auto;
  background-color: #FEE2E2;
  flex-shrink: 0;
  justify-content: center;
  align-items: center;
  width: 3rem;
  height: 3rem;
  border-radius: 9999px;
}

.image svg {
  color: #DC2626;
  width: 1.5rem;
  height: 1.5rem;
}

.content {
  margin-top: 0.75rem;
  text-align: center;
}

.title {
  color: #111827;
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.5rem;
}

.message {
  margin-top: 0.5rem;
  color: #6B7280;
  font-size: 0.875rem;
  line-height: 1.25rem;
}

.actions {
  margin: 0.75rem 1rem;
  background-color: #F9FAFB;
}

.desactivate {
  display: inline-flex;
  padding: 0.5rem 1rem;
  background-color: #DC2626;
  color: #ffffff;
  font-size: 1rem;
  line-height: 1.5rem;
  font-weight: 500;
  justify-content: center;
  width: 100%;
  border-radius: 0.375rem;
  border-width: 1px;
  border-color: transparent;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.cancel {
  display: inline-flex;
  margin-top: 0.75rem;
  padding: 0.5rem 1rem;
  background-color: #ffffff;
  color: #374151;
  font-size: 1rem;
  line-height: 1.5rem;
  font-weight: 500;
  justify-content: center;
  width: 100%;
  border-radius: 0.375rem;
  border: 1px solid #D1D5DB;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

/* check box */


/* isUpdateShowModal 모달창 css */
.cookies-card {
  width: 280px;
  height: fit-content;
  background-color: rgb(255, 250, 250);
  border-radius: 10px;
  border: 1px solid rgb(206, 206, 206);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;
  padding: 20px;
  gap: 15px;
  position: relative;
  font-family: Arial, Helvetica, sans-serif;
  box-shadow: 0px 10px 10px rgba(0, 0, 0, 0.066);
}

.cookie-heading {
  color: rgb(34, 34, 34);
  font-weight: 800;
}
.cookie-para {
  font-size: 11px;
  font-weight: 400;
  color: rgb(51, 51, 51);
}
.button-wrapper {
  width: 100%;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}
.cookie-button {
  width: 50%;
  padding: 8px 0;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.accept {
  background-color: rgb(34, 34, 34);
  color: white;
}
.reject {
  background-color: #ececec;
  color: rgb(34, 34, 34);
}
.accept:hover {
  background-color: rgb(0, 0, 0);
}
.reject:hover {
  background-color: #ddd;
}
.exit-button {
  position: absolute;
  top: 17px;
  right: 17px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.exit-button:hover {
  background-color: #ddd;
  color: white;
}
.svgIconCross {
  height: 10px;
}

/* isUpdateSuccessful 모달창 */
.cookies-card2 {
  width: 280px;
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
}
.cookie-para2 {
  font-size: 11px;
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
}

/* 에러 모달 css */
.cookies-card3 {
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

.cookie-heading3 {
    color: rgb(34, 34, 34);
    font-weight: 800;
    text-align: center;
    font-size: 1.2em;
}

.cookie-para3 {
    font-size: 1em;
    font-weight: 400;
    color: rgb(51, 51, 51);
}

.button-wrapper3 {
    width: 50%;
    height: auto;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
}

.cookie-button3 {
    width: 100%;
    padding: 8px 0;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.accept3 {
    background-color: rgb(34, 34, 34);
    color: white;
    font-size: 1em;
}

</style>