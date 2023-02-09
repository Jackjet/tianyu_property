<template>
  <el-main>
    <el-col :span="24">
      <el-form :inline="true" class='el-InputForm'>
        <el-form-item label="姓名">
          <el-input clearable size="small" placeholder="请输入联系人姓名" v-model="searchItem.FullName"></el-input>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input clearable size="small" placeholder="请输入联系方式" v-model="searchItem.PhoneNum"></el-input>
        </el-form-item>
        <el-form-item style='margin-left: 1%;'>
          <el-button size='small' @click="search" icon="el-icon-search">查询</el-button>
          <el-button size='small' @click="reset" icon="el-icon-refresh-left">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col :span="24" class="center">
      <div style="padding-top: 20px;">
        <el-button type="primary"  plain size='mini' icon="el-icon-down" @click="downloadCode">导出访客申请码</el-button>
        <el-button type="danger"  plain size='mini' @click="handleDeletes" icon="el-icon-delete">删除</el-button>
      </div>
      <TableList :table-data='tableData' v-loading="isSubmitLoading" :table-selection="tableSelection" :table-label="tableHeader" :table-option="tableOpction" @onHandleSelectionChange="onHandleSelectionChange">
      </TableList>
    </el-col>
    <el-col :span='24'>
      <pagination ref="page" :total="total" @pageChange="pageChange"></pagination>
    </el-col>
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible1"
      width="70%">
      <div>
         <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="margin-top:20px;">
            <el-form-item label="家庭组ID" >
              <el-input :disabled='true' v-model="form.GroupID"></el-input>
            </el-form-item>
            <el-form-item label="户主姓名" prop='FullName'>
              <el-input :disabled='true' v-model="form.FullName"></el-input>
            </el-form-item>
            <el-form-item label="联系方式" prop='PhoneNum'>
              <el-input :disabled='true' v-model="form.PhoneNum"></el-input>
            </el-form-item>
            <el-form-item label="家庭住址" prop='UserGroupName'>
                <el-input :disabled='true' v-model="form.UserGroupName"></el-input>
            </el-form-item>
            <el-form-item label="注册时间" prop='InputTime'>
              <el-date-picker
              :disabled='true'
              v-model="form.InputTime"
              type="datetime"
              placeholder="选择日期时间">
            </el-date-picker>
            </el-form-item>
            <el-form-item label="梯控权限" v-if='!disabled'>
              <el-cascader
              :options="options"
              v-model="rules"
              :props="props"
              @change="rulesChange"
               collapse-tags
              clearable></el-cascader>
            </el-form-item>
            <el-form-item :label="disabled ? '梯控权限' : '当前选择'">
              <el-input type='textarea' :disabled='true' v-model="form.rule"></el-input>
            </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取消</el-button>
        <el-button type="primary" @click="handleOk">确 定</el-button>
      </span>
    </el-dialog>
  </el-main>
</template>
<script>
import { findById,findAll,deleterule ,findAllDevice,permissionedit} from '@/api/authority.js';
import TableList from '@/components/table/tableList'
import Pagination from '@/components/table/Pagination'

