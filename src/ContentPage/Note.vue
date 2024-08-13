<template>
    <TopBar />
    <div class="group">
        <div class="circle-container">
            <div class="circle" id="CIRCLE">
                <div class="inner-circle">
                    <span class="label">정답률</span>
                    <span class="value">{{ correctRate }}%</span>
                </div>
            </div>
        </div>
        <div class="header-container">
            <div>총 문제</div>
            <div>{{ totalQCount }}개</div>
        </div>

        <div class="header-container">
            <div>틀린 문제</div>
            <div>{{ wrongCount }}개</div>
        </div>

        <div class="header-container">
            <div>남은 시간</div>
            <div>{{ remainingTime }}</div>
        </div>
    </div>
    <div class="table-container">
        <div v-for="(item, index) in totalQuestionList" :key="index" class="table-row">
            
            <div class="history_info">
                <div class="popup-container">
                    <div class="popup info-popup">
                        <div class="info-message">{{ item.qualification }}</div>
                    </div>
                    <div class="popup info-popup">
                        <div class="info-message">{{ item.examDate }}</div>
                    </div>
                    <div class="popup info-popup">
                        <div class="info-message">{{ item.subject }}</div>
                    </div>
                    <img v-if="item.is_correct" class="collectImg" src="@/assets/collectanswer.png" alt="">
                    <img v-else class="wrongImg" src="@/assets/wronganswer.png" alt="">
                </div>
                <div class="license_info">
                    <p>{{ index + 1 }}.&nbsp;{{ item.question }}</p>
                    <img v-if="item.imagePath != ''" :src="item.imagePath">
                </div>
                <div class="detail_info" >
                    <img :class="getClassImg(item, item.option1)" v-if="item.option1.includes('https://ifh.cc')" :src="item.option1">
                    <div :class="getClass(item, item.option1)" v-else >{{ 1 }}.&nbsp;{{ item.option1 }}</div>
                </div>
                <div class="detail_info" >
                    <img :class="getClassImg(item, item.option2)" v-if="item.option2.includes('https://ifh.cc')" :src="item.option2">
                    <div :class="getClass(item, item.option2)" v-else >{{ 2 }}.&nbsp;{{ item.option2 }}</div>
                </div>
                <div class="detail_info" >
                    <img :class="getClassImg(item, item.option3)" v-if="item.option3.includes('https://ifh.cc')" :src="item.option3">
                    <div :class="getClass(item, item.option3)" v-else >{{ 3 }}.&nbsp;{{ item.option3 }}</div>
                </div>
                <div class="detail_info" >
                    <img :class="getClassImg(item, item.option4)" v-if="item.option4.includes('https://ifh.cc')" :src="item.option4">
                    <div :class="getClass(item, item.option4)" v-else>{{ 4 }}.&nbsp;{{ item.option4 }}</div>
                </div>
                <div class="description-container">
                    <div @click="openDescription(index)">해설보기</div>
                    <div class="description" v-show="isOpenDescription[index]">
                        <div class="description-title">해설</div>
                        <p >
                            <text class="textformat">{{ item.description }}</text>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!--- 에러 모달 -->
    <div class="modal" v-if="isShowModal">
        <div class="cookies-card2" >
            <p class="cookie-heading2">문제풀이 불러오기</p>
            <p class="cookie-para2">
                {{ modalMsg }}
            </p>
            <div class="button-wrapper2">
                <button class="accept2 cookie-button2" @click="modalCheck()">확인</button>
            </div>
        </div>
    </div>
    <BottomBar />
</template>


<script>
import TopBar from '../components/TopBar.vue';
import BottomBar from '../components/BottomBar.vue';
import axios from 'axios';

