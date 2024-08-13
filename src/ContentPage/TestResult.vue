<template>
  <TopBar />
  <div class="results-summary-container">
       <div :class="['confetti', isPassed ? 'confetti-pass' : 'confetti-fail']">
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
        <div class="confetti-piece"></div>
      </div>
      <div :class="['results-summary-container__result', isPassed ? 'result-pass' : 'result-fail']">
        <div :class="['result-box', isPassed ? 'result-box-pass' : 'result-box-fail']">
          <div :class="['heading-primary', isPassed ? 'heading-primary-pass' : 'heading-primary-fail']">{{ScoreSum}}</div>
          <p :class="['result', isPassed ? 'result-pass' : 'result-fail']">of {{ QnumberSum }}</p>
        </div>
        <div class="result-text-box">
          <div :class="['heading-secondary', isPassed ? 'heading-secondary-pass' : 'heading-secondary-fail']">{{isPassed ? '합격 기준입니다.' : '불합격 기준입니다.'}}</div>
        </div>
      </div>
      <div class="results-summary-container__options">
        <div class="heading-secondary">남은 시간 : {{ remainTime }}</div>
        <div class="summary-result-options">
          <div 
          v-for="(subject, index) in Subject" 
          :key="index" 
          :class="['result-option', isPassedScore(index) ? 'result-option-pass' : 'result-option-fail']"
        >
          <div class="icon-box">
            <span :class="isPassedScore(index) ? 'icon-text-pass' : 'icon-text-fail'">{{ subject }}</span>
          </div>
          <div class="result-box"><span>{{ Score[index] }}</span> / {{ Qnumber[index] }}</div>
        </div>
          <div class="summary__cta">
            <button class="btn btn__continue" @click="goToMyHistoryPage">{{ goToPageName }}</button>
          </div>
        </div>
      </div>
    </div>
    <BottomBar />

    <!-- 에러 모달 -->
   <div class="modal" v-if="isErrorModal">
        <div class="cookies-card2">
            <p class="cookie-heading2">{{ modalTitle }}</p>
            <p class="cookie-para2">
                {{ modalMsg }}
            </p>
            <div class="button-wrapper2">
                <button class="accept2 cookie-button2" @click="isErrorModal =false">확인</button>
            </div>
        </div>
    </div>
</template>
<script>
import TopBar from '@/components/TopBar.vue';
import BottomBar from '@/components/BottomBar.vue'
import axios from 'axios';

export default {
  name: "TestResult",
  components: {
    TopBar, BottomBar
  },
  data() {
    return {
      memberId: '',
      Subject: [],
      Qnumber: [],
      Score: [],
      remainTime: '', 
      goToPageName : '기록 페이지로 가기',

      isErrorModal: false, // 에러 모달 v-if
      modalTitle: '',
      modalMsg: ''
    }
  },

  methods:{
    goToMyHistoryPage(){
      if(sessionStorage.getItem('USER_ID') != null) {
        this.$router.push({ name : 'MyHistoryPage'});
      } else {
        this.$router.push({ name : 'CategoryChoice' });
      }
    },
    isPassedScore(index) {
      return this.Score[index] >= this.Qnumber[index] * 0.4;
    },

    // 시험결과 가져오기
    async loadExamRecord() {
      const vm = this;
      const loadData = {
        start_test_date : vm.$cookies.get('startTestDate'),
        member_id : vm.memberId,
        exam_date : vm.$cookies.get('exam_date'),
        detail_license_name : vm.$cookies.get('detail_license'),
        license_name : vm.$cookies.get('license'),
        subject : vm.$cookies.get("selectedSubjects"),
        mode : vm.$cookies.get("mode")
      };
      await axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/loadTestScore",
        data : loadData,
      })
        .then(response => {
          if(response.data.result > 0) {
            vm.Subject = response.data.subject;
            vm.Qnumber = response.data.qnumber;
            vm.Score = response.data.score;
            vm.remainTime = response.data.remaintime;
          } else {
            this.modalTitle = '로드 실패';
            this.modalMsg = '데이터 로드에 실패했습니다.';
            this.isErrorModal = true;
          }
        })
        .catch(() => {
          this.errorModalContent();
        })
    },

    errorModalContent() {
      this.title = '에러 발생'
      this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
      this.isErrorModal = true;
    },
  },
  computed:{
    QnumberSum() {
      return this.Qnumber.reduce((acc, val) => acc + val, 0);
    },
    ScoreSum() {
      return this.Score.reduce((acc, val) => acc + val, 0);
    },
    isPassed() {
      return this.ScoreSum >= this.QnumberSum * 0.6 && this.Score.every((score, index) => score >= this.Qnumber[index] * 0.4);
    },
  },
  mounted(){
    if((sessionStorage.getItem('JSESSIONID') != null && sessionStorage.getItem('USER_ID') != null)) {
      this.memberId = sessionStorage.getItem('USER_ID');
      this.goToPageName = '기록 페이지로 가기';
    } else {
      this.memberId = localStorage.getItem('GUEST');
      this.goToPageName = '홈으로 가기';
    }
    this.loadExamRecord();
  }
}
</script>
<style scoped>
.results-summary-container {
  position: relative;
  display: flex;
  top: 6%;
  flex-direction: column;
  width: 100%;
  height: 88%;
}

