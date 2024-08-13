<template>
  <TopBar />
  <div class="practive_mode_container">
    <div class="popup-container">
      <div class="popup info-popup">
        <div class="info-message">{{ selectedOption2 }}</div>
      </div>
      <div class="popup info-popup">
        <div class="info-message">{{ selectedOption3 }}</div>
      </div>
      <div class="popup info-popup">
        <div class="info-message">{{ problems[currentProblemIndex].subject_name }}</div>
      </div>
    </div>
    <div class="current_Q">
      <p>{{ currentProblemIndex + 1 }}. {{ problems[currentProblemIndex].question }}</p>
      <img :src="problems[currentProblemIndex].image">
    </div>
    <span class="toggle-btn" @click="toggleBottomSheet"></span>
    <div class="radio-input" :class="{ 'is-visible': isBottomSheetVisible }">
      <div class="info">
        <span class="steps" @click="showExplanation">해설보기</span>
        <span class="close-explanation" @click="toggleBottomSheet">&times;</span>
      </div>
      <div class="scrollable">
        <input type="radio" id="value-1" name="value-radio" value="option1" @change="checkAnswer('option1')"
          v-model="selectedAnswer">
        <label
          :class="{ 'correct': selectedAnswer === 'option1' && isCorrectAnswer, 'incorrect': selectedAnswer === 'option1' && !isCorrectAnswer }"
          for="value-1">
          <div v-if="imgCheckOP1">{{ problems[currentProblemIndex].option1 }}</div>
          <img v-else :src="problems[currentProblemIndex].option1">
        </label>

        <input type="radio" id="value-2" name="value-radio" value="option2" @change="checkAnswer('option2')"
          v-model="selectedAnswer">
        <label
          :class="{ 'correct': selectedAnswer === 'option2' && isCorrectAnswer, 'incorrect': selectedAnswer === 'option2' && !isCorrectAnswer }"
          for="value-2">
          <div v-if="imgCheckOP2">{{ problems[currentProblemIndex].option2 }}</div>
          <img v-else :src="problems[currentProblemIndex].option2">
        </label>

        <input type="radio" id="value-3" name="value-radio" value="option3" @change="checkAnswer('option3')"
          v-model="selectedAnswer">
        <label
          :class="{ 'correct': selectedAnswer === 'option3' && isCorrectAnswer, 'incorrect': selectedAnswer === 'option3' && !isCorrectAnswer }"
          for="value-3">
          <div v-if="imgCheckOP3">{{ problems[currentProblemIndex].option3 }}</div>
          <img v-else :src="problems[currentProblemIndex].option3">
        </label>

        <input type="radio" id="value-4" name="value-radio" value="option4" @change="checkAnswer('option4')"
          v-model="selectedAnswer">
        <label
          :class="{ 'correct': selectedAnswer === 'option4' && isCorrectAnswer, 'incorrect': selectedAnswer === 'option4' && !isCorrectAnswer }"
          for="value-4">
          <div v-if="imgCheckOP4">{{ problems[currentProblemIndex].option4 }}</div>
          <img v-else :src="problems[currentProblemIndex].option4">
        </label>
      </div>
      <div class="btn_collection" v-if="currentProblemIndex + 1 < 20" @click="nextQuestion">
        다음 문제 >
      </div>
      <div class="btn_collection" @click="viewResult" v-if="currentProblemIndex + 1 == 20">
        {{ Subject }} 점수 보기
      </div>
    </div>
    <div class="last_Q" v-if="currentProblemIndex + 1 == 20">
      <p>현재 과목의 마지막 문제입니다.</p>
    </div>

  </div>

  <!---------해설보기 모달 창--------->
  <div class="modal" v-if="isModalVisible">
    <div class="modal-content">
      <p>{{ }}</p>
      <span class="close" @click="closeModal">&times;</span>
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
  name: "PracticeMode",
  components: {
    TopBar
  },
  data() {
    return {
      problems: [{}],
      currentProblemIndex: 0,
      currentSubjectIndex: 0,
      selectedOption1: '',
      selectedOption2: '',
      selectedOption3: '',
      mode: '',
      selectedSubjects: [],
      isBottomSheetVisible: true,
      isModalVisible: false,
      currentProblem: {},

      correctAnswer: '', // 정답을 저장
      selectedAnswer: '', // 사용자가 선택한 답변
      isCorrectAnswer: false, // 정답 여부
      isAnswerSelected: false, // 답변이 선택되었는지 여부
      imgCheckOP1: true,
      imgCheckOP2: true,
      imgCheckOP3: true,
      imgCheckOP4: true,

      memberId: null,
      start_test_date: null,
      is_correct: null,

      isErrorModal: false, // 에러 모달 v-if
      modalTitle: '',
      modalMsg: ''
    };
  },
  methods: {
    // 한문제 풀때마다 푼 기록 저장하는 함수
    async storeProblem() {
      if (this.isCorrectAnswer == true) {
        this.is_correct = 1; // 정답
      } else {
        this.is_correct = 0; // 오답
      }
      const postData = {
        select_answer: this.selectedAnswerValue,
        member_id: this.memberId,
        question_idx: this.problems[this.currentProblemIndex].question_idx,
        is_correct: this.is_correct,
        start_test_date: this.$cookies.get("start_test_date")
      }

      await axios({
        method: 'post',
        header: { 'Content-Type': 'application/json; charset=UTF-8' },
        url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/userSelectAnswer",
        data: postData,
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
    },

    // 사용자가 선택한 모든문제 로드하는 함수
    loadProblems() {
      const criteria = {
        license: this.$cookies.get('license'),
        detail_license_name: this.selectedOption2,
        exam_date: this.selectedOption3,
        subject_name: this.selectedSubjects[this.currentSubjectIndex],
        member_id: this.memberId,
        start_test_date: this.start_test_date
      };

      axios.post('https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/mode/practiceModeLoadExam', criteria)
        .then(response => {
          this.problems = [],
            this.problems = response.data;
          this.currentProblemIndex = 0;
          this.loadCurrentProblem();
        })
        .catch(() => {
          this.errorModalContent();
        });
    },

    // 사용자가 선택한 문제중 현재과목의 문제를 로드하는 함수
    loadCurrentProblem() {
      if (this.problems.length > 0) {
        this.currentProblem = this.problems[this.currentProblemIndex];
        this.correctAnswer = this.currentProblem.answer; // 서버에서 가져온 정답
        this.selectedAnswer = ''; // 새 문제를 로드할 때 선택된 답변 초기화
        this.isAnswerSelected = false;
        this.isCorrectAnswer = false;

        this.imgCheckOP1 = this.currentProblem.option1.startsWith("https://ifh") ? false : true;
        this.imgCheckOP2 = this.currentProblem.option2.startsWith("https://ifh") ? false : true;
        this.imgCheckOP3 = this.currentProblem.option3.startsWith("https://ifh") ? false : true;
        this.imgCheckOP4 = this.currentProblem.option4.startsWith("https://ifh") ? false : true;
      }
    },

    // 다음 문제를 로드하는 함수
    nextQuestion() {
      if (!this.isAnswerSelected) {
        return;
      }

      if (this.currentProblemIndex < this.problems.length - 1) {
        this.storeProblem();
        this.currentProblemIndex++;
        this.loadCurrentProblem();
        this.selectedAnswer = ''; // 라디오 버튼 초기화

      } else {
        this.viewResult();
      }

    },

    viewResult() {
      this.storeProblem();
      this.$router.push({ name: 'PracticeResult' });
    },

    // 사용자가 선택한 값과 정답을 검증하는 함수
    checkAnswer(option) {
      this.selectedAnswer = option;
      this.selectedAnswerValue = this.currentProblem[option]; // option의 실제 값
      this.isAnswerSelected = true;

      const answerText = this.currentProblem[option];
      this.isCorrectAnswer = answerText == this.correctAnswer;
    },

    // 해설 보기
    showExplanation() {
      this.isModalVisible = true;
    },

    // 해설 모달 닫기
    closeModal() {
      this.isModalVisible = false;
    },

    // 바텀시트 동작 기능
    toggleBottomSheet() {
      this.isBottomSheetVisible = !this.isBottomSheetVisible;
    },

    errorModalContent() {
      this.title = '에러 발생'
      this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
      this.isErrorModal = true;
    },
  },

  mounted() {
    if ((sessionStorage.getItem('JSESSIONID') != null && sessionStorage.getItem('USER_ID') != null)) {
      this.memberId = sessionStorage.getItem('USER_ID');
    } else {
      this.memberId = localStorage.getItem('GUEST');
    }

    this.selectedOption1 = this.$cookies.get('license');
    this.selectedOption2 = this.$cookies.get('detail_license');
    this.selectedOption3 = this.$cookies.get('exam_date');
    this.mode = this.$cookies.get('mode');
    const selectedSubjects = this.$cookies.get('selectedSubjects');
    this.selectedSubjects = JSON.parse(selectedSubjects);
    this.start_test_date = this.$cookies.get('start_test_date');

    this.currentSubjectIndex = 0;

    this.loadProblems();
  },
};
</script>

<style scoped>
.practive_mode_container {
  top: 6%;
  position: fixed;
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
  position: absolute;
  right: 1em;
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

.modal-content {
  background-color: #fefefe;
  margin: auto;
  padding: 20px;
  border: 1px solid #888;
  border-radius: 5px;
  width: 80%;
  max-width: 500px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  display: flex;
  ;
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

.radio-input label:hover {
  background-color: rgba(24, 24, 24, 0.13);
  border: 1px solid #bbb;
}

/* 정답 및 오답 스타일 */
.correct {
  border-color: rgb(22, 245, 22);
  color: rgb(16, 184, 16);
}

.incorrect {
  border-color: red;
  color: red;
}

.radio-input input:checked+label.correct {
  border-color: rgb(22, 245, 22);
  color: rgb(16, 184, 16);
}

.radio-input input:checked+label.incorrect {
  border-color: red;
  color: red;
}

/* 라디오 버튼 스타일 */
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

.radio-input label:hover {
  background-color: rgba(24, 24, 24, 0.13);
  border: 1px solid #bbb;
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