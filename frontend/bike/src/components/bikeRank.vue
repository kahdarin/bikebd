<template>
    <div>
        <el-dropdown @command="handleCommand">
            <el-button type="primary" style="margin-left: 100px;">
                选择类型<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item command="most">最热门单车</el-dropdown-item>
                    <el-dropdown-item command="least">最少使用单车</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <h2 v-if="selectedType === 'most'" style="text-align: center;margin-left: -100px;margin-top: -32px;">最热门单车</h2>
        <h2 v-if="selectedType === 'least'" style="text-align: center;margin-left: -100px;margin-top: -32px;">最少使用单车</h2>
        <div v-if="selectedType === 'most'">

            <sne-table ref="sRef" :loading="loading" :stripe="stripe" :selector="true" size="mini" row-key="bike_id"
                height="calc(100% - 140px)" :data-source="bikeDataMost" :columns="columns"
                @selection-change="handleSelectionChange">
                <template #count="{ data }">
                    <span>{{ data.count }}</span>
                </template>
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
        </div>

        <div v-if="selectedType === 'least'">
            <sne-table ref="sRef" :loading="loading" :stripe="stripe" :selector="true" size="mini" row-key="bike_id"
                height="calc(100% - 140px)" :data-source="bikeDataLeast" :columns="columns"
                @selection-change="handleSelectionChange">
                <template #count="{ data }">
                    <span>{{ data.count }}</span>
                </template>
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
        </div>
    </div>
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
            bikeDataMost: [],
            bikeDataLeast: [],
            columns: [
                { prop: 'count', label: '使用次数', width: '150' },
                { prop: 'bike_id', label: '单车ID', width: '180' },
                { prop: 'production_date', label: '生产日期', width: '180' },
                { prop: 'market_date', label: '上市日期', width: '180' },
                { prop: 'warranty_date', label: '保修日期', width: '180' },
                { prop: 'using_condition', label: '使用情况', width: '180' },
                { prop: 'current_location', label: '当前位置', width: '180' },
                { prop: 'usage_record', label: '使用记录', width: '180' }
            ],
            selectedType: 'most',
        }
    },

    mounted() {
        this.fetchDataMost();
        this.fetchDataLeast();
    },
    methods: {
        fetchDataMost() {
            axios.post('/data', {
                task: "DataHotBike",
                consider: "most"
            })
                .then(response => {
                    console.log('data Response mosthotBike:', response.msg);
                    if (response.type === 'Ok') {
                        this.bikeDataMost = response.msg;
                        console.log("mostHotBike:", this.bikeDataMost)
                    } else {
                        this.$message.error("没有符合要求的单车!")
                    }
                })
                .catch(error => {
                    console.error('获取单车信息失败', error);
                });
        },
        fetchDataLeast() {
            axios.post('/data', {
                task: "DataHotBike",
                consider: "least"
            })
                .then(response => {
                    console.log('data Response leasthotBike:', response.msg);
                    if (response.type === 'Ok') {
                        this.bikeDataLeast = response.msg;
                        console.log("leastHotBike:", this.bikeDataLeast)
                    } else {
                        this.$message.error("没有符合要求的单车!")
                    }
                })
                .catch(error => {
                    console.error('获取单车信息失败', error);
                });
        },
        handleCommand(command) {
            if (command === 'most') {
                this.selectedType = 'most';
            } else if (command === 'least') {
                this.selectedType = 'least';
            }
        },
    }
}
</script>