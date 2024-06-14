<template>
    <div class="input-container">

        <div class="input-row1">
            <el-date-picker v-model="filters.production_date" class="input-style" type="daterange" size=""
                range-separator="-" start-placeholder="生产时间" end-placeholder="输入日期" :shortcuts="shortcuts" />
            <el-date-picker v-model="filters.market_date" class="input-style" type="daterange" range-separator="-"
                start-placeholder="上市日期" end-placeholder="输入日期" :shortcuts="shortcuts" />
            <el-date-picker v-model="filters.warranty_date" class="input-style" type="daterange" range-separator="-"
                start-placeholder="保修时间" end-placeholder="输入日期" :shortcuts="shortcuts" />
        </div>

        <div class="input-row2">
            <el-button type="primary" size="middle" style="margin-right: 7px;" plain
                @click="toggleShowForm">添加单车</el-button>
            <el-dialog v-model="showForm" title="添加单车">
                <el-form :model="newBike" label-width="120px">
                    <el-form-item label="单车ID" required>
                        <el-input v-model="newBike.bike_id" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="生产日期" required>
                        <el-date-picker v-model="newBike.production_date" type="date" placeholder="选择日期"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="上市日期" required>
                        <el-date-picker v-model="newBike.market_date" type="date" placeholder="选择日期"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="保修时间" required>
                        <el-date-picker v-model="newBike.warranty_date" type="date" placeholder="选择日期"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="使用情况">
                        <el-select v-model="newBike.using_condition" placeholder="请选择">
                            <el-option label="使用中" value="使用中"></el-option>
                            <el-option label="待投放" value="待投放"></el-option>
                            <el-option label="已损坏" value="已损坏"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitNewBike">提交</el-button>
                        <el-button @click="showForm = false">取消</el-button>
                    </el-form-item>
                </el-form>
            </el-dialog>
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
    <sne-table @update="handleUpdate" ref="sRef" :loading="loading" :stripe="stripe" :selector="true" size="mini" row-key="bike_id"
        height="calc(100% - 140px)" :data-source="bikeData" :columns="columns" @selection-change="handleSelectionChange">
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
            newBike: {
                bike_id: '',
                production_date: '',
                market_date: '',
                warranty_date: '',
                using_condition: '',
            },
            showForm: false,
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
        toggleShowForm() {
            console.log('点击了添加单车按钮');
            this.showForm = true;
            console.log(this.showForm);
        },
        submitNewBike() {
            console.log('创建新单车:', this.newUser);
            const formatDate = (date) => {
                if (!date) return '';
                const d = new Date(date);
                return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
            };
            axios.post('/create', {
                task: "CreateBike",
                bike_id: this.newBike.bike_id,
                production_date: formatDate(this.newBike.production_date),
                market_date: formatDate(this.newBike.market_date),
                warranty_date: formatDate(this.newBike.warranty_date),
                using_condition: this.newBike.using_condition,
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
            console.log("bikeUpdate", data)
            axios.post('/update', {
                task: "UpdateBike",
                bike_id: data.bike_id,
                production_date: data.production_date,
                market_date: data.market_date,
                warranty_date: data.warranty_date,
                using_condition: data.using_condition,
                current_location: data.current_location,
                usage_record: data.usage_record
            })
                .then(response => {
                    console.log('Response UpdateBike:', response);
                    if (response.type === 'Ok') {
                        this.$message.success('修改成功')
                    } else {
                        this.$message.error(response.msg)
                    }
                })
                .catch(error => {
                    console.error('更新单车信息失败', error);
                })
                .finally(() => {
                    this.loading = false;
                });

            console.log("bikeUpdatedone", data)
        },
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

.input-row1 {
    display: flex;
    margin-bottom: 0px;
    margin-left: -200px;
}

.input-row2 {
    display: flex;
    margin-left: -95px;
    margin-bottom: 0px;
}

.button-row {
    display: flex;
    margin-left: 0px;
}
</style>