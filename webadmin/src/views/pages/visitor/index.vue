<template>
  <el-main class="main">
    <el-col :span="24">
      <el-form :inline="true" class='el-InputForm'>

        <el-form-item label="访客姓名">
          <el-input clearable
                     size="small"
                    placeholder="请输入访客姓名"
                    v-model="SearchItem.VisitorName">
          </el-input>
        </el-form-item>

        <el-form-item label="被访客姓名">
          <el-input clearable
                     size="small"
                    placeholder="请输入被访人"
                    v-model="SearchItem.IntervieweeName">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button size="small" @click="SearchNoteList" icon="el-icon-search">
            搜索
          </el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <el-col :span="24" style="padding-top: 20px;">
      <el-button type="danger" plain size='mini' v-if="permissions.batchDelete" @click="handleDelete" icon="el-icon-delete">批量删除
      </el-button>
    </el-col>


    <el-col :span='24'>
      <TableList :table-data='tableData'
                 v-loading="isSubmitLoading"
                 @onHandleSelectionChange="handleSelectionChange"
                 :table-selection="tableSelection"
                 :table-label="tableHeader"
                 :table-option="tableOpction"
      >
      </TableList>
    </el-col>
    <el-col :span='24'>
      <pagination ref="page" :total="total" @pageChange="pageChange"></pagination>
    </el-col>
  </el-main>
</template>

<script>
import TableList from '@/components/table/tableList'
import Pagination from '@/components/table/Pagination'
import {
  getVisitorList,
  visitorRecordUpdate
} from "@/api/visitor";

