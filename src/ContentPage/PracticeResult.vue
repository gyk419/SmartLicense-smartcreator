<template>
  <TopBar />
  <div class="results-summary-container">
    <div :class="['confetti', resultClass]">
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
    <div :class="['results-summary-container__result', resultClass]">
      <div :class="['result-box', resultClass]">
        <div :class="['heading-primary', resultClass]">{{ practiceScore.correctCount }}</div>
        <p :class="['result', resultClass]">of {{ practiceScore.totalProblems }}</p>
      </div>
      <div class="result-text-box">
        <div :class="['heading-secondary', resultClass]">{{ resultMessage }}</div>
      </div>
    </div>
    <div class="results-summary-container__options">
      <div class="heading-secondary heading-secondary--blue">과목 : {{ subject }}</div>
      <div class="summary-result-options">
        <div class="result-option result-option-memory">
          <div class="icon-box">

            <span class="reaction-icon-text">총 문항 갯수</span>
          </div>
          <div class="result-box"><span>{{ practiceScore.totalProblems }}</span></div>
        </div>
        <div class="result-option result-option-verbal">
          <div class="icon-box">

            <span class="memory-icon-text">정답 갯수</span>
          </div>
          <div class="result-box"><span>{{ practiceScore.correctCount }}</span></div>
        </div>
        <div class="result-option result-option-reaction">
          <div class="icon-box">

            <span class="verbal-icon-text">오답 갯수</span>
          </div>
          <div class="result-box"><span>{{ (practiceScore.totalProblems) - (practiceScore.correctCount) }}</span></div>
        </div>
        <div class="summary__cta">
          <button class="btn btn__continue" @click="btnClick()">{{ buttonText }}</button>
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
import BottomBar from '@/components/BottomBar.vue';
import axios from 'axios';
export default {
  name: "PracticeResult",
  components: {
    TopBar, BottomBar
  },
  data() {
    return {
      practiceScore: [],
      memberId: null,
      subject: null,
      is_pass: null,

      isErrorModal: false, // 에러 모달 v-if
      modalTitle: '',
      modalMsg: ''
    }
  },
  methods: {
    btnClick() {
      let selectedSubjects = JSON.parse(this.$cookies.get('selectedSubjects'));
      let scoreResult = JSON.parse(this.$cookies.get('scoreResult')) || [];

      scoreResult.push({
        correctCount: this.practiceScore.correctCount,
        questionCount: this.practiceScore.totalProblems
      });
      this.$cookies.set('scoreResult', JSON.stringify(scoreResult));

      if (selectedSubjects.length > 1) {
        selectedSubjects.splice(0, 1); // 인덱스가 0인 것을 하나 지우기
        this.$cookies.set('selectedSubjects', JSON.stringify(selectedSubjects));
        this.$router.push({ name: 'PracticeMode' });
      } else {
        this.$router.push({ name: 'MyHistoryPage' });
        this.storeExamRecord();
        this.$cookies.remove('scoreResult');
      }
    },

    storeExamRecord() {
      let scoreResult = JSON.parse(this.$cookies.get('scoreResult'));
      let totalCorrectCount = 0;
      let totalQuestionCount = 0;
      let passedSubjects = 0;

      // 저장된 과목의 점수와 전체 점수를 합산
      scoreResult.forEach(result => {
        totalCorrectCount += result.correctCount;
        totalQuestionCount += result.questionCount;

        if (result.correctCount >= result.questionCount * 0.4) {
          passedSubjects = 1;
        } else{
          passedSubjects = 0;
        }
      });

      // 전체 평균 조건 확인
      const averageScore = totalCorrectCount / totalQuestionCount;
      const isPass = (passedSubjects == 1) && (averageScore >= 0.6);

      const recordData = {
        mode: this.$cookies.get('mode'),
        start_test_date: this.$cookies.get('start_test_date'),
        member_id: this.memberId,
        exam_date: this.$cookies.get('exam_date'),
        detail_license_name: this.$cookies.get('detail_license'),
        license_name: this.$cookies.get('license'),
        subject_count: this.$cookies.get('subject_count'),
        is_pass: isPass ? 1 : 0 // 1: 합격, 0: 불합격
      };

      // 응시 시험 기록 저장
      axios({
        method: 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/storeExamRecordPractice",
        data: recordData,
      })
        .then(response => {
          if (response.data.result == 0) {
            this.modalTitle = '저장 실패';
            this.modalMsg = '저장에 실패했습니다';
            this.isErrorModal = true;
          }
        })
        .catch(() => {
          this.errorModalContent();
        })
    },

    loadUserSelectAnswer() {
      let subjectindex = JSON.parse(this.$cookies.get('selectedSubjects'));
      this.subject = subjectindex[0];
      const postData = {
        detail_license: this.$cookies.get('detail_license'),
        license: this.$cookies.get('license'),
        exam_date: this.$cookies.get('exam_date'),
        subject_name: subjectindex[0],
        member_id: this.memberId,
        start_test_date: this.$cookies.get("start_test_date")
      }
      axios({
        method: 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/loadPracticeScore",
        data: postData,
      })
        .then(response => {
          this.practiceScore = response.data;
        })
        .catch(() => {
          this.errorModalContent();
        });
    },

    errorModalContent() {
      this.title = '에러 발생'
      this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
      this.isErrorModal = true;
    },
  },

  computed: {
    passed() {
      return this.practiceScore.correctCount >= this.practiceScore.totalProblems * 0.4;
    },
    resultClass() {
      return this.passed ? 'passed' : 'failed';
    },
    resultMessage() {
      return this.passed ? '합격 기준입니다.' : '불합격 기준입니다.';
    },
    buttonText() {
      let selectedSubjects = JSON.parse(this.$cookies.get('selectedSubjects'));
      return selectedSubjects.length > 1 ? '다음 과목 풀기' : '기록 페이지로 가기';
    }
  },
  mounted() {
    if ((sessionStorage.getItem('JSESSIONID') != null && sessionStorage.getItem('USER_ID') != null)) {
      this.memberId = sessionStorage.getItem('USER_ID');
    } else {
      this.memberId = localStorage.getItem('GUEST');
    }
    this.loadUserSelectAnswer();
  },
}
</script>

