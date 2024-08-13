<template>
    <TopBar />
    <div class="group">
        <svg viewBox="0 0 24 24" aria-hidden="true" class="icon_search">
            <g>
                <path
                    d="M21.53 20.47l-3.66-3.66C19.195 15.24 20 13.214 20 11c0-4.97-4.03-9-9-9s-9 4.03-9 9 4.03 9 9 9c2.215 0 4.24-.804 5.808-2.13l3.66 3.66c.147.146.34.22.53.22s.385-.073.53-.22c.295-.293.295-.767.002-1.06zM3.5 11c0-4.135 3.365-7.5 7.5-7.5s7.5 3.365 7.5 7.5-3.365 7.5-7.5 7.5-7.5-3.365-7.5-7.5z">
                </path>
            </g>
        </svg>
        <input ref="searchInput" class="input" type="search" placeholder="세부자격을 검색하세요." v-model="searchQuery"
            @keyup.enter="handleEnter" />
    </div>
    <div class="table-container">
        <div v-if="isLoading">
            <p>로딩 중...</p>
        </div>
        <div v-for="(item, index) in paginatedItems" :key="index" class="table-row">
            <div class="history_info" @click="goIncorrectNote(item.exam_record_idx)">
                <div class="license_info">
                    <span>{{ item.exam_date }}</span>&nbsp;<span>{{ item.detail_license_name }}</span>
                </div>
                <div class="detail_info">
                    <strong :class="{ 'practiceColor': item.mode === '연습', 'testColor': item.mode === '시험' }">{{ item.mode
                        }}</strong>&nbsp;|&nbsp;<span>과목 수 <strong>{{ item.subject_count
                            }}</strong></span>&nbsp;|&nbsp;<span>문제 수 <strong>{{
            item.question_count
        }}</strong></span>&nbsp;|&nbsp;<span>정답 갯수
                        <strong>{{ item.correct_count }}</strong></span>&nbsp;|&nbsp;<strong
                        :class="{ 'passed': item.is_pass != 0, 'failed': item.is_pass == 0 }">
                        {{ item.is_pass == 0 ? '불합격' : '합격' }}
                    </strong>
                </div>
                <div class="date_info">
                    <span>응시일 {{ formatDate(item.start_test_date) }}</span>
                </div>
            </div>
            <div class="delete_history" @click="openModal(index)">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#03020a" class="bi bi-trash"
                    viewBox="0 0 16 16">
                    <path
                        d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z" />
                    <path
                        d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z" />
                </svg>
            </div>
        </div>
    </div>
    <ul class="wrapper">
        <li class="icon black" @click="firstPage">
            <span v-show="currentPage !== 1">
                <svg viewBox="0 0 16 16" class="bi bi-chevron-double-left" fill="currentColor" height="16" width="16"
                    xmlns="http://www.w3.org/2000/svg">
                    <path
                        d="M8.354 1.646a.5.5 0 0 1 0 .708L2.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"
                        fill-rule="evenodd"></path>
                    <path
                        d="M12.354 1.646a.5.5 0 0 1 0 .708L6.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"
                        fill-rule="evenodd"></path>
                </svg>
            </span>
        </li>
        <li class="icon black" @click="prevPage">
            <span v-show="currentPage !== 1">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                    class="bi bi-chevron-left" viewBox="0 0 16 16">
                    <path fill-rule="evenodd"
                        d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0" />
                </svg>
            </span>
        </li>
        <li class="icon black">
            <span>{{ currentPage }}</span>
        </li>
        <li class="icon black" @click="nextPage">
            <span v-show="Math.ceil(totalItems / itemsPerPage) !== currentPage">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                    class="bi bi-chevron-right" viewBox="0 0 16 16">
                    <path fill-rule="evenodd"
                        d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708" />
                </svg>
            </span>
        </li>
        <li class="icon black" @click="lastPage">
            <span v-show="Math.ceil(totalItems / itemsPerPage) !== currentPage">
                <svg viewBox="0 0 16 16" class="bi bi-chevron-double-right" fill="currentColor" height="16" width="16"
                    xmlns="http://www.w3.org/2000/svg">
                    <path
                        d="M3.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L9.293 8 3.646 2.354a.5.5 0 0 1 0-.708z"
                        fill-rule="evenodd"></path>
                    <path
                        d="M7.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L13.293 8 7.646 2.354a.5.5 0 0 1 0-.708z"
                        fill-rule="evenodd"></path>
                </svg>
            </span>
        </li>
    </ul>
    <BottomBar />

    <!-- 삭제 모달 -->
    <div class="modal" v-if="isShowModal">
        <div class="card">
            <div class="header">
                <div class="image"><svg aria-hidden="true" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24"
                        fill="none">
                        <path
                            d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z"
                            stroke-linejoin="round" stroke-linecap="round"></path>
                    </svg></div>
                <div class="content">
                    <span class="title">기록 삭제</span>
                    <p class="message">정말로 기록 삭제를 원하십니까? 삭제 시 모든 정보가 삭제되며 복구할 수 없습니다.</p>
                    <br>

                </div>
                <div class="actions">
                    <button class="desactivate" type="button" @click="deleteExamRecord">확인</button>
                    <button class="cancel" type="button" @click="isShowModal = false">취소</button>
                </div>
            </div>
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
import TopBar from '../components/TopBar.vue';
import BottomBar from '../components/BottomBar.vue';
import axios from 'axios';

