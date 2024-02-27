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

<div id="server_info">
    <el-card class="card" v-for="(server, index) in serverList" :key="index">
        <div slot="header" class="clearfix">
            <span>{{ server.clusterName }}</span>
        </div>
        <ul>
            <li>最大玩家数: {{ server.maxPlayers }}</li>
            <li>当前玩家数: {{ server.nowPlayers }}</li>
            <li>游戏天数: {{ server.playDay }}</li>
            <li>季节: {{ server.season }}</li>
            <li>Mod数量: {{ server.totalModNum }}</li>
            <li>玩家列表:
                <ul>
                    <li v-for="(player, playerIndex) in server.playerList" :key="playerIndex">{{ player }}</li>
                </ul>
            </li>
        </ul>
    </el-card>
</div>

<script>
    new Vue({
        el: '#server_info',
        data: {
            serverList: [],
        },
        created() {
            this.fetchServerInfo();
        },
        methods: {
            fetchServerInfo() {
                get("/server/serverInfo").then((data) => {
                    this.serverList = data;
            });
            }
        }
    });
</script>

</body>
</html>
