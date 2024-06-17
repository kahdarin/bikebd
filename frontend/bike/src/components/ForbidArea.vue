<template>
    <div class="input-left">
        <el-button type="primary" size="small" style="margin-right: 15px;" plain @click="toggleShowForm">添加禁停区</el-button>
        <el-dialog v-model="showForm" title="添加禁停区">
            <el-form :model="newArea" label-width="120px">
                <el-form-item label="禁停区ID">
                    <el-input v-model="newArea.area_id" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="顶点坐标" required>
                    <el-input v-model="newArea.vertexs" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitNewArea">提交</el-button>
                    <el-button @click="showForm = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-input v-model="filters.area_id" class="input-style" placeholder="禁停区ID" />
        <el-input v-model="filters.vertexs" class="input-style" placeholder="顶点坐标" />
        <el-button link type="primary" icon="Search" style="margin-left: 8px;" @click="searchAreas">搜索</el-button>
        <el-button link type="primary" style="margin-left: 5px;" @click="clearFilters">
            <el-icon style="margin-right: 5px;">
                <CircleClose />
            </el-icon> 清除
        </el-button>
    </div>
    <sne-table @delete="confirmDelete" @update="handleUpdate" @map="handleMap" ref="sRef" :loading="loading"
        :stripe="stripe" :selector="true" size="mini" row-key="area_id" height="calc(100% - 140px)" :data-source="areaData"
        :columns="columns" @selection-change="handleSelectionChange" :show-delete="showDelete" :show-operate="showOperate"
        :show-map="showMap">
        <template #area_id="{ data }">
            <span>{{ data.area_id }}</span>
        </template>
        <template #vertex="{ data }">
            <span>{{ data.vertex }}</span>
        </template>
        <template #operate="{ data }">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(data)">修改</el-button>
            <el-popconfirm confirm-button-text="Yes" cancel-button-text="No" :icon="InfoFilled" icon-color="#626AEF"
                title="确认删除?" @confirm="() => confirmDelete(data)">
                <template #reference>
                    <el-button link type="primary" icon="Delete">删除</el-button>
                </template>
            </el-popconfirm>
            <el-button link type="primary" icon="Map" @click="showMap(data)">地图</el-button>
        </template>
    </sne-table>

    <el-dialog v-model="mapVisible" width="60%" @opened="initMap" top="10vh">
        <template #title>
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <el-button type="primary" @click="toggleBikeVisibility">显示单车</el-button>
            </div>
        </template>

        <div id="container" style="width: 100%; height: 600px;"></div>
    </el-dialog>
</template>

<script>
import axios from 'axios';
import SneTable from './table.vue';
import { InfoFilled } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import AMapLoader from '@amap/amap-jsapi-loader';