export default {
    name: 'MyHistoryPage',
    components: {
        TopBar,
        BottomBar
    },
    data() {
        return {
            memberId: '',
            items: [],
            currentPage: 1, // 현재 페이지
            itemsPerPage: 6, // 페이지당 표시할 항목 수
            totalItems: 0, // 총 항목 수
            isLoading: false, // 로딩 상태

            isShowModal: false,
            selectedItemIndex: null, // 선택한 항목의 인덱스

            searchQuery: '',
            filteredItems: [],

            isErrorModal: false,
            modalMsg: '',
            modalTitle: ''
        };
    },
    computed: {
        paginatedItems() {
            if (this.items.length === 0 || this.currentPage < 1) {
                return [];
            }
            return this.items;
        }
    },
    methods: {
        async firstPage() {
            if (this.currentPage > 1) {
                this.currentPage = 1;
                await this.loadRecrds();
            }
        },
        async prevPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
                await this.loadRecrds();
            }
        },
        async nextPage() {
            if (this.currentPage < Math.ceil(this.totalItems / this.itemsPerPage)) {
                this.currentPage++;
                await this.loadRecrds();
            }
        },
        async lastPage() {
            const totalPageCount = Math.ceil(this.totalItems / this.itemsPerPage);
            if (this.currentPage < totalPageCount) {
                this.currentPage = totalPageCount;
                await this.loadRecrds();
            }
        },
        async loadRecrds() {
            if (this.searchQuery) {
                await this.searchRecords(); // 검색 중일 때
            } else {
                await this.loadExamRecord(); // 검색 중이 아닐 때
            }
        },
        async loadExamRecord() {
            this.isLoading = true;
            try {
                const response = await axios.post("https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/incorrectNote/loadExamRecord", {
                    memberId: this.memberId,
                    page: this.currentPage,
                    itemsPerPage: this.itemsPerPage
                }, {
                    headers: { 'Content-Type': 'application/json; charset=UTF-8' }
                });

                if (response.data.records && Array.isArray(response.data.records)) {
                    this.items = [...response.data.records];
                    this.totalItems = response.data.total;
                } else {
                    this.modalTitle = '에러 발생';
                    this.modalMsg = '올바르지 않은 데이터 구조입니다.';
                    this.isErrorModal = true;
                }
            } catch {
                this.errorModalContent();
            } finally {
                this.isLoading = false;
            }
        },
        formatDate(dateString) {
            const date = new Date(dateString);
            const year = date.getFullYear();
            const month = date.getMonth() + 1;
            const day = date.getDate();
            const hour = date.getHours();
            const minute = date.getMinutes();
            return `${year}. ${month}. ${day} ${hour}시 ${minute}분`;
        },
        openModal(index) {
            this.selectedItemIndex = index;
            this.isShowModal = true;
        },
        async deleteExamRecord() {
            try {
                await axios.post("https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/incorrectNote/deleteExamRecord", {
                    memberId: this.memberId,
                    examRecordIdx: this.items[this.selectedItemIndex].exam_record_idx
                }, {
                    headers: { 'Content-Type': 'application/json; charset=UTF-8' }
                });
                this.items.splice(this.selectedItemIndex, 1);
                this.isShowModal = false;
                if (this.items.length == 0) { this.currentPage-- }
                this.loadExamRecord(); // 새로고침
            } catch {
                this.errorModalContent();
            }
        },
        async handleEnter() {
            this.$refs.searchInput.blur();
            await this.searchRecords();
        },

        async searchRecords() {
            this.isLoading = true;
            try {
                const response = await axios.post("https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/incorrectNote/searchRecord", {
                    memberId: this.memberId,
                    searchQuery: this.searchQuery,
                    page: this.currentPage,
                    itemsPerPage: this.itemsPerPage
                }, {
                    headers: { 'Content-Type': 'application/json; charset=UTF-8' }
                });

                if (response.data.records && Array.isArray(response.data.records)) {
                    this.items = [...response.data.records];
                    this.totalItems = response.data.total;

                    const totalPages = Math.ceil(this.totalItems / this.itemsPerPage);

                    if (this.currentPage > totalPages) {
                        this.currentPage = 1;
                        await this.searchRecords();
                    }
                } else {
                    this.modalTitle = '에러 발생';
                    this.modalMsg = '올바르지 않은 데이터 구조입니다.';
                    this.isErrorModal = true;
                }
            } catch {
                this.errorModalContent();
            } finally {
                this.isLoading = false;
            }
        },

        goIncorrectNote(exam_record_idx) {
            this.$router.push({ name: 'IncorrectNote', query: { where: exam_record_idx } });
        },

        errorModalContent() {
            this.modalTitle = '에러 발생'
            this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
            this.isErrorModal = true;
        },
    },
    mounted() {
        this.memberId = sessionStorage.getItem('USER_ID');
        this.loadExamRecord();
    }
};
</script>


