<template>
    <TopBar />
    <div class="login-container">
        <div class="login-form">
            <br>
            <div class="input-group">
                <input placeholder="현재 비밀번호를 입력하세요." type="password" v-model="member.userPw" @input="pwCheckMsg = ''"/>
                <text id="passwordCheck" class="infoMessage">{{ pwCheckMsg }}</text>
            </div>
            
            <button type="button" @click="goToChangeInfo">확인</button>
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
                <button class="accept2 cookie-button2" @click="isErrorModal = false">확인</button>
            </div>
        </div>
    </div>

</template>
<script>
import TopBar from '@/components/TopBar.vue';
import BottomBar from '@/components/BottomBar.vue';
import axios from 'axios';

export default {
    name: "FindByPwPage",
    components: {
        TopBar, BottomBar
    },
    data() {
        return {
            member : {
                userId : sessionStorage.getItem('USER_ID'),
                userPw : '',
            },
            pwCheckMsg : '',

            isErrorModal : false, // 에러 모달 창 실행 여부
            modalTitle : '', // 에러 모달 제목
            modalMsg : '', // 에러 모달 메세지
        }
    },
    methods: {
        goToChangeInfo(){
            const vm = this;
            axios({
                method : 'post',
                header: { 'Content-Type': 'application/json; charset=UTF-8' },
                url: "https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app/memberLogin/userPWCheck",
                data : vm.member,
            })
                .then(response => {
                    if(response.data.result > 0) {
                        this.$router.push({ name : 'ChangeInfo'})
                    } else {
                        vm.pwCheckMsg = response.data.message;
                    }
                })
                .catch(() => {
                    vm.errorModalContent();
                })
        },

        // 에러 발생시 오류 모달창 활성화
        errorModalContent() {
            this.modalTitle = '에러 발생'
            this.modalMsg = '오류가 발생했습니다. 잠시후 다시 시도해 주세요.';
            this.isErrorModal = true;
        },
    },

    watch : {
    }
}
</script>
<style scoped>
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
    height: 2em;
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
  height: 5%;
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