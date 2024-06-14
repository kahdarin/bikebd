<template>
    <div class="input-left">

        <el-button type="primary" size="small" style="margin-right: 8px;" plain @click="toggleShowForm">添加骑行者</el-button>
        <el-dialog v-model="showForm" title="添加骑行者">
            <el-form :model="newRider" label-width="120px">
                <el-form-item label="骑行者ID" required>
                    <el-input v-model="newRider.rider_id" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="骑行者姓名" required>
                    <el-input v-model="newRider.rider_name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="年龄">
                    <el-input v-model="newRider.age" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="电话号码">
                    <el-input v-model="newRider.phone_number" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="账号状态" required>
                    <el-select v-model="newRider.account_condition" placeholder="请选择账号状态">
                        <el-option label="正常" value="正常"></el-option>
                        <el-option label="注销" value="注销"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitNewRider">提交</el-button>
                    <el-button @click="showForm = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-input v-model="filters.rider_id" class="input-style" placeholder="Rider ID" />
        <el-input v-model="filters.rider_name" class="input-style" placeholder="Rider Name" />
        <el-input v-model="filters.age" class="input-style" placeholder="Age" />
        <el-input v-model="filters.phone_number" class="input-style" placeholder="Phone Number" />
        <el-input v-model="filters.account_condition" class="input-style" placeholder="Account Condition" />
        <el-button link type="primary" icon="Search" style="margin-left: 8px;" @click="searchRiders">Search</el-button>
        <el-button link type="primary" style="margin-left: 5px;" @click="clearFilters">
            <el-icon style="margin-right: 5px;">
                <CircleClose />
            </el-icon> Clear
        </el-button>
    </div>
    <sne-table ref="sRef" :loading="loading" :stripe="stripe" :selector="true" size="mini" row-key="rider_id"
        height="calc(100% - 140px)" :data-source="RiderData" :columns="columns" @selection-change="handleSelectionChange">
        <template #rider_name="{ data }">
            <span>{{ data.rider_name }}</span>
        </template>
        <template #age="{ data }">
            <span>{{ data.age }}</span>
        </template>
        <template #phone_number="{ data }">
            <span>{{ data.phone_number }}</span>
        </template>
        <template #operate="{ data }">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(data)">修改</el-button>
            <el-button link type="primary" icon="Delete" @click="handleDelete(data)">删除</el-button>
        </template>
    </sne-table>
</template>

<script>
import axios from 'axios';
import TableComponent from './table.vue';
import SneTable from './table.vue';
export default {
    props: {
        dataSource: Array,
        columns: Array,
        columnSize: String
    },
    components: {
        TableComponent,
        SneTable,
    },
    data() {
        return {
            newRider: {
                rider_id: '',
                rider_name: '',
                age: '',
                phone_number: '',
                // account_condition: '',
                account_condition: '正常'
            },
            showForm: false,
            loading: false,
            stripe: true,
            RiderData: [],
            filters: {
                rider_id: '',
                rider_name: '',
                age: '',
                phone_number: '',
                account_condition: ''
            },
            columns: [
                { prop: 'rider_id', label: 'Rider ID', width: '150' },
                { prop: 'rider_name', label: 'Rider Name', width: '150' },
                { prop: 'age', label: '年龄', width: '150' },
                { prop: 'phone_number', label: '电话号码', width: '150' },
                { prop: 'account_condition', label: '账号状态', width: '150' }
            ]
        };
    },
    mounted() {
        this.fetchData();
    },
    methods: {
        toggleShowForm() {
            this.showForm = true;
        },
        submitNewRider() {
            axios.post('/create', {
                task: "CreateRider",
                rider_id: this.newRider.rider_id,
                rider_name: this.newRider.rider_name,
                age: this.newRider.age,
                phone_number: this.newRider.phone_number,
                account_condition: this.newRider.account_condition
            })
                .then(response => {
                    if (response.type === 'Ok') {
                        this.$message.success('创建成功');
                        this.fetchData();
                        this.showForm = false;
                    } else {
                        this.$message.error(response.msg);
                    }
                })
                .catch(error => {
                    console.error('创建骑行者失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        fetchData() {
            axios.post('/read', { task: "ReadRider", sort: "id" })
                .then(response => {
                    // console.log('search Response readRider:', response.msg[0]);
                    if (response.type === 'Ok') {
                        this.RiderData = response.msg;
                        // console.log("RiderData:", this.RiderData)
                    }
                    else {
                        this.$message.error("没有符合要求的骑行者!")
                    }
                })
                .catch(error => {
                    console.error('获取骑行者信息失败', error);
                });
        },
        searchRiders() {
            axios.post('/read', {
                task: "ReadRider",
                rider_id: this.filters.rider_id,
                rider_name: this.filters.rider_name,
                age: this.filters.age,
                phone_number: this.filters.phone_number,
                account_condition: this.filters.account_condition
            })
                .then(response => {
                    console.log('search Response SearchRider:', response);
                    if (response.type === 'Ok') {
                        this.RiderData = response.msg;
                        // console.log("RiderData:", this.RiderData)
                    } else {
                        this.$message.error("没有符合要求的骑行者!")
                    }
                })
                .catch(error => {
                    console.error('搜索骑行者信息失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });


        },
        clearFilters() {
            this.filters.rider_id = '';
            this.filters.rider_name = '';
            this.filters.age = '';
            this.filters.phone_number = '';
            this.filters.account_condition = '';
        },
        handleSelectionChange(selectedItems) {
            console.log('Selected items:', selectedItems);
        }
    }
}
</script>

<style scoped>
.input-style {
    width: 150px;
    margin-left: 0px;
    margin-right: 0px;
}

.input-left {
    margin-left: 158px;
    margin-right: 0px;
    margin-bottom: 0px;
}
</style>