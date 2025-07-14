import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'

// https://vite.dev/config/
export default defineConfig(({ command, mode }) => {
  // 加载环境变量（根据当前 mode 加载对应 .env 文件）
  const env = loadEnv(mode, process.cwd(), '')

  return {
    // 区分生产环境路径和开发环境路径
    base: env.VITE_BASE_URL || '/',       // 使用逻辑或确保并发环境正常工作
    // 根据环境配置代理
    server: {
      port: 5173,                                         // 设置开发环境前端端口，选填
      proxy: env.VITE_DEBUG_MODE === 'true' ? {           // 设置开发环境代理
        '/api': {                                         // 设置拦截器格式（斜杠+拦截器名字），名字可以自定义
          target: env.VITE_API_BASE_URL,                  // 代理的目标地址(后端设置的端口号)
          changeOrigin: true,                             // 是否设置同源
          rewrite: path => path.replace(/^\/api/, '')     // 重定向
        }
      } : {}
    },
    build: {
      sourcemap: env.VITE_DEBUG_MODE === 'true'
    },
    plugins: [
      vue(),
      vueDevTools(),
      AutoImport({
        // 自动导入 ElementPlus 相关 API (比如：ElMessage, ElButton 等)
        resolvers: [
          ElementPlusResolver(),
          // 自动导入 ElementPlus 图标组件 (使用格式：i-ep-icon)
          IconsResolver({
            // prefix: 'Icon',
          })
        ],
      }),
      Components({
        resolvers: [
          // 自动导入 ElementPlus 组件
          ElementPlusResolver(),
          // 自动注册 ElementPlus 图标组件
          IconsResolver()
        ],
      }),
      Icons({
        autoInstall: true
      })
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    },
  }
})
