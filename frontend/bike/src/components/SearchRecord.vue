<template>
    <div class="input-left">
        <div class="row1">
            <el-date-picker v-model="filters.time_range" type="datetimerange" start-placeholder="选择开始时间"
                end-placeholder="选择结束时间" format="YYYY-MM-DD HH:mm:ss" date-format="YYYY/MM/DD ddd" time-format="A hh:mm:ss"
                class="input-style" />

        </div>
        <div class="row2">
            <el-button type="primary" size="small" style="margin-right: 15px;" plain
                @click="toggleShowForm">添加记录</el-button>
            <el-dialog v-model="showForm" title="添加记录" style="margin-top: 90px;">
                <el-form :model="newRecord" label-width="120px">
                    <el-form-item  label="订单ID" required>
                        <el-input v-model="newRecord.order_id" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="单车ID" required>
                        <el-input v-model="newRecord.bike_id" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="骑行者ID" required>
                        <el-input v-model="newRecord.rider_id" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="时间范围" required>
                        <div class="block">
                            <el-date-picker v-model="newRecord.time_range" type="datetimerange" start-placeholder="选择开始时间"
                                end-placeholder="选择结束时间" format="YYYY-MM-DD HH:mm:ss" date-format="YYYY-MM-DD"
                                time-format="hh-mm-ss" />
                        </div>
                    </el-form-item>
                    <el-form-item label="开始位置经度" required>
                        <el-input v-model="newRecord.start_location_x" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="开始位置纬度" required>
                        <el-input v-model="newRecord.start_location_y" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="结束位置经度" required>
                        <el-input v-model="newRecord.end_location_x" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="结束位置纬度" required>
                        <el-input v-model="newRecord.end_location_y" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitNewRecord">提交</el-button>
                        <el-button @click="showForm = false">取消</el-button>
                    </el-form-item>
                </el-form>
            </el-dialog>
            <el-input v-model="filters.order_id" class="input-style" placeholder="订单ID" />
            <el-input v-model="filters.bike_id" class="input-style" placeholder="单车ID" />
            <el-input v-model="filters.rider_id" class="input-style" placeholder="骑行者ID" />
            <el-button link type="primary" icon="Search" style="margin-left: 8px;" @click="searchRecords">搜索</el-button>
            <el-button link type="primary" style="margin-left: 5px;" @click="clearFilters">
                <el-icon style="margin-right: 5px;">
                    <CircleClose />
                </el-icon> 清除
            </el-button>
        </div>




    </div>
    <sne-table ref="sRef" :loading="loading" :stripe="stripe"
        :showOperate="showOperate" :selector="true" size="mini" row-key="order_id" height="calc(100% - 140px)"
        :data-source="recordData" :columns="columns" @selection-change="handleSelectionChange" :show-delete="showDelete"
        :show-operate="showOperate">
        <template #order_id="{ data }">
            <span>{{ data.order_id }}</span>
        </template>
        <template #bike_id="{ data }">
            <span>{{ data.bike_id }}</span>
        </template>
        <template #rider_id="{ data }">
            <span>{{ data.rider_id }}</span>
        </template>
        <template #start_time="{ data }">
            <span>{{ data.start_time }}</span>
        </template>
        <template #end_time="{ data }">
            <span>{{ data.end_time }}</span>
        </template>
        <template #start_location="{ data }">
            <span>{{ data.start_location }}</span>
        </template>
        <template #end_location="{ data }">
            <span>{{ data.end_location }}</span>
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
            newRecord: {
                order_id: '1234567',
                bike_id: '22006',
                rider_id: '3131',
                start_time: '',
                end_time: '',
                start_location_x: '123.456',
                start_location_y: '41.897',
                end_location_x: '123.789',
                end_location_y: '41.765'
            },
            showForm: false,
            loading: false,
            stripe: true,
            showDelete: true,
            showOperate: false,
            recordData: [],
            filters: {
                order_id: '',
                bike_id: '',
                rider_id: '',
                start_time: '',
                end_time: '',
                start_location: '',
                end_location: ''
            },
            columns: [
                { prop: 'order_id', label: '订单ID', width: '180' },
                { prop: 'bike_id', label: '自行车ID', width: '180' },
                { prop: 'rider_id', label: '骑手ID', width: '180' },
                { prop: 'start_time', label: '开始时间', width: '180' },
                { prop: 'end_time', label: '结束时间', width: '180' },
                { prop: 'start_location', label: '开始位置', width: '180' },
                { prop: 'end_location', label: '结束位置', width: '180' },
                {prop:'duration',label:'骑行时长',width:'180'}
            ]
        };
    },
    mounted() {
        this.fetchData();
    },
    methods: {
        toggleShowForm() {
            console.log('点击了添加记录按钮');
            this.showForm = true;
            console.log(this.showForm);
        },
        submitNewRecord() {
            console.log('提交新记录:', this.newRecord);
            console.log('time:', this.formatDate(this.newRecord.time_range[0]),this.formatDate(this.newRecord.time_range[1]));
            axios.post('/create', {
                task: "CreateRecord",
                order_id: this.newRecord.order_id,
                bike_id: this.newRecord.bike_id,
                rider_id: this.newRecord.rider_id,
                start_time: this.formatDate(this.newRecord.time_range[0]),
                end_time: this.formatDate(this.newRecord.time_range[1]),
                start_location_x: this.newRecord.start_location_x,
                start_location_y: this.newRecord.start_location_y,
                end_location_x: this.newRecord.end_location_x,
                end_location_y: this.newRecord.end_location_y
            })
                .then(response => {
                    console.log('Response CreateRecord:', response.msg);
                    if (response.type === 'Ok') {
                        this.$message.success('创建成功')
                        this.fetchData();
                        this.showForm = false; // 关闭表单
                    } else {
                        this.$message.error(response.msg)
                    }
                })
                .catch(error => {
                    console.error('创建记录失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        fetchData() {
            axios.post('/read', { task: "ReadRecord" })
                .then(response => {
                    console.log('search Response readRecord:', response.msg);
                    if (response.type === 'Ok') {
                        this.recordData = response.msg;
                        console.log("recordData:", this.recordData)
                    } else {
                        this.$message.error("没有符合要求的记录!")
                    }
                })
                .catch(error => {
                    console.error('获取记录信息失败', error);
                });
        },
        searchRecords() {
        console.log("enter search record", this.filters)
        
        // 检查 time_range 是否存在并且有值
        const startTime = this.filters.time_range ? this.filters.time_range[0] : '';
        const endTime = this.filters.time_range ? this.filters.time_range[1] : '';

        axios.post('/read', {
            task: "ReadRecord",
            order_id: this.filters.order_id,
            bike_id: this.filters.bike_id,
            rider_id: this.filters.rider_id,
            start_time: startTime,
            end_time: endTime,
        })
        .then(response => {
            console.log('search Response SearchRecord:', response.msg);
            if (response.type === 'Ok') {
                this.recordData = response.msg;
            } else {
                this.$message.error("没有符合要求的记录!")
            }
        })
        .catch(error => {
            console.error('搜索记录信息失败', error);
        })
        .finally(() => {
            this.loading = false;
        });
    },
        clearFilters() {
            this.filters.order_id = '';
            this.filters.bike_id = '';
            this.filters.rider_id = '';
            this.filters.time_range = '';
            this.filters.start_location = '';
            this.filters.end_location = '';
        },
        handleSelectionChange(selectedItems) {
            console.log('Selected items:', selectedItems);
        },
        formatDate(date) {
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const seconds = String(d.getSeconds()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
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

.row1 {
    /* display: flex; */
    margin-bottom: 0px;
    margin-left: 87px;
}

.row2 {
    display: flex;
    margin-left: 0px;
    margin-bottom: 0px;
}
</style>