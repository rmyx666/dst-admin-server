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
    .actions {
        margin-top: 10px;
    }
</style>
<body>

<div id="server_info">
    <el-button type="primary" @click="showAddDialog">添加服务器</el-button>
    <el-dialog title="服务器信息" :visible.sync="dialogVisible">
        <el-form :model="form">
            <el-form-item label="ID">
                <el-input v-model="form.id" :disabled="isEdit"></el-input>
            </el-form-item>
            <el-form-item label="IP">
                <el-input v-model="form.ip"></el-input>
            </el-form-item>
            <el-form-item label="用户名">
                <el-input v-model="form.username"></el-input>
            </el-form-item>
            <el-form-item label="密码">
                <el-input v-model="form.password" type="password"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveServerInfo">保存</el-button>
        </div>
    </el-dialog>
    <el-card class="card" v-for="(server, index) in serverList" :key="index">
        <div slot="header" class="clearfix">
            <span>{{ server.ip }}</span>
            <el-button type="text" @click="showEditDialog(server)">编辑</el-button>
            <el-button type="text" @click="deleteServerInfo(server.id)">删除</el-button>
        </div>
        <ul>
            <li>ID: {{ server.id }}</li>
            <li>IP: {{ server.ip }}</li>
            <li>用户名: {{ server.username }}</li>
            <li>密码: {{ server.password }}</li>
        </ul>
    </el-card>
</div>

<script src="https://cdn.jsdelivr.net/npm/vue@2"></script>
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/element-ui/lib/index.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/element-ui/lib/theme-chalk/index.css">
<script>
    new Vue({
        el: '#server_info',
        data: {
            serverList: [],
            dialogVisible: false,
            isEdit: false,
            form: {
                id: null,
                ip: '',
                username: '',
                password: ''
            }
        },
        created() {
            this.fetchServerInfo();
        },
        methods: {
            fetchServerInfo() {
                axios.get('/serverInfo/infos')
                    .then(response => {
                        this.serverList = response.data.data;
                    })
                    .catch(error => {
                        console.error("There was an error fetching the server info!", error);
                    });
            },
            showAddDialog() {
                this.dialogVisible = true;
                this.isEdit = false;
                this.resetForm();
            },
            showEditDialog(server) {
                this.dialogVisible = true;
                this.isEdit = true;
                this.form = Object.assign({}, server);
            },
            saveServerInfo() {
                if (this.isEdit) {
                    // Update existing server info
                    axios.post('/serverInfo/update', this.form)
                        .then(response => {
                            this.fetchServerInfo();
                            this.dialogVisible = false;
                        })
                        .catch(error => {
                            console.error("There was an error updating the server info!", error);
                        });
                } else {
                    // Save new server info
                    axios.post('/serverInfo/save', this.form)
                        .then(response => {
                            this.fetchServerInfo();
                            this.dialogVisible = false;
                        })
                        .catch(error => {
                            console.error("There was an error saving the server info!", error);
                        });
                }
            },
            deleteServerInfo(id) {
                axios.get('/serverInfo/del', { params: { id: id } })
                    .then(response => {
                        this.fetchServerInfo();
                    })
                    .catch(error => {
                        console.error("There was an error deleting the server info!", error);
                    });
            },
            resetForm() {
                this.form = {
                    id: null,
                    ip: '',
                    username: '',
                    password: ''
                };
            }
        }
    });
</script>

</body>
</html>
