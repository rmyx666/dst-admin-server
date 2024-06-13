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


    <el-table :data="serverList" style="width: 100%" stripe>

        <el-table-column label="服务器ID" prop="serverId"></el-table-column>
        <el-table-column label="服务器名称" prop="serverName"></el-table-column>
        <el-table-column label="服务器IP" prop="serverIp"></el-table-column>
        <el-table-column label="房间ID" prop="roomId"></el-table-column>
        <el-table-column label="房间名称" prop="roomName"></el-table-column>
        <el-table-column label="主端口号" prop="masterPort"></el-table-column>
        <el-table-column label="地面端口号" prop="groundPort"></el-table-column>
        <el-table-column label="洞穴端口号" prop="cavesPort"></el-table-column>
        <el-table-column label="游戏服务器名称" prop="clusterName"></el-table-column>
        <el-table-column label="在线情况">
            <template slot-scope="scope">
                {{scope.row.nowPlayers}}/{{scope.row.maxPlayers}}
            </template>
        </el-table-column>
        <el-table-column label="存档的天数" prop="playDay"></el-table-column>
        <el-table-column label="存档的季节" prop="season"></el-table-column>
        <el-table-column label="MOD总数" prop="totalModNum"></el-table-column>
        <el-table-column label="地面状态">
            <template slot-scope="scope">
                <el-tag :type="scope.row.masterStatus ? 'success' : 'danger'">
                    {{ scope.row.masterStatus ? '启动' : '关闭' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="洞穴状态">
            <template slot-scope="scope">
                <el-tag :type="scope.row.cavesStatus ? 'success' : 'danger'">
                    {{ scope.row.cavesStatus ? '启动' : '关闭' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="CPU使用率">
            <template slot-scope="scope">
                <el-progress
                        :percentage="scope.row.cpu ? scope.row.cpu.used : 0"
                        :text-inside="true"
                        :stroke-width="18"
                        :color="getColor(scope.row.cpu ? scope.row.cpu.used : 0)">
                </el-progress>
            </template>
        </el-table-column>
        <el-table-column label="内存使用率">
            <template slot-scope="scope">
                <el-progress
                        :percentage="scope.row.mem ? scope.row.mem.usage : 0"
                        :text-inside="true"
                        :stroke-width="18"
                        :color="getColor(scope.row.mem ? scope.row.mem.usage : 0)">
                </el-progress>
            </template>
        </el-table-column>
        <el-table-column label="在线情况">
            <template slot-scope="scope">
                {{ scope.row.nowPlayers}}/{{scope.row.maxPlayers}}
            </template>
        </el-table-column>
        <el-table-column label="操作">
            <template slot-scope="scope">
                <el-button type="primary" @click="updateRoomDialog(scope.row)">修改</el-button>
                <el-button type="danger" @click="deleteRoom(scope.row)">删除</el-button>
            </template>
        </el-table-column>
        <el-table-column label="详情">
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
            <el-form-item label="服务器" prop="serverId">
                <el-select v-model="form.serverId" placeholder="请选择服务器">
                    <el-option
                            v-for="server in serverInfoList"
                            :key="server.id"
                            :value="server.id">
                        <span>ID: {{ server.id }}, IP: {{ server.ip }}, 服务器名称: {{ server.name }}</span>
                    </el-option>
                </el-select>
            </el-form-item>
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
    new Vue({
        el: '#server_info',
        data: {
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
        created() {
            this.fetchRoomList();
            this.fetchServerInfoList();  // 新增
        },
        methods: {
            fetchServerInfoList() {  // 新增
                get("/serverInfo/infos").then((data) => {

                    this.serverInfoList = data;

                });
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
                get("/room/serverInfos").then((data) => {
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
                    post("/room/save?serverId="+this.form.serverId, this.form)
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
                        post('/room/update?serverId='+this.updateRoomForm.serverId, this.updateRoomForm)
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
                    get(`/room/del?roomId=`+room.roomId+'&serverId='+room.serverId)
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
        }
    });
</script>

</body>
</html>
