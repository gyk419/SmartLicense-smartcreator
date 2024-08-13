<template>
  <TopBar />
  <div class="practive_mode_container">
    <div class="popup-container">
      <div class="popup info-popup">
        <div class="info-message">{{ DetailLicense }}</div>
      </div>
      <div class="popup info-popup">
        <div class="info-message">{{ TestDate }}</div>
      </div>
      <div class="popup info-popup">
        <div class="info-message">{{ Subject }}</div>
      </div>
    </div>
    <div class="current_Q">
      <p>{{ Qnumber }}. {{ Question }}</p>
      <img :src="imagePath">
    </div>
    <span class="toggle-btn" @click="toggleBottomSheet"></span>
    <div class="radio-input" :class="{ 'is-visible': isBottomSheetVisible }">
      <div class="infoo">
        <span class="close-explanation" @click="toggleBottomSheet">&times;</span>
      </div>
      <div class="scrollable">
        <input type="radio" id="value-1" name="value-radio" :value="Option1" v-model="selectedOption">
        <label for="value-1" ><div v-if="imgCheckOP1">{{ Option1 }}</div><img v-else :src="Option1"></label>
        <input type="radio" id="value-2" name="value-radio" :value="Option2" v-model="selectedOption">
        <label for="value-2"><div v-if="imgCheckOP2">{{ Option2 }}</div><img v-else :src="Option2"></label>
        <input type="radio" id="value-3" name="value-radio" :value="Option3" v-model="selectedOption">
        <label for="value-3"><div v-if="imgCheckOP3">{{ Option3 }}</div><img v-else :src="Option3"></label>
        <input type="radio" id="value-4" name="value-radio" :value="Option4" v-model="selectedOption">
        <label for="value-4"><div v-if="imgCheckOP4">{{ Option4 }}</div><img v-else :src="Option4"></label>
        <input type="radio" id="value-5" name="value-radio" :value="Option5" v-model="selectedOption">
        <label for="value-5">{{ Option5 }}</label>
      </div>
      <div class="btn_collection" v-if="Qnumber < totalQuestionList.length" @click="nextQuestion">
        다음 문제 >
      </div>
      <div class="btn_collection" @click="viewResult" v-if="Qnumber == totalQuestionList.length">
        제출하고 결과보기
      </div>
    </div>
    <div class="last_Q" v-if="Qnumber == totalQuestionList.length">
      <p>마지막 문제입니다.</p>
    </div>
    <div class="timer-container">
      <div class="timer">{{ formattedTime }}</div>
    </div>
  </div>

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
import axios from 'axios';
export default {
  name: "TestMode",
  components: {
    TopBar,
  },
  data() {
    return {
      memberId: '',
      imgCheckOP1 : true,
      imgCheckOP2 : true,
      imgCheckOP3 : true,
      imgCheckOP4 : true,
      imagePath : '', // 문제 이미지 경로
      totalQuestionList : [], // DB에서 불러온 총 문제    
      DetailLicense: "", // 자격명
      TestDate: "", // 시험 시작 날짜 및 시간 
      Subject: "", // 과목명
      Qnumber: '', // 문항 (ex. 1,2,3 ...)
      Question: "", // 문제
      Option1: "", // 보기1
      Option2: "", // 보기2
      Option3: "", // 보기3
      Option4: "", // 보기4
      Option5: '잘 모르겠어요',
      selectedOption: null, // 사용자가 선택한 답
      isModalVisivle: false, // 모달창 열기 여부
      isBottomSheetVisible: true,
      isCorret : 0,
      timeRemaining: 150 * 60, // 150분을 초로 변환 (9000)
      timer: null,

      Score : [], // 합격 불합격 여부를 확인하기 위해 과목별 점수를 저장할 변수
      correctSum : 0, // 과목별 정답 개수를 저장할 변수
      subjectQnumber : [], // 과목별 총 문제수를 저장
      subjectQnumberSum : 0, // 과목별 문제수를 저장(1씩더하다가 과목명이 변경되면 Qnumber에 저장 후 0으로 초기화)
      Pass : '0', // 합격 여부

      isErrorModal: false, // 에러 모달 v-if
      modalTitle: '',
      modalMsg: ''
    }
  },
  computed: {

    formattedTime() {
      const hours = Math.floor(this.timeRemaining / 3600);
      const minutes = Math.floor((this.timeRemaining % 3600) / 60);
      const seconds = this.timeRemaining % 60;

      let timeString = '';
      if (hours > 0) {
        timeString += `${hours}시간 `;
      }
      if (minutes > 0) {
        timeString += `${minutes}분 `;
      }
      if (seconds > 0 || timeString === '') { // 초가 0이어도 시간과 분이 둘 다 0이면 표시
        timeString += `${seconds}초`;
      }

      return timeString.trim();
    }
  },
  methods: {
    // 남은 시간 계산 함수
    remainingTimeCal() {
      const hours = Math.floor(this.timeRemaining / 3600);
      const minutes = Math.floor((this.timeRemaining % 3600) / 60);
      const seconds = this.timeRemaining % 60;

      let timeString = '';
      if (hours > 0) {
        timeString += `${hours}시간 `;
      }
      if (minutes > 0) {
        timeString += `${minutes}분 `;
      }
      if (seconds > 0 || timeString === '') { // 초가 0이어도 시간과 분이 둘 다 0이면 표시
        timeString += `${seconds}초`;
      }

      return timeString.trim();
    },

    imgCheck(img) {
      if(img.includes("https://ifh.cc")) { return false; }
      else { return true; }
    },

    toggleBottomSheet() {
      this.isBottomSheetVisible = !this.isBottomSheetVisible;
    },
    // 결과페이지 이동 버튼 클릭
    async viewResult() {
      const vm = this;
      if(vm.selectedOption === '') {
        return;
      }
      vm.subjectQnumberSum += 1;
      if(vm.totalQuestionList[vm.Qnumber-1].answer === vm.selectedOption){
        vm.isCorret = 1; // 정답일때
        vm.correctSum += vm.isCorret;
      } else {
        vm.isCorret = 0; // 오답일때
      }
      vm.Score.push(vm.correctSum);
      vm.subjectQnumber.push(vm.subjectQnumberSum);
      vm.correctSum = 0;
      
      const postData = {
        select_answer : vm.selectedOption,
        member_id : vm.memberId,
        question_idx : vm.totalQuestionList[vm.Qnumber - 1].question_idx,
        is_correct : vm.isCorret,
        start_test_date : vm.$cookies.get("startTestDate")
      }

      // 사용자가 선택한 답 저장
      await axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/userSelectAnswer",
        data : postData,
      })
        .then({})
        .catch(() => {
          this.errorModalContent();
        });
      
      let remainingTime = vm.remainingTimeCal();
      clearInterval(vm.timer);
      
      // 합격 여부 확인하는 부분
      const scoreSum = vm.Score.reduce((acc, val) => acc + val, 0);
      const qNumberSum = vm.subjectQnumber.reduce((acc, val) => acc + val, 0);
      const isPass = (scoreSum >= (qNumberSum * 0.6)) && vm.Score.every((score, index) => score >= vm.subjectQnumber[index] * 0.4);

      // 불합격일 경우
      if(isPass === true) {
        vm.Pass = '1';
      }

      const recordData = {
        mode : vm.$cookies.get('mode'),
        remaining_time : remainingTime,
        start_test_date : vm.$cookies.get('startTestDate'),
        member_id : vm.memberId,
        exam_date : vm.$cookies.get('exam_date'),
        detail_license_name : vm.$cookies.get('detail_license'),
        license_name : vm.$cookies.get('license'),
        subject_count : JSON.parse(this.$cookies.get('selectedSubjects')).length,
        question_count : vm.totalQuestionList.length,
        is_pass : vm.Pass
      };

      // 응시 시험 기록 저장
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/storeExamRecord",
        data : recordData,
      })
        .then(response => {
          if(response.data > 0) {
            vm.$router.push({ name: 'TestResult' });  
          } else {
            this.errorModalContent();
          }
        })
        .catch(()=>{
          this.errorModalContent();
        })
    },
    // 다음문제 버튼 클릭
    nextQuestion() {
      const vm = this;
      if(vm.selectedOption === '') {
        return;
      }
      vm.subjectQnumberSum += 1;
      if(vm.totalQuestionList[vm.Qnumber-1].answer === vm.selectedOption){
        vm.isCorret = 1; // 정답일때
        vm.correctSum += vm.isCorret;
      } else {
        vm.isCorret = 0; // 오답일때
      }
      const postData = {
        select_answer : vm.selectedOption,
        member_id : vm.memberId,
        question_idx : vm.totalQuestionList[vm.Qnumber-1].question_idx,
        is_correct : vm.isCorret,
        start_test_date : this.$cookies.get("startTestDate")
      }

      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/userSelectAnswer",
        data : postData,
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
        });
      
      vm.Qnumber += 1; // 문제번호
      vm.Question = vm.totalQuestionList[vm.Qnumber-1].question;
      vm.Option1 = vm.totalQuestionList[vm.Qnumber-1].option1;
      vm.imgCheckOP1 = vm.Option1.startsWith("https://ifh") ? false : true;
      vm.Option2 = vm.totalQuestionList[vm.Qnumber-1].option2;
      vm.imgCheckOP2 = vm.Option2.startsWith("https://ifh") ? false : true;
      vm.Option3 = vm.totalQuestionList[vm.Qnumber-1].option3;
      vm.imgCheckOP3 = vm.Option3.startsWith("https://ifh") ? false : true;
      vm.Option4 = vm.totalQuestionList[vm.Qnumber-1].option4;
      vm.imgCheckOP4 = vm.Option4.startsWith("https://ifh") ? false : true;
      if(vm.Option4.startsWith("https://ifh")){
        vm.imgCheckOP4 = false;
      }
      if(vm.totalQuestionList[vm.Qnumber-1].subject_name !== vm.Subject) {
        vm.Subject = vm.totalQuestionList[vm.Qnumber-1].subject_name
        vm.Score.push(vm.correctSum);
        vm.subjectQnumber.push(vm.subjectQnumberSum);
        vm.correctSum = 0;
        vm.subjectQnumberSum = 0;
      }
      vm.imagePath = vm.totalQuestionList[vm.Qnumber -1].image;
      vm.selectedOption = '';
      vm.isCorret = 0;
    },

    // 타이머 시작
    startTimer() {
      this.timer = setInterval(() => {
        if (this.timeRemaining > 0) {
          this.timeRemaining--;
        } else {
          clearInterval(this.timer);
          this.viewResult();
        }
      }, 1000);
    },

    // 시험 문제 불러오는 함수
    loadExam() {
      this.DetailLicense = this.$cookies.get("detail_license");
      this.TestDate = this.$cookies.get("exam_date");
      const data = {
        detail_license_name : this.DetailLicense,
        exam_date : this.TestDate,
        subject : this.$cookies.get("selectedSubjects"),
        license_name : this.$cookies.get('license')
      };
      axios({
        method : 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/testModeLoadExam",
        data: data,
      })
        .then(response => {
          this.totalQuestionList = response.data.exam;
          this.Subject =  this.totalQuestionList[0].subject_name; // 과목명
          this.Qnumber = 1; // 문제번호
          this.Question = this.totalQuestionList[0].question;
          this.Option1 = this.totalQuestionList[0].option1;
          this.imgCheckOP1 = this.Option1.startsWith("https://ifh") ? false : true;
          this.Option2 = this.totalQuestionList[0].option2;
          this.imgCheckOP2 = this.Option2.startsWith("https://ifh") ? false : true;
          this.Option3 = this.totalQuestionList[0].option3;
          this.imgCheckOP3 = this.Option3.startsWith("https://ifh") ? false : true;
          this.Option4 = this.totalQuestionList[0].option4;
          this.imgCheckOP4 = this.Option4.startsWith("https://ifh") ? false : true;
          this.totalQNumber = this.totalQuestionList.length;
          this.imagePath = this.totalQuestionList[0].image;
          const currentDate = new Date();
          const formattedDate = `${currentDate.getFullYear()}-${currentDate.getMonth() + 1}-${currentDate.getDate()} ${currentDate.getHours()}:${currentDate.getMinutes()}:${currentDate.getSeconds()}`;
          this.$cookies.set("startTestDate", formattedDate);
          this.timeRemaining = response.data.timeRemaining * 60; // 
          // 타이머
          this.startTimer();
          this.selectedOption = '';
          this.isCorret = 0;
        })
        .catch(()=>{
          this.errorModalContent();
        });
    },

    errorModalContent() {
      this.title = '에러 발생'
      this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
      this.isErrorModal = true;
    },
    
  },
  mounted() {
    // 시험 문제 불러오기
    this.loadExam();
    if((sessionStorage.getItem('JSESSIONID') != null && sessionStorage.getItem('USER_ID') != null)) {
      this.memberId = sessionStorage.getItem('USER_ID');
    } else {
      this.memberId = localStorage.getItem('GUEST');
    }
  },
  beforeUnmount() {
    clearInterval(this.timer);
  }
}
</script>

