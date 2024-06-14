<template>
  <div class="profile">
    <el-card class="profile-card">
      <div slot="header" class="profile-header">
        <el-avatar :src="avatar" size="large"></el-avatar>
        <div class="profile-name">{{ name }}</div>
      </div>
      <div class="profile-body">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="个人资料">
            <el-form :model="formData" :rules="rules" ref="form" label-width="100px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="formData.username"></el-input>
              </el-form-item>
              <!-- <el-form-item label="邮箱" prop="email">
                  <el-input v-model="formData.email"></el-input>
                </el-form-item> -->
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="formData.phone"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitForm('form')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="视频云盘">
            <el-table :data="videoList" style="width: 100%">
              <el-table-column prop="name" label="视频名称"></el-table-column>
              <el-table-column prop="size" label="视频大小"></el-table-column>
              <el-table-column prop="date" label="上传日期"></el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="更改密码">
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input type="password" v-model="passwordForm.oldPassword"></el-input>
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input type="password" v-model="passwordForm.newPassword"></el-input>
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input type="password" v-model="passwordForm.confirmPassword"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitForm_password('passwordForm')">保存</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>
  
<script>
import axios from 'axios';
import { useStore } from 'vuex'
import avatar_url from '../assets/mina.png'
export default {

  setup() {

  },
  data() {
    return {
      userId: null,
      activeTab: '0',
      formData: {
        username: '',
        phone: ''
      },
      rules: {
        //   email: [
        //     { required: true, message: '请输入邮箱', trigger: 'blur' },
        //     { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        //   ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ]
      },
      videoList: [
        { name: '视频1', size: '100MB', date: '2021-10-01' },
        { name: '视频2', size: '200MB', date: '2021-10-02' },
        { name: '视频3', size: '300MB', date: '2021-10-03' }
      ],
      //avatar: 'https://avatars.githubusercontent.com/u/125264?s=200&v=4',
      avatar: avatar_url,
      name: 'admin'
    }
  },
  methods: {
    submitForm(formName) {
      const user_name = this.formData.username;
      const phone_number = this.formData.phone;
      console.log("account 114:", user_name, phone_number)
      this.$refs[formName].validate(valid => {
        if (valid) {
          console.log("account 117:", this.userId)

          axios.post('/update', { task: "UpdateUser", user_id: this.userId, user_name: user_name, phone_number: phone_number })
            .then(response => {
              console.log('API Response UpdateUser:', response);
              if (response.type === 'Ok') {
                this.$message.success('保存成功')
              }
              else {
                this.$message.error(response.msg)
              }
            })
            .catch(error => {
              console.error('获取用户信息失败', error);
            });

        }
      })
    },
    submitForm_password(formName) {
      const old_password = this.formData.oldPassword;
      const new_password = this.formData.newPassword;
      //console.log("account 114:", user_name, phone_number)
      this.$refs[formName].validate(valid => {
        if (valid) {
          //console.log("account 117:", this.userId)
          axios.post('/read', {
            task: 'ReadUser',
            user_id: this.userId,
            password: old_password
          })
            .then(response => {
              //console.log('API Response readUser:', response);
              if (response.type === 'Ok') {
                //this.$message.success('保存成功')
                axios.post('/update', {
                  task: "UpdateUser",
                  user_id: this.userId,
                  password: new_password
                })
                  .then(response => {
                    console.log('API Response UpdateUser:', response);
                    if (response.type === 'Ok') {
                      this.$message.success('更新成功')
                    }
                    else {
                      this.$message.error(response.msg)
                    }
                  })
                  .catch(error => {
                    console.error('获取用户信息失败', error);
                  });
              }
              else {
                this.$message.error("密码错误")
              }
            })
            .catch(error => {
              console.error('获取用户信息失败', error);
            });




        }
      })
    },
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
  },
  mounted() {
    const store = useStore();
    const userId = store.getters.getUserId;
    this.userId = userId;
    console.log("account_data")
    console.log("userid = ", userId)
    axios.post('/read', { task: "ReadUser", user_id: userId })
      .then(response => {
        console.log('API Response:', response);
        const userData = response.msg[0];
        this.name = userData.user_name;
        this.formData.username = userData.user_name;
        this.formData.phone = userData.phone_number;
      })
      .catch(error => {
        console.error('获取用户信息失败', error);
      });
  }
}


</script>
  
<style scoped>
.profile {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.profile-card {
  width: 800px;
}

.profile-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.profile-name {
  font-size: 24px;
  margin-left: 20px;
}

.profile-body {
  padding: 20px;
}
</style>