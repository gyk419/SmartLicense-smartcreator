<template>
  <div id="splash_screen">
    <h1 class="splash_logo">Smart License</h1>
    <div class="loader"></div>
  </div>

</template>

<script>
export default {
  name: 'SplashScreen',
  data() {
    return {

    };
  },
// test
  methods: {
    startLoading() {
      setTimeout(() => {
        this.$router.push({ name: 'LoginPage' });
      }, 500); // 3000ms = 3초
    }
  },

  mounted() {
    this.startLoading();
  }
}
</script>

<style scoped>
@import "@/css/common.css";

#splash_screen {
  display: flex;
  align-items: center;
  text-align: center;
  justify-content: center;
  flex-direction: column;
}

.splash_logo {
  color: aliceblue;
  font-size: 1.5em;
}

.loader {
  border: 4px solid rgba(0, 0, 0, .3);
  border-left-color: transparent;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  animation: spin89345 1s linear infinite;
  margin-top: 100px;
}

@keyframes spin89345 {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}
</style>