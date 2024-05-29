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
        <el-table-column label="房间ID" prop="roomId"></el-table-column>
        <el-table-column label="房间名称" prop="roomName"></el-table-column>
        <el-table-column label="主端口号" prop="masterPort"></el-table-column>
        <el-table-column label="地面端口号" prop="groundPort"></el-table-column>
        <el-table-column label="洞穴端口号" prop="cavesPort"></el-table-column>
        <el-table-column label="房间集群名称" prop="clusterName"></el-table-column>
        <el-table-column label="最大玩家数量" prop="maxPlayers"></el-table-column>
        <el-table-column label="当前玩家数量" prop="nowPlayers"></el-table-column>
        <el-table-column label="存档的天数" prop="playDay"></el-table-column>
        <el-table-column label="存档的季节" prop="season"></el-table-column>
        <el-table-column label="MOD总数" prop="totalModNum"></el-table-column>
        <el-table-column label="地面状态">
            <template slot-scope="scope">
                {{ scope.row.masterStatus ? '启动' : '关闭' }}
            </template>
        </el-table-column>
        <el-table-column label="洞穴状态">
            <template slot-scope="scope">
                {{ scope.row.cavesStatus ? '启动' : '关闭' }}
            </template>
        </el-table-column>
        <el-table-column label="CPU核心数">
            <template slot-scope="scope">
                {{ scope.row.cpu ? scope.row.cpu.cpuNum : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="CPU总的使用率">
            <template slot-scope="scope">
                {{ scope.row.cpu ? scope.row.cpu.total.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="CPU系统使用率">
            <template slot-scope="scope">
                {{ scope.row.cpu ? scope.row.cpu.sys.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="CPU用户使用率">
            <template slot-scope="scope">
                {{ scope.row.cpu ? scope.row.cpu.used.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="CPU当前等待率">
            <template slot-scope="scope">
                {{ scope.row.cpu ? scope.row.cpu.wait.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="CPU当前空闲率">
            <template slot-scope="scope">
                {{ scope.row.cpu ? scope.row.cpu.free.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="内存总量">
            <template slot-scope="scope">
                {{ scope.row.mem ? scope.row.mem.total.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="已用内存">
            <template slot-scope="scope">
                {{ scope.row.mem ? scope.row.mem.used.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="剩余内存">
            <template slot-scope="scope">
                {{ scope.row.mem ? scope.row.mem.free.toFixed(2) : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="内存使用率">
            <template slot-scope="scope">
                {{ scope.row.mem ? scope.row.mem.usage.toFixed(2) + '%' : '-' }}
            </template>
        </el-table-column>
        <el-table-column label="在线情况">
            <template slot-scope="scope">
                {{ scope.row.nowPlayers}}/{{scope.row.maxPlayers}}
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
            addRoomDialogVisible: false,
            form: {
                masterPort: 10888,
                groundPort: 10999,
                cavesPort: 10998
            },
            addRoomRules: {
                roomId: [
                    {required: true, message: '请输入房间id', trigger: 'blur'},
                    { validator: validateId, trigger: 'blur' },
                ],
                roomName: [
                    { required: true, message: '请输入房间名称', trigger: 'blur' },
                    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
                ],
                masterPort: [
                    { required: true, message: '请输入主端口号', trigger: 'blur' },
                    { validator: validatePort, trigger: 'blur'}
                ],
                groundPort: [
                    { required: true, message: '请输入地面端口号', trigger: 'blur' },
                    { validator: validatePort, trigger: 'blur'}
                ],
                cavesPort: [
                    { required: true, message: '请输入洞穴端口号', trigger: 'blur' },
                    { validator: validatePort, trigger: 'blur'}
                ],
            },
        },
        created() {
            this.fetchRoomList();
        },
        methods: {
            fetchRoomList() {
                get("/room/infos").then((data) => {
                    this.serverList = data;
                    this.loading = false;
                });
            },
            closeAddRoomDialog() {
                this.form = {
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
                    post("/room/save", this.form)
                        .then((data) => {
                            this.$message.success('新增成功')
                            this.fetchRoomList()
                            this.closeAddRoomDialog()
                        })
                        .catch((msg) => { this.$message.error(msg) })
                })
            },
            goDetail(room) {
                RoomUtil.saveRoomId(room.roomId)
                const dom = document.createElement('a')
                dom.href = '/room_main'
                dom.target = '_parent'
                dom.click()
            }
        }
    });
</script>

</body>
</html>
