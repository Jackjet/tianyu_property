<template>
  <el-main class="main">
    <div>
      <h4>{{title}}</h4>
    </div>
    <el-col class="main-center">
      <el-form v-loading="loading" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm">
        <el-form-item label="卡号:" :label-width="this.formLabelWidth" prop='Supply_CardNumber'>
          <el-input clearable size="medium" placeholder="请输入卡号" v-model="ruleForm.Supply_CardNumber"></el-input>
        </el-form-item>
        <el-form-item label="卡类型:" :label-width="this.formLabelWidth" prop='CardType'>
          <el-select clearable size="medium" placeholder="请选择卡类型" v-model="ruleForm.CardType" style="width:100%">
            <el-option label="乘梯卡" :value="1" />
            <el-option label="记次数卡" :value="2" />
            <el-option label="投入/退出卡" :value="3" />
            <el-option label="挂失卡" :value="4" />
            <el-option label="恢复卡" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="卡子类型:" :label-width="this.formLabelWidth" prop='CardSonType' v-if="ruleForm.CardType===1||ruleForm.CardType===2">
          <el-select clearable size="medium" placeholder="请选择卡子类型" v-model="ruleForm.CardSonType" style="width:100%">
            <el-option label="单层自动登记" :value="0" />
            <el-option label="残疾人或老年人卡" :value="1" />
            <el-option label="VIP卡" :value="2" />
            <el-option label="普通卡" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="授权时限:" :label-width="this.formLabelWidth" prop='CardValidPeriod' v-if="ruleForm.CardType===1||ruleForm.CardType===2">
          <el-date-picker v-model="ruleForm.CardValidPeriod" type="date" :picker-options="pickerOptions" value-format="yyyy-MM-dd" placeholder="请选择授权时限" style="width:100%">
          </el-date-picker>
        </el-form-item>
        <!-- 乘梯卡 -->
        <el-form-item label="选择人员:" :label-width="this.formLabelWidth" prop='PersonID' v-if="ruleForm.CardType===1">
          <el-select clearable size="medium" placeholder="请选择人员" filterable v-model="ruleForm.PersonID" style="width:100%">
            <el-option v-for="item in personnelList" :label="item.FullName+' — '+item.UserGroupName" :value="item.PersonID" :key="item.PersonID" />
          </el-select>
        </el-form-item>
        <!-- 挂失卡 -->
        <el-form-item label="选择人员:" :label-width="this.formLabelWidth" prop='ReportLostCardNumber' v-if="ruleForm.CardType===4||ruleForm.CardType===5">
          <el-select clearable size="medium" placeholder="请选择人员" filterable v-model="ruleForm.ReportLostCardNumber" style="width:100%">
            <el-option v-for="item in personnelList1" :label="item.FullName+`${item.UserGroupName?' — '+item.UserGroupName:''}`+' — '+item.CardNumber" :value="item.CardNumber" :key="item.CardNumber" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="卡权限:" :label-width="this.formLabelWidth" prop='DeviceType' v-if="ruleForm.CardType===1">
        </el-form-item> -->
        <el-form-item label="可用次数:" :label-width="this.formLabelWidth" prop='Frequency' v-if="ruleForm.CardType===2">
          <el-input clearable size="medium" placeholder="请输入可用次数" v-model="ruleForm.Frequency"></el-input>
        </el-form-item>
        <el-form-item label="持卡人:" :label-width="this.formLabelWidth" prop='FullName' v-if="ruleForm.CardType!==1">
          <el-input clearable size="medium" placeholder="请输入持卡人" v-model="ruleForm.FullName"></el-input>
        </el-form-item>
        <el-form-item label="联系方式:" :label-width="this.formLabelWidth" prop='PhoneNum' v-if="ruleForm.CardType!==1">
          <el-input clearable size="medium" placeholder="请输入联系方式" v-model="ruleForm.PhoneNum"></el-input>
        </el-form-item>
        <el-form-item label="梯控权限:" :label-width="this.formLabelWidth" prop='Authority' v-if="ruleForm.CardType===2">
          <el-cascader v-model="current" placeholder="请选择梯控权限" :props="defaultProps" :options="AuthoritylList" @change="handleChange" style="width:100%"></el-cascader>
        </el-form-item>
        <!-- <el-form-item label="当前选择:" :label-width="this.formLabelWidth" v-if="ruleForm.CardType===2">
          <el-input clearable size="medium" disabled v-model="current"></el-input>
        </el-form-item> -->
        <div class="demo-drawer__footer drawer_footer">
          <el-button size="medium" @click="goBack">取 消</el-button>
          <el-button size="medium" type="primary" @click="submitForm('ruleForm')">保 存</el-button>
        </div>
      </el-form>
    </el-col>
  </el-main>
