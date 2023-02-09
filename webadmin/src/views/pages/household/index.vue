<template>
  <el-main>
    <el-col :span="24">
      <el-form :inline="true" class='el-InputForm'>
        <el-form-item label="姓名">
          <el-input clearable size="small" placeholder="请输入户主姓名" v-model="searchItem.FullName"></el-input>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input clearable size="small" placeholder="请输入联系方式" v-model="searchItem.PhoneNum"></el-input>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select size="small" clearable v-model="type" placeholder="请选择审核状态">
            <el-option label="待审核" value="2"></el-option>
            <el-option label="已通过" value="0"></el-option>
            <el-option label="已拒绝" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item style='margin-left: 1%;'>
          <el-button size='small' @click="search" icon="el-icon-search">查询</el-button>
          <el-button size='small' @click="reset" icon="el-icon-refresh-left">重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col :span="24" class="center">
      <div style="padding-top: 20px;text-align:left">
        <el-button type="primary"  plain size='mini' @click="handleAdd" icon="el-icon-plus">邀请</el-button>
        <el-button type="danger"  plain size='mini' @click="handleDeletes" icon="el-icon-delete">删除</el-button>
      </div>
      <TableList :table-data='tableData' v-loading="isSubmitLoading" :table-selection="tableSelection" :table-label="tableHeader" :table-option="tableOpction" @onHandleSelectionChange="onHandleSelectionChange">
      </TableList>
    </el-col>
    <el-col :span='24'>
      <pagination ref="page" :total="total" @pageChange="pageChange"></pagination>
    </el-col>
    <el-dialog
      title="户主邀请"
      :visible.sync="dialogVisible"
      width="30%">
      <div class="code">
        <el-image :src='codeSrc'></el-image>
        <p>（小程序扫描后填写户主信息并提交审核）</p>
        <span @click="downLoadCode">下载二维码</span>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible1"
      width="70%">
      <div>
         <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="margin-top:20px;">
            <el-form-item label="家庭组ID" >
              <el-input disabled='true' v-model="form.GroupID"></el-input>
            </el-form-item>
            <el-form-item label="户主姓名" prop='FullName'>
              <el-input :disabled='disabled' v-model="form.FullName"></el-input>
            </el-form-item>
            <el-form-item label="联系方式" prop='PhoneNum'>
              <el-input :disabled='disabled' v-model="form.PhoneNum"></el-input>
            </el-form-item>
            <el-form-item  v-if='disabled' label="家庭住址" prop='UserGroupName'>
              <el-row>
                <el-col span='22'>
                  <el-input :disabled='disabled' v-model="form.UserGroupName"></el-input>
                </el-col>
                <el-col span='2' style='text-align:center' v-if='!disabled'>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item v-if='!disabled' v-for='(v,i) in address' :key='i' :label="v.label" prop='liaisonName'>
              <el-row>
                <el-col span='22'>
                 <el-input :disabled='disabled' v-model="v.value"></el-input>
                </el-col>
                <el-col span='2' style='text-align:center'>
                  <i style="cursor:pointer" v-if='i == 0' class="el-icon-plus" @click="addAddress"></i>
                  <i  @click="delAddress(v.label)" v-else style="cursor:pointer" class="el-icon-delete"></i>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="注册时间" prop='InputTime'>
              <el-date-picker
              :disabled='true'
              v-model="form.InputTime"
              type="datetime"
              placeholder="选择日期时间">
            </el-date-picker>
            </el-form-item>
            <el-form-item label="梯控权限" prop='rule'>
              <el-input type='textarea'  :disabled='true' v-model="form.rule"></el-input>
            </el-form-item>
            <el-form-item label="" v-if='dataNull'>
              <!-- <el-input type='textarea'  :disabled='true' v-model="form.rule"></el-input> -->
              当前家庭组暂无成员
            </el-form-item>
            <el-row :style="disabled ? '' : 'border:1px solid black;padding-top:20px;margin-bottom:20px'" v-for="(v,i) in form.family" :key='i'>
              <el-col span='18'>
                <el-form-item label="家庭成员" v-if='v.FullName'>
              <el-input :disabled='true' v-model="v.FullName"></el-input>
              </el-form-item>
              <el-form-item label="成员电话" v-if='v.PhoneNum'>
                <el-input :disabled='true' v-model="v.PhoneNum"></el-input>
              </el-form-item>
              <el-form-item label="成员邮箱" v-if='v.Email'>
                <el-input :disabled='true' v-model="v.Email"></el-input>
              </el-form-item>
              </el-col>
              <el-col span='6' style="height:100%;text-align:center;line-height:100%;cursor:pointer">
                <span v-if='!disabled' style="display:block" @click="deleteUser(v.UserGroupID)">删除</span>
              </el-col>
            </el-row>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取消</el-button>
        <el-button type="primary" @click="handleOk('form')">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="通过审核"
      :visible.sync="dialogVisible2"
      width="70%">
      <div>
         <el-form ref="form1" :model="form" :rules="rules" label-width="120px" style="margin-top:20px;">
            <el-form-item label="家庭组ID" >
              <el-input :disabled='true' v-model="form.GroupID"></el-input>
            </el-form-item>
            <el-form-item label="户主姓名">
              <el-input :disabled='true' v-model="form.FullName"></el-input>
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input :disabled='true' v-model="form.PhoneNum"></el-input>
            </el-form-item>
            <el-form-item label="家庭住址">
                <el-input :disabled='true' v-model="form.UserGroupName"></el-input>
            </el-form-item>
            <el-form-item label="注册时间" >
              <el-date-picker
              :disabled='true'
              v-model="form.InputTime"
              type="datetime"
              placeholder="选择日期时间">
            </el-date-picker>
            </el-form-item>
            <el-form-item label="梯控权限"   prop='rules1'>
              <el-cascader
              :options="options"
              v-model="form.rules1"
              :props="props"
              @change="rulesChange"
               collapse-tags
              clearable></el-cascader>
            </el-form-item>
            <el-form-item label="当前选择">
              <el-input type='textarea' :disabled='true' v-model="form.rule"></el-input>
            </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible2 = false">取消</el-button>
        <el-button type="primary" @click="handleOk1('form1')">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="拒绝原因"
      :visible.sync="dialogVisible3"
      width="70%">
      <div>
         <el-form ref="form1" :model="form1" :rules="rules1"  label-width="120px" style="margin-top:20px;">
            <el-form-item label="拒绝原因" prop='reason'>
              <el-input type='textarea' v-model="form1.reason"></el-input>
            </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible3 = false">取消</el-button>
        <el-button type="primary" @click="handleOk2('form1')">确 定</el-button>
      </span>
    </el-dialog>
  </el-main>
