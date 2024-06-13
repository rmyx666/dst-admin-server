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
        <h3>QQ群：683251529</h3>
        <br/>
        <img width="500px" src="/images/code.JPG"/>
        <br/>

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