.heading-primary-pass,
.heading-secondary-pass,
.heading-tertiary-pass {
  color: #49862C;
  text-transform: capitalize;
  margin-bottom: 15px;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.heading-primary-fail,
.heading-secondary-fail,
.heading-tertiary-fail {
  color: #ef2e2e;
  text-transform: capitalize;
  margin-bottom: 15px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.heading-primary-pass {
  font-size: 36px;
  font-weight: 700;
  background-color: #49862C;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  transform: scale(1.4);
}
.heading-primary-fail {
  font-size: 36px;
  font-weight: 700;
  background-color: #ef2e2e;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  transform: scale(1.4);
}

.heading-secondary {
  font-size: 1.3em;
  font-weight: 700;
  margin-top: 15px;
  margin-bottom: .7em;
  letter-spacing: 1px;
}

.text-center {
  text-align: center;
}

.margin-1 {
  margin-bottom: 10px;
}

.results-summary-container__result {
  display: flex;
  flex-direction: column;
  text-align: center;
  align-items: center;
  justify-content: center;
  padding: 20px 10px;
}

.results-summary-container__result.result-pass .result-box {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background-color: #EBFBEE;
  border: 10px solid #70CA77;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.results-summary-container__result.result-fail .result-box {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background-color: #fdcccc;
  border: 10px solid #f94d4d;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.results-summary-container__result .result-pass {
  margin-top: -12px;
  font-size: 14px;
  font-weight: 400;
  color: #49862C;
}
.results-summary-container__result .result-fail {
  margin-top: -12px;
  font-size: 14px;
  font-weight: 400;
  color: #ef2e2e;
}

.results-summary-container__options {
  padding: 0 16px;
}

.summary-result-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.result-option {
  width: 100%;
  height: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-radius: 3px;
}

.result-option-fail {
  background-color: hsl(0, 100%, 97%);
}

.result-option-pass {
  background-color: hsl(166, 100%, 97%);
}

.icon-box {
  display: flex;
  font-size: 16px;
  align-items: center;
  gap: 2px;
}

.icon-text-fail {
  color: hsl(0, 100%, 67%);
}

.icon-text-pass {
  color: hsl(166, 100%, 37%);
}

.result-box {
  font-size: 14px;
  color: hsl(241, 100%, 89%);
  font-weight: 700;
}

.result-box span {
  font-size: 14px;
  color: hsl(224, 30%, 27%);
}

.btn {
  width: 100%;
  padding: 10px;
  color: #ffffff;
  background-color: #353535;
  border: none;
  border-radius: 3px;
  font-size: 17px;
  text-transform: uppercase;
  letter-spacing: 2px;
  font-weight: 500;
  margin: 15px 0 30px 0;
  transition: all 0.3s;
}

.confetti-pass {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  width: 100%;
  height: 50%;
  top: 2.5em;
  overflow: hidden;
  z-index: 1000;
}

.confetti-fail {
  visibility: hidden;
}

.confetti-piece {
  position: absolute;
  width: 10px;
  height: 20px;
  background-color: hsl(39, 100%, 56%);
  top: 0;
  opacity: 0;
  animation: makeItRain 3000ms infinite ease-in-out;
}

.confetti-piece:nth-child(1) {
  left: 7%;
  transform: rotate(-10deg);
  animation-delay: 182ms;
  animation-duration: 2000ms;
}

.confetti-piece:nth-child(2) {
  left: 14%;
  transform: rotate(20deg);
  animation-delay: 161ms;
  animation-duration: 2076ms;
}

.confetti-piece:nth-child(3) {
  left: 21%;
  transform: rotate(-51deg);
  animation-delay: 481ms;
  animation-duration: 2103ms;
}

.confetti-piece:nth-child(4) {
  left: 28%;
  transform: rotate(61deg);
  animation-delay: 334ms;
  animation-duration: 1008ms;
}

.confetti-piece:nth-child(5) {
  left: 35%;
  transform: rotate(-52deg);
  animation-delay: 302ms;
  animation-duration: 1776ms;
}

.confetti-piece:nth-child(6) {
  left: 42%;
  transform: rotate(38deg);
  animation-delay: 180ms;
  animation-duration: 1168ms;
}

.confetti-piece:nth-child(7) {
  left: 49%;
  transform: rotate(11deg);
  animation-delay: 395ms;
  animation-duration: 1200ms;
}

.confetti-piece:nth-child(8) {
  left: 56%;
  transform: rotate(49deg);
  animation-delay: 14ms;
  animation-duration: 1887ms;
}

.confetti-piece:nth-child(9) {
  left: 63%;
  transform: rotate(-72deg);
  animation-delay: 149ms;
  animation-duration: 1805ms;
}

.confetti-piece:nth-child(10) {
  left: 70%;
  transform: rotate(10deg);
  animation-delay: 351ms;
  animation-duration: 2059ms;
}

.confetti-piece:nth-child(11) {
  left: 77%;
  transform: rotate(4deg);
  animation-delay: 307ms;
  animation-duration: 1132ms;
}

.confetti-piece:nth-child(12) {
  left: 84%;
  transform: rotate(42deg);
  animation-delay: 464ms;
  animation-duration: 1776ms;
}

.confetti-piece:nth-child(13) {
  left: 91%;
  transform: rotate(-72deg);
  animation-delay: 429ms;
  animation-duration: 1818ms;
}

.confetti-piece:nth-child(14) {
  left: 94%;
  transform: rotate(-72deg);
  animation-delay: 429ms;
  animation-duration: 818ms;
}

.confetti-piece:nth-child(15) {
  left: 96%;
  transform: rotate(-72deg);
  animation-delay: 429ms;
  animation-duration: 2818ms;
}

.confetti-piece:nth-child(16) {
  left: 98%;
  transform: rotate(-72deg);
  animation-delay: 429ms;
  animation-duration: 2818ms;
}

.confetti-piece:nth-child(17) {
  left: 50%;
  transform: rotate(-72deg);
  animation-delay: 429ms;
  animation-duration: 2818ms;
}

.confetti-piece:nth-child(18) {
  left: 60%;
  transform: rotate(-72deg);
  animation-delay: 429ms;
  animation-duration: 1818ms;
}

.confetti-piece:nth-child(odd) {
  background-color: hsl(0, 100%, 67%);
}

.confetti-piece:nth-child(even) {
  z-index: 1;
}

.confetti-piece:nth-child(4n) {
  width: 6px;
  height: 14px;
  animation-duration: 4000ms;
  background-color: #c33764;
}

.confetti-piece:nth-child(5n) {
  width: 3px;
  height: 10px;
  animation-duration: 4000ms;
  background-color: #b06ab3;
}

.confetti-piece:nth-child(3n) {
  width: 4px;
  height: 12px;
  animation-duration: 2500ms;
  animation-delay: 3000ms;
  background-color: #dd2476;
}

.confetti-piece:nth-child(3n-7) {
  background-color: hsl(166, 100%, 37%);
}

@keyframes makeItRain {
  from {
    opacity: 0;
  }

  50% {
    opacity: 1;
  }

  to {
    transform: translateY(200px);
  }
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