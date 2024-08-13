<template>
    <TopBar />
    <div class="content_container">
        <div class="selectbox_wrap">
            <p>자격을 선택하세요.</p>
            <div class="select">
                <div class="selected" @click="toggleDropdown('first')">
                    {{ selectedOption1 }}
                    <svg xmlns="http://www.w3.org/2000/svg" class="arrow">
                        <path d="M7 5l5 5 5-5H7z" />
                    </svg>
                </div>
                <ul class="options" v-if="dropdownOpen === 'first'">
                    <li v-for="option in options1" :key="option" @click="selectOption(option, 'first')">{{ option }}
                    </li>
                </ul>
            </div>
            <p>세부 자격을 선택하세요.</p>
            <div class="select">
                <div class="selected" @click="toggleDropdown('second')">
                    {{ selectedOption2 }}
                    <svg xmlns="http://www.w3.org/2000/svg" class="arrow">
                        <path d="M7 5l5 5 5-5H7z" />
                    </svg>
                </div>
                <ul class="options" v-if="dropdownOpen === 'second'">
                    <li v-for="option in options2" :key="option" @click="selectOption(option, 'second')">{{ option }}
                    </li>
                </ul>
            </div>
            <p>시험 일정을 선택하세요.</p>
            <div class="select">
                <div class="selected" @click="toggleDropdown('third')">
                    {{ selectedOption3 }}
                    <svg xmlns="http://www.w3.org/2000/svg" class="arrow">
                        <path d="M7 5l5 5 5-5H7z" />
                    </svg>
                </div>
                <ul class="options" v-if="dropdownOpen === 'third'">
                    <li v-for="option in options3" :key="option" @click="selectOption(option, 'third')">{{ option }}
                    </li>
                </ul>
            </div>
        </div>
        <div class="mydict" v-if="isShow">
            <div>
                <label>
                    <input type="radio" name="radio" value="연습" v-model="mode">
                    <span>연습 모드</span>
                </label>

                <label>
                    <input type="radio" name="radio" value="시험" v-model="mode">
                    <span>시험 모드</span>
                </label>

            </div>
        </div>
        <div class="subject_wrap" v-show="isShow">
            <div v-for="(subject, index) in subjects" :key="index">
                <input :id="'checkBox' + index" type="checkbox" :class="'sub' + (index + 1)"
                    :value="subject.subject_name" :checked="selectedSubjects.includes(subject.subject_name)"
                    @change="toggleSubject(subject.subject_name)">
                <label :for="'checkBox' + index">{{ subject.subject_number }}과목 </label>: <label
                    :for="'checkBox' + index">{{ subject.subject_name }}</label>(<label :for="'checkBox' + index">{{
                    subject.question_total_count + "문항)" }}</label>
            </div>
        </div>
        <button class="go_solve" v-show="isShow" @click="goSolve">문제 풀기</button>
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

    <BottomBar />
</template>



<script>
import TopBar from '@/components/TopBar.vue';
import BottomBar from '@/components/BottomBar.vue'
import axios from 'axios';
export default {
    name: "CategoryChoice",
    components: {
        TopBar, BottomBar
    },
    data() {
        return {
            mode: null,
            dropdownOpen: null,
            selectedOption1: '선택',
            selectedOption2: '선택',
            selectedOption3: '선택',
            options1: [],
            options2: [],
            options3: [],
            subjects: [],
            isShow: false,
            selectedSubjects: [],

            isErrorModal: false, // 에러 모달 v-if
            modalTitle: '',
            modalMsg: ''
        };
    },
    methods: {
        toggleMode(selectMode) {
            if (this.mode === selectMode) {
                this.mode = null;
            } else {
                this.mode = selectMode;
            }
        },
        toggleDropdown(dropdown) {
            this.dropdownOpen = this.dropdownOpen === dropdown ? null : dropdown;
        },
        selectOption(option, dropdown) {
            if (dropdown === 'first') {
                this.selectedOption1 = option;
                this.selectedOption2 = '선택'; // 세부 자격 초기화
                this.selectedOption3 = '선택'; // 시험 일정 초기화
                this.options2 = []; // 세부 자격 옵션 초기화
                this.options3 = []; // 시험 일정 옵션 초기화
                this.subjects = []; // 과목 정보 초기화
                this.isShow = false; // 과목 정보 표시 여부 초기화
                this.mode = null; // 연습모드, 시험모드 초기화
                this.loadDetailLicense(option); // 자격을 선택할 때 세부 자격을 로드
            } else if (dropdown === 'second') {
                this.selectedOption2 = option;
                this.selectedOption3 = '선택'; // 시험 일정 초기화
                this.options3 = []; // 시험 일정 옵션 초기화
                this.subjects = []; // 과목 정보 초기화
                this.isShow = false; // 과목 정보 표시 여부 초기화
                this.mode = null; // 연습모드, 시험모드 초기화
                this.loadSessionQuestion(option); // 세부자격을 선택할 때 시험 일정을 로드
            } else if (dropdown === 'third') {
                this.selectedOption3 = option;
                this.loadSubjects(); // 시험 일정을 선택할 때 과목 정보를 로드
            }
            this.dropdownOpen = null;
        },

        toggleSubject(subjectName) {
            if (this.selectedSubjects.includes(subjectName)) {
                this.selectedSubjects = this.selectedSubjects.filter(sub => sub !== subjectName);
            } else {
                this.selectedSubjects.push(subjectName);
            }
        },

        goSolve() {
            const now = new Date();
            const formattedDateTime = `${now.getFullYear()}-${now.getMonth() + 1}-${now.getDate()} ${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;

            if (!this.mode) {
                this.isErrorModal = true;
                this.modalTitle = '모드 선택';
                this.modalMsg = '모드를 선택해주세요.';
                return;
            }

            if (this.selectedSubjects.length === 0) {
                this.isErrorModal = true;
                this.modalTitle = '과목 선택';
                this.modalMsg = '최소 1과목 이상 선택해주세요.';
                return;
            }

            // 연습 모드일 경우
            if (this.mode === '연습') {
                this.$cookies.set('license', this.selectedOption1);
                this.$cookies.set('detail_license', this.selectedOption2);
                this.$cookies.set('exam_date', this.selectedOption3);
                this.$cookies.set('mode', this.mode);
                this.$cookies.set('selectedSubjects', JSON.stringify(this.selectedSubjects));
                this.$cookies.set('start_test_date', formattedDateTime);
                this.$cookies.set('subject_count', this.selectedSubjects.length);
                this.$router.push({ name: 'PracticeMode' });

                // 시험 모드일 경우
            } else if (this.mode === '시험') {
                this.$cookies.set('license', this.selectedOption1);
                this.$cookies.set('detail_license', this.selectedOption2);
                this.$cookies.set('exam_date', this.selectedOption3);
                this.$cookies.set('mode', this.mode);
                this.$cookies.set('selectedSubjects', JSON.stringify(this.selectedSubjects));
                this.$router.push({ name: 'TestMode' });
            }
        },

        loadLicense() {
            axios({
                method: 'post',
                headers: { 'Content-Type': 'application/json; charset=UTF-8' },
                url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/solveProblem/loadLicense",
            })
                .then(response => {
                    this.options1 = response.data.map(item => item.license_name);  // 여기에 데이터 매핑을 추가
                })
                .catch(() => {
                    this.errorModalContent;
                });
        },

        loadDetailLicense(license) {
            axios({
                method: 'post',
                headers: { 'Content-Type': 'application/json; charset=UTF-8' },
                url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/solveProblem/loadDetailLicense",
                data: { license: license }
            })
                .then(response => {
                    this.options2 = response.data.map(item => item.detail_license_name);  // 여기에 데이터 매핑을 추가
                })
                .catch(() => {
                    this.errorModalContent;
                });
        },

        loadSessionQuestion(detailLicense) {
            axios({
                method: 'post',
                headers: { 'Content-Type': 'application/json; charset=UTF-8' },
                url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/solveProblem/loadSessionQuestion",
                data: { detailLicense: detailLicense }
            })
                .then(response => {
                    this.options3 = response.data.map(item => item.exam_date);  // 여기에 데이터 매핑을 추가
                    this.subjects = response.data; // subjects 배열을 업데이트
                })
                .catch(() => {
                    this.errorModalContent;
                });
        },

        loadSubjects() {
            axios({
                method: 'post',
                headers: { 'Content-Type': 'application/json; charset=UTF-8' },
                url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/solveProblem/getSubjects",
                data: {
                    license_name: this.selectedOption1,
                    detail_license_name: this.selectedOption2,
                    exam_date: this.selectedOption3
                }
            })
                .then(response => {
                    this.subjects = response.data; // 과목 정보를 subjects 배열에 저장
                    this.selectedSubjects = response.data.map(item => item.subject_name);
                    this.isShow = this.subjects.length > 0; // 과목 정보가 있을 때만 표시
                })
                .catch(() => {
                    this.errorModalContent;
                });
        },

        errorModalContent() {
            this.title = '에러 발생'
            this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
            this.isErrorModal = true;
        },

    },
    watch: {
        selectedOption1() {
            this.isShow = false;
        },
        selectedOption2() {
            this.isShow = false;
        },
        selectedOption3() {
            this.isShow = false;
        }
    },

    mounted() {
        this.loadLicense();
    },
}


</script>



<style scoped>
.subject_wrap {
    height: 35%;
}

.content_container {
    top: 6%;
    position: fixed;
    overflow-y: auto;
    height: 88%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.selectbox_wrap {
    width: 100%;
    height: 50%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.selectbox_wrap>p {
    font-weight: bold;
}

.select {
    width: 60%;
    margin: 0.5em 0 1em 0;
    position: relative;
    font-size: 1em;
}

.selected {
    padding: 0.5em;
    background-color: #f9f9f9;
    border: 1px solid #d1d1d1;
    color: #000;
    border-radius: 5px;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: center;

}

.selected .arrow {
    width: 1em;
    height: 1em;
    fill: rgb(0, 0, 0);
}

.options {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 5px;
    margin-top: 5px;
    max-height: 150px;
    overflow-y: auto;
    z-index: 1000;
}

.options li {
    padding: 0.5em;
    cursor: pointer;
    text-align: center;
    list-style: none;
}

.options li:hover {
    background-color: #f0f0f0;
}

.mode_wrap {
    width: 100%;
    height: 15%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.practice_mode,
.test_mode {
    border-radius: 20px;
    background: #DBDBDB;
    color: #000;
    font-size: 1em;
    width: 25%;
    height: 35%;
    margin: 0em 1em;
}

.practice_mode.active,
.test_mode.active {
    background: #5271FF;
    color: #FFF;
}

.subject_btn_wrap {
    width: 100%;
    height: 40%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
}

.sub1,
.sub2,
.sub3,
.sub4,
.sub5 {
    width: 1.5em;
    height: 1.5em;
    margin-bottom: 1em;
    margin-right: 0.5em;
}

.go_solve {
    background-color: #5271FF;
    color: #fff;
    border-radius: 5px;
    width: 40%;
    height: 7%;
    border: none;
    margin-bottom: 4em;
}

:focus {
    outline: 0;
    border-color: #2260ff;
    box-shadow: 0 0 0 4px #b5c9fc;
}

.mydict {
    height: 10%;
}

.mydict div {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;

}

.mydict input[type="radio"] {
    clip: rect(0 0 0 0);
    clip-path: inset(100%);
    height: 1px;
    overflow: hidden;
    position: absolute;
    white-space: nowrap;
    /* width: 1px; */
}

.mydict input[type="radio"]:checked+span {
    box-shadow: 0 0 0 0.0625em #0043ed;
    background-color: #dee7ff;
    z-index: 1;
    color: #0043ed;
}

label span {
    display: block;
    cursor: pointer;
    background-color: #fff;
    padding: 0.375em .75em;
    position: relative;
    margin-left: .0625em;
    box-shadow: 0 0 0 0.0625em #b5bfd9;
    letter-spacing: .05em;
    color: #3e4963;
    text-align: center;
    transition: background-color .5s ease;
}

label:first-child span {
    border-radius: .375em 0 0 .375em;
}

label:last-child span {
    border-radius: 0 .375em .375em 0;
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
