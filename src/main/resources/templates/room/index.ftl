<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <#import "../system/user/spring.ftl" as spring>
    <title><@spring.message code="setting.player.title"/></title>
    <#include "../common/header.ftl"/>
</head>
<body>

<div id="server_info" v-loading="loading">
    <el-button type="primary" @click="addRoomDialogVisible = true">新增房间</el-button>

    <!-- 齿轮按钮 -->
    <el-button type="primary" icon="el-icon-setting" @click="toggleColumnDialog">选择列</el-button>

    <!-- 列选择对话框 -->
    <el-dialog title="选择显示的列" :visible.sync="columnDialogVisible">
        <el-checkbox-group v-model="selectedColumns">
            <el-checkbox v-for="column in columns" :label="column.prop" :key="column.prop">
                {{ column.label }}
            </el-checkbox>
        </el-checkbox-group>
        <span slot="footer" class="dialog-footer">
        <el-button @click="columnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveColumnSettings">保存</el-button>
      </span>
    </el-dialog>

<#--    <el-table :data="serverList" style="width: 100%" stripe>-->

<#--        <el-table-column label="房间ID" prop="roomId"></el-table-column>-->
<#--        <el-table-column label="房间名称" prop="roomName"></el-table-column>-->
<#--        <el-table-column label="主端口号" prop="masterPort"></el-table-column>-->
<#--        <el-table-column label="地面端口号" prop="groundPort"></el-table-column>-->
<#--        <el-table-column label="洞穴端口号" prop="cavesPort"></el-table-column>-->
<#--        <el-table-column label="游戏服务器名称" prop="clusterName"></el-table-column>-->
<#--        <el-table-column label="在线情况">-->
<#--            <template slot-scope="scope">-->
<#--                {{scope.row.nowPlayers}}/{{scope.row.maxPlayers}}-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--        <el-table-column label="存档的天数" prop="playDay"></el-table-column>-->
<#--        <el-table-column label="存档的季节" prop="season"></el-table-column>-->
<#--        <el-table-column label="MOD总数" prop="totalModNum"></el-table-column>-->
<#--        <el-table-column label="地面状态">-->
<#--            <template slot-scope="scope">-->
<#--                <el-tag :type="scope.row.masterStatus ? 'success' : 'danger'">-->
<#--                    {{ scope.row.masterStatus ? '启动' : '关闭' }}-->
<#--                </el-tag>-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--        <el-table-column label="洞穴状态">-->
<#--            <template slot-scope="scope">-->
<#--                <el-tag :type="scope.row.cavesStatus ? 'success' : 'danger'">-->
<#--                    {{ scope.row.cavesStatus ? '启动' : '关闭' }}-->
<#--                </el-tag>-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--        <el-table-column label="CPU使用率">-->
<#--            <template slot-scope="scope">-->
<#--                <el-progress-->
<#--                        :percentage="cpuInfo"-->
<#--                        :text-inside="true"-->
<#--                        :stroke-width="18"-->
<#--                        :color="getColor(cpuInfo)">-->
<#--                </el-progress>-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--        <el-table-column label="内存使用率">-->
<#--            <template slot-scope="scope">-->
<#--                <el-progress-->
<#--                        :percentage="menInfo"-->
<#--                        :text-inside="true"-->
<#--                        :stroke-width="18"-->
<#--                        :color="getColor(menInfo)">-->
<#--                </el-progress>-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--        <el-table-column label="在线情况">-->
<#--            <template slot-scope="scope">-->
<#--                {{ scope.row.nowPlayers}}/{{scope.row.maxPlayers}}-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--        <el-table-column label="操作">-->
<#--            <template slot-scope="scope">-->
<#--                <el-button type="primary" @click="updateRoomDialog(scope.row)">修改</el-button>-->
<#--                <el-button type="danger" @click="deleteRoom(scope.row)">删除</el-button>-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--        <el-table-column label="详情">-->
<#--            <template slot-scope="scope">-->
<#--                <el-button @click="goDetail(scope.row)">详情</el-button>-->
<#--            </template>-->
<#--        </el-table-column>-->
<#--    </el-table>-->
    <!-- 表格 -->
    <!-- 表格 -->
    <el-table :data="serverList" style="width: 100%" stripe>
        <!-- 房间ID -->
        <el-table-column v-if="isColumnVisible('roomId')" label="房间ID" prop="roomId"></el-table-column>

        <!-- 房间名称 -->
        <el-table-column v-if="isColumnVisible('roomName')" label="房间名称" prop="roomName"></el-table-column>

        <!-- 主端口号 -->
        <el-table-column v-if="isColumnVisible('masterPort')" label="主端口号" prop="masterPort"></el-table-column>

        <!-- 地面端口号 -->
        <el-table-column v-if="isColumnVisible('groundPort')" label="地面端口号" prop="groundPort"></el-table-column>

        <!-- 洞穴端口号 -->
        <el-table-column v-if="isColumnVisible('cavesPort')" label="洞穴端口号" prop="cavesPort"></el-table-column>

        <!-- 游戏服务器名称 -->
        <el-table-column v-if="isColumnVisible('clusterName')" label="游戏服务器名称" prop="clusterName"></el-table-column>

        <!-- 在线情况 -->
        <el-table-column v-if="isColumnVisible('nowPlayers')" label="在线情况">
            <template slot-scope="scope">
                {{scope.row.nowPlayers}}/{{scope.row.maxPlayers}}
            </template>
        </el-table-column>

        <!-- 存档的天数 -->
        <el-table-column v-if="isColumnVisible('playDay')" label="存档的天数" prop="playDay"></el-table-column>

        <!-- 存档的季节 -->
        <el-table-column v-if="isColumnVisible('season')" label="存档的季节" prop="season"></el-table-column>

        <!-- MOD总数 -->
        <el-table-column v-if="isColumnVisible('totalModNum')" label="MOD总数" prop="totalModNum"></el-table-column>

        <!-- 地面状态 -->
        <el-table-column v-if="isColumnVisible('masterStatus')" label="地面状态">
            <template slot-scope="scope">
                <el-tag :type="scope.row.masterStatus ? 'success' : 'danger'">
                    {{ scope.row.masterStatus ? '启动' : '关闭' }}
                </el-tag>
            </template>
        </el-table-column>

        <!-- 洞穴状态 -->
        <el-table-column v-if="isColumnVisible('cavesStatus')" label="洞穴状态">
            <template slot-scope="scope">
                <el-tag :type="scope.row.cavesStatus ? 'success' : 'danger'">
                    {{ scope.row.cavesStatus ? '启动' : '关闭' }}
                </el-tag>
            </template>
        </el-table-column>

        <!-- CPU使用率 -->
        <el-table-column v-if="isColumnVisible('cpuInfo')" label="CPU使用率">
            <template slot-scope="scope">
                <el-progress
                        :percentage="cpuInfo"
                        :text-inside="true"
                        :stroke-width="18"
                        :color="getColor(cpuInfo)">
                </el-progress>
            </template>
        </el-table-column>

        <!-- 内存使用率 -->
        <el-table-column v-if="isColumnVisible('menInfo')" label="内存使用率">
            <template slot-scope="scope">
                <el-progress
                        :percentage="menInfo"
                        :text-inside="true"
                        :stroke-width="18"
                        :color="getColor(menInfo)">
                </el-progress>
            </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column v-if="isColumnVisible('actions')" label="操作">
            <template slot-scope="scope">
                <el-button type="primary" @click="updateRoomDialog(scope.row)">修改</el-button>
                <el-button type="danger" @click="deleteRoom(scope.row)">删除</el-button>
            </template>
        </el-table-column>

        <!-- 详情 -->
        <el-table-column v-if="isColumnVisible('details')" label="详情">
            <template slot-scope="scope">
                <el-button @click="goDetail(scope.row)">详情</el-button>
            </template>
        </el-table-column>
    </el-table>
    <el-dialog
            title="新增房间"
            :visible.sync="addRoomDialogVisible"
            width="30%"
            :before-close="closeAddRoomDialog">
        <el-form ref="form" :model="form" label-width="100px" :rules="addRoomRules">
            <el-form-item label="房间id" prop="roomId">
                <el-input v-model="form.roomId"></el-input>
            </el-form-item>
            <el-form-item label="房间名称" prop="roomName">
                <el-input v-model="form.roomName"></el-input>
            </el-form-item>
            <el-form-item label="主端口号" prop="masterPort">
                <el-input v-model="form.masterPort"></el-input>
            </el-form-item>
            <el-form-item label="地面端口号" prop="groundPort">
                <el-input v-model="form.groundPort"></el-input>
            </el-form-item>
            <el-form-item label="洞穴端口号" prop="cavesPort">
                <el-input v-model="form.cavesPort"></el-input>
            </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button @click="closeAddRoomDialog">取 消</el-button>
            <el-button type="primary" @click="addRoom">确 定</el-button>
        </span>
    </el-dialog>
    <!-- 修改房间对话框 -->
    <el-dialog
            title="修改房间信息"
            :visible.sync="updateRoomDialogVisible"
            width="30%"
            :before-close="closeUpdateRoomDialog">
        <el-form ref="updateRoomForm" :model="updateRoomForm" label-width="100px" :rules="updateRoomRules">
            <el-form-item label="房间id">
                <el-input v-model="updateRoomForm.roomId" disabled></el-input>
            </el-form-item>
            <el-form-item label="房间名称" prop="roomName">
                <el-input v-model="updateRoomForm.roomName"></el-input>
            </el-form-item>
            <el-form-item label="主端口号" prop="masterPort">
                <el-input v-model="updateRoomForm.masterPort"></el-input>
            </el-form-item>
            <el-form-item label="地面端口号" prop="groundPort">
                <el-input v-model="updateRoomForm.groundPort"></el-input>
            </el-form-item>
            <el-form-item label="洞穴端口号" prop="cavesPort">
                <el-input v-model="updateRoomForm.cavesPort"></el-input>
            </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button @click="closeUpdateRoomDialog">取 消</el-button>
            <el-button type="primary" @click="updateRoom">确 定</el-button>
        </span>
    </el-dialog>