<style scoped>
.results-summary-container {
  position: fixed;
  display: flex;
  flex-direction: column;
  top: 20%;
  width: 100%;
  height: 88%;
  backface-visibility: hidden;
}

.heading-primary,
.heading-secondary,
.heading-tertiary {
  color: #49862C;
  text-transform: capitalize;
  margin-bottom: 15px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.heading-primary {
  font-size: 36px;
  font-weight: 700;
  background-color: #49862C;
  background-clip: text;

  -webkit-text-fill-color: transparent;
  transform: scale(1.4);
}

.heading-secondary {
  font-size: 1.1em;
  font-weight: 900;
  margin-top: 15px;
}

.heading-secondary--blue {
  color: #000;
}

.heading-tertiary {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #000
}

.paragraph {
  font-size: 17px;
  line-height: 1.6;
  color: #000
}

.paragraph-text-box {
  width: 100%;
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
  border-radius: 10px 10px 0 0;
}

.results-summary-container__result .result-box.passed {
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

.results-summary-container__result .result-box.failed {
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

.results-summary-container__result .result.passed {
  margin-top: -12px;
  font-size: 14px;
  font-weight: 550;
  color: #49862C;
}

.results-summary-container__result .result.failed {
  margin-top: -12px;
  font-size: 14px;
  font-weight: 550;
  color: #f94d4d;
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
  background-color: hsl(0, 100%, 97%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-radius: 3px;
  cursor: pointer;
}

.result-option-memory {
  background-color: hsl(39, 100%, 97%);
}

.result-option-verbal {
  background-color: hsl(166, 100%, 97%);
}

.result-option-Visual {
  background-color: hsl(234, 85%, 97%);
}

.icon-box {
  display: flex;
  font-size: 16px;
  align-items: center;
  gap: 2px;
}

.reaction-icon-text {
  color: hsl(39, 100%, 56%);
}

.memory-icon-text {
  color: hsl(166, 100%, 37%);
}

.verbal-icon-text {
  color: hsl(0, 100%, 67%);
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
  cursor: pointer;
  margin: 15px 0 30px 0;
  transition: all 0.3s;
}

.confetti.passed {
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

.confetti.failed {
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

.passed {
  color: #49862C;
}

.failed {
  color: #FF0000;
}

.heading-primary.passed {
  background-color: #49862C;
}

.heading-primary.failed {
  background-color: #FF0000;
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