export default {
  components: {TableList, Pagination},
  data() {
    return {
      // 许可证明：
      permissions: {
        add: false, //添加许可
        edit: false,  //修改
        enable: false,  //通过
        detail: true,  //详情
        templateDownload: false,//模板下载
        batchRetPassword: false,//批量重置密码
        batchDelete: true,//批量删除
        userExport: false,//用户导出
        userImport: false,//用户导入
        batchEnable: false,//batchEnable
        batchBan: false//批量禁用
      },
      action: process.env.VUE_APP_BASE_API + '/webadmin/system/webAdminUser/import',//上传地址
      headers: {
        Authorization: 'token ' + JSON.parse(sessionStorage.getItem("UserInfo")).token,
      },//请求头
      fileList: [],//文件列表
      tableSelection: {
        key: true,
        type: 'selection',
        detaile: false
      },//列表选项
      isSubmitLoading: false,//提交按钮懒加载
      DeletelistiD: [],//批量选择
      enbleList: [],//授权列表
      tableData: [],//表格数据
      tableHeader: [
        {label: '访客姓名', list: 'VisitorApplyName'},
        {label: '来访时间', list: 'VisitorBeginTime'},
        {label: '结束时间', list: 'VisitorEndTime'},
        {label: '通行区域', list: 'rule'},
        {label: '被访人', list: 'FullName'},
        {label: '当前状态', list: 'VisitorStatus'},
      ],//表格头
      tableOpction: {
        label: '操作',
        width: '230px',
        value: 0,
        options: [
          {
            label: '已取消',
            key: 0,
            style: '{"color": "#0f0"}',
            type: 'text',
            // 显示隐藏总控制，条件在方法里面写，返回true或者false
            show: (row) => {
              if (row.VisitorStatus==="访客申请"||row.VisitorStatus==="邀请访客") {
                return true
              } else {
                return false
              }
            },
            icon: 'el-icon-circle-check',
            method: (row) => {
            }
          },
          {
            label: '取消访问',
            key: 0,
            show: (row) => {
              if (row.VisitorStatus!=="访客申请"&&row.VisitorStatus!=="邀请访客") {
                return true
              } else {
                return false
              }
            },
            style: '{"color": "#f00"}',
            type: 'text',
            icon: 'el-icon-circle-check',
            method: (row) => {
              this.handleCancel(row)
            }
          }]
      },//表格操作
      SearchItem: {
        VisitorApplyName: '',
        VisitorName: '',
      },//查询体
      lastItem: {
        VisitorApplyName: '',
        VisitorName: '',
      },//末尾查询体
      total: 0,//总数
      pageSize: '10',//分页大小
      currentPage: '1',//当前页
      sessionid: ''
    }
  },
  created() {
    //从浏览器session里面获取sessionid。封装成许可数组
    let UserInfo = JSON.parse(sessionStorage.getItem("UserInfo"))
    const that = this
    that.sessionid = UserInfo.sessionid;
    this.fetchData()
  },
  methods: {
    //获取数据列表
    async fetchData() {
      var that = this
      let datas = ''
          datas = this.SearchItem,
          datas.currentpage = this.currentPage,
          datas.sessionid = this.sessionid,
          datas.pagesize = this.pageSize
      Object.entries(that.SearchItem).map((item, index) => {
        that.lastItem[item[0]] = that.SearchItem[item[0]]
      })
      that.isSubmitLoading = true//开始按钮懒加载
      const res = await getVisitorList(datas)  //查询全部信息
      console.log(res)
      that.isSubmitLoading = false//关闭按钮懒加载
      if (res.data.VisitorApplys) {
        res.data.VisitorApplys.map((v,k)=>{
          if (v.VisitorStatus===4){
            if (v.VisitorType===1){
              v.VisitorStatus="访客申请"
            }else{
              v.VisitorStatus="邀请访客"
            }
          }else if(v.VisitorStatus===0){
              v.VisitorStatus="被访人邀请"
          }else if(v.VisitorStatus===1){
            v.VisitorStatus="等待审核"
          }else if(v.VisitorStatus===2){
            v.VisitorStatus="被访人同意"
          }else if(v.VisitorStatus===3){
            v.VisitorStatus="被访人拒绝"
          }
          let rules = ""
          v.rules.map(item => {
            rules += item.DeviceName;
            let itemArr = item.LiftRule.split(',');
            rules += itemArr[0] + "至" + itemArr[itemArr.length - 1]+"楼";
          })
          v.rule = rules
        })
      } else {
        res.data.VisitorApplys = []
      }
      this.tableData = res.data.VisitorApplys.filter(item=>{
        return item.VisitorStatus!==5
      })//更新列表数据
      this.total = res.data.totle//更新总数
    },
    //全选
    handleSelectionChange(vals) {
      const that = this
      that.DeletelistiD = []  //先清空选择项和授权项
      that.enbleList = [] //
      vals.map(function (v, k) {
        that.DeletelistiD.push(v.VisitorApplyID)
        that.enbleList.push({id: v.id, enable: v.enable})
      })
    },
    // 搜索
    SearchNoteList() {
      this.currentPage = 1
      this.$refs.page.Page(1)
      this.fetchData()
    },
    // 翻页
    pageChange(item) {
      let that = this
      this.pageSize = item.limit
      this.currentPage = item.page
      Object.entries(that.SearchItem).map((item, index) => {
        that.SearchItem[item[0]] = that.lastItem[item[0]]
      })
      this.fetchData()
    },

    // 取消当前数据 重载列表
    async handleCancel(data) {
      const that = this
      that.$confirm('将会取消本次访问, 是否继续?', '提示', {
        type: 'warning'
      }).then(async () => {
        that.isSubmitLoading = true
        let datas = {
          sessionid: that.sessionid,
          VisitorApplyID: data.VisitorApplyID,
          VisitorStatus:4
        }
        const response = await visitorRecordUpdate(datas)
        if (response.data.result==0){
          that.isSubmitLoading = false
          that.$message.success('取消访问成功')
          that.fetchData()
        }else{
          that.$message.error('取消失败')
        }
      }).catch(() => {
        return false
      })
    },
    // 删除当前数据 重载列表
    async handleDelete(data) {
      const that = this
      if (that.DeletelistiD.length === 0) {
        return that.$message.warning('请先选择要删除的记录')
      }
      that.$confirm('删除会清空选中的访客记录, 是否继续?', '提示', {
        type: 'warning'
      }).then(async () => {
        that.isSubmitLoading = true
        that.DeletelistiD.map(item => {
          this.deleteCard(that.sessionid, item)
        })
        that.isSubmitLoading = false
        that.$message.success('批量删除成功')
        that.fetchData()
      }).catch(() => {
        return false
      })
    },
    async deleteCard(sessionid, VisitorApplyID) {
      let datas = {
        sessionid: sessionid,
        VisitorApplyID: VisitorApplyID,
        VisitorStatus:5
      }
      const response = await visitorRecordUpdate(datas)
    },
  }
}
</script>
<style lang="scss" scoped>
.el-button{
  color: #fff;
  background-color: #656ABE;
}
</style>