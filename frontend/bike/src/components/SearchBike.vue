<template>
    <div class="input-container">
        <div class="input-row1">

            <el-date-picker v-model="filters.production_date" class="input-style" type="daterange" size=""range-separator="-"
                start-placeholder="生产时间" end-placeholder="输入日期" :shortcuts="shortcuts" />
            <el-date-picker v-model="filters.market_date" class="input-style" type="daterange" range-separator="-"
                start-placeholder="上市日期" end-placeholder="输入日期" :shortcuts="shortcuts" />
            <el-date-picker v-model="filters.warranty_date" class="input-style" type="daterange" range-separator="-"
                start-placeholder="保修时间" end-placeholder="输入日期" :shortcuts="shortcuts" />
        </div>
        <div class="input-row">
            <el-input v-model="filters.bike_id" class="input-style" placeholder="单车ID" />
            <el-input v-model="filters.using_condition" class="input-style" placeholder="使用情况" />
            <el-input v-model="filters.current_location" class="input-style" placeholder="当前位置" />
            <el-input v-model="filters.usage_record" class="input-style" placeholder="使用记录" />
            <el-button link type="primary" style="margin-left: 30px;" icon="Search" @click="searchBikes">搜索</el-button>
            <el-button link type="primary" @click="clearFilters">
                <el-icon style="margin-right: 5px;">
                    <CircleClose />
                </el-icon> 清除
            </el-button>
        </div>
    </div>
    <sne-table ref="sRef" :loading="loading" :stripe="stripe" :selector="true" size="mini" row-key="bike_id"
        height="calc(100% - 140px)" :data-source="bikeData" :columns="columns"
        @selection-change="handleSelectionChange">
        <template #bike_id="{ data }">
            <span>{{ data.bike_id }}</span>
        </template>
        <template #production_date="{ data }">
            <span>{{ data.production_date }}</span>
        </template>
        <template #market_date="{ data }">
            <span>{{ data.market_date }}</span>
        </template>
        <template #warranty_date="{ data }">
            <span>{{ data.warranty_date }}</span>
        </template>
        <template #using_condition="{ data }">
            <span>{{ data.using_condition }}</span>
        </template>
        <template #current_location="{ data }">
            <span>{{ data.current_location }}</span>
        </template>
        <template #usage_record="{ data }">
            <span>{{ data.usage_record }}</span>
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
        columns: Array
    },
    components: {
        TableComponent,
        SneTable
    },
    data() {
        return {
            loading: false,
            stripe: true,
            bikeData: [],
            filters: {
                bike_id: '',
                production_date: '',
                market_date: '',
                warranty_date: '',
                using_condition: '',
                current_location: '',
                usage_record: ''
            },
            columns: [
                { prop: 'bike_id', label: '单车ID', width: '180' },
                { prop: 'production_date', label: '生产日期', width: '180' },
                { prop: 'market_date', label: '上市日期', width: '180' },
                { prop: 'warranty_date', label: '保修日期', width: '180' },
                { prop: 'using_condition', label: '使用情况', width: '180' },
                { prop: 'current_location', label: '当前位置', width: '180' },
                { prop: 'usage_record', label: '使用记录', width: '180' }
            ],
            shortcuts: [
                {
                    text: '最近一周',
                    value: () => {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                        return [start, end];
                    },
                },
                {
                    text: '最近一个月',
                    value: () => {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                        return [start, end];
                    },
                },
                {
                    text: '最近三个月',
                    value: () => {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                        return [start, end];
                    },
                },
            ]
        };
    },
    mounted() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            axios.post('/read', { task: "ReadBike" })
                .then(response => {
                    console.log('search Response readBike:', response.msg);
                    if (response.type === 'Ok') {
                        this.bikeData = response.msg;
                        console.log("bikedata:", this.bikeData)
                    } else {
                        this.$message.error("没有符合要求的单车!")
                    }
                })
                .catch(error => {
                    console.error('获取单车信息失败', error);
                });
        },
        searchBikes() {
            console.log("date", this.filters)
            axios.post('/read', {
                task: "ReadBike",
                bike_id: this.filters.bike_id,

                production_start: this.filters.production_date[0] ? `${this.filters.production_date[0].getFullYear()}-${(this.filters.production_date[0].getMonth() + 1).toString().padStart(2, '0')}-${this.filters.production_date[0].getDate().toString().padStart(2, '0')}` : '',
                production_end: this.filters.production_date[1] ? `${this.filters.production_date[1].getFullYear()}-${(this.filters.production_date[1].getMonth() + 1).toString().padStart(2, '0')}-${this.filters.production_date[1].getDate().toString().padStart(2, '0')}` : '',

                market_start: this.filters.market_date[0] ? `${this.filters.market_date[0].getFullYear()}-${(this.filters.market_date[0].getMonth() + 1).toString().padStart(2, '0')}-${this.filters.market_date[0].getDate().toString().padStart(2, '0')}` : '',
                market_end: this.filters.market_date[1] ? `${this.filters.market_date[1].getFullYear()}-${(this.filters.market_date[1].getMonth() + 1).toString().padStart(2, '0')}-${this.filters.market_date[1].getDate().toString().padStart(2, '0')}` : '',

                warranty_start: this.filters.warranty_date[0] ? `${this.filters.warranty_date[0].getFullYear()}-${(this.filters.warranty_date[0].getMonth() + 1).toString().padStart(2, '0')}-${this.filters.warranty_date[0].getDate().toString().padStart(2, '0')}` : '',
                warranty_end: this.filters.warranty_date[1] ? `${this.filters.warranty_date[1].getFullYear()}-${(this.filters.warranty_date[1].getMonth() + 1).toString().padStart(2, '0')}-${this.filters.warranty_date[1].getDate().toString().padStart(2, '0')}` : '',

                using_condition: this.filters.using_condition,
                current_location: this.filters.current_location,
                usage_record: this.filters.usage_record
            })
                .then(response => {
                    console.log('search Response SearchBike:', response.data);
                    if (response.type === 'Ok') {
                        this.bikeData = response.msg;
                    } else {
                        this.$message.error("没有符合要求的单车!")
                    }
                })
                .catch(error => {
                    console.error('搜索单车信息失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        clearFilters() {
            this.filters.bike_id = '';
            this.filters.production_date = '';
            this.filters.market_date = '';
            this.filters.warranty_date = '';
            this.filters.using_condition = '';
            this.filters.current_location = '';
            this.filters.usage_record = '';
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
    width: 180px;
    margin-left: 0px;
    margin-right: 0px;
}

.input-container {
    margin-left: 250px;
    margin-right: 0px;
    margin-bottom: 0px;
}

.input-row {
    display: flex;
    margin-bottom: 0px;
}

.input-row1 {
    display: flex;
    margin-bottom: 0px;
    margin-left: -200px;
}

.button-row {
    display: flex;
    margin-left: 0px;
}
</style>