export default {
  components: { TableList, Pagination },
  data() {
    return {
      Authority:[],
      GroupID:'',
      rules:'',
      props: { multiple: true },
      options: [],
      address:[],
      form:{},
      disabled:false,
      dialogVisible: false,
      dialogVisible1: false,
      title:'组详情页',
      codeSrc:'',
      codeName:'',
      tableSelection: {
        key: true,
        type: 'selection',
        detaile: false
      },
      isSubmitLoading: false,
      tableData: [],
      tableHeader: [
        { label: '家庭组ID', list: 'GroupID' },
        { label: '联系人', list: 'FullName' },
        { label: '家庭住址', list: 'UserGroupName' },
        { label: '通行权限', list: 'ruleName' },
        { label: '联系方式', list: 'PhoneNum' },
      ],
      tableOpction: {
        label: '操作',
        width: '220px',
        value: 0,
        options: [
          {
            label: '组详情',
            key: 0,
            type: 'text',
            icon: '',
            State: true,
            method: (row) => {
              this.handleDetail(row)
            }
          },
          {
            label: '组编辑',
            key: 0,
            type: 'text',
            icon: '',
            State: true,
            method: (row) => {
              this.handleEdit(row)
            }
          },
          ]
      },
      searchItem: {
       
      },
      lastItem: {
        
      },
      total: 0,
      pageSize: '20',
      currentPage: '1',

    }
  },
  created() {
    this.fetchData(this.type)
  },
  methods: {
    // 重置
    reset() {
      const that = this
      that.searchItem.FullName = ''
      that.searchItem.PhoneNum = ''
    },
    // 获取列表
    async fetchData() {
      const that = this
      let data = {}
      data = this.searchItem
      data.sessionid =  JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
      data.currentpage = this.currentPage
      data.pagesize = this.pageSize
      try {
        that.isSubmitLoading = true
        const res = await findAll(data)
        that.isSubmitLoading = false
        this.tableData = res.data.Rules
        this.tableData.forEach((v,i)=>{
          let str = ''
        let arr = []
          v.rule.forEach((v1,i1)=>{
            v1.LiftRule.split(',').forEach((v2,i2)=>{
              str+=v1.DeviceName+'-'+v2+'F,'
            })
          })
          v.ruleName = str
        })
        this.total = res.data.totle
      } catch (even) {
        this.$message.error(even.msg)
      }
    },
    // 时间格式化
    add0(m){
      return m<10?'0'+m:m 
    },
    format(shijianchuo){
      //shijianchuo是整数，否则要parseInt转换
      var time = new Date(shijianchuo);
      var y = time.getFullYear();
      var m = time.getMonth()+1;
      var d = time.getDate();
      var h = time.getHours();
      var mm = time.getMinutes();
      var s = time.getSeconds();
      return y+'-'+this.add0(m)+'-'+this.add0(d)+' '+this.add0(h)+':'+this.add0(mm)+':'+this.add0(s);
    },
    // 搜索
    search() {
      this.currentPage = 1
      this.$refs.page.Page(1)
      this.fetchData(this.type)
    },
    // 翻页
    pageChange(item) {
      let that = this
      this.pageSize = item.limit
      this.currentPage = item.page
      if (that.lastItem.startDate === '' || that.lastItem.startDate === null) {
        that.time = []
      }
      Object.entries(that.searchItem).map((item, index) => {
        that.searchItem[item[0]] = that.lastItem[item[0]]
      })
      if (that.lastItem.startDate === '' || that.lastItem.startDate === null) {
        that.time = []
      }
      this.fetchData('page')
    },
    // 编辑
    async handleEdit(row) {
      this.GroupID = row.GroupID
      this.dialogVisible1 = true
      this.title = '组编辑页'
      this.disabled = false
      let data = {
        sessionid : JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
      }
      findAllDevice(data).then((res)=>{
        let options = []
        let children = []
        for(let i = 1;i <= 64;i++){
          children.push(
            {
            value:i+'F'+'_'+i,
            label:i+'F',
            }
          )
        }
        res.data.device.forEach((v,i)=>{
          options.push({
            value:v.DeviceName+'_'+v.DeviceID,
            label:v.DeviceName,
            children:children
          })
        })
        this.options = options
      })
      this.getDetaile(row)
    },
    rulesChange(){
      console.log(this.rules)
      let str = ''
      let arr = []
      this.rules.forEach((v,i)=>{
        str+=v[0].split('_')[0]+'-'+v[1].split('_')[0]+','
        let obj = {
          deviceid:v[0].split('_')[1]*1,
          LiftRules:''
        }
        this.rules.forEach((v1,i1)=>{
          if(v1[0].split('_')[1] == obj.deviceid){
            obj.LiftRules+= v1[1].split('_')[1]+','
          }
        })
        obj.LiftRules = obj.LiftRules.slice(0, -1);
          arr.push(obj)
      })
      var result = [];
      var obj = {};
      for(var i =0; i<arr.length; i++){
          if(!obj[arr[i].deviceid]){
              result.push(arr[i]);
              obj[arr[i].deviceid] = true;
          }
      }
      this.Authority = result
      this.form.rule = str.slice(0, -1);
    },
    // 详情
    async handleDetail(row) {
      this.dialogVisible1 = true
      this.title = '组详情页'
      this.disabled = true
      this.getDetaile(row)
    },
    // 获取详情
    async getDetaile(row){
      let data = {
        PersonID:row.PersonID,
      }
      data.sessionid = JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
      const res = await findById(data)
      let str = ''
      let arr = []
      res.data.Rules.forEach((v,i)=>{
        v.rule.forEach((v1,i1)=>{
          v1.LiftRule.split(',').forEach((v2,i2)=>{
            str+=v1.DeviceName+'-'+v2+'F,'
            let arr1 = [
              v1.DeviceName+'_'+v1.DeviceID,
              v2+'F_'+v2,
            ]
            arr.push(arr1)
          })
        })
        this.rules = arr
         v.InputTime = this.format(v.InputTime)
        if(v.GroupPower == 1){
          this.form = v
        }
      })
      this.form.rule = str.slice(0, -1);
      this.rulesChange()
    },
    // 多选
    onHandleSelectionChange(val) {
      let str = ""
      val.forEach(item => {
        str += item.GroupID + ","
      });
      this.checkbox = str.slice(0, -1);
    },
    // 批量删除
    handleDeletes() {
      this.$confirm('此操作将永久删除选中数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.checkbox.split(',').forEach((v,i)=>{
          let data = {
            GroupID: v,
            sessionid : JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
          }
          deleterule(data).then(res => {
              this.fetchData()
              setTimeout(()=>{
                this.$message.success('删除成功！');
              },100)
          })
        })
        
      }).catch(() => { });
    },
    handleOk(formName){
      if(this.title == '组编辑页'){
        let data = {
          GroupID:this.GroupID*1,
          Authority:this.Authority
        }
        data.sessionid = JSON.parse(sessionStorage.getItem('UserInfo')).sessionid 
        permissionedit(data).then((res)=>{
          this.fetchData()
          setTimeout(()=>{
            this.$message.success('编辑成功！');
          },100)
        })
      }
      this.dialogVisible1 = false
    },
    downloadCode () {
      window.open( process.env.VUE_APP_BASE_API + '/accesscontrol/visitor/PictureDownload', '_blank')
    },
  }
}
</script>
<style lang='scss' scoped>
  .code{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    .el-image{
      width: 60%;
    }
    p{
      font-size: 14px;
    }
    span{
      color: blue;
      cursor: pointer;
    }
  }
  .el-button{
    color: #fff;
    background-color: #656ABE;
  }
</style>

