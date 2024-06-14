<template>

    <div class="input-left">
        <el-button type="primary" size="small" style="margin-right: 15px;" plain @click="toggleShowForm">添加用户</el-button>
        <el-dialog v-model="showForm" title="添加用户">
            <el-form :model="newUser" label-width="120px">
                <el-form-item label="用户ID" required>
                    <el-input v-model="newUser.user_id" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户名" required>
                    <el-input v-model="newUser.user_name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码" required>
                    <el-input type="password" v-model="newUser.password" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="权限" required>
                    <el-select v-model="newUser.authority" placeholder="请选择">
                        <el-option label="管理员" value="manager"></el-option>
                        <el-option label="员工" value="staff"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="电话号码">
                    <el-input v-model="newUser.phone_number" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitNewUser">提交</el-button>
                    <el-button @click="showForm = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-input v-model="filters.user_id" class="input-style" placeholder="用户ID" />
        <el-input v-model="filters.user_name" class="input-style" placeholder="用户名" />
        <el-input v-model="filters.authority" class="input-style" placeholder="权限" />
        <el-input v-model="filters.phone_number" class="input-style" placeholder="电话号码" />
        <el-button link type="primary" icon="Search" style="margin-left: 8px;" @click="searchUsers">搜索</el-button>
        <el-button link type="primary" style="margin-left: 5px;" @click="clearFilters">
            <el-icon style="margin-right: 5px;">
                <CircleClose />
            </el-icon> 清除
        </el-button>
    </div>
    <sne-table @delete="confirmDelete" @update="handleUpdate" ref="sRef" :loading="loading" :stripe="stripe"
        :selector="true" size="mini" row-key="user_id" height="calc(100% - 140px)" :data-source="userData"
        :columns="columns" @selection-change="handleSelectionChange" :show-delete="showDelete" :show-operate="showOperate">
        <template #user_name="{ data }">
            <span>{{ data.user_name }}</span>
        </template>
        <template #authority="{ data }">
            <span>{{ data.authority }}</span>
        </template>
        <template #phone_number="{ data }">
            <span>{{ data.phone_number }}</span>
        </template>
        <template #operate="{ data }">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(data)">修改</el-button>
            <el-popconfirm confirm-button-text="Yes" cancel-button-text="No" :icon="InfoFilled" icon-color="#626AEF"
                title="确认删除?" @confirm="() => confirmDelete(data)">
                <template #reference>
                    <el-button link type="primary" icon="Delete">删除</el-button>
                </template>
            </el-popconfirm>
        </template>
    </sne-table>
</template>

<script>
import axios from 'axios';
import TableComponent from './table.vue';
import SneTable from './table.vue';
import { InfoFilled } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus'

export default {
    props: {
        
        dataSource: Array,
        columns: Array
    },
    components: {
        TableComponent,
        SneTable,
        InfoFilled
    },
    data() {
        return {
            newUser: {
                user_id: '',
                user_name: '',
                password: '',
                authority: '',
                phone_number: ''
            },
            showForm :false,
            loading: false,
            stripe: true,
            showDelete: true,
            showOperate:true,
            userData: [],
            filters: {
                user_id: '',
                user_name: '',
                authority: '',
                phone_number: ''
            },
            columns: [
                { prop: 'user_id', label: '用户ID', width: '180' },
                { prop: 'user_name', label: '用户名', width: '180' },
                { prop: 'authority', label: '权限', width: '180' },
                { prop: 'phone_number', label: '电话号码', width: '180' }
            ]
            
        };
    },
    mounted() {
        this.fetchData();
    },
    methods: {
        toggleShowForm() {
            console.log('点击了添加用户按钮');
            this.showForm = true;
            console.log(this.showForm);
        },
        submitNewUser() {
            console.log('提交新用户:', this.newUser);
            axios.post('/create', {
                task: "CreateUser",
                user_id: this.newUser.user_id,
                user_name: this.newUser.user_name,
                password: this.newUser.password,
                authority: this.newUser.authority,
                phone_number: this.newUser.phone_number
            })
                .then(response => {
                    console.log('Response CreateUser:', response.msg);
                    if (response.type === 'Ok') {
                        this.$message.success('创建成功')
                        this.fetchData();
                        this.showForm = false; // 关闭表单

                    } else {
                        this.$message.error(response.msg)
                    }
                })
                .catch(error => {
                    console.error('搜索用户信息失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        fetchData() {
            axios.post('/read', { task: "ReadUser" })
                .then(response => {
                    console.log('search Response readUser:', response.msg);
                    if (response.type === 'Ok') {
                        this.userData = response.msg;
                        console.log("userdata:", this.userData)
                    } else {
                        this.$message.error("没有符合要求的用户!")
                    }
                })
                .catch(error => {
                    console.error('获取用户信息失败', error);
                });
        },
        searchUsers() {
            axios.post('/read', {
                task: "ReadUser",
                user_id: this.filters.user_id,
                user_name: this.filters.user_name,
                authority: this.filters.authority,
                phone_number: this.filters.phone_number
            })
                .then(response => {
                    console.log('search Response SearchUser:', response.msg);
                    if (response.type === 'Ok') {
                        this.userData = response.msg;
                    } else {
                        this.$message.error("没有符合要求的用户!")
                    }
                })
                .catch(error => {
                    console.error('搜索用户信息失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        clearFilters() {
            this.filters.user_id = '';
            this.filters.user_name = '';
            this.filters.authority = '';
            this.filters.phone_number = '';
        },
        handleSelectionChange(selectedItems) {
            console.log('Selected items:', selectedItems);
        },

        confirmDelete(data) {
            axios.post('/delete', {
                task: "DeleteUser",
                user_id: data.user_id,
            })
                .then(response => {
                    console.log('Response DeleteUser:', response.msg);
                    if (response.type === 'Ok') {
                        this.$message.success('删除成功')
                        this.fetchData();
                    } else {
                        this.$message.error(response)
                    }
                })
                .catch(error => {
                    console.error('搜索用户信息失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });

            //console.log("userDelete",data)
        },

        handleUpdate(data) {
            console.log("userUpdate", data)
            axios.post('/update', {
                task: "UpdateUser",
                user_id: data.user_id,
                user_name: data.user_name,
                authority: data.authority,
                phone_number: data.phone_number
            })
                .then(response => {
                    console.log('Response UpdateUser:', response);
                    if (response.type === 'Ok') {
                        this.$message.success('修改成功')
                    } else {
                        this.$message.error(response.msg)
                    }
                })
                .catch(error => {
                    console.error('搜索用户信息失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });

            console.log("userUpdatedone", data)
        }
    }
}
</script>

<style scoped>
.input-style {
    width: 180px;
    margin-left: 0px;
    margin-right: 0px;
}

.input-left {
    margin-left: 163px;
    margin-right: 0px;
    margin-bottom: 0px;
}
</style>