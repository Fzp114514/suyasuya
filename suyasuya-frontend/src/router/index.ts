import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/study',
    name: 'study',
    component: () => import('@/views/Study.vue')
  },
  {
    path: '/game',
    name: 'game',
    component: () => import('@/views/Game.vue')
  },
  {
    path: '/life',
    name: 'life',
    component: () => import('@/views/Life.vue')
  },
  {
    path: '/tittleTattle',
    name: 'tittleTattle',
    component: () => import('@/views/TittleTattle.vue')
  },
  {
    path: '/anime',
    name: 'anime',
    component: () => import('@/views/Anime.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/history',
    name: 'history',
    component: () => import('@/views/History.vue')
  },
  {
    path: '/search/:keyword',
    name: 'search',
    component: () => import('@/views/Search/index.vue'),
    redirect: '/search/videos',
    children: [
      {
        path: 'videos',
        name: 'searchVideos',
        component: () => import('@/views/Search/SearchVideo.vue')
      },
      {
        path: 'users',
        name: 'searchUsers',
        component: () => import('@/views/Search/SearchUser.vue')
      }
    ]
  },
  {
    path: '/video/:videoId',
    name: 'video',
    component: () => import('@/views/Video.vue')
  },
  {
    path: '/space/:userId',     //动态路由传参，需传入userId以确认用户身份
    name: 'space',
    component: () => import('@/views/Space/index.vue'),
    children: [
      {
        path: '',
        name: 'spaceHome',
        component: () => import('@/views/Space/SpaceHome.vue')
      },
      {
        path: 'videos',
        name: 'videos',
        component: () => import('@/views/Space/Videos.vue')
      },
      {
        path: 'favorites',
        name: 'favorites',
        component: () => import('@/views/Space/Favorites.vue')
      }
    ]
  },
  {
    path: '/account',
    name: 'account',
    component: () => import('@/views/Account/index.vue'),
    redirect: '/account/home',
    children: [
      {
        path: 'home',
        name: 'accountHome',
        component: () => import('@/views/Account/AccountHome.vue')
      },
      {
        path: 'myInfo',
        name: 'myInfo',
        component: () => import('@/views/Account/MyInfo.vue')
      },
      {
        path: 'myAvatar',
        name: 'myAvatar',
        redirect: '/account/myAvatar/home',
        children: [
          {
            path: 'home',
            name: 'myAvatarHome',
            component: () => import('@/views/Account/MyAvatar/index.vue'),
          },
          {
            path: 'upload',
            name: 'avatarUpload',
            component: () => import('@/views/Account/MyAvatar/AvatarUpload.vue')
          }
        ]
      },
      {
        path: 'submitVideo',
        name: 'submitVideo',
        component: () => import('@/views/Account/SubmitVideo.vue')
      }
    ]
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})
// 前置导航守卫
router.beforeEach((to, from) => {
  if (!localStorage.getItem('token') && to.name !== 'login' && to.name !== 'register') {
    return { name: 'login' }
  }
})
router.afterEach(() => {
  window.scrollTo(0, 0)
})

export default router