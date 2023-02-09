<template>
  <el-main class="main">
    <div><h4>{{ title }}</h4></div>
    <el-col class="main-center">
      <el-form
          v-loading="loading"
          :model="ruleForm"
          :rules="rules"
          ref="ruleForm"
          label-width="150px"
          class="demo-ruleForm"
      >
        <el-form-item label="管理员账号:" :label-width="this.formLabelWidth" prop='AccountName'>
          <el-input
              clearable
              minlength="5"
              maxlength='100'
              size="medium"
              placeholder=""
              v-model="ruleForm.AccountName"
          ></el-input>
        </el-form-item>
        <el-form-item label="管理员姓名:" :label-width="this.formLabelWidth" prop='Name'>
          <el-input
              clearable
              maxlength='100'
              size="medium"
              placeholder="请输入角色权限名称"
              v-model="ruleForm.Name"
          ></el-input>
        </el-form-item>
        <el-form-item label="管理员密码:" :label-width="this.formLabelWidth" prop='AccountPassword'>
          <el-input
              clearable
              maxlength='100'
              size="medium"
              autocomplete="off"

              v-model="ruleForm.AccountPassword"
          ></el-input>
        </el-form-item>
        <el-form-item label="请确认密码:" :label-width="this.formLabelWidth" prop='verifyPassword'>
          <el-input
              clearable
              maxlength='100'
              size="medium"
              autocomplete="off"
              v-model="ruleForm.verifyPassword"
          ></el-input>
        </el-form-item>
        <el-form-item v-if="this.title==='编辑'" label="账号状态:" :label-width="this.formLabelWidth" prop='name'>
          <el-select clearable size="medium" v-model="ruleForm.AccountStatetext" autocomplete="off">
            <el-option label="启用" value="0"></el-option>
            <el-option label="禁用" value="1"></el-option>
          </el-select>
        </el-form-item>
        <div class="demo-drawer__footer drawer_footer">
          <el-button size="medium" @click="goBack">取 消</el-button>
          <el-button size="medium" type="primary" @click="submitForm('ruleForm')">保 存</el-button>
        </div>
      </el-form>
    </el-col>
  </el-main>
</template>
<script>
import {setUpAccount, upDateAccount} from "@/api/systemManage.js";

export default {
  components: {},
  data() {
    return {
      title: '新增',
      loading: false,
      ruleForm: {
        AccountName: '',
        Name: '',
        AccountPassword: '',
        verifyPassword: '',
        AccountState: 0,
        AccountStatetext: '启用',
        AccountID:''
      },
      formLabelWidth: "120px",
      rules: {
        AccountName: [
          {
            required: true,
            message: "请输入管理员账号",
            trigger: "change",
          },
        ],
        Name: [
          {
            required: true,
            message: "请输入管理员真实姓名",
            trigger: "change",
          },
        ],
        AccountPassword: [
          {
            required: true,
            message: "请输入管理员密码",
            trigger: "change",
          }, {
            pattern: /^[0-9A-Za-z_]{6,12}$/, //正则
            message: '请输入6-12位新密码,字母数字下划线组合'
          }
        ],
        verifyPassword: [
          {
            required: true,
            message: "请确认密码",
            trigger: "change",
          }, {
            pattern: /^[0-9A-Za-z_]{6,12}$/, //正则
            message: '请输入6-12位新密码,字母数字下划线组合'
          }
        ]
      }
    }
  },
  created() {
    //从浏览器session里面获取sessionid。封装成许可数组
    let UserInfo = JSON.parse(sessionStorage.getItem("UserInfo"))
    const that = this
    that.sessionid = UserInfo.sessionid;
    that.title = that.$route.query.type
    if (that.$route.query.type === '编辑') {
      console.log(this.$route.query.data)
      let data = this.$route.query.data
      this.ruleForm.AccountName = data.AccountName
      this.ruleForm.Name = data.Name
      this.ruleForm.AccountPassword = data.AccountPassword
      this.ruleForm.AccountID = data.AccountID
      this.ruleForm.AccountStatetext=data.AccountState
    }
  },
  methods: {
    submitForm(ruleForm) {
      const that = this
      if (that.ruleForm.AccountPassword !== that.ruleForm.verifyPassword) {
        return this.$message.error('新密码和确认密码输入不一致!')
      }
      that.$refs["ruleForm"].validate((valid) => {
        if (valid) {
          this.ruleForm.AccountStatetext==='启用'?this.ruleForm.AccountState=0:this.ruleForm.AccountState=1
          let data = {
            AccountName: that.ruleForm.AccountName,
            AccountPassword: parseInt(that.ruleForm.AccountPassword),
            Name: that.ruleForm.Name,
            sessionid: that.sessionid
          }
          that.loading = true;
          if (that.$route.query.type === '编辑') {
            // 编辑
            data.Flag=1;
            data.AccountState=this.ruleForm.AccountState;
            data.AccountID=this.ruleForm.AccountID;
            console.log(data)
            upDateAccount(data).then((res) => {
              console.log(res)
              that.loading = false;
              if (res.data.result === 0) {
                that.$message.success('更新成功');
                that.goBack()
              } else if (res.data.result === -1) {
                that.$message.erarr('更新失败');
              } else if (res.data.result === -2) {
                that.$message.erarr('更新重复');
              }
            });
          } else {
            // 新增
            setUpAccount(data).then((res) => {
              console.log(res)
              that.loading = false;
              if (res.data.result === 0) {
                that.$message.success('新增成功');
                that.goBack()
              } else if (res.data.result === -1) {
                that.$message.erarr('新增失败');
              } else if (res.data.result === -2) {
                that.$message.erarr('账号重复');
              }
            });
          }
        } else {
          return false;
        }
      });
    },
    goBack() {
      this.$router.push('/systemManage')
    }
  }
}
</script>
<style lang="scss" scoped>
.main {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 5px;
  padding: 20px;

  .main-center {
    padding: 20px;
    border-radius: 5px;
    width: 65%;
    margin: 0 auto;
  }
}

.demo-drawer__footer {
  margin-top: 80px;
  text-align: center;
}
</style>