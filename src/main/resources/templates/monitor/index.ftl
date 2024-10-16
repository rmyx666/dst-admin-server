<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <#import "../system/user/spring.ftl" as spring>
    <title><@spring.message code="setting.player.title"/></title>
    <#include "../common/header.ftl"/>
    <script src="https://cdn.jsdelivr.net/npm/echarts/dist/echarts.min.js"></script>
</head>

<body>
<div id="app">
    <el-form inline>
        <el-form-item label="开始时间">
            <el-date-picker v-model="startTime" type="datetime" placeholder="选择开始时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
            <el-date-picker v-model="endTime" type="datetime" placeholder="选择结束时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="粒度">
            <el-select v-model="type" placeholder="选择时间粒度">
                <el-option label="分钟" value="minute"></el-option>
                <el-option label="小时" value="hour"></el-option>
                <el-option label="天" value="day"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="fetchPlayerOnlineTrend">获取趋势图</el-button>
        </el-form-item>
    </el-form>

    <div id="trendChart" style="width: 100%; height: 400px;"></div>

    <!-- 新增的玩家数据展示区域 -->
    <el-table :data="playerData" style="width: 100%">
        <el-table-column prop="userId" label="用户ID"></el-table-column>
        <el-table-column prop="name" label="玩家昵称"></el-table-column>
        <el-table-column prop="prefab" label="玩家角色"></el-table-column>
        <el-table-column prop="playerage" label="生存天数"></el-table-column>
    </el-table>
</div>

<script src="https://cdn.jsdelivr.net/npm/vue@2"></script>
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/element-ui/lib/index.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/element-ui/lib/theme-chalk/index.css">
<script>
    new Vue({
        el: '#app',
        data() {
            return {
                roomId: RoomUtil.getRoomId(), // 获取 roomId
                serverId: RoomUtil.getServerId(), // 获取 roomId
                startTime: '',  // 开始时间
                endTime: '',    // 结束时间
                type: 'hour',   // 默认粒度为小时
                trendData: [],  // 存储返回的趋势数据
                playerData: [], // 存储返回的玩家数据
                chart: null     // 存储ECharts实例
            };
        },
        created() {
            // 设置默认的时间范围为最近三天
            const now = new Date();
            this.endTime = now;
            this.startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);  // 三天前
        },
        mounted() {
            // 初始化ECharts
            this.chart = echarts.init(document.getElementById('trendChart'));
            this.fetchPlayerOnlineTrend();
        },
        methods: {
            fetchPlayerOnlineTrend() {
                const param = {
                    roomId: this.roomId,
                    startTime: this.startTime,
                    endTime: this.endTime,
                    type: this.type
                };

                axios.post("/monitor/getPlayerOnlineTrend?roomId=" + this.roomId+"&serverId="+this.serverId, param)
                    .then(response => {
                        this.trendData = response.data.data;  // 将接口返回的趋势数据赋值给 trendData
                        this.renderChart();
                        // 调用获取玩家在线生存天数接口
                        this.fetchPlayerOnlineAge();
                    })
                    .catch(error => {
                        console.error('Error fetching player online trend:', error);
                    });
            },
            fetchPlayerOnlineAge() {
                const param = {
                    roomId: this.roomId
                };

                axios.post("/monitor/getPlayerOnlineAge?roomId=" + this.roomId+"&serverId="+this.serverId, param)
                    .then(response => {
                        this.playerData = response.data.data; // 将接口返回的玩家数据赋值给 playerData
                    })
                    .catch(error => {
                        console.error('Error fetching player online age:', error);
                    });
            },
            renderChart() {
                const timePeriods = this.trendData.map(item => item.timePeriod);
                const counts = this.trendData.map(item => ({
                    count: item.count,
                    playerLogs: item.playerLogs // 将 playerLogs 包含在这里
                }));

                // 生成 ECharts 图表配置
                const option = {
                    title: {
                        text: '玩家在线情况趋势图',
                    },
                    tooltip: {
                        trigger: 'axis',
                        formatter: (params) => {
                            const trendInfo = counts[params[0].dataIndex]; // 使用 dataIndex 获取对应的数据
                            let content = params[0].name + '<br/>玩家数量: ' + trendInfo.count + '<br/>玩家详情:';

                            if (Array.isArray(trendInfo.playerLogs)) {
                                trendInfo.playerLogs.forEach(log => {
                                    content += '<br/>用户ID: ' + log.userId + ', 昵称: ' + log.name + ', 角色: ' + log.prefab + ', 生存天数: ' + log.playerage;
                                });
                            } else {
                                content += '<br/>没有玩家详情';
                            }

                            return content;
                        }
                    },
                    xAxis: {
                        type: 'category',
                        data: timePeriods,
                    },
                    yAxis: {
                        type: 'value',
                    },
                    series: [{
                        name: '玩家数量',
                        type: 'line',
                        data: counts.map(item => item.count), // 只获取 count
                        smooth: true, // 平滑曲线
                        itemStyle: {
                            color: '#5470c6',
                        },
                    }],
                };

                // 使用刚指定的配置项和数据显示图表
                this.chart.setOption(option);
            }
        }
    });
</script>

</body>
</html>