export default {
    name : 'IncorrectNote',
    components: {
        TopBar,
        BottomBar
    },
    data() {
        return {
            totalQuestionList: [],
            totalQCount : 0,
            wrongCount : 0,
            remainingTime : '',
            correctRate : 0,
            isShowModal : false,
            modalMsg : '',
            member_id : sessionStorage.getItem('USER_ID'),
            isOpenDescription : [],
            exam_record_idx : '',
        };
    },
    computed: {
       
    },
    methods: {
        // 해설 보기 클릭시 해설 보여주기
        openDescription(index) {
            const vm = this;
            vm.isOpenDescription[index] = vm.isOpenDescription[index] == false ? true : false;
        },
        // 맞은 문제
        getClass(item, optionIndex) {
            if (item.is_correct && item.select_answer == optionIndex) {
                return 'correct';
            } else if (!item.is_correct && item.select_answer == optionIndex) {
                return 'wrong';
            } else if (!item.is_correct && item.answer == optionIndex) {
                return 'correct';
            } else {
                return '';
            }
        },

        // 맞은 문제 이미지 보여주기
        getClassImg(item, optionIndex) {
            if (item.is_correct && item.select_answer == optionIndex) {
                return 'correct-Img';
            } else if (!item.is_correct && item.select_answer == optionIndex) {
                return 'wrong-Img';
            } else if (!item.is_correct && item.answer == optionIndex) {
                return 'correct-Img';
            } else {
                return '';
            }
        },

        modalCheck() {
            this.isShowModal = false;
        },

        incorrectNoteLoad(){
            const vm = this;
            const postData = {
                member_id : vm.member_id,
                exam_record_idx : vm.exam_record_idx
            };
            axios({
                method : 'post',
                header: { 'Content-Type': 'application/json; charset=UTF-8' },
                url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/incorrectNote/incorrectNoteLoad",
                data: postData,
            })
                .then(response => {
                    if(response.data != null) {
                        vm.totalQuestionList = response.data.question;
                        vm.totalQCount = response.data.exam_record.question_count;
                        vm.remainingTime = response.data.exam_record.remaining_time;
                        let count = 0; // 정답 개수
                        vm.totalQuestionList.forEach((item) => {
                            count += item.is_correct; 
                        });
                        vm.wrongCount = vm.totalQCount - count;
                        vm.correctRate = ((count / vm.totalQCount) * 100).toFixed(1);
                        const circleStyle = document.getElementById('CIRCLE');
                        circleStyle.style.background = `conic-gradient(#5471ff ${((vm.correctRate / 100) * 360).toFixed(1)}deg, rgb(224, 224, 224) 0deg)`;
                        // totalQuestionList의 길이에 맞춰 isOpenDescription 배열을 초기화
                        vm.isOpenDescription = vm.totalQuestionList.map(() => false);

                    } else {
                        vm.modalMsg = '문제 정보를 불러오는데 오류가 발생하였습니다. 잠시후 다시 시도해주세요.'
                        vm.isShowModal = true;
                    }
                })
                .catch(() => {
                    vm.modalMsg = '잠시후 다시 시도해주세요';
                    vm.isShowModal = true;
                })
        }
    },
    mounted() {
        this.exam_record_idx = this.$route.query.where;
        this.incorrectNoteLoad(); // 응시한 문제 로드
    }
};
</script>


<style scoped>
@import "@/css/common.css";

.correct {
  border-color: #70CA77;
  color: #70CA77;
}

.wrong {
  border-color: #F00;
  color: #F00;
}

.correct-Img {
    opacity: 0.5;
    border-radius: 5px;
    border: solid 2px;
    border-color: #70CA77;
    color: #70CA77;
}

.wrong-Img {
    opacity: 0.5;
    border-radius: 5px;
    border: solid 2px;
    border-color: red;
    color: red;
}

.table-container {
    top: 16%;
    position: fixed;
    overflow-y: auto;
    /* 컨텐츠가 넘칠 경우 스크롤 */
    height: 78%;
    /* 전체 높이에서 상단바와 하단바 높이 제외 */
    display: flex;
    flex-direction: column;
    /* 세로 중앙 정렬 (필요에 따라 조정) */
    width: 100%;
    background-color: rgb(224, 224, 224);
    /* justify-content: center; */
    align-items: center;
    -ms-overflow-style: none;
    /* 인터넷 익스플로러 */
    scrollbar-width: none;
    /* 파이어폭스 */
}

