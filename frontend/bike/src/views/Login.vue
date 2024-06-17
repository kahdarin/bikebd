<template>
  <el-form ref="form" :model="loginUser" label-width="55px" class="loginForm">
    <h3 class="login_title">登录</h3>
    <el-form-item label="用户名">
      <el-input v-model="loginUser.username" placeholder="请输入用户名"></el-input>
    </el-form-item>
    <el-form-item label="密码">
      <el-input v-model="loginUser.password" type="password" placeholder="请输入密码"></el-input>
    </el-form-item>
    <el-form-item style="width: 100%">
      <el-button type="primary" style="width: 100%;background: #505458;border: none" @click="login">登录</el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
//import axios from 'axios'  // 导入 axios
import axios from '@/api/request'
import { useStore } from 'vuex'

export default {

  name: 'Login',
  setup() {
    const store = useStore();
    const router = useRouter()

    const loginUser = reactive({
      username: '',
      password: ''
    })

    const login = async () => {
      try {
        console.log(loginUser.username, loginUser.password)
        const response = await axios.post('/read', {
          task: 'ReadUser',
          user_name: loginUser.username,
          password: loginUser.password
        }, {
          headers: {
            'Content-Type': 'application/json'
          }
        })
        console.log(response)
        if (response.type === 'Ok') {
          const userId = response.msg[0].user_id;
          const authority = response.msg[0].authority;

          console.log("userId", userId);
          console.log("authority login", authority);

          // 使用 localStorage 存储 userId 和 authority
          localStorage.setItem('userId', userId);
          localStorage.setItem('authority', authority);

          // 更新 Vuex store（如果仍然需要在其他组件中使用）
          store.dispatch('updateUserId', userId);
          store.dispatch('updateAuthority', authority);

          if (authority === 'admin') {
            console.log('login_yes')
            router.push('/HomeAdmin')
          } else {
            router.push('/Home')
          }
        } else if (response.data.type === 'Err') {
          alert('用户名或密码错误')
        }
      } catch (error) {
        console.error('登录失败', error)
        alert('登录失败，请稍后再试')
      }
    }

    return { loginUser, login }
  },
}
</script>

<style>
.loginForm {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 90px auto;
  width: 350px;
  padding: 35px 35px 35px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}
</style>