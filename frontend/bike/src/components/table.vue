<template>
    <div class="table_out">
        <el-scrollbar max-height="550px" class="scrollbar">
            <el-table ref="sneTable" :data="paginatedData" :row-key="rowKey" v-loading="loading"
                :default-expand-all="isExpandAll" :tree-props="treeProps" :border="border" :stripe="stripe"
                :height="height" :max-height="maxHeight" size="default" @row-click="rowClick"
                @selection-change="selectionChange" @sort-change="sortChange" @current-change="currentRowChange"
                :span-method="onSpanMethod" style="width: 100%; font-size: 14px;">
                <el-table-column v-if="selector" reserve-selection fixed="left" type="selection" header-align="center"
                    align="center" width="50" :selectable="checkSelectTable" />
                <el-table-column v-if="index" align="center" fixed="left" type="index" label="序号"
                    width="100"></el-table-column>
                <template v-for="(column, i) in columns">
                    <el-table-column v-if="isShow(column)" :prop="column.prop" :label="column.label"
                        :width="column.width" :min-width="column.minWidth" :sortable="true"
                        :align="column.align || 'center'" :fixed="column.fixed" :show-overflow-tooltip="showTooltip">
                        <template #default="{ row }">
                            <span v-if="!column.slotName && editableRow !== row">
                                {{ row[column.prop] || row[column.prop] === 0 ? row[column.prop] : '' }}</span>
                            <el-input v-else-if="editableRow === row" v-model="row[column.prop]" />
                            <slot :name="column.slotName" :data="row" />
                        </template>
                    </el-table-column>
                </template>

                <el-table-column v-if="showOperate" label="操作" align="center" width='150px'>
                    <template #default="{ row }">
                        <el-button link type="primary" :icon="editableRow === row ? 'Check' : 'Edit'"
                            @click="editableRow === row ? handleUpdate(row) : editableRow = row">
                            {{ editableRow === row ? '保存' : '修改' }}
                        </el-button>
                        <el-popconfirm v-if="showDelete" confirm-button-text="Yes" cancel-button-text="No" :icon="InfoFilled"
                            icon-color="#626AEF" title="确认删除?" @confirm="() => confirmDelete(row)">
                            <template #reference>
                                <el-button link type="primary" icon="Delete">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
        </el-scrollbar>
        <div class="pagination_style">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[20, 100, 200, 300, 400]" :small="small" :disabled="disabled" :background="background"
                layout="total, sizes, prev, pager, next, jumper" :total="dataSource.length"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { defineProps } from 'vue'
import { Delete, Edit, Search, Share, Upload, InfoFilled } from '@element-plus/icons-vue'


const props = defineProps({
    // 是否带边框
    border: { type: Boolean, default: true },
    // 是否带斑马纹
    stripe: { type: Boolean, default: false },
    // 是否有序号
    index: { type: Boolean, default: true },
    // 表格高度 不传默认是计算后的高度
    height: { type: [Number, String], default: null },
    // 表格最大高度
    maxHeight: { type: Number, default: null },
    // 表格大小
    size: { type: String, default: "large" },
    // 表数据
    dataSource: { type: Array, default: () => [] },
    // 加载
    loading: { type: Boolean, default: false },
    // 表头
    columns: { type: [String, Object, Array], required: true },
    // 是否显示tooltip
    showTooltip: { type: Boolean, default: true },
    // 是否多选
    selector: { type: Boolean, default: false },
    // 唯一标识
    rowKey: { type: String, default: null },
    // 是否展开
    isExpandAll: { type: Boolean, default: false },
    // 渲染嵌套数据的配置选项
    treeProps: { type: Object, default: () => { } },
    // 是否显示删除按钮
    showDelete: { type: Boolean, default: false },

    showOperate: { type: Boolean, default: false },

})

const currentPage = ref(1)
const pageSize = ref(100)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)
const sortProp = ref(null)
const sortOrder = ref(null)
const editableRow = ref(null)

const paginatedData = computed(() => {
    let data = [...props.dataSource]
    if (sortProp.value) {
        data.sort((a, b) => {
            if (sortOrder.value === 'ascending') {
                return a[sortProp.value] > b[sortProp.value] ? 1 : -1
            } else {
                return a[sortProp.value] < b[sortProp.value] ? 1 : -1
            }
        })
    }
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return data.slice(start, end)
})

const handleSizeChange = (newSize) => {
    pageSize.value = newSize
}

const handleCurrentChange = (newPage) => {
    currentPage.value = newPage
}

const emit = defineEmits(['delete', 'update'])

function onSpanMethod({ rowIndex, columnIndex }) {
    // let obj = { rowspan: 1, colspan: 1 };
    // emit("onSpanMethod", { rowIndex, columnIndex }, val => {obj = val;});
    // return obj;
}
function isShow(c) {
    return c.show === undefined ? true : c.show;
}
// 当前行变化
function currentRowChange(row) {
    // if (row) emit("currentRowChange", row);
}
function sortChange({ column, prop, order }) {
    sortProp.value = prop
    sortOrder.value = order
    emit("sortChange", { column, prop, order });
}
// 多选
function selectionChange(values) {
    emit("selection-change", values);
}

// 设置多选框(数据增加selectionIsSelect字段，判断当前是否可勾选)
function checkSelectTable(row) {
    return row.selectionIsSelect !== undefined ? row.selectionIsSelect : true;
}

// // 点击某一行
function rowClick(row) {
    // if (row) emit("row-click", row);
}

// 确认删除
function confirmDelete(row) {
    emit('delete', row);
    //console.log("table_delete",row)
}

// 修改
function handleUpdate(row) {
    emit('update', row);
    editableRow.value = null;
    console.log("table_update")
}

</script>

<style scoped>
.pagination_style {
    margin-top: 20px;
    margin-left: 20px;
}

.table_out {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    height: 100%;
    margin-left: 100px;
    overflow-x: hidden;
    /* 隐藏横向滚动条 */
    width: 1050px;
}

.scrollbar {
    width: 100%;
    /* 确保滚动条容器宽度为100% */
}
</style>