/* ( 크롬, 사파리, 오페라, 엣지 ) 동작 */
.table-container::-webkit-scrollbar {
    display: none;
}

/* .table-cell {
    flex: 1;
    text-align: center;

} */

.table-row {
    display: flex;
    justify-content: space-between;
    width: 95%;
    border-radius: 5px;
    background-color: #f9f9f9;
    padding: 0.6em;
    margin: .3em 0 .3em 0;
    justify-content: center;
    align-items: center;
    font-size: .8em;
}

.history_info {
    width: 92%;
    color: #828595;
}

.license_info {
    font-weight: 700;
    font-size: 0.9em;
    margin-bottom: .4em;
    color: #282f4a;
}

.collectImg {
    margin-left: auto;
    width: 2em;
    height: 70%;
}

.wrongImg {
    margin-left: auto;
    width: 1.3em;
    height: 70%;
    margin-right: 0.28em; 
}
.detail_info {
    font-size: 1em;
    margin-bottom: .3em;
}

.date_info {
    font-size: .7em;
}

.delete_history {
    width: 8%;
}

strong {
    color: #7cdbd2;
}



/* 검색창 디자인 */
.group {
    display: flex;
    line-height: 28px;
    align-items: center;
    justify-content: center;
    position: fixed;
    top: 6%;
    width: 100%;
    /* padding: 0em 1em; */
    /* max-width: 500px; */
    height: 10%;
}

.input {
    width: 100%;
    height: 40px;
    line-height: 28px;
    padding: 0 1rem;
    padding-left: 2.5rem;
    border: 2px solid transparent;
    border-radius: 8px;
    outline: none;
    background-color: #e0e0e0;
    color: #0d0c22;
    transition: 0.3s ease;
}

.input::placeholder {
    color: #9e9ea7;
}

.input:focus {
    outline: none;
    border-color: rgba(0, 48, 73, 0.4);
    background-color: #fff;
    box-shadow: 0 0 0 4px rgb(0 48 73 / 10%);
}

.icon_search {
    position: absolute;
    left: 2rem;
    fill: #9e9ea7;
    width: 1rem;
    height: 1rem;
}

/* COMMON STYLES*/
.popup-container {
  display: flex;
  height: 5%;
  align-items: center;
  margin-top: .2em;
  margin-bottom: .5em;
}

.popup {
  margin: 0em .5em .5em 0em;
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

/* 정답률 */
.circle-container {
    width: 25%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.circle {
    font-size: 10px;
    width: 50px;
    height: 50px;
    display: flex;
    border-radius: 50%;
    position: relative;
    /* background: conic-gradient(#5471ff 108deg, rgb(224, 224, 224) 0deg); */
}

.inner-circle {
    width: 90%;
    height: 90%;
    border-radius: inherit;
    background-color: #eff6ff;
    margin: auto;
    display: flex;
    color: #1d4ed8;
    justify-content: center;
    flex-direction: column;
}

.inner-circle .label {
    font-size: 8px;
    position: relative;
    top: 6px;
    text-align: center;
}

.inner-circle .value {
    position: relative;
    bottom: 6px;
    font-size: 12px;
    font-weight: bold;
    display: block;
    text-align: center;
}

.header-container {
    width: 25%;
    text-align: center;
    padding: 10px 15px;
    font-family: Arial, sans-serif;
}

.header-container div {
    font-size: 0.8em;
    font-weight: bold;
    color: #1d4ed8;
    text-align: center;
}

.header-container div:first-child {
    color: #ababac;
    font-size: 0.5em;
    font-weight: 0;
    text-align: center;
}

/* 해설 보기 */
.description-container {
    margin: 12px 0 7px 0;
    font-size: 0.7em;
    text-align: center;
}

.description {
    text-align: left;
    width: 100%;
}

.description-title{
    color: #0d0c22;
    font-weight: bold;
    font-size: 1em;
}

.description p {
    font-size: 0.8em;
    margin-top: 5px;
    text-align: left;
    width: 100%;
    height: auto;
    color: #ababac;
    border: none;
    outline: none;
}

.textformat {
    white-space: pre-wrap;
}
</style>