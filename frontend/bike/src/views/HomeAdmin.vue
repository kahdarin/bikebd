<template>
    <el-container class="home-container">
        <!-- el-header头部区域 -->
        <el-header>
            <div>
                <img src="../assets/misaki.jpg" alt="">
                <span>单车后台管理系统</span>
            </div>
            <el-button type="info" @click="logout">退出登录</el-button>
        </el-header>
        <!-- el-container主体区域 -->
        <el-container>
            <!-- el-aside侧边栏 -->
            <el-aside width="200px">
                <!-- 侧边栏菜单区域 -->
                <!-- 背景颜色 -->
                <el-menu background-color="#333744" text-color="#fff" active-text-color="#409eff">
                    <!-- 动态绑定 :index需要字符串格式 -->
                    <el-sub-menu :index="i.id + ''" v-for="i in menulist" :key="i.id">
                        <!-- 一级菜单 -->
                        <template v-slot:title>
                            <!-- 一级菜单图标 -->
                            <i :class="iconsObj[i.id]"></i>
                            <!-- 一级菜单文本 -->
                            <span>{{ i.authName }}</span>
                        </template>
                        <!-- 二级菜单 -->
                        <el-menu-item :index="j.id + ''" v-for="j in i.children" :key="j.id"
                            @click="navigateTo(j.authName)">
                            <template v-slot:title>
                                <!-- 二级菜单图标 -->
                                <i class="el-icon-menu"></i>
                                <!-- 二级菜单文本 -->
                                <span>{{ j.authName }}</span>
                            </template>

                        </el-menu-item>
                    </el-sub-menu>
                </el-menu>

            </el-aside>
            <!-- el-main右侧内容 -->
            <el-main>
                <account-data v-if="currentView === 'AccountData'"></account-data>
                <search-user v-if="currentView === 'SearchUser'"></search-user>
                <search-rider v-if="currentView === 'SearchRider'"></search-rider>
                <search-bike v-if="currentView === 'SearchBike'"></search-bike>
            </el-main>
        </el-container>
    </el-container>
</template>
<script>
import AccountData from '@/components/AccountData.vue'
import SearchUser from '@/components/SearchUser.vue'
import SearchRider from '@/components/SearchRider.vue'
import SearchBike from '@/components/SearchBike.vue'
import SearchRecord from '@/components/SearchRecord.vue'

export default {
    components: {
        AccountData,
        SearchUser,
        SearchRider,
        SearchBike,
        SearchRecord
    },
    data() {
        return {

            currentView: 'AccountData', // 默认显示 AccountData 组件
            // 直接定义菜单数据
            menulist: [
                {
                    id: '1',
                    authName: '员工管理',
                    children: [
                        { id: '11', authName: '用户概览', path: '/SearchUser' }
                    ]
                },
                {
                    id: '2',
                    authName: '数据管理',
                    children: [
                        { id: '21', authName: '骑行者管理', path: '/SearchUser' },
                        { id: '22', authName: '单车管理', path: '/account' },
                        { id: '23', authName: '订单管理', path: '/account' },
                        { id: '24', authName: '禁停区管理', path: '/account' }
                    ]
                },
                {
                    id: '3',
                    authName: '个人信息',
                    children: [
                        { id: '31', authName: '个人信息', path: '/HomeAdmin' }
                    ]
                },
                {
                    id: '4',
                    authName: '数据统计',
                    children: [
                        { id: '41', authName: '数据报表', path: '/account' }
                    ]
                }
            ],
            // 将id和类名对应起来
            iconsObj: {
                '1': 'iconfont icon-yonghu',
                '2': 'iconfont icon-shangpin',
                '3': 'iconfont icon-danju',
                '4': 'iconfont icon-baobiao',
            }
        }
    },
    methods: {
        logout() {
            window.sessionStorage.clear()
            this.$router.push('/login') // 重定向到登录页面
        },
        navigateTo(authName) {
            //this.$router.push(path);
            if (authName === '用户概览') {
                this.currentView = 'SearchUser';
            } else if (authName === '个人信息') {
                this.currentView = 'AccountData'; // 可以根据需要调整默认组件
            } else if (authName === '骑行者管理') {
                this.currentView = 'SearchRider'; // 可以根据需要调整默认组件
            } else if (authName === '单车管理') {
                this.currentView = 'SearchBike'; // 可以根据需要调整默认组件
            } else if (authName === '订单管理') {
                this.currentView = 'SearchRecord'; // 可以根据需要调整默认组件
            }
        }
    },
}
</script>

<style lang="less">
html,
body {
    height: 100%; // 确保 html 和 body 元素高度为100%
    margin: 0; // 移除默认边距
}

.home-container {
    /* 实现全屏 */
    //background-color: var(--el-color-primary-light-7);
    background-color: #eaedf1;
    height: 100%;
}

// .el-container {
//     //height: 500px;
//     height: 100%; // 确保 Element UI 容器高度为100%
// }

.el-header {
    background-color: #343d41;
    display: flex;
    justify-content: space-between;
    /*此时会留有空白*/
    margin-left: -20px;
    /*padding失效*/
    align-items: center;
    /*按钮上下居中*/

    /*文本*/
    color: #fff;
    font-size: 20px;

    >div {
        /*文本居中*/
        display: flex;
        align-items: center;

        span {
            margin-left: 15px;
        }

    }

}

.el-header img {
    width: 50px;
    /* 根据需要调整宽度 */
    height: 50px;
    /* 根据需要调整高度 */
}

.el-aside {
    height: 100%; // 侧边栏全屏
    height: 700px;
    width: 50px;
    background-color: #333744;
}

.el-main {
    height: 100%; // 菜单全屏
    //height: 600px;
    background-color: #eaedf1;
}

.iconfont {
    margin-right: 10px;
}
</style>