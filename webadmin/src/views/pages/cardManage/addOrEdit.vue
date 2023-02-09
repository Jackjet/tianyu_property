<template>
  <el-main class="main">
    <div>
      <h4>{{title}}</h4>
    </div>
    <el-col class="main-center">
      <el-form v-loading="loading" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm">
        <el-form-item label="设备名称:" :label-width="this.formLabelWidth" prop='DeviceName'>
          <el-input clearable size="medium" placeholder="请输入设备名称" :disabled="title==='详情'" v-model="ruleForm.DeviceName"></el-input>
        </el-form-item>
        <el-form-item label="设备标识:" :label-width="this.formLabelWidth" prop='DeviceIdentification'>
          <el-input clearable size="medium" placeholder="请输入设备标识" :disabled="title==='详情'" v-model="ruleForm.DeviceIdentification"></el-input>
        </el-form-item>
        <el-form-item label="设备类型:" :label-width="this.formLabelWidth" prop='DeviceType'>
          <el-select clearable size="medium" placeholder="请选择设备类型" :disabled="title==='详情'" v-model="ruleForm.DeviceType" style="width:100%">
            <el-option label="发卡器" value="3">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备型号:" :label-width="this.formLabelWidth" prop='DeviceModel'>
          <el-input clearable size="medium" placeholder="请输入设备型号" :disabled="title==='详情'" v-model="ruleForm.DeviceModel"></el-input>
        </el-form-item>
        <el-form-item label="服务器IP:" :label-width="this.formLabelWidth" prop='ServerIP'>
          <el-input clearable size="medium" placeholder="请输入服务器IP" :disabled="title==='详情'" v-model="ruleForm.ServerIP"></el-input>
        </el-form-item>
        <el-form-item label="服务端口:" :label-width="this.formLabelWidth" prop='DevicePort'>
          <el-input clearable size="medium" placeholder="请输入服务端口" :disabled="title==='详情'" v-model="ruleForm.DevicePort"></el-input>
        </el-form-item>
        <el-form-item label="WIFI名称:" :label-width="this.formLabelWidth" prop='DeviceWifiName'>
          <el-input clearable size="medium" placeholder="请输入WIFI名称" :disabled="title==='详情'" v-model="ruleForm.DeviceWifiName"></el-input>
        </el-form-item>
        <el-form-item label="WIFI密码:" :label-width="this.formLabelWidth" prop='DeviceWifiPassWord'>
          <el-input clearable size="medium" placeholder="请输入WIFI密码" :disabled="title==='详情'" v-model="ruleForm.DeviceWifiPassWord"></el-input>
        </el-form-item>
        <el-form-item label="联网类型:" :label-width="this.formLabelWidth" prop='Ip_Mode'>
          <el-select clearable size="medium" placeholder="请选择网络类型" :disabled="title==='详情'" v-model="ruleForm.Ip_Mode" style="width:100%">
            <el-option label="动态" value="0">
            </el-option>
            <el-option label="静态" value="1">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="ruleForm.Ip_Mode==='1'" label="联网IP:" :label-width="this.formLabelWidth" :prop='ruleForm.Ip_Mode==="1"?"DeviceIP":""'>
          <el-input clearable size="medium" placeholder="请输入联网IP" :disabled="title==='详情'" v-model="ruleForm.DeviceIP"></el-input>
        </el-form-item>
        <el-form-item v-if="ruleForm.Ip_Mode==='1'" label="掩码:" :label-width="this.formLabelWidth" :prop='ruleForm.Ip_Mode==="1"?"Mask":""'>
          <el-input clearable size="medium" placeholder="请输入掩码" :disabled="title==='详情'" v-model="ruleForm.Mask"></el-input>
        </el-form-item>
        <el-form-item v-if="ruleForm.Ip_Mode==='1'" label="网关:" :label-width="this.formLabelWidth" :prop='ruleForm.Ip_Mode==="1"?"Gateway":""'>
          <el-input clearable size="medium" placeholder="请输入网关" :disabled="title==='详情'" v-model="ruleForm.Gateway"></el-input>
        </el-form-item>
        <el-form-item v-if="ruleForm.Ip_Mode==='1'" label="DNS:" :label-width="this.formLabelWidth" :prop='ruleForm.Ip_Mode==="1"?"Dns":""'>
          <el-input clearable size="medium" placeholder="请输入DNS" :disabled="title==='详情'" v-model="ruleForm.Dns"></el-input>
        </el-form-item>
        <div class="demo-drawer__footer drawer_footer">
          <el-button size="medium" @click="goBack">返 回</el-button>
          <el-button size="medium" type="primary" @click="submitForm('ruleForm')" v-if="title!=='详情'">保 存</el-button>
        </div>
      </el-form>
    </el-col>
    <el-dialog width="40%" title="设备配置" :visible.sync="dialogFormVisible" :before-close="handleClose">
      <div style="display: flex;justify-content: center;">
        <div id="qrcode" ref="qrcode"></div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="downloadE">下载</el-button>
      </div>
    </el-dialog>
  </el-main>