<style scoped>
@import "@/css/common.css";

.table-container {
    top: 16%;
    position: fixed;
    overflow-y: auto;
    /* 컨텐츠가 넘칠 경우 스크롤 */
    height: 70%;
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
    padding: .7em;
    margin: .3em 0 .3em 0;
    justify-content: center;
    align-items: center;
    font-size: .9em;
    height: 16%;
}

.history_info {
    width: 92%;
    color: #828595;
}

.license_info {
    font-weight: 900;
    font-size: 1em;
    margin-bottom: .4em;
    color: #282f4a;
}

.detail_info {
    font-size: .8em;
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

/* 페이지 네이션 */
.wrapper {
    display: inline-flex;
    list-style: none;
    height: 8%;
    width: 100%;
    top: 86%;
    justify-content: center;
    position: absolute;
    bottom: 0;
    align-items: center;
}

.wrapper .icon {
    position: relative;
    background: #fff;
    border-radius: 50%;
    margin: 10px;
    width: 40px;
    height: 40px;
    font-size: 18px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    box-shadow: 0 10px 10px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.2s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.wrapper svg {
    fill: green;
}

/* 검색창 디자인 */
.group {
    display: flex;
    line-height: 28px;
    align-items: center;
    position: fixed;
    top: 6%;
    width: 100%;
    padding: 0em 1em;
    max-width: 500px;
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

.passed {
    color: #70CA77;
}

.failed {
    color: #f00;
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

.practiceColor {
    color: #a36518;
    font-weight: 800;
}

.testColor {
    color: #5471ff;
    font-weight: 800;
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