<template>
    <div class="input-left">
        <el-input v-model="filters.rider_id" class="input-style" placeholder="Rider ID" />
        <el-input v-model="filters.rider_name" class="input-style" placeholder="Rider Name" />
        <el-input v-model="filters.age" class="input-style" placeholder="Age" />
        <el-input v-model="filters.phone_number" class="input-style" placeholder="Phone Number" />
        <el-input v-model="filters.account_condition" class="input-style" placeholder="Account Condition" />
        <el-button link type="primary" icon="Search" style="margin-left: 8px;" @click="searchRiders">Search</el-button>
        <el-button link type="primary" style="margin-left: 5px;" @click="clearFilters">
            <el-icon style="margin-right: 5px;"><CircleClose /></el-icon> Clear
        </el-button>
    </div>
    <sne-table ref="sRef" :loading="loading" :stripe="stripe" :selector="true" size="mini" row-key="rider_id"
        height="calc(100% - 140px)" :data-source="RiderData" :columns="columns"
        @selection-change="handleSelectionChange">
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
        },
        handleUpdate(data) {
            console.log('Update:', data);
        },
        handleDelete(data) {
            console.log('Delete:', data);
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
    margin-left: 250px;
    margin-right: 0px;
    margin-bottom: 0px;
}
</style>