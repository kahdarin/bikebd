<template>
    <div class="input-left">
        <el-button v-if="showCreate" type="primary" size="small" style="margin-right: 15px;" plain
            @click="toggleShowForm">添加禁停区</el-button>
        <el-dialog v-model="showForm" @opened="initMapCreate" @closed="destroyMap">
            <template #header>
                <div style="margin-left: 1%;">
                    <span>添加禁停区</span>
                </div>
            </template>
            <el-form :model="newArea" label-width="120px" style="margin-left: -7%;">
                <el-form-item label="禁停区ID">
                    <el-input v-model="newArea.area_id" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="顶点坐标" required>
                    <el-input v-model="newArea.vertexs" autocomplete="off"></el-input>
                </el-form-item>
                <div id="createContainer" style="margin-left: 10%; width: 90%; height: 380px;"></div>
                <el-form-item style="margin-top: 10px; margin-left: -11%;">
                    <el-button type="primary" @click="toggleCreateArea">{{ polygonCreateEditing ? '完成编辑' : '开始创建'
                    }}</el-button>
                    <div style="margin-left: 45%;">
                        <el-button type="primary" @click="submitNewArea">提交</el-button>
                        <el-button @click="showForm = false">取消</el-button>
                    </div>
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
        :show-map-bike="showMap">
        <template #area_id="{ data }">
            <span>{{ data.area_id }}</span>
        </template>
        <template #vertex="{ data }">
            <span>{{ data.vertex }}</span>
        </template>
        <template #bikes="{ data }">
            <span>{{ data.bikes }}</span>
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

    <el-dialog v-model="mapVisible" width="60%" @opened="initMapRead" @closed="destroyMap" top="10vh">
        <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <el-button type="primary" @click="toggleBikeVisibility">显示单车</el-button>
                <el-button type="primary" @click="toggleEditArea">{{ isEditing ? '完成编辑' : '编辑区域' }}</el-button>
            </div>
        </template>

        <div id="readContainer" style="width: 100%; height: 600px;"></div>
    </el-dialog>
</template>