</template>
<script>
import { inviteperson,findAll,queryfamily,personapproval,delusergroup,editGroup} from '@/api/household.js';
import { findAllDevice ,permissionedit} from '@/api/authority.js';

import TableList from '@/components/table/tableList'
import Pagination from '@/components/table/Pagination'

export default {
  components: { TableList, Pagination },
  data() {
    return {
      Authority:[],
      detaileRow:{},
      dataNull:'',
      GroupID:'',
      props: { multiple: true },
      options: [],
      address:[
        
      ],
      type:'',
      form:{},
      form1:{},
      disabled:false,
      dialogVisible: false,
      dialogVisible1: false,
      dialogVisible2: false,
      dialogVisible3: false,
      title:'组详情页',
      codeSrc:'',
      base64:'',
      tableSelection: {
        key: true,
        type: 'selection',
        detaile: false
      },
      rules: {
        FullName: [
          { required: true, message: '请输入户主姓名', trigger: 'change' }
        ],
        PhoneNum: [
          { required: true, message: '请输入联系电话', trigger: 'change' }
        ],
        rules1: [
          { required: true, message: '请选择梯控权限', trigger: 'change' }
        ],
      },
      rules1: {
        reason: [
          { required: true, message: '请输入拒绝原因', trigger: 'change' }
        ],
      },

      isSubmitLoading: false,
      tableData: [],
      tableHeader: [
        { label: '家庭组ID', list: 'GroupID' },
        { label: '户主姓名', list: 'FullName' },
        { label: '联系方式', list: 'PhoneNum' },
        { label: '家庭住址', list: 'UserGroupName' },
        { label: '注册时间', list: 'InputTime' },
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
          {
            label: '通过',
            key: 0,
            type: 'text',
            icon: '',
            show:(row)=>{
              if(row.PersonStatus == 2){
                return true
              }else{
                return false
              }
            },
            State: true,
            method: (row) => {
              this.changeStatus(row,'1')
            }
          },
          {
            label: '拒绝',
            key: 0,
            type: 'text',
            icon: '',
            State: true,
            show:(row)=>{
              if(row.PersonStatus == 2){
                return true
              }else{
                return false
              }
            },
            method: (row) => {
              this.changeStatus(row,'2')
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
      checkbox1:'',
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    addAddress(){
      if(this.form.UserGroupName == ''){
        this.$message({
          message: '请填写完全家庭住址信息',
          type: 'warning'
        });
      }else{
        let last = this.address[this.address.length-1]
        if(!last){
          this.address.push({
            label:'家庭住址'+(this.address.length*1+1),
            value:''
          })
        }else if(last.value){
          this.address.push({
            label:'家庭住址'+(this.address.length*1+1),
            value:''
          })
        }else{
            this.$message({
              message: '请填写完全家庭住址信息',
              type: 'warning'
            });
        }
      }
    },
    delAddress(label){
        let address = []
        this.address.forEach((v,i)=>{
          if(v.label != label){
            address.push(v)
          }
        })
        this.address = this.sortName(address)
    },
    sortName(arr){
      arr.forEach((v,i)=>{
        v.label = '家庭住址'+(i*1+1)
      })
      return arr
    },
    // 添加
    async handleAdd() {
      this.dialogVisible = true
      let data = {
        power:1,
        GroupID:''
      }
      data.sessionid = JSON.parse(sessionStorage.getItem('UserInfo')).sessionid 
      const res = await inviteperson(data)
      this.codeSrc = process.env.VUE_APP_IMAGE_API + res.data.imgurl
      this.base64 = res.data.base64
    },
    // 更改状态
    changeStatus(row,status){
      this.detaileRow = row
      if(status == 1){
        this.dialogVisible2 = true
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
        this.getDetaile(row,true)
      }else{
        this.dialogVisible3 = true
      }
    },
    rulesChange(){
      let str = ''
      let arr = []
      console.log(this.form.rules1)
      this.form.rules1.forEach((v,i)=>{
        str+=v[0].split('_')[0]+'-'+v[1].split('_')[0]+','
        let obj = {
          deviceid:v[0].split('_')[1]*1,
          LiftRules:''
        }
        this.form.rules1.forEach((v1,i1)=>{
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
    // 重置
    reset() {
      const that = this
      that.searchItem.FullName = ''
      that.searchItem.PhoneNum = ''
    },
    // 下载二维码
    downLoadCode(){
      var url = this.base64
      const a = document.createElement('a');
      a.href = url;
      a.setAttribute('download', '二维码');
      a.click();
    },
    // 获取列表
    async fetchData() {
      const that = this
      let data = {}
      data = this.searchItem
      data.sessionid =  JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
      data.PersonStatus = that.type
      data.currentpage = this.currentPage
      data.pagesize = this.pageSize
      try {
        that.isSubmitLoading = true
        const res = await findAll(data)
        that.isSubmitLoading = false
        this.tableData = res.data.Groups
        this.tableData.forEach((v,i)=>{
          v.InputTime = this.format(v.InputTime)
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
    // 时间格式化
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
      this.fetchData()
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
      this.dialogVisible1 = true
      this.title = '组编辑页'
      this.disabled = false
      this.GroupID = row.GroupID
      this.getDetaile(row)
    },
    // 详情
    async handleDetail(row) {
      this.dialogVisible1 = true
      this.title = '组详情页'
      this.disabled = true
      this.getDetaile(row)
    },
    // 获取详情
    async getDetaile(row,status){
      this.detaileRow = row
      let data = {
        GroupID:row.GroupID,
        Flag:2
      }
      this.GroupID = row.GroupID
      data.sessionid = JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
      const res = await queryfamily(data)
      let str = ''
      let arr = []
      let arr2 = []
      res.data.Persons.forEach((v,i)=>{
        v.InputTime = this.format(v.InputTime)
        if(v.GroupPower == 1){
          v.rules.forEach((v1,i1)=>{
            v1.LiftRule.split(',').forEach((v2,i2)=>{
              str+=v1.DeviceName+'-'+v2+'F,'
              let arr1 = [
                v1.DeviceName+'_'+v1.DeviceID,
                v2+'F_'+v2,
              ]
              arr.push(arr1)
            })
          })
          this.form.rules1 = arr
          this.form = v
          let address = []
          v.UserGroupName.split(',').forEach((v1,i1)=>{
            address.push({
              label:'家庭住址'+(i1*1+1),
              value:v1
            })
          })
          this.address = address
          if(status){
            this.rulesChange()
          }
        }else{
          console.log(v.Status)
          if(v.Status == 2){
            arr2.push(v)
          }
        }
      })
      this.form.rule = str
      this.form.family = arr2
      if(arr2.length > 0){
        this.dataNull = false
      }else{
        this.dataNull = true
      }
    },
    // 多选
    onHandleSelectionChange(val) {
      let str = ""
      let str1 = ''
      val.forEach(item => {
        str += item.PersonID + ","
        str1 += item.GroupID + ","
      });
      this.checkbox = str.slice(0, -1);
      this.checkbox1 = str1.slice(0, -1);
    },
    // 批量删除
    handleDeletes() {
      this.$confirm('此操作将永久删除选中数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.checkbox1.split(',').forEach((v,i)=>{
          let data = {
            GroupID: v,
            Flag:1,
            sessionid : JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
          }
          delusergroup(data).then(res => {
              this.fetchData()
          })
        })
        setTimeout(()=>{
          this.$message.success('删除成功！');
        },100)
      }).catch(() => { });
    },
    deleteUser(id){
      this.$confirm('此操作将永久删除该组人员信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let data = {
            UserGroupID: id,
            Flag:2,
            sessionid : JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
          }
        delusergroup(data).then(res => {
          setTimeout(()=>{
            this.getDetaile(this.detaileRow)
            this.$message.success('删除成功！');
          },100)
        })
        
      }).catch(() => { });
    },
    handleOk(formName){
      if(this.title == '组编辑页'){
        this.$refs[formName].validate((valid) => {
        if (valid) {
          let data = {
            sessionid:JSON.parse(sessionStorage.getItem('UserInfo')).sessionid,
            GroupID:this.GroupID,
            FullName:this.form.FullName,
            PhoneNum:this.form.PhoneNum,
            UserGroupName:''
          }
          let flag = 0
          this.address.forEach((v,i)=>{
            if(v.value != ''){
              data.UserGroupName+=v.value+','
            }else{
              flag = 1
            }
          })
          if(flag == 1){
            this.$message({
              message: '请填写完全家庭住址信息',
              type: 'warning'
            });
          }else{
            data.UserGroupName = data.UserGroupName.slice(0, -1)
            editGroup(data).then(res => {
              if (res.data.result === 0) {
                this.$message.success('编辑成功！');
              } else {
                this.$message.error('编辑失败');
              }
              setTimeout(()=>{
                this.fetchData()
              },100)
            })
          }
        } else {
          return false;
        }
      });
      }
      this.dialogVisible1 = false
      
    },
     handleOk2(formName){
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          let data = {
            PersonID:this.detaileRow.PersonID,
            Status:2,
            reason:this.form1.reason
          }
          data.sessionid = JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
          const res = await personapproval(data)
          setTimeout(()=>{
            this.fetchData()
              this.$message.success('拒绝成功！');
          },100)
          this.dialogVisible3 = false
        } else {
          return false;
        }
      });
    },
    async handleOk1(formName){
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          let data = {
            PersonID:this.detaileRow.PersonID,
            Status:1,
            reason:''
          }
          data.sessionid = JSON.parse(sessionStorage.getItem('UserInfo')).sessionid
          const res = await personapproval(data)
          let data1 = {
            GroupID:this.GroupID*1,
            Authority:this.Authority
          }
          data1.sessionid = JSON.parse(sessionStorage.getItem('UserInfo')).sessionid 
          permissionedit(data1).then((res)=>{
            this.fetchData()
          })
          setTimeout(()=>{
            this.fetchData()
            this.$message.success('通过成功！');
          },100)
          this.dialogVisible2 = false
        } else {
          return false;
        }
      });
    }
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

