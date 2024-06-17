<template>
    <h2 style="text-align: center;margin-left: -100px;margin-top: -32px;">最热门单车</h2>


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
    components: {
        TableComponent,
        SneTable
    },
    data() {
        return {
            RiderData: [],
            columns: [
                { prop: 'rider_id', label: 'Rider ID', width: '150' },
                { prop: 'rider_name', label: 'Rider Name', width: '150' },
                { prop: 'age', label: '年龄', width: '150' },
                { prop: 'phone_number', label: '电话号码', width: '150' },
                { prop: 'account_condition', label: '账号状态', width: '150' }
            ],
            selectedType: 'most',
        }
    },

    mounted() {
        console.log("mounted hot rider")
        this.fetchData();
    },
    methods: {
        fetchData() {
            console.log("fetchData")
            axios.post('/data', {
                task: "DataHotRider",
            })
                .then(response => {
                    console.log('data Response hotRider:', response.msg);
                    if (response.type === 'Ok') {
                        this.RiderData = response.msg;
                        console.log("RiderData:", this.RiderData)
                    } else {
                        this.$message.error("没有符合要求的骑行者!")
                    }
                })
                .catch(error => {
                    console.error('获取骑行者信息失败', error);
                });
        },
    }
}
</script>