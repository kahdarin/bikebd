import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import HomeAdmin from '@/views/HomeAdmin.vue'
import Account from '@/components/AccountData.vue'
import SearchUser from '@/components/SearchUser.vue'
import ForbidArea from '@/components/ForbidArea.vue'
const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/login',
    name: 'Login2',
    component: Login
  },
  {
    path: '/Home',
    name: 'Home',
    component: Home
  },
  {
    path: '/HomeAdmin',
    name: 'HomeAdmin',
    component: HomeAdmin
  },
  {
    path: '/account',
    name: 'Account',
    component: Account
  },
  {
    path: '/SearchUser',
    name: 'SearchUser',
    component: SearchUser
  },
{
  path: '/ForbidArea',
  name: 'ForbidArea',
  component: ForbidArea
}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router