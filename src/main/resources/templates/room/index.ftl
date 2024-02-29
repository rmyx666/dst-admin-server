<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <#import "../system/user/spring.ftl" as spring>
    <title><@spring.message code="setting.player.title"/></title>
    <#include "../common/header.ftl"/>
</head>
<style>
    .card {
        margin: 10px;
    }
</style>
<body>

<div id="server_info" v-loading="loading">
    <div style="display: flex;">
        <div style="width: 70%; padding: 10px">
            <el-table :data="serverList" style="width: 100%" stripe @row-click="rowClick">
                <el-table-column label="服务器名称" prop="clusterName" ></el-table-column>
                <el-table-column label="季节" prop="season" ></el-table-column>
                <el-table-column label="mod数量" prop="totalModNum" ></el-table-column>
                <el-table-column label="在线情况">
                    <template slot-scope="scope">
                        {{scope.row.nowPlayers}}/{{scope.row.maxPlayers}}
                    </template>
                </el-table-column>
                <el-table-column label="详情">
                    <a href="/room_main" target="_parent">详情</a>
                </el-table-column>
            </el-table>
        </div>
        <div style="width: 30%;  padding: 10px">
            <el-empty v-if="server == null" description="请点击一个服务器来查看信息"></el-empty>
            <el-descriptions v-else title="服务器详情" :column="1">
                <el-descriptions-item label="服务器名称">{{server.clusterName}}</el-descriptions-item>
                <el-descriptions-item label="季节">{{server.season}}</el-descriptions-item>
                <el-descriptions-item label="mod数量">{{server.totalModNum}}</el-descriptions-item>
                <el-descriptions-item label="在线情况">{{server.nowPlayers}}/{{server.maxPlayers}}</el-descriptions-item>
                <el-descriptions-item label="在线玩家">
                    <el-empty v-if="server.playerList == null || server.playerList.length === 0" description="当前没有玩家在线"></el-empty>
                    <ul>
                        <li v-for="(player, playerIndex) in server.playerList" :key="playerIndex">{{ player }}</li>
                    </ul>
                </el-descriptions-item>
            </el-descriptions>
        </div>
    </div>
</div>

<script>

    new Vue({
        el: '#server_info',
        data: {
            loading: true,
            server: null,
            serverList: []
        },
        created() {
            this.fetchServerInfo();
        },
        methods: {
            fetchServerInfo() {
                get("/server/serverInfo").then((data) => {
                    this.serverList = data;
                    this.loading = false;
                });
            },
            rowClick(row) {
                this.server = row
            }
        }
    });
</script>

</body>
</html>
