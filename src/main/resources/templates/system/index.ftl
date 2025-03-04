<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <#import "user/spring.ftl" as spring>
    <title><@spring.message code="setting.player.title"/></title>
    <#include "../common/header.ftl"/>
</head>
<style>
    .card {
        margin: 10px;
    }
</style>
<body>

<div id="sys_index">
    <el-tabs v-model="activeName">
        <el-tab-pane label="<@spring.message code="setting.system.time.task"/>" name="first">
            <el-card class="card">
                <div slot="header" class="clearfix">
                    <span><@spring.message code="setting.system.time.task.desc"/></span>
                </div>
                <div style="margin: 5px" v-for="item in scheduleVO.backupTimeList">
                        <@spring.message code="setting.system.task.execution.time"/>：{{item.time}},

                        <@spring.message code="setting.system.task.execution.status"/>：
                        <strong style="color: green" v-if="item.count > 0">
                            <@spring.message code="setting.system.task.execution.status.done"/></strong><strong style="color: orange" v-if="item.count === 0">
                        <@spring.message code="setting.system.task.execution.status.not.performed"/></strong>
                </div>

                <el-row style="margin: 5px">
                    <el-col :span="5">
                        <el-button :size="size" type="primary" @click="addBackupTime()"><@spring.message code="setting.player.admin.add"/> <@spring.message code="setting.system.task.execution.time"/></el-button>
                    </el-col>
                </el-row>

                <tempate v-for="(item,key) in backupTimeList">
                    <div style="margin: 5px">
                        <el-time-picker style="width:120px" :size="size" placeholder="<@spring.message code="home.pane1.card1.dst.please.choose"/> <@spring.message code="setting.system.task.execution.time"/>" v-model="item.time" clearable></el-time-picker>
                        <el-button :size="size" type="warning" style="margin-left: 5px" @click="delBackupTime(key)"><@spring.message code="setting.player.admin.delete"/></el-button>
                    </div>
                </tempate>
            </el-card>

            <el-card class="card">
                <div slot="header" class="clearfix">
                    <span>定时更新游戏服务器</span>
                    <span style="color: red;margin-left: 40px"><@spring.message code="setting.system.smart.update"/>：</span>
                    <el-switch v-model="smartUpdateServer" active-text="<@spring.message code="setting.system.open"/>" inactive-text="<@spring.message code="setting.system.close"/>"></el-switch>
                    <span>（定时时间到时关闭所有房间，更新服务器版本，更新成功后开启所有房间）</span>
                </div>
                <div v-for="item in scheduleVO.updateServerTimeList"><@spring.message code="setting.system.task.execution.time"/>：{{item.time}}, <@spring.message code="setting.system.task.execution.status"/>：
                    <strong style="color: green" v-if="item.count > 0">
                        <@spring.message code="setting.system.task.execution.status.done"/></strong><strong style="color: orange" v-if="item.count === 0">
                        <@spring.message code="setting.system.task.execution.status.not.performed"/></strong>
                </div>
                <el-row style="margin: 5px">
                    <el-col :span="5">
                        <el-button :size="size" type="primary" @click="addUpdateServerTime()"><@spring.message code="setting.player.admin.add"/> <@spring.message code="setting.system.task.execution.time"/></el-button>
                    </el-col>
                </el-row>

                <tempate v-for="(item,key) in updateServerTimeList">
                    <div style="margin: 5px">
                        <el-time-picker :size="size" style="width:120px" placeholder="<@spring.message code="home.pane1.card1.dst.please.choose"/> <@spring.message code="setting.system.task.execution.time"/>" v-model="item.time" clearable></el-time-picker>
                        <el-button :size="size" type="warning" style="margin-left: 5px" @click="delUpdateServerTime(key)"><@spring.message code="setting.player.admin.delete"/></el-button>
                    </div>
                </tempate>
            </el-card>

            <el-card class="card">
                <div slot="header" class="clearfix">
                    <span>定时更新游戏mod</span>
                    <span style="color: red;margin-left: 40px"><@spring.message code="setting.system.smart.update"/>：</span>
                    <el-switch v-model="smartUpdateMod" active-text="<@spring.message code="setting.system.open"/>" inactive-text="<@spring.message code="setting.system.close"/>"></el-switch>
                    <span>（定时时间到时如果房间无人，重启房间以更新mod）</span>
                </div>
                    <div v-for="item in scheduleVO.updateModTimeList"><@spring.message code="setting.system.task.execution.time"/>：{{item.time}}, <@spring.message code="setting.system.task.execution.status"/>：
                        <strong style="color: green" v-if="item.count > 0">
                            <@spring.message code="setting.system.task.execution.status.done"/></strong><strong style="color: orange" v-if="item.count === 0">
                            <@spring.message code="setting.system.task.execution.status.not.performed"/></strong>
                    </div>
                <el-row style="margin: 5px">
                    <el-col :span="5">
                        <el-button :size="size" type="primary" @click="addUpdateModTime()"><@spring.message code="setting.player.admin.add"/> <@spring.message code="setting.system.task.execution.time"/></el-button>
                    </el-col>
                </el-row>

                <tempate v-for="(item,key) in updateModTimeList">
                    <div style="margin: 5px">
                        <el-time-picker :size="size" style="width:120px" placeholder="<@spring.message code="home.pane1.card1.dst.please.choose"/> <@spring.message code="setting.system.task.execution.time"/>" v-model="item.time" clearable></el-time-picker>
                        <el-button :size="size" type="warning" style="margin-left: 5px" @click="delUpdateModTime(key)"><@spring.message code="setting.player.admin.delete"/></el-button>
                    </div>
                </tempate>
            </el-card>

            <el-card class="card">
                <div slot="header" class="clearfix">
                    <span>自动启动设置</span>
                </div>

                <el-row style="margin: 5px">
                    <el-col :span="8">
                        <el-switch v-model="autoStartMaster" active-text="自动启动地面" inactive-text="关闭地面自动启动"></el-switch>
                    </el-col>
                </el-row>

                <el-row style="margin: 5px">
                    <el-col :span="8">
                        <el-switch v-model="autoStartCaves" active-text="自动启动洞穴" inactive-text="关闭洞穴自动启动"></el-switch>
                    </el-col>
                </el-row>

                <el-row style="margin: 5px">
                    <el-col :span="8">
                        <el-switch v-model="autoRegenerate" active-text="自动重置世界" inactive-text="关闭自动重置"></el-switch>
                        <span>（如果游戏时长大于0小于40天，且12小时内用户在线时长少于60分钟，重置该世界，每天晚上五点判定一次 如果游戏7天内游玩时间少于60分钟，重置该世界，每周五晚上六点判定一次）</span>
                    </el-col>
                </el-row>
            </el-card>


            <el-card style="margin: 10px; position: sticky; bottom: 0;  z-index: 10;">
                <el-button :size="size" type="primary" @click="saveSchedule()"><@spring.message code="home.pane1.card1.dst.active.save"/></el-button>
            </el-card>
        </el-tab-pane>
        <el-tab-pane label="<@spring.message code="setting.system.ground.run.log"/>" name="second">
            <el-card class="card">
                <el-row style="margin: 5px">
                    <el-col :span="3">
                        <el-input :size="size" placeholder="<@spring.message code="setting.system.run.log.desc"/>"
                                  v-model="num1" type="number" clearable></el-input>

                    </el-col>
                    <el-col :span="6">
                        <el-button :size="size" type="primary"
                                   @click="getDstLog(0,num1)"><@spring.message code="setting.system.pull"/></el-button>
                    </el-col>
                </el-row>
            </el-card>
            <el-card class="card">
                <ul>
                    <li v-for="log in masterLog">{{ log }}</li>
                </ul>
            </el-card>
        </el-tab-pane>
        <el-tab-pane label="<@spring.message code="setting.system.cave.run.log"/>" name="third">
            <el-card class="card">
                <el-row style="margin: 5px">
                    <el-col :span="3">
                        <el-input :size="size" placeholder="<@spring.message code="setting.system.run.log.desc"/>"
                                  v-model="num2" type="number" clearable></el-input>
                    </el-col>
                    <el-col :span="6">
                        <el-button :size="size" type="primary"
                                   @click="getDstLog(1,num2)"><@spring.message code="setting.system.pull"/></el-button>
                    </el-col>
                </el-row>
            </el-card>
            <el-card class="card">
                <ul>
                    <li v-for="log in cavesLog">{{ log }}</li>
                </ul>
            </el-card>
        </el-tab-pane>
        <el-tab-pane label="<@spring.message code="setting.system.player.chat.log"/>" name="fourth">
            <el-card class="card">
                <el-row style="margin: 5px">
                    <el-col :span="3">
                        <el-input :size="size" placeholder="<@spring.message code="setting.system.run.log.desc"/>"
                                  v-model="num3" type="number" clearable></el-input>
                    </el-col>
                    <el-col :span="6">
                        <el-button :size="size" type="primary"
                                   @click="getDstLog(2,num3)"><@spring.message code="setting.system.pull"/></el-button>
                    </el-col>
                </el-row>
            </el-card>
            <el-card class="card">
                <ul>
                    <li v-for="log in chatLog">{{ log }}</li>
                </ul>
            </el-card>
        </el-tab-pane>
        <el-tab-pane label="<@spring.message code="setting.system.laboratory"/>" name="fifth">
            <el-card class="card">
                <div slot="header" class="clearfix">
                    <span><@spring.message code="setting.system.smart.update"/></span>
                </div>
                <ul>
                    <li>Klei <@spring.message code="setting.system.dst.latest.version"/>：
                        <strong style="color: green">{{versionMap.steamVersion}}</strong>
                    </li>
                    <li><@spring.message code="setting.system.dst.now.version"/>：
                        <strong style="color: green">{{versionMap.localVersion}}</strong>
                    </li>
                </ul>
            </el-card>
