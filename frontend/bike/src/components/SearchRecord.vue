<template>
    <div class="input-left">
        <el-button type="primary" size="small" style="margin-right: 15px;" plain @click="toggleShowForm">添加记录</el-button>
        <el-dialog v-model="showForm" title="添加记录">
            <el-form :model="newRecord" label-width="120px">
                <el-form-item label="记录ID" required>
                    <el-input v-model="newRecord.record_id" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="记录名称" required>
                    <el-input v-model="newRecord.record_name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" required>
                    <el-input v-model="newRecord.description" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="日期" required>
                    <el-date-picker v-model="newRecord.date" type="date" placeholder="选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitNewRecord">提交</el-button>
                    <el-button @click="showForm = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-input v-model="filters.record_id" class="input-style" placeholder="记录ID" />
        <el-input v-model="filters.record_name" class="input-style" placeholder="记录名称" />
        <el-input v-model="filters.description" class="input-style" placeholder="描述" />
        <el-date-picker v-model="filters.date" type="date" placeholder="选择日期" class="input-style"></el-date-picker>
        <el-button link type="primary" icon="Search" style="margin-left: 8px;" @click="searchRecords">搜索</el-button>
        <el-button link type="primary" style="margin-left: 5px;" @click="clearFilters">
            <el-icon style="margin-right: 5px;">
                <CircleClose />
            </el-icon> 清除
        </el-button>
    </div>
    <sne-table @delete="confirmDelete" @update="handleUpdate" ref="sRef" :loading="loading" :stripe="stripe"
        :selector="true" size="mini" row-key="record_id" height="calc(100% - 140px)" :data-source="recordData"
        :columns="columns" @selection-change="handleSelectionChange" :show-delete="showDelete" :show-operate="showOperate">
        <template #record_name="{ data }">
            <span>{{ data.record_name }}</span>
        </template>
        <template #description="{ data }">
            <span>{{ data.description }}</span>
        </template>
        <template #date="{ data }">
            <span>{{ data.date }}</span>
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
                record_id: '',
                record_name: '',
                description: '',
                date: ''
            },
            showForm: false,
            loading: false,
            stripe: true,
            showDelete: true,
            showOperate: true,
            recordData: [],
            filters: {
                record_id: '',
                record_name: '',
                description: '',
                date: ''
            },
            columns: [
                { prop: 'record_id', label: '记录ID', width: '180' },
                { prop: 'record_name', label: '记录名称', width: '180' },
                { prop: 'description', label: '描述', width: '180' },
                { prop: 'date', label: '日期', width: '180' }
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
            axios.post('/create', {
                task: "CreateRecord",
                record_id: this.newRecord.record_id,
                record_name: this.newRecord.record_name,
                description: this.newRecord.description,
                date: this.newRecord.date
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
            axios.post('/read', {
                task: "ReadRecord",
                record_id: this.filters.record_id,
                record_name: this.filters.record_name,
                description: this.filters.description,
                date: this.filters.date
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
            this.filters.record_id = '';
            this.filters.record_name = '';
            this.filters.description = '';
            this.filters.date = '';
        },
        handleSelectionChange(selectedItems) {
            console.log('Selected items:', selectedItems);
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