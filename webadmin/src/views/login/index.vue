<template>
  <div class="login-container">
    <div class="center">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="inImg">
          <img class="" src="../../assets/image/leftlogo.png" alt="">
        </div>
        <div class="loginBox">
          <div class="title-container">
            <h3 class="title">物业梯控管理系统</h3>
          </div>
          <el-form-item prop="xx" class="item ">
            <span class="svg-container">
              <i class="el-icon-user"/>
            </span>
            <el-input maxlength='100' ref="username" v-model="loginForm.username" placeholder="请输入账号" name="username"
                      type="text" tabindex="1" autocomplete="off"/>
          </el-form-item>

          <el-form-item prop="password" class="item ">
            <span class="svg-container">
              <i class="el-icon-lock"/>
            </span>
            <do-password-input v-model="loginForm.password"></do-password-input>
          </el-form-item>

          <el-form-item prop="captchaValue" class="item ">
            <span class="svg-container">
              <img class="el-icon-user" src="../../assets/image/captcha.png"/>
            </span>
            <el-input maxlength='4' ref="captchaValue" v-model="loginForm.captchaValue" placeholder="请输入验证码" name="captchaValue"
                      type="text" autocomplete="off">
              <template slot="suffix">
                <div class="item-input-img" >
                  <img class="item-input-captcha" :src=captchaSrc  alt="loading...">
                  <div class="item-input-refresh">
                    <img  src="../../assets/image/refresh.png" style="margin-top: -10px;"  @click="getImageVerifyCode" alt="刷新验证码">
                  </div>
                </div>
              </template>
            </el-input>
          </el-form-item>
          <div class="tips wid">
            <el-checkbox v-model="rememberMe" style="float: left;margin-left:15px; margin-top: 10px;margin-bottom: 50px;" label="记住密码"
                         name="type"></el-checkbox>
            <span></span>
          </div>
          <el-button :loading="loading" type="primary" class=""
                     @click.native.prevent="login">
            登录
          </el-button>
        </div>
      </el-form>
    </div>
    <el-dialog append-to-body :visible.sync="dialogVisible" :show-close="false" width="450px">
      <slideverify
          ref="dialogopen"
          :l="42"
          :r="10"
          :w="410"
          :h="200"
          :block_y="block_y"
          :imgurl="imgurl"
          :miniimgurl="miniimgurl"
          @success="imageVerifySuccess"
          @fail="imageVerifyFail"
          @refresh="imageVerifyRefresh"
          :slider-text="text">
      </slideverify>
    </el-dialog>
  </div>
</template>

<script>
import {getVerifyCode, login} from '@/api/user'
import {isEmpty, throttle} from '@/utils/tool'
import Cookies from 'js-cookie'
import CryptoJS from 'crypto-js'
import DoPasswordInput from "@/components/input/DoPasswordInput"
import slideverify from '@/components/slideVerify/DoSlideVerify'