<#--        </el-tab-pane>-->
<#--        <el-tab-pane label="<@spring.message code="setting.system.advanced.settings"/>" name="sixth">-->
            <el-card class="card">
                <div slot="header" class="clearfix">
                    <span><@spring.message code="setting.system.advanced.tips"/></span>
                </div>
                <el-row style="margin: 5px">
                    <el-col :span="3">
                        <@spring.message code="setting.system.advanced.master.port"/>：
                    </el-col>
                    <el-col :span="5">
                        <el-input type="number" v-model="gamePort.masterPort" placeholder="<@spring.message code="setting.system.advanced.master.tips"/>"/>
                    </el-col>
                </el-row>
                <el-row style="margin: 5px">
                    <el-col :span="3">
                        <@spring.message code="setting.system.advanced.ground.port"/>：
                    </el-col>
                    <el-col :span="5">
                        <el-input type="number" v-model="gamePort.groundPort" placeholder="<@spring.message code="setting.system.advanced.ground.tips"/>"/>
                    </el-col>
                </el-row>
                <el-row style="margin: 5px">
                    <el-col :span="3">
                        <@spring.message code="setting.system.advanced.caves.port"/>：
                    </el-col>
                    <el-col :span="5">
                        <el-input type="number" v-model="gamePort.cavesPort" placeholder="<@spring.message code="setting.system.advanced.caves.tips"/>"/>
                    </el-col>
                </el-row>
            </el-card>
            <el-card style="margin: 10px; position: sticky; bottom: 0;  z-index: 10;">
                <el-button :size="size" type="primary" @click="saveGamePort()"><@spring.message code="home.pane1.card1.dst.active.save"/></el-button>
            </el-card>
        </el-tab-pane>
    </el-tabs>
