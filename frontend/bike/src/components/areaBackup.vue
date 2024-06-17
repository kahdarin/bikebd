<template>
    <div id="container" style="width: 100%; height: 650px;"></div>


</template>
  
<script>

import MapContainer from '@/components/MapContainer.vue';
import AMapLoader from '@amap/amap-jsapi-loader';

export default {
    name: 'ForbidArea',
    mounted() {
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
                    zoom: 11, //地图级别
                    center: [121.5, 31.23], //地图中心点
                    layers: [layer], //layer为创建的默认图层
                });

                //异步加载控件
                AMap.plugin("AMap.ToolBar", function () {
                    var toolbar = new AMap.ToolBar(); //缩放工具条实例化
                    map.addControl(toolbar);
                });
            })
            .catch((e) => {
                console.error(e); //加载错误提示
            });
    }
}

</script>
  
<style scoped>
/* 你可以在这里添加一些样式 */
</style>