</template>
<script>
import { personQueryAll, rl_distributioncards, deviceList, reportLossInFormation } from "@/api/cardManage.js";
export default {
  data() {
    return {
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        },
      },
      title: '系统发卡',
      loading: false,
      personnelList: [],
      personnelList1: [],
      AuthoritylList: [],
      defaultProps: {
        multiple: true,
        // value: "DeviceID",
        value: "UnifiedID",
        label: "DeviceName",
      },
      current: "",
      ruleForm: {
        sessionid: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
        DeviceID: this.$route.query.DeviceID * 1,
        Supply_CardNumber: this.$route.query.Supply_CardNumber,//卡号 2a575ea3
        // Supply_CardNumber: "2a575ea3",
        CardType: '',//卡类型
        CardSonType: '',//卡子类型
        CardValidPeriod: '',//有效期
        PersonID: '',//人员ID
        FullName: '',//持卡人姓名
        PhoneNum: '',//手机号
        Frequency: '20',//卡次数
        Authority: [],//梯控权限  
        ReportLostCardNumber: '',//被挂失卡片
      },
      formLabelWidth: "120px",
      rules: {
        Supply_CardNumber: [{ required: true, message: "请输入卡号", trigger: "change" }],
        CardType: [{ required: true, message: "请选择卡类型", trigger: "change" }],
        CardSonType: [{ required: true, message: "请选择卡子类型", trigger: "change" }],
        // CardValidPeriod: [{ required: true, message: "请选择授权时限", trigger: "change" }],
        PersonID: [{ required: true, message: "请选择人员", trigger: "change" }],
        FullName: [{ required: true, message: "请输入持卡人", trigger: "change" }],
        PhoneNum: [{ required: true, message: "请输入联系方式", trigger: "change" }],
        Frequency: [{ required: true, message: "请输入可用次数", trigger: "change" }],
        Authority: [{ required: true, message: "请选择梯控权限", trigger: "change" }],
        ReportLostCardNumber: [{ required: true, message: "请选择人员", trigger: "change" }],
      }
    }
  },
  created() {
    // 全部人员接口
    personQueryAll({ sessionid: this.ruleForm.sessionid }).then(res => {
      this.personnelList = res.data.info;
    })
    // 挂失人员列表
    reportLossInFormation({ sessionid: this.ruleForm.sessionid, CardState: 3 }).then(res => {
      this.personnelList1 = res.data;
    })
    // 设备列表
    deviceList({ sessionid: this.ruleForm.sessionid }).then(res => {
      this.AuthoritylList = res.data.device;
      this.AuthoritylList.forEach(item => {
        item.children = [];
        for (let i = 1; i <= 64; i++) {
          item.children.push({
            // DeviceID: i,
            UnifiedID: i,
            DeviceName: i + "F"
          })
        }
      })
    })
  },
  methods: {
    handleChange(val) {
      let arr = [], data = [];
      val.forEach(item => {
        if (arr.indexOf(item[0]) === -1) {
          arr.push(item[0])
        }
      })
      arr.forEach(v => {
        data.push({
          "UnifiedID": v + ''
        })
      })
      data.forEach(k => {
        let str = '';
        val.forEach(item => {
          if (k["UnifiedID"] == item[0]) {
            str += item[1] + ',';
          }
        })
        // console.log(str)
        k["LiftRules"] = str.slice(0, -1);
      })
      this.ruleForm.Authority = data;
    },
    submitForm(ruleForm) {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.ruleForm.CardType === 1) {
            delete this.ruleForm.FullName;
            delete this.ruleForm.PhoneNum;
            delete this.ruleForm.Frequency;
            delete this.ruleForm.Authority;
            delete this.ruleForm.ReportLostCardNumber;
          } else if (this.ruleForm.CardType === 2) {
            delete this.ruleForm.ReportLostCardNumber;
            delete this.ruleForm.PersonID;
          } else {
            delete this.ruleForm.CardSonType;
            delete this.ruleForm.CardValidPeriod;
            delete this.ruleForm.PersonID;
            delete this.ruleForm.Frequency;
            delete this.ruleForm.Authority;
          }
          rl_distributioncards(this.ruleForm).then(res => {
            this.loading = false;
            if (res.data.result === 0) {
              this.$message.success(res.data.msg);
              this.goBack();
            } else {
              this.$message.error(res.data.msg);
            }
          })
        }
      })
    },
    goBack() {
      this.$router.push('/cardManage')
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