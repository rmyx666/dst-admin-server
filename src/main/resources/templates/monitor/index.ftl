<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <#import "../system/user/spring.ftl" as spring>
    <title><@spring.message code="setting.player.title"/></title>
    <#include "../common/header.ftl"/>
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
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

<script src="https://cdn.jsdelivr.net/npm/vue@2.7.14"></script>
<script src="https://cdn.jsdelivr.net/npm/axios@1.6.0/dist/axios.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/element-ui@2.15.13/lib/index.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/element-ui@2.15.13/lib/theme-chalk/index.css">
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
                roomTrendData: [],  // 存储房间操作趋势数据
                playerData: [], // 存储返回的玩家数据
                chart: null     // 存储ECharts实例
            };
        },
        created() {
            // 设置默认的时间范围为最近7天
            const now = new Date();
            this.endTime = now;
            this.startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);  // 7天前
        },
        mounted() {
            // 初始化ECharts
            this.chart = echarts.init(document.getElementById('trendChart'));
            this.fetchPlayerOnlineTrend();
        },
        methods: {
            // 获取玩家在线趋势
            fetchPlayerOnlineTrend() {
                const param = {
                    roomId: this.roomId,
                    startTime: this.startTime,
                    endTime: this.endTime,
                    type: this.type
                };

                axios.post("/monitor/getPlayerOnlineTrend?roomId=" + this.roomId + "&serverId=" + this.serverId, param)
                    .then(response => {
                        this.trendData = response.data.data;  // 将接口返回的趋势数据赋值给 trendData
                        this.fetchRoomOperationTrend(); // 获取房间操作趋势数据
                    })
                    .catch(error => {
                        console.error('Error fetching player online trend:', error);
                    });
            },

            // 获取房间操作趋势
            fetchRoomOperationTrend() {
                const param = {
                    roomId: this.roomId,
                    startTime: this.startTime,
                    endTime: this.endTime,
                    type: this.type
                };

                axios.post("/monitor/getRoomOperationTrend?roomId=" + this.roomId + "&serverId=" + this.serverId, param)
                    .then(response => {
                        this.roomTrendData = response.data.data; // 将接口返回的房间操作趋势数据赋值给 roomTrendData
                        this.renderChart(); // 合并数据并渲染图表
                        // 调用获取玩家在线生存天数接口
                        this.fetchPlayerOnlineAge();
                    })
                    .catch(error => {
                        console.error('Error fetching room operation trend:', error);
                    });
            },

            // 获取玩家在线生存天数
            fetchPlayerOnlineAge() {
                const param = {
                    roomId: this.roomId
                };

                axios.post("/monitor/getPlayerOnlineAge?roomId=" + this.roomId + "&serverId=" + this.serverId, param)
                    .then(response => {
                        this.playerData = response.data.data; // 将接口返回的玩家数据赋值给 playerData
                    })
                    .catch(error => {
                        console.error('Error fetching player online age:', error);
                    });
            },

            // 渲染趋势图
            renderChart() {
                const timePeriods = this.trendData.map(item => item.timePeriod);
                const counts = this.trendData.map(item => ({
                    count: item.count,
                    playerLogs: item.playerLogs, // 玩家在线趋势数据
                    timePeriod: item.timePeriod // 时间段
                }));

                // 合并房间操作趋势数据
                const roomCounts = this.roomTrendData.map(item => ({
                    count: item.count,
                    masterStatus: item.masterStatus,
                    cavesStatus: item.cavesStatus,
                    playDay: item.playDay,
                    timePeriod: item.timePeriod // 时间段
                }));

                // 生成 ECharts 图表配置
                const option = {
                    title: {
                        text: '房间监控',
                    },
                    tooltip: {
                        trigger: 'axis',
                        confine: true,
                        position: function(point, params, dom, rect, size) {
                            let x = point[0];
                            let y = point[1];

                            if (x + size.contentSize[0] > window.innerWidth - 20) {
                                x = point[0] - size.contentSize[0] - 20;
                            }

                            if (y + size.contentSize[1] > window.innerHeight - 20) {
                                y = point[1] - size.contentSize[1] - 20;
                            }

                            return [x, y];
                        },
                        formatter: (params) => {
                            const playerTrendInfo = counts[params[0].dataIndex];
                            const roomTrendInfo = roomCounts[params[1].dataIndex];

                            let content = '<div style="font-size: 12px; line-height: 1.6;">';
                            content += '<strong>时间:</strong> ' + playerTrendInfo.timePeriod + '<br/>';

                            content += '<strong>玩家数据:</strong><br/>';
                            content += '└ 数量: ' + playerTrendInfo.count + '<br/>';

                            if (Array.isArray(playerTrendInfo.playerLogs) && playerTrendInfo.playerLogs.length > 0) {
                                playerTrendInfo.playerLogs.forEach(log => {
                                    content += '└ ' + log.name + '(ID:' + log.userId + ') ' + log.prefab + ' ' + log.playerage + '天<br/>';
                                });
                            } else {
                                content += '└ 暂无玩家<br/>';
                            }

                            content += '<strong>房间数据:</strong><br/>';
                            content += '└ 地面: ' + (roomTrendInfo.masterStatus ? '启动' : '停止') + '<br/>';
                            content += '└ 洞穴: ' + (roomTrendInfo.cavesStatus ? '启动' : '停止') + '<br/>';
                            content += '└ 天数: ' + roomTrendInfo.playDay + '<br/>';
                            content += '</div>';

                            return content;
                        }

                    },


                    xAxis: {
                        type: 'category',
                        data: timePeriods,
                    },
                    yAxis: [
                        {
                            type: 'value',
                            name: '玩家数量',
                        },
                        {
                            type: 'value',
                            name: '房间操作数量',
                            position: 'right',
                            axisLine: {
                                show: true,
                            },
                            axisLabel: {
                                formatter: function(value) {
                                    return value;
                                }
                            },
                        }
                    ],
                    series: [
                        {
                            name: '玩家数量',
                            type: 'line',
                            data: counts.map(function(item) { return item.count; }), // 玩家在线趋势的 count
                            smooth: true,
                            itemStyle: {
                                color: '#5470c6',
                            },
                            yAxisIndex: 0,  // 指定使用左边的 Y 轴
                        },
                        {
                            name: '房间操作数量',
                            type: 'line',
                            data: roomCounts.map(function(item) { return item.count; }), // 房间操作趋势的 count
                            smooth: true,
                            itemStyle: {
                                color: '#ff7f50',
                            },
                            yAxisIndex: 1,  // 指定使用右边的 Y 轴
                        }
                    ],
                };

                // 使用刚指定的配置项和数据显示图表
                this.chart.setOption(option);
            }
        }
    });
</script>

</body>
</html>