<script>
import axios from 'axios';
import SneTable from './table.vue';
import { InfoFilled } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import AMapLoader from '@amap/amap-jsapi-loader';
import { useStore } from 'vuex'

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
            showCreate: true,
            showDelete: true,
            showOperate: true,
            showMap: true,
            areaData: [],
            mapVisible: false,
            currentAreaID: '',
            map: '',
            bikeLayerVisible: false,
            maplayer: '',
            labelsLayer: '',
            polygonEditor: '',
            polygonIsEditing: false,
            polygonCreateEditing: false,
            currentArea: {
                vertices: [],
                area_id: '',
            },
            tmpPolygon: '',
            isEditing: false,
            filters: {
                area_id: '',
                vertexs: ''
            },
            columns: [
                { prop: 'area_id', label: '禁停区ID', width: '180' },
                { prop: 'vertex', label: '顶点坐标', width: '500' },
                { prop: 'bikes', label: '区域内单车', width: '500' }
            ]
        };
    },
    mounted() {
        this.fetchData();
        const store = useStore();
        const authority = store.getters.getAuthority;
        console.log("authority", authority);
        if (authority === 'staff') {
            this.showDelete = false;
            this.showOperate = false;
            this.showCreate = false;
        }
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
            this.tmpPolygon = polygon;
            map.add(polygon);
            map.setFitView([polygon])
        },
        initMapRead() {
            console.log("initMap1")
            AMapLoader.load({

                key: "bc6f24d83744f335f42197e23a32c04a", //申请好的Web端开发者key，调用 load 时必填
                version: "2.0", //指定要加载的 JS API 的版本，缺省时默认为 1.4.15
            })
                .then((AMap) => {
                    console.log("initMap2")
                    //JS API 加载完成后获取AMap对象
                    const layer = new AMap.createDefaultLayer({
                        zooms: [3, 20], //可见级别
                        visible: true, //是否可见
                        opacity: 1, //透明度
                        zIndex: 0, //叠加层级
                    });

                    const map = new AMap.Map("readContainer", {
                        viewMode: "2D", //默认使用 2D 模式
                        zoom: 11.5, //地图级别
                        center: [121.5, 31.23], //地图中心点
                        layers: [layer], //layer为创建的默认图层
                    });
                    this.map = map;
                    this.maplayer = layer;
                    this.addPolygon(map, this.currentArea.vertices);
                    //异步加载控件
                    AMap.plugin("AMap.ToolBar", function () {
                        var toolbar = new AMap.ToolBar(); //缩放工具条实例化
                        map.addControl(toolbar);
                    });
                    console.log("initMap3")
                })
                .catch((e) => {
                    console.error(e); //加载错误提示
                });

        },
        initMapCreate() {
            //console.log("initMap1")
            AMapLoader.load({

                key: "bc6f24d83744f335f42197e23a32c04a", //申请好的Web端开发者key，调用 load 时必填
                version: "2.0", //指定要加载的 JS API 的版本，缺省时默认为 1.4.15
            })
                .then((AMap) => {
                    this.Amap = AMap;
                    //console.log("initMap2")
                    //JS API 加载完成后获取AMap对象
                    const layer = new AMap.createDefaultLayer({
                        zooms: [3, 20], //可见级别
                        visible: true, //是否可见
                        opacity: 1, //透明度
                        zIndex: 0, //叠加层级
                    });

                    const map = new AMap.Map("createContainer", {
                        viewMode: "2D", //默认使用 2D 模式
                        zoom: 11.5, //地图级别
                        center: [121.5, 31.23], //地图中心点
                        layers: [layer], //layer为创建的默认图层
                    });
                    this.map = map;
                    //异步加载控件
                    AMap.plugin(["AMap.ToolBar", "AMap.PolygonEditor"], function () {
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
            //this.currentAreaID = data.area_id;
            const vertexString = data.vertex;
            this.currentArea.vertices = vertexString.split('#').map(point => {
                const [lng, lat] = point.split(',');
                return [parseFloat(lng), parseFloat(lat)];
            });
            this.currentArea.area_id = data.area_id;
        },
        destroyMap() {
            if (this.map) {
                this.map.destroy();
                this.map = '';
            }
        },
        toggleCreateArea() {
            this.polygonCreateEditing = !this.polygonCreateEditing;
            AMap.plugin(["AMap.PolygonEditor"], () => {
                if (this.polygonCreateEditing) {
                    var polygonEditor = new AMap.PolygonEditor(this.map);
                    this.polygonEditor = polygonEditor;
                    this.polygonEditor.open(); // 开启编辑模式
                } else {
                    console.log("test302")
                    this.tmpPolygon = this.polygonEditor.getTarget();
                    console.log("target:", this.tmpPolygon.getPath())
                    let path = this.tmpPolygon.getPath().map(point => `${point.lng},${point.lat}`).join('#');
                    this.newArea.vertexs = path;
                    console.log("path:", path);
                    this.polygonEditor.close(); // 关闭编辑模式
                    console.log("Editor closed")
                }
            });
        },

        toggleEditArea() {
            this.isEditing = !this.isEditing;
            AMap.plugin(["AMap.PolygonEditor"], () => {
                var polygonEditor = new AMap.PolygonEditor(this.map, this.tmpPolygon);
                if (this.isEditing) {
                    polygonEditor.open(); // 开启编辑模式
                } else {
                    this.tmpPolygon = polygonEditor.getTarget();
                    console.log("target:", this.tmpPolygon.getPath())
                    let path = this.tmpPolygon.getPath().map(point => `${point.lng},${point.lat}`).join('#');
                    polygonEditor.setTarget();
                    polygonEditor.close(); // 关闭编辑模式
                    console.log("Editor closed")
                    this.handleUpdate({
                        area_id: this.currentArea.area_id,
                        vertex: path
                    });

                }
            });
        },
        toggleBikeVisibility() {
            if (this.bikeLayerVisible) {
                if (this.labelsLayer) {
                    this.map.remove(this.labelsLayer);

                    this.labelsLayer = null;
                }
                this.bikeLayerVisible = false;
            } else {
                axios.post('/read', {
                    task: "ReadArea",
                    show_bike: true,
                    area_id: this.currentArea.area_id,
                })
                    .then(response => {
                        if (response.type === 'Ok') {
                            const bikeInformation = response.msg[0].bikes;
                            console.log("bikeInformation", bikeInformation);
                            this.labelsLayer = new AMap.LabelsLayer({
                                zooms: [3, 20],
                                zIndex: 1000,
                                collision: true,
                                allowCollision: true
                            });
                            const icon = {
                                type: "image",
                                image: "./src/assets/point.png",
                                size: [36, 30],
                                anchor: "center",
                            };
                            bikeInformation.forEach(bike => {
                                const [lng, lat] = bike.current_location.split(',');
                                const labelMarker = new AMap.LabelMarker({
                                    icon: icon,
                                    zIndex: 16,
                                    rank: 1,
                                    position: new AMap.LngLat(lng, lat),
                                    name: bike.bike_id
                                });
                                this.labelsLayer.add(labelMarker);
                            });
                            this.map.add(this.labelsLayer);
                            this.bikeLayerVisible = true;
                        } else {
                            this.$message.error('无搜索结果');
                        }
                    })
                    .catch(error => {
                        console.error('搜索失败', error);
                    });
            }
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
                        this.newArea.area_id = '';
                        this.newArea.vertexs = '';
                    } else {
                        this.$message.error('创建失败');
                    }
                })
                .catch(error => {
                    console.error('创建失败', error);
                });
        },
        fetchData() {
            axios.post('/read', {
                task: "ReadArea",
                show_bike: true
            })
                .then(response => {
                    console.log('Response ReadArea:', response.msg);
                    if (response.type === 'Ok') {
                        this.areaData = response.msg.map(area => ({
                            ...area,
                            bikes: (area.bikes || []).map(bike => bike.bike_id).join(', ')
                        }));
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