<style scoped>
.infoo {
  display: flex;
  justify-content: right;
}

.practive_mode_container {
  top: 6%;
  height: 94%;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  position: relative;
}

.scrollable {
  overflow-y: auto;

}

.scrollable::-webkit-scrollbar {
  display: none;
}

.select_test {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  font-size: 1.5em;
  font-weight: 500;
}

.current_sub {
  font-size: 1.2em;
}

.current_Q {
  margin: 0.7em;
  padding: 0.3em;
  box-sizing: border-box;
  height: 95%;
  z-index: 0;
}

.A_wrap {
  display: flex;
}

@keyframes blink-effect {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0;
  }
}

.last_Q {
  margin-left: -1.7em;
  position: absolute;
  top: -1.9em;
  color: red;
  font-weight: bold;
  animation: blink-effect 1s ease-in-out infinite;
  z-index: 10;
}

.btn_collection {
  display: flex;
  width: 100%;
  border-radius: 5px;
  align-items: center;
  justify-content: center;
  text-align: center;
  background-color: #353535;
  color: #fff;
  padding: 10px;
  margin-top: 10px;
}

/* COMMON STYLES*/
.popup-container {
  display: flex;
  height: 5%;
  align-items: center;
  margin-top: .5em;
}

.popup {
  margin: 0em .5em 0em .5em;
  box-shadow: 4px 4px 10px -10px rgba(0, 0, 0, 1);
  width: auto;
  height: 70%;
  padding: .3em;
  justify-content: left;
  align-items: center;
  display: flex;
  border-radius: 4px;
  font-weight: 300;
  font-size: .7em;
}

/* INFO */

.info-popup {
  background-color: #eff6ff;
  border: solid 1px #1d4ed8;
}

.info-icon path {
  fill: #1d4ed8;
}

.info-message {
  color: #1d4ed8;
}

.radio-input input {
  display: none;
}

.radio-input {
  display: flex;
  flex-direction: column;
  padding: 12px;
  background: #fff;
  color: #000;
  border-radius: 10px;
  box-shadow: 0px 87px 78px -39px rgba(0, 0, 0, 0.4);
  width: 100%;
  position: fixed;
  bottom: -100%;
  transition: bottom .5s ease-in-out;
  /* height: 53%; */
  /* overflow-y: auto; */
  box-shadow: 0px -1px 1px 0px rgba(0, 0, 0, 0.1);

}

.radio-input.is-visible {
  bottom: 0;
}

.info {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toggle-btn {
  position: relative;
  width: 3em;
  height: 3em;
  margin-right: 2.7em;
  margin-bottom: 1em;
}

.toggle-btn::after {
  position: absolute;
  left: 35px;
  top: 20px;
  content: '';
  width: 1em;
  /* 사이즈 */
  height: 1em;
  /* 사이즈 */
  border-top: 5px solid #000;
  /* 선 두께 */
  border-right: 5px solid #000;
  /* 선 두께 */
  transform: rotate(315deg);
  /* 각도 */
}

.question {
  color: rgb(49, 49, 49);
  font-size: 1rem;
  line-height: 1rem;
  font-weight: 800;
}

.steps {
  background-color: rgb(0, 0, 0);
  padding: 4px;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
  line-height: 12px;
  font-weight: 600;
}

.radio-input label {
  display: flex;
  background-color: #fff;
  padding: 14px;
  margin: 8px 0 0 0;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: 1px solid rgba(187, 187, 187, 0.164);
  color: #000;
  transition: .3s ease;
}

.radio-input img {
  margin: auto;
}

.radio-input label:hover {
  background-color: #eff6ff;
  border: solid 1px #1d4ed8;
}

.result {
  margin-top: 10px;
  font-weight: 600;
  font-size: 12px;
  display: none;
  transition: display .4s ease;
}
.timer-container{
  position: absolute;
  right: 1em;
  top: -1.9em;
  color: black;
  font-weight: bold;
  z-index: 10;
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