const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // build 경로 설정
  // outputDir: "../todomypet_backend/src/main/resources/static",
  devServer: {
    // 프록시 설정
    proxy: {
      '/api': {
        target: 'https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app',
        changeOrigin: true
      },
      '/memberLogin': {
        target: 'https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app',
        changeOrigin: true
      },
      '/solveProblem': {
        target: 'https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app',
        changeOrigin: true
      },
      '/mode': {
        target: 'https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app',
        changeOrigin: true
      },
      '/incorrectNote': {
        target: 'https://port-0-smartlicense-smartcreator-lzqfjvj513cc6b09.sel4.cloudtype.app',
        changeOrigin: true
      }
    }
  }
})