export default {
    components: {
        SneTable,
        InfoFilled
    },
    data() {
        return {
            newArea: {
                area_id: '',
                vertexs: ''
            },
            showForm: false,
            loading: false,
            stripe: true,
            showDelete: true,
            showOperate: true,
            showMap: true,
            areaData: [],
            mapVisible: false,
            currentAreaID: '',
            map: '',
            vertices: [],
            filters: {
                area_id: '',
                vertexs: ''
            },
            columns: [
                { prop: 'area_id', label: '禁停区ID', width: '180' },
                { prop: 'vertex', label: '顶点坐标', width: '500' }
            ]
        };
    },
    mounted() {
        this.fetchData();
    },
    methods: {
        addPolygon(map, vertices) {
            const polygon = new AMap.Polygon({
                path: vertices,
                strokeColor: "#FF33FF", // 线颜色
                strokeOpacity: 0.2, // 线透明度
                strokeWeight: 3, // 线宽
                fillColor: "#1791fc", // 填充色
                fillOpacity: 0.35 // 填充透明度
            });
            map.add(polygon);
        },
        initMap() {
            //console.log("initMap")
            AMapLoader.load({
                key: "bc6f24d83744f335f42197e23a32c04a", //申请好的Web端开发者key，调用 load 时必填
                version: "2.0", //指定要加载的 JS API 的版本，缺省时默认为 1.4.15
            })
                .then((AMap) => {
                    //JS API 加载完成后获取AMap对象
                    const layer = new AMap.createDefaultLayer({
                        zooms: [3, 20], //可见级别
                        visible: true, //是否可见
                        opacity: 1, //透明度
                        zIndex: 0, //叠加层级
                    });

                    const map = new AMap.Map("container", {
                        viewMode: "2D", //默认使用 2D 模式
                        zoom: 11.5, //地图级别
                        center: [121.5, 31.23], //地图中心点
                        layers: [layer], //layer为创建的默认图层
                    });
                    this.map = map;
                    this.addPolygon(map, this.vertices);
                    //异步加载控件
                    AMap.plugin("AMap.ToolBar", function () {
                        var toolbar = new AMap.ToolBar(); //缩放工具条实例化
                        map.addControl(toolbar);
                    });
                })
                .catch((e) => {
                    console.error(e); //加载错误提示
                });

        },
        handleMap(data) {
            this.mapVisible = true;
            this.currentAreaID = data.area_id;
            const vertexString = data.vertex;
            this.vertices = vertexString.split('#').map(point => {
                const [lng, lat] = point.split(',');
                return [parseFloat(lng), parseFloat(lat)];
            });
        },
        toggleBikeVisibility() {
            axios.post('/read', {
                task: "ReadArea",
                show_bike: true,
                area_id: this.currentAreaID,
            })
                .then(response => {
                    if (response.type === 'Ok') {

                        const bikeImformation = response.msg[0].bikes;
                        console.log("bikeImformation", bikeImformation)
                        const labelsLayer = new AMap.LabelsLayer({
                            zooms: [3, 20],
                            zIndex: 1000,
                            collision: true,
                            allowCollision: true
                        });
                        const text = {
                            content: "test", //要展示的文字内容
                            direction: "right", //文字方向，有 icon 时为围绕文字的方向，没有 icon 时，则为相对 position 的位置
                            offset: [-20, -5], //在 direction 基础上的偏移量
                            //文字样式
                            style: {
                                fontSize: 12, //字体大小
                                fillColor: "#22886f", //字体颜色
                                strokeColor: "#fff", //描边颜色
                                strokeWidth: 2, //描边宽度
                            },
                        };
                        const icon = {
                            type: "image", //图标类型，现阶段只支持 image 类型
                            image: "./src/assets/point.png", //可访问的图片 URL
                            size: [48, 20], //图片尺寸
                            anchor: "center", //图片相对 position 的锚点，默认为 bottom-center
                        };
                        bikeImformation.forEach(bikeImformation => {

                            const [lng, lat] = bikeImformation.current_location.split(',');
                            const labelMarker = new AMap.LabelMarker({
                                //text: text,
                                icon: icon,
                                zIndex: 16,
                                rank: 1,
                                position: new AMap.LngLat(lng, lat),
                                name: bikeImformation.bike_id
                            });
                            labelsLayer.add(labelMarker);

                        });
                        this.map.add(labelsLayer);
                    } else {
                        this.$message.error('无搜索结果');
                    }
                })
                .catch(error => {
                    console.error('搜索失败', error);
                });
        },
        toggleShowForm() {
            this.showForm = true;
        },
        submitNewArea() {
            axios.post('/create', {
                task: "CreateArea",
                "area_id": this.newArea.area_id,
                vertex: this.newArea.vertexs
            })
                .then(response => {
                    if (response.type === 'Ok') {
                        this.$message.success('创建成功');
                        this.fetchData();
                        this.showForm = false;
                    } else {
                        this.$message.error('创建失败');
                    }
                })
                .catch(error => {
                    console.error('创建失败', error);
                });
        },
        fetchData() {
            axios.post('/read', { task: "ReadArea" })
                .then(response => {
                    console.log('Response ReadArea:', response.msg);
                    if (response.type === 'Ok') {
                        this.areaData = response.msg;
                        console.log('this.areaData:', this.areaData);
                    } else {
                        this.$message.error('获取数据失败');
                    }
                })
                .catch(error => {
                    console.error('获取数据失败', error);
                });
        },

        searchAreas() {
            axios.post('/read', {
                task: "ReadArea",
                show_bike: true,
                area_id: this.filters.area_id,
            })
                .then(response => {
                    if (response.type === 'Ok') {
                        this.areaData = response.msg;
                    } else {
                        this.$message.error('无搜索结果');
                    }
                })
                .catch(error => {
                    console.error('搜索失败', error);
                });
        },
        clearFilters() {
            this.filters.area_id = '';
            this.filters.vertexs = '';
            this.fetchData();
        },
        handleSelectionChange(selectedItems) {
            console.log('Selected items:', selectedItems);
        },
        confirmDelete(data) {
            axios.post('/delete', {
                task: "DeleteArea",
                area_id: data.area_id
            })
                .then(response => {
                    if (response.type === 'Ok') {
                        this.$message.success('删除成功');
                        this.fetchData();
                    } else {
                        this.$message.error('删除失败');
                    }
                })
                .catch(error => {
                    console.error('删除失败', error);
                });
        },
        handleUpdate(data) {
            console.log("yes!!!", data)
            axios.post('/update', {
                task: "UpdateArea",
                area_id: data.area_id,
                vertex: data.vertex
            })
                .then(response => {
                    if (response.type === 'Ok') {
                        this.$message.success('修改成功');
                    } else {
                        this.$message.error('修改失败');
                    }
                })
                .catch(error => {
                    console.error('修改失败', error);
                });
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