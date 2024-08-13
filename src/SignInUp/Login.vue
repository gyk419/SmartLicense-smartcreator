<template>
  <TopBar />
  <div class="login-container">
    <form class="login-form">
      <p class="heading">로그인</p>
      <br>
      <div class="input-group">
        <input @input="changeId()" v-model="userId" required="" placeholder="아이디" id="username name=" type="text" />
        <text v-show="showIdMsg" id="userID" class="infoMessage">아이디를 입력하세요.</text>
      </div>
      <div class="input-group">
        <input @input="changePw()" v-model="userPw" required="" placeholder="비밀번호" name="password" id="password" type="password" />
        <text v-show="showPwMsg" id="userPW" class="infoMessage">비밀번호를 입력하세요.</text>
      </div>
      <button type="button" @click="login()">로그인</button>
      <div class="bottom-text">
        <p>계정이 없으신가요? <a href="/signup">회원가입</a></p>
        <p><a href="/findbyid" class="idhref">아이디 찾기</a><a href="/findbypw" class="pwhref">비밀번호 찾기</a></p>
      </div>
      <br />
      <hr>
      <br>
      <button class="withoutlogin" type="button" @click="goToCC()">로그인 없이 시작</button>
      <text class="noLoginMessage">로그인 없이 시작할 경우, 기록이 저장되지 않아 나중에 확인할 수 없습니다.</text>
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
              <button class="accept2 cookie-button2" @click="closeModal()">확인</button>
          </div>
      </div>
  </div>
</template>

<script>
import TopBar from '@/components/TopBar.vue';
import axios from 'axios';
export default {
  name: "LoginPage",
  components: {
    TopBar,
  },
  data() {
    return {
      isErrorModal : false, // 에러 모달 창 실행 여부
      modalTitle : '', // 모달 제목
      modalMsg : '', // 모달 메세지
      isLoginSuccess : 0, // 로그인 성공 여부

      showIdMsg : false, // 아이디를 입력하세요 메세지  (true : 보임, false : 안보임)
      showPwMsg : false, // 비밀번호를 입력하세요 메세지 (true : 보임, false : 안보임)
      userId : "", // 사용자 ID
      userPw : "", // 사용자 PW

    }
  },
  methods: {
    closeModal() {
      this.isErrorModal = false;
      if(this.isLoginSuccess == 1) {
        this.$router.push({ name: 'CategoryChoice' });
      }
    },
    // 아이디 입력을 감지 했을 경우
    changeId() {
      this.showIdMsg = false;
    },

    // 비밀번호 입력을 감지 했을 경우
    changePw() {
      this.showPwMsg = false;
    },

    goToCC() {
      if(localStorage.getItem("GUEST") !== null) {
        this.$cookies.set('GUEST', 1);
        this.$router.push({ name: 'CategoryChoice' });
      } else {
        axios({
          method : 'post',
          header: { 'Content-Type': 'application/json; charset=UTF-8' },
          url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/memberLogin/guestLogin",
          data: null,
        })
          .then(response => {
            if(response.data.result > 0) {
              localStorage.setItem('GUEST',response.data.guestId);
              this.$cookies.set('GUEST', 1);
              this.$router.push({ name: 'CategoryChoice' });
            } else{
              this.modalTitle = '게스트로그인 실패';
              this.modalMsg = '게스트로그인에 실패하였습니다. 잠시후 다시 시도해주세요.';
              this.isErrorModal = true;
              this.isLoginSuccess = 0;
            }
          })
          .catch(() => {
            this.errorModalContent();
          });
      }
    },

    // 로그인
    login() {
      const vm = this;
      if(vm.userId == '' && vm.userPw == '') {
        vm.showIdMsg = true;
        vm.showPwMsg = true;
        return;
      }
      if(vm.userId == '') {
        vm.showIdMsg = true;
        return;
      }
      if(vm.userPw == '') {
        vm.showPwMsg = true;
        return ;
      }
      let dt = {
        userId : vm.userId,
        userPw : vm.userPw
      };
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/memberLogin/verify",
        data: dt,
      })
        .then(function(response){
          if(response.data.result == 0) {
            vm.modalTitle = '로그인 실패';
            vm.modalMsg = response.data.message;
            vm.isLoginSuccess = 0;
            vm.isErrorModal = true;
          }
          if(response.data.result == 1) {
            sessionStorage.setItem('JSESSIONID', response.data.JSESSIONID);
            sessionStorage.setItem('USER_ID', response.data.USER_ID);
            vm.modalTitle = '로그인 성공';
            vm.modalMsg = '로그인에 성공하였습니다.';
            vm.isLoginSuccess = 1;
            vm.isErrorModal = true;
          }
        })
        .catch(function(){
          vm.errorModalContent();
        });
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
    if( (sessionStorage.getItem('JSESSIONID') != null && sessionStorage.getItem('USER_ID') != null)){
      this.$router.push({ name: 'CategoryChoice' });
    }
  },
}
</script>

<style scoped>
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

.bottom-text>p:nth-child(odd) {
  margin-bottom: 0.5em;
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
  margin-bottom: 5px;
}

.input-group {
  margin-bottom: 20px;

}

.input-group input {
  background: none;
  border: 1px solid #d1d1d1;
  padding: 15px 23px;
  font-size: 16px;
  border-radius: 8px;
  color: #000000;
  width: 100%;
  /* box-shadow: rgba(0, 0, 0, 0.06) 0px 1px 1px,
    rgba(0, 0, 0, 0.09) 0px 1px 1px, rgba(0, 0, 0, 0.09) 0px 1px 2px,
    rgba(0, 0, 0, 0.09) 0px 1px 4px, rgba(0, 0, 0, 0.09) 0px 1px 8px; */
  height: 2em;
}

.input-group input:focus {
  border-color: #0173ed;
  outline: none;
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
  font-size: .7em;
  font-weight: 400;
}

.bottom-text a {
  color: #5271ff;
  text-decoration: none;
  transition: color 0.3s ease;

}

.bottom-text p {
  margin-top: 0.5em;
}

.withoutlogin {
  background-color: #353535;
}

.idhref {
  margin-right: .5em;
}

.pwhref {
  margin-left: .5em;
}

.noLoginMessage {
  display: block;
  color: rgb(26, 26, 26);
  -webkit-text-stroke-width: 1;
  -webkit-text-stroke-color: #000;
  font-family: Inter;
  font-size: 0.7em;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  margin-top: 1em;
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