</template>
<script>
import { addCardIssuer, updateCardIssuer } from "@/api/cardManage.js";
import QRCode from 'qrcodejs2';
export default {
  data() {
    return {
      dialogFormVisible: false,
      title: '新增',
      loading: false,
      ruleForm: {
        sessionid: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
        DeviceName: '',
        DeviceIdentification: '',
        DeviceType: '',
        DeviceModel: '',
        DeviceWifiName: '',
        DeviceWifiPassWord: '',
        DevicePort: '',
        ServerIP: '',
        Ip_Mode: '',
        DeviceIP: '',
        Mask: '',
        Gateway: '',
        Dns: ''
      },
      formLabelWidth: "120px",
      rules: {
        DeviceName: [{ required: true, message: "请输入设备名称", trigger: "change" }],
        DeviceIdentification: [{ required: true, message: "请输入设备标识", trigger: "change" }],
        DeviceType: [{ required: true, message: "请输入设备类型", trigger: "change" }],
        DeviceModel: [{ required: true, message: "请输入设备型号", trigger: "change" }],
        DevicePort: [{ required: true, message: "请填写服务端口", trigger: "change" }],
        ServerIP: [{ required: true, message: "请填写服务器IP", trigger: "change" }],
        DeviceWifiName: [{ required: true, message: "请填写WIFI名称", trigger: "change" }],
        DeviceWifiPassWord: [{ required: true, message: "请填写WIFI密码", trigger: "change" }],
        Ip_Mode: [{ required: true, message: "请选择网络类型", trigger: "change" }],
        DeviceIP: [{ required: true, message: "请输入联网IP", trigger: "change" }],
        Mask: [{ required: true, message: "请输入掩码", trigger: "change" }],
        Gateway: [{ required: true, message: "请输入网关", trigger: "change" }],
        Dns: [{ required: true, message: "请输入DNS", trigger: "change" }],
      }
    }
  },
  created() {
    this.title = this.$route.query.type
    if (this.$route.query.type === '编辑' || this.$route.query.type === '详情') {
      this.ruleForm = this.$route.query;
    }
  },
  methods: {
    submitForm(ruleForm) {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.$route.query.type === '编辑') {
            this.ruleForm.sessionid = JSON.parse(sessionStorage.getItem("UserInfo")).sessionid;
            updateCardIssuer(this.ruleForm).then(res => {
              this.loading = false;
              if (res.data.result === 0) {
                this.dialogFormVisible = true;
                this.$nextTick(() => {
                  new QRCode('qrcode', {
                    width: 200,
                    height: 200,
                    correctLevel: 3,
                    text: res.data.content,
                  })
                })
                this.$message.success('编辑成功');
                // this.goBack();
              } else {
                this.$message.error(res.data.msg);
              }
            })
          } else {
            addCardIssuer(this.ruleForm).then(res => {
              this.loading = false;
              if (res.data.result === 0) {
                this.dialogFormVisible = true;
                this.$nextTick(() => {
                  new QRCode('qrcode', {
                    width: 200,
                    height: 200,
                    text: res.data.content,
                  })
                })
                this.$message.success('添加成功');
                // this.goBack();
              } else {
                this.$message.error(res.data.msg)
              }
            })
          }
        }
      })
    },
    downloadE() {
      let canvarData = this.$refs.qrcode.getElementsByTagName('canvas');
      let a = document.createElement("a");
      let event = new MouseEvent('click');
      a.href = canvarData[0].toDataURL("image/png");
      a.download = "drcQrcode";
      a.dispatchEvent(event);
    },
    goBack() {
      this.$router.push('/cardManage')
    },
    handleClose() {
      this.dialogFormVisible = false
      this.$refs.qrcode.innerHTML = "";
    }
  }
}
</script>
<style lang='scss' scoped>
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