export default {
  name: 'Login',
  components: {DoPasswordInput, slideverify},
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        captchaValue:'',
        captchaId:''
      },
      captchaSrc: '',
      // 是否开启滑块验证功能
      isShowImageCaptcha: false,
      // 滑块相关参数
      dialogVisible: false,
      block_y: '',
      imgurl: '',
      miniimgurl: '',
      checkMoveId: '',
      rememberMe: false,
      loading: false,
      loginRules: {
        username: [{required: true, message: "请输入账号", trigger: ['change', 'blur']}, {
          pattern: /^[0-9A-Za-z_]{1,12}$/, //正则
          message: '请输入6-12位账号,字母数字下划线组合'
        }],
        password: [{required: true, message: "请输入密码", trigger: ['change', 'blur']}, {
          pattern: /^[0-9A-Za-z_]{6,12}$/, //正则
          message: '请输入6-12位新密码,字母数字下划线组合'
        }],
        captchaValue:[{required: true, message: "请输入验证码", trigger: ['change', 'blur']}, {
          pattern: /^[0-9A-Za-z_]{4}$/, //正则
          message: '请输入4位验证码,字母数字下划线组合'
        }],
      },
    }
  },
  mounted() {
    // 清空历史存储的面包屑
    sessionStorage.setItem("theBreadcrumb1", '')
    sessionStorage.setItem("theBreadcrumb2", '')

    // this.isNeedVerify()
    this.init()
  },
  methods: {
    init() {
      let rememberPassword = Cookies.get("password")
      if (!isEmpty(rememberPassword)) {
        rememberPassword = CryptoJS.AES.decrypt(rememberPassword, 'secret key 123').toString(CryptoJS.enc.Utf8)
        this.loginForm.password = rememberPassword
        this.loginForm.username = Cookies.get("username")
        this.rememberMe = true
      } else {
        this.rememberMe = false
      }
    },

    login() {
      const that = this
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          if (that.isShowImageCaptcha) {
            that.dialogVisible = true
            that.getImageVerifyCode()
          } else {
            that.handleLogin()
          }
        } else {
          return false
        }
      })
    },
    async getImageVerifyCode() {
      const that = this
      that.captchaId = ''
      that.captchaSrc = ''
      that.block_y = ''
      let data = await getVerifyCode()
      let imgObj = data.data
      that.captchaId = imgObj.captchaId
      that.captchaSrc = "https://dingdingkaimen.cn/"+ imgObj.capUrl
    },

    imageVerifySuccess(left) {
      this.handleLogin(left)
    },
    imageVerifyFail() {
      this.getImageVerifyCode()
    },
    imageVerifyRefresh() {
      this.getImageVerifyCode()
    },
    onRefresh: throttle(function () {
      this.getImageVerifyCode()
    }, 1000),
    handleLogin(left) {
      let that = this
      // let parms = Object.assign({}, this.loginForm)
      let parms = {
        AccountName: that.loginForm.username,
        AccountPassword: that.loginForm.password,
        CaptchaId: that.captchaId,
        CaptchaValue: that.loginForm.captchaValue,
      }
      that.loading = true
      login(parms).then(rep => {
        console.log(rep)
        if (rep.data.result + '' === '0') {
          let thedata = {
            AccountID: rep.data.AccountID,
            AccountName: that.loginForm.username,
            AccountPassword:this.loginForm.password,
            sessionid: rep.data.sessionid,
            Grade:rep.data.Grade
          }
          sessionStorage.setItem("UserInfo", JSON.stringify(thedata))
          that.saveRememberPassword()
          that.$router.push('/')
        } else {
          //判断错误提示是否有滑块关键字，有就表示需要刷新滑块重新验证
          let msg = rep.data.msg;
          if (msg.indexOf('滑块') !== -1) {
            that.getImageVerifyCode()
          } else {
            that.dialogVisible = false;
          }
          that.$message.error(rep.data.msg)
          that.getImageVerifyCode()
        }
      }).finally(() => {
        that.loading = false
      })
    },
    saveRememberPassword() {
      if (this.rememberMe) {
        let rememberPassword = CryptoJS.AES.encrypt(this.loginForm.password, 'secret key 123');
        //自动保存密码设置为7天过期
        Cookies.set("username", this.loginForm.username, {expires: 7})
        Cookies.set("password", rememberPassword, {expires: 7})
      } else {
        Cookies.remove("username")
        Cookies.remove("password")
      }
    }
  },
  created() {
    this.getImageVerifyCode()
  }
}
</script>
<style lang="scss" scoped>
$bg: #fff;
$light_gray: #000;
$cursor: #000;

/* reset element-ui css */
.login-container {
  height: 100%;
  min-height: 550px;
  min-width: 1100px;
  width: 100%;
  background: url('../../assets/image/bg.png');
  background-size: 100% 100%;
  display: flex;
  display: -webkit-flex;
  align-items: center;
  justify-content: center;


  .center {
    width: 1400px;
    height: 650px;
    background: #FFFFFF;
    opacity: 0.74;
    border-radius: 42px;
    display: -webkit-flex; /* Safari */
    display: flex;
    flex-direction: row;
    min-width: 800px;
    margin: auto;

    .inImg {
      width: 60%;
      margin: auto;
    }

    .loginBox {
      width: 40%;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }
  }


  .login-form {
    position: relative;
    width: 100%;
    height: clac (650 - 80px);
    max-width: 100%;
    padding: 40px;
    overflow: hidden;
    display: flex;
    /deep/ .el-button {
      width: 334px;
      height: 62px;
      background: #5E64CE;
      opacity: 1;
      border-radius: 31px;
      margin-left: 20px;
    }
    /deep/ .el-form-item {
      border: none;
      border-bottom: 1px solid #E1DFDF;
      border-radius: 0;
      width: 358px;
      opacity: 1;
      .item-input-img{
        .item-input-captcha{
          width: 125px;
          height: 40px;
          background: #FFFFFF;
          opacity: 1;
          border-radius: 5px;
          float: left;
        }
        .item-input-refresh{
          width: 60px;
          height: 40px;
          float: left;
          background: #FFFFFF;
          opacity: 1;
          border-radius: 5px;
          display: inline-block;
          img{
            height: 21px;
            width: 21px;
          }
        }

      }
    }
    /deep/.el-checkbox__label{
      padding-left: 15px;
      color: #bbbbbb;
    }



  }

  /deep/ .el-input {
    display: inline-block;
    height: 35px;
    width: 90%;
    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 2px 5px 2px 15px;
      color: $light_gray;
      height: 35px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }
}


.svg-container {
  padding: 6px 5px 6px 15px;
  color: #889aa4;
  vertical-align: middle;
  width: 30px;
  display: inline-block;
  box-sizing: border-box;
}

.title-container {
  position: relative;
  max-width: 360px !important;

  .title {
    font-size: 28px;
    color: #222222;
    margin: 0px auto 30px auto;
    text-align: center;
    font-weight: 548;
  }
}

.tips {
  font-size: 14px;
  color: #000;
  cursor: pointer;

  :hover {
    color: #008080;
  }
}

.wid,
.el-button,
.user_name {
  max-width: 360px;
}

/deep/ .el-checkbox__inner is-checked {
  background: #409EFF;
}

</style>