</div>

</body>

<script>

    new Vue({
        el: '#sys_index',
        data: {
            autoStartMaster: false,
            autoStartCaves: false,
            autoRegenerate: false,
            roomId: null,
            serverId: null,
            activeName: 'first',
            num1: 20,
            num2: 20,
            num3: 20,
            masterLog: [],
            cavesLog: [],
            chatLog: [],
            scheduleVO: undefined,
            updateModTimeList: [],
            updateServerTimeList: [],
            backupTimeList: [],
            smartUpdateMod:false,
            smartUpdateServer:false,
            versionMap: {},
            model: {
                backupTimeList: [],
                updateModTimeList: [],
                updateServerTimeList: [],
            },
            labelPosition:'left',
            size:'medium',
            gamePort:{},
        },
        created() {
            this.roomId = RoomUtil.getRoomId()
            this.serverId = RoomUtil.getServerId()
            this.getScheduleList();
            this.getVersion();
            this.getLabelPosition();
            this.getGamePort();
        },
        mounted(){
         window.onresize = () => {
                this.getLabelPosition()
            }
          },
        methods: {
            getLabelPosition(){
                let windowWidth = window.innerWidth
                    console.log('getLabelPosition',windowWidth)
                    if(windowWidth < 768){
                    this.labelPosition = 'top'
                    this.size= 'mini'
                }
                else {
                    this.labelPosition = 'left'
                    this.size = 'medium'
                }

            },
            getScheduleList() {
                get("/system/getScheduleList?roomId=" + this.roomId+"&serverId="+this.serverId).then((data) => {
                    this.scheduleVO = data;
                    //初始化
                    this.updateModTimeList = [];
                    this.updateServerTimeList = [];
                    this.backupTimeList = [];
                    if (this.scheduleVO.updateModTimeList) {
                        this.scheduleVO.updateModTimeList.forEach(e => {
                            let obj = {};
                            obj.time = "2020-10-23 " + e.time;
                            obj.count = e.count;
                            this.updateModTimeList.push(obj);
                        })
                    }
                    if (this.scheduleVO.updateServerTimeList) {
                        this.scheduleVO.updateServerTimeList.forEach(e => {
                            let obj = {};
                            obj.time = "2020-10-23 " + e.time;
                            obj.count = e.count;
                            this.updateServerTimeList.push(obj);
                        })
                    }
                    if (this.scheduleVO.backupTimeList) {
                        this.scheduleVO.backupTimeList.forEach(e => {
                            let obj = {};
                            obj.time = "2020-10-23 " + e.time;
                            obj.count = e.count;
                            this.backupTimeList.push(obj);
                        })
                    }
                    this.autoStartMaster = data.autoStartMaster ? data.autoStartMaster : false;
                    this.autoStartCaves = data.autoStartCaves ? data.autoStartCaves : false;
                    this.autoRegenerate = data.autoRegenerate ? data.autoRegenerate : false;
                    this.smartUpdateMod = data.smartUpdateMod ? data.smartUpdateMod : false;
                    this.smartUpdateServer = data.smartUpdateServer ? data.smartUpdateServer : false;
                })
            },
            getVersion(){
                get("/system/getVersion").then((data) => {
                    this.versionMap = data;
                });
            },
            addUpdateModTime() {
                this.updateModTimeList.push({count: 0, time: '2020-10-23 06:00:00'});
            },
            delUpdateModTime(key) {
                this.updateModTimeList.splice(key, 1);
            },
            addUpdateServerTime() {
                this.updateServerTimeList.push({count: 0, time: '2020-10-23 06:00:00'});
            },
            delUpdateServerTime(key) {
                this.updateServerTimeList.splice(key, 1);
            },
            addBackupTime() {
                this.backupTimeList.push({count: 0, time: '2020-10-23 06:00:00'});
            },
            delBackupTime(key) {
                this.backupTimeList.splice(key, 1);
            },
            //格式化时间
            formatTime(date, fmt) {
                if (date == null || date == undefined || date == '') {
                    return null;
                }
                var date = new Date(date);
                if (/(y+)/.test(fmt)) {
                    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
                }
                var o = {
                    'M+': date.getMonth() + 1,
                    'd+': date.getDate(),
                    'h+': date.getHours(),
                    'm+': date.getMinutes(),
                    's+': date.getSeconds()
                };
                for (var k in o) {
                    if (new RegExp('(' + k + ')').test(fmt)) {
                        var str = o[k] + '';
                        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : ('00' + str).substr(str.length));
                    }
                }
                return fmt;
            },
            saveSchedule() {
                let params = {};
                params.backupTimeList = [];
                params.updateModTimeList = [];
                params.updateServerTimeList = [];
                if (this.backupTimeList.length > 0) {
                    this.backupTimeList.forEach(e => {
                        let formatTime = this.formatTime(e.time, "yyyy-MM-dd hh:mm:ss");
                        let obj = {time: formatTime, count: e.count};
                        params.backupTimeList.push(obj);
                    })
                }
                if (this.updateModTimeList.length > 0) {
                    this.updateModTimeList.forEach(e => {
                        let formatTime = this.formatTime(e.time, "yyyy-MM-dd hh:mm:ss");
                        let obj = {time: formatTime, count: e.count};
                        params.updateModTimeList.push(obj);
                    })
                }
                if (this.updateServerTimeList.length > 0) {
                    this.updateServerTimeList.forEach(e => {
                        let formatTime = this.formatTime(e.time, "yyyy-MM-dd hh:mm:ss");
                        let obj = {time: formatTime, count: e.count};
                        params.updateServerTimeList.push(obj);
                    })
                }
                params.autoStartMaster = this.autoStartMaster
                params.autoStartCaves = this.autoStartCaves
                params.autoRegenerate = this.autoRegenerate
                params.smartUpdateMod = this.smartUpdateMod;
                params.smartUpdateServer = this.smartUpdateServer;
                post("/system/saveSchedule?roomId=" + this.roomId+"&serverId="+this.serverId, params).then((data) => {
                    if (data) {
                        this.$message({message: data.message, type: 'warning'});
                    } else {
                        this.$message({message: '<@spring.message code="player.save.success"/>', type: 'success'});
                        this.getScheduleList()
                    }
                })
            },
            getDstLog(type, rowNum) {
                let params = {type: type, rowNum: rowNum}
                get("/system/getDstLog?roomId=" + this.roomId+"&serverId="+this.serverId, params).then((data) => {
                    switch (type){
                        case 0:
                            this.masterLog = data;
                            break;
                        case 1:
                            this.cavesLog = data;
                            break;
                        case 2:
                            this.chatLog = data;
                            break;
                    }
                })
            },
            getGamePort(){
                get("/system/getGamePort?roomId=" + this.roomId+"&serverId="+this.serverId).then((data) => {
                    this.gamePort = data;
                })
            },
            saveGamePort(){
                post("/system/saveGamePort?roomId=" + this.roomId+"&serverId="+this.serverId,this.gamePort).then((data) => {
                    if (data) {
                        this.$message({message: data.message, type: 'warning'});
                    } else {
                        this.$message({message: '<@spring.message code="player.save.success"/>', type: 'success'});
                        this.getGamePort()
                    }
                })
            }

        }
    });


</script>

</html>
