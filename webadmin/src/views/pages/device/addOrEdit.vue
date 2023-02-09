<template>
  <el-main class="main">
    <div>
      <h4>{{title}}</h4>
    </div>
    <el-col class="main-center">
      <el-form v-loading="loading" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm">
        <el-form-item label="梯号:" :label-width="this.formLabelWidth" prop='UnifiedID'>
          <el-input clearable size="medium" placeholder="请输入梯号" :disabled="title==='编辑'" v-model.number="ruleForm.UnifiedID"></el-input>
        </el-form-item>
        <el-form-item label="设备名称:" :label-width="this.formLabelWidth" prop='DeviceName'>
          <el-input clearable size="medium" placeholder="请输入设备名称" v-model="ruleForm.DeviceName"></el-input>
        </el-form-item>
        <el-form-item label="设备标识:" :label-width="this.formLabelWidth" prop='DeviceIdentification'>
          <el-input clearable size="medium" placeholder="请输入设备标识" :disabled="title==='编辑'" v-model="ruleForm.DeviceIdentification"></el-input>
        </el-form-item>
        <el-form-item label="设备类型:" :label-width="this.formLabelWidth" prop='DeviceType'>
          <el-select size="medium" placeholder="请选择设备类型" disabled v-model="ruleForm.DeviceType" style="width:100%">
            <!-- <el-option label="门禁" value="1">
            </el-option> -->
            <el-option label="梯控" value="2">
            </el-option>
            <!-- <el-option label="发卡器" value="3">
            </el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="设备型号:" :label-width="this.formLabelWidth" prop='DeviceModel'>
          <el-input clearable size="medium" placeholder="请输入设备型号" :disabled="title==='编辑'" v-model="ruleForm.DeviceModel"></el-input>
        </el-form-item>
        <!-- <el-form-item label="设备偏值:" :label-width="this.formLabelWidth" prop='DeviceInstalTimel'>
          <el-input clearable size="medium" placeholder="请输入设备偏值" v-model="ruleForm.DeviceInstalTimel"></el-input>
        </el-form-item> -->
        <el-form-item label="设备安装时间:" :label-width="this.formLabelWidth" prop='DeviceInstalTimel'>
          <el-date-picker v-model="ruleForm.DeviceInstalTimel" type="date" :disabled="title==='编辑'" value-format="yyyy-MM-dd" placeholder="选择安装时间" style="width:100%">
          </el-date-picker>
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
import { deviceAddDevice, updateDevice } from "@/api/device.js";
export default {
  data() {
    return {
      title: '新增',
      loading: false,
      ruleForm: {
        sessionid: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
        DeviceName: '',
        DeviceIdentification: '',
        DeviceType: '2',
        DeviceModel: '',
        DeviceInstalTimel: '',
        UnifiedID: ''
      },
      formLabelWidth: "120px",
      rules: {
        UnifiedID: [{ required: true, message: "请输入梯号", trigger: "change" }],
        DeviceName: [{ required: true, message: "请输入设备名称", trigger: "change" }],
        DeviceIdentification: [{ required: true, message: "请输入设备标识", trigger: "change" }],
        DeviceType: [{ required: true, message: "请输入设备类型", trigger: "change" }],
        DeviceModel: [{ required: true, message: "请输入设备型号", trigger: "change" }],
        DeviceInstalTimel: [{ required: true, message: "请选择安装时间", trigger: "change" }],
      }
    }
  },
  created() {
    this.title = this.$route.query.type
    if (this.$route.query.type === '编辑') {
      this.ruleForm = this.$route.query;
    }
  },
  mounted() {
  },
  methods: {
    submitForm(ruleForm) {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.$route.query.type === '编辑') {
            let data = {
              sessionid: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
              DeviceName: this.ruleForm.DeviceName,
              DeviceID: this.ruleForm.DeviceID
            }
            updateDevice(data).then(res => {
              this.loading = false;
              if (res.data.result === 0) {
                this.$message.success(res.data.msg)
                this.goBack();
              } else {
                this.$message.error(res.data.msg)
              }
            })
          } else {
            deviceAddDevice(this.ruleForm).then(res => {
              this.loading = false;
              if (res.data.result === 0) {
                this.$message.success(res.data.msg)
                this.goBack();
              } else {
                this.$message.error(res.data.msg)
              }
            })
          }
        }
      })
    },
    goBack() {
      this.$router.push('/device')
    },
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