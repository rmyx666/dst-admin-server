<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <title>关于</title>
    <#include "../common/header.ftl"/>
</head>
<style>
    .context {
        font-size: 20px;
        color: #2c52bf
    }
    .step{
        font-size: 30px
    }
    strong{
        color: red;
    }
</style>
<body>

<div id="about_page">
    <el-card>
        <h3>关于dst-admin 当前版本号：V1.5.0</h3>
        <h3>Copyright © 2020-2023 Qinming. All rights reserved.</h3>

        <h3>QQ群：683251529</h3>

    </el-card>

</div>

</body>

<script>

    new Vue({
        el: '#about_page',
        data: {
            active: 0,
        },
        methods: {}
    });


</script>

</html>
