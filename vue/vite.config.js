import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { VitePWA } from 'vite-plugin-pwa';
import path from 'path';

export default defineConfig({
  // 配置Vue插件
  plugins: [
    vue(),
    VitePWA({
      // PWA核心配置，将manifest内容整合到这里
      manifest: {
        name: "Portal应用",
        short_name: "Portal",
        description: "基于 Vite 的 PWA 应用",
        start_url: "/login",
        display: "standalone",
        background_color: "#ffffff",
        theme_color: "#409EFF",
        orientation: "portrait",
        prefer_related_applications: false,
        dir: "auto",
        lang: "zh-CN",
        scope: "/",
        categories: ["工具", "效率"],
        shortcuts: [
          {
            name: "我的收藏",
            short_name: "收藏",
            description: "快速访问我的收藏",
            url: "/mine"
          }
        ],
        share_target: {
          action: "/share",
          method: "POST",
          enctype: "multipart/form-data",
          params: {
            title: "title",
            text: "text",
            url: "url",
            files: [
              {
                name: "file",
                accept: ["image/*"]
              }
            ]
          }
        }
      },
      devOptions: {
        enabled: true
      },
      // Service Worker配置
      strategies: 'generateSW',
      workbox: {
        skipWaiting: true,
        clientsClaim: true,
        // 缓存策略配置
        runtimeCaching: [
          {
            urlPattern: /^http:\/\/localhost:8080/,
            handler: 'NetworkFirst',
            options: {
              cacheName: 'api-cache',
              expiration: {
                maxEntries: 50,
                maxAgeSeconds: 60 * 30 // 30分钟
              }
            }
          },
          {
            urlPattern: /\.(?:js|css|png|jpg|jpeg|svg|gif)$/,
            handler: 'CacheFirst',
            options: {
              cacheName: 'static-cache',
              expiration: {
                maxEntries: 100,
                maxAgeSeconds: 60 * 60 * 24 * 7 // 7天
              }
            }
          }
        ]
      }
    })
  ],

  // 开发服务器配置
  server: {
    open: '/index', // 启动时自动打开的路径
    host: '0.0.0.0',
    port: 8080,
    hot: true, // 热更新
    overlay: false // 关闭错误覆盖层
  },

  // 构建配置
  build: {
    // 生产环境源映射
    sourcemap: false
  },

  // 解析配置
  resolve: {
    fullySpecified: false,
    alias: {
      '@': path.resolve(__dirname, 'src') // 配置@别名
    },
    extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
  },

  // 全局变量定义
  define: {
    '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': JSON.stringify(true),
    '__VUE_OPTIONS_API__': JSON.stringify(true),
  }
});