</div>

<script>
    const validateId = (rule, value, callback) => {
        value = Number(value)
        if (!Number.isInteger(value)) {
            callback(new Error('请输入数字'))
        }
        if (value < 1 || value > 100) {
            callback(new Error('id只能在1-100之间'))
        }
        callback()
    }
    const validatePort = (rule, value, callback) => {
        value = Number(value)
        if (!Number.isInteger(value)) {
            callback(new Error('请输入数字'))
        }
        if (value > 65535) {
            callback(new Error('端口号不能超过65535'))
        }
        callback()
    }
    let vue = new Vue({
        el: '#server_info',
        data: {
            columnDialogVisible: false, // 新增: 控制列选择对话框的显示
            columns: [ // 新增: 定义列的元数据
                { label: "房间ID", prop: "roomId" },
                { label: "房间名称", prop: "roomName" },
                { label: "主端口号", prop: "masterPort" },
                { label: "地面端口号", prop: "groundPort" },
                { label: "洞穴端口号", prop: "cavesPort" },
                { label: "游戏服务器名称", prop: "clusterName" },
                { label: "在线情况", prop: "nowPlayers", template: "playersTemplate" },
                { label: "存档的天数", prop: "playDay" },
                { label: "存档的季节", prop: "season" },
                { label: "MOD总数", prop: "totalModNum" },
                { label: "地面状态", prop: "masterStatus", type: "tag" },
                { label: "洞穴状态", prop: "cavesStatus", type: "tag" },
                { label: "CPU使用率", prop: "cpuInfo", type: "progress" },
                { label: "内存使用率", prop: "menInfo", type: "progress" },
                { label: "操作", prop: "actions", type: "actions" },
                { label: "详情", prop: "details", type: "details" }
            ],
            selectedColumns: [], // 新增: 存储用户选择的列

            cpuInfo: 0,
            cpuNum: 0,
            menInfo: 0,
            menTotal: 0,

            loading: true,
            serverList: [],
            serverInfoList: [],  // 新增
            selectedServerId: '',  // 新增
            addRoomDialogVisible: false,
            form: {
                serverId: '',  // 新增
                masterPort: 10888,
                groundPort: 10999,
                cavesPort: 10998
            },
            addRoomRules: {
                roomId: [
                    {required: true, message: '请输入房间id', trigger: 'blur'},
                    {validator: validateId, trigger: 'blur'},
                ],
                roomName: [
                    {required: true, message: '请输入房间名称', trigger: 'blur'},
                    {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
                ],
                masterPort: [
                    {required: true, message: '请输入主端口号', trigger: 'blur'},
                    {validator: validatePort, trigger: 'blur'}
                ],
                groundPort: [
                    {required: true, message: '请输入地面端口号', trigger: 'blur'},
                    {validator: validatePort, trigger: 'blur'}
                ],
                cavesPort: [
                    {required: true, message: '请输入洞穴端口号', trigger: 'blur'},
                    {validator: validatePort, trigger: 'blur'}
                ],
                serverId: [  // 新增
                    {required: true, message: '请选择服务器', trigger: 'change'}
                ],
            },
            // 修改房间信息的数据和校验规则
            updateRoomDialogVisible: false,
            updateRoomForm: {
                serverId: '',
                roomId: '',
                roomName: '',
                masterPort: '',
                groundPort: '',
                cavesPort: ''
            },
            updateRoomRules: {
                roomName: [
                    {required: true, message: '请输入房间名称', trigger: 'blur'},
                    {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
                ],
                masterPort: [
                    {required: true, message: '请输入主端口号', trigger: 'blur'},
                    {validator: validatePort, trigger: 'blur'}
                ],
                groundPort: [
                    {required: true, message: '请输入地面端口号', trigger: 'blur'},
                    {validator: validatePort, trigger: 'blur'}
                ],
                cavesPort: [
                    {required: true, message: '请输入洞穴端口号', trigger: 'blur'},
                    {validator: validatePort, trigger: 'blur'}
                ],
            },
        },
        mounted() {
            this.loadColumnSettings(); // 新增: 在页面加载时读取本地存储的列设置
        },
        created() {
            this.fetchRoomList();
            this.getHardwareInfo();
            //刷新服务器信息
            this.timer = setInterval(function () {
                vue.getHardwareInfo();
            }, 2000);
        },
        destroyed() {
            clearInterval(this.timer)
        },
        methods: {

            toggleColumnDialog() { // 新增: 显示或隐藏列选择对话框
                this.columnDialogVisible = !this.columnDialogVisible;
            },
            saveColumnSettings() { // 新增: 保存用户的列选择到本地存储
                localStorage.setItem('columnSettings', JSON.stringify(this.selectedColumns));
                this.columnDialogVisible = false;
            },
            loadColumnSettings() { // 新增: 读取本地存储的列设置
                const savedSettings = JSON.parse(localStorage.getItem('columnSettings'));
                if (savedSettings) {
                    this.selectedColumns = savedSettings;
                } else {
                    // 默认显示所有列
                    this.selectedColumns = this.columns.map(column => column.prop);
                }
            },
            isColumnVisible(prop) { // 新增: 判断列是否应该显示
                return this.selectedColumns.includes(prop);
            },

            getHardwareInfo() {
                get("/home/getHardwareInfo").then((data) => {
                    if (data) {
                        this.menInfo = data.mem.usage;
                        this.cpuInfo = data.cpu.used;
                        this.menTotal = data.mem.total;
                        this.cpuNum = data.cpu.cpuNum;
                    }
                })
            },
            getColor(percentage) {
                if (percentage < 50) {
                    return '#67c23a'; // 绿色
                } else if (percentage < 80) {
                    return '#e6a23c'; // 黄色
                } else {
                    return '#f56c6c'; // 红色
                }
            },
            fetchRoomList() {
                get("/room/localInfos").then((data) => {
                    this.serverList = data;
                    this.loading = false;
                });
            },
            closeAddRoomDialog() {
                this.form = {
                    serverId: '',  // 修改
                    masterPort: 10888,
                    groundPort: 10999,
                    cavesPort: 10998
                }
                this.addRoomDialogVisible = false
            },
            addRoom() {
                this.$refs.form.validate((valid) => {
                    if (!valid) {
                        return false;
                    }
                    post("/room/save?serverId=" + this.form.serverId, this.form)
                        .then((data) => {
                            this.$message.success('新增成功')
                            this.fetchRoomList()
                            this.closeAddRoomDialog()
                        })
                        .catch((msg) => {
                            this.$message.error(msg)
                        })
                })
            },
            goDetail(room) {
                RoomUtil.saveRoomId(room.roomId)
                RoomUtil.saveServerId(room.serverId)
                const dom = document.createElement('a')
                dom.href = '/room_main'
                dom.target = '_parent'
                dom.click()
            },
            // 修改房间信息方法
            updateRoomDialog(room) {
                // 将房间信息填充到修改表单中
                this.updateRoomForm = {
                    serverId: room.serverId,
                    roomId: room.roomId,
                    roomName: room.roomName,
                    masterPort: room.masterPort,
                    groundPort: room.groundPort,
                    cavesPort: room.cavesPort
                };
                this.updateRoomDialogVisible = true;
            },
            // 关闭修改房间对话框方法
            closeUpdateRoomDialog() {
                // 清空修改表单
                this.$refs.updateRoomForm.resetFields();
                this.updateRoomDialogVisible = false;
            },       // 修改房间信息提交方法
            updateRoom() {
                this.$refs.updateRoomForm.validate(valid => {
                    if (valid) {
                        // 提交修改的房间信息
                        post('/room/update?serverId=' + this.updateRoomForm.serverId, this.updateRoomForm)
                            .then(() => {
                                this.$message.success('修改成功');
                                this.fetchRoomList();
                                this.closeUpdateRoomDialog();
                            })
                            .catch(error => {
                                this.$message.error('修改失败: ' + error);
                            });
                    } else {
                        return false;
                    }
                });
            },
            // 删除房间信息方法
            deleteRoom(room) {
                this.$confirm('确认删除该房间吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // 调用删除接口
                    get(`/room/del?roomId=` + room.roomId + '&serverId=' + room.serverId)
                        .then(() => {
                            this.$message.success('删除成功');
                            this.fetchRoomList();
                        })
                        .catch(error => {
                            this.$message.error('删除失败: ' + error);
                        });
                }).catch(() => {
                    this.$message.info('已取消删除');
                });
            }
        },
    });
</script>

</body>
</html>
