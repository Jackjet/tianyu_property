<template>
  <el-main class="main">
    <el-col :span="24">
      <el-form :inline="true" class='el-InputForm'>

        <el-form-item label="管理员姓名">
          <el-input clearable
                    size="small"
                    placeholder="请输入姓名"
                    v-model="SearchItem.AccountName">
          </el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select clearable size="small" v-model="SearchItem.AccountState" placeholder="请选择状态">
            <el-option label="启用" value="0"></el-option>
            <el-option label="禁用" value="1"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button size="small" @click="SearchNoteList" icon="el-icon-search">
            搜索
          </el-button>

        </el-form-item>
      </el-form>
    </el-col>

    <el-col :span="24" style="padding-top: 20px;">
      <el-button v-if="permissions.add" type="primary" plain size="mini"  @click="handleAdd" icon="el-icon-plus">新增</el-button>
    </el-col>
    <el-col :span='24'>
      <TableList :table-data='tableData'
                 v-loading="isSubmitLoading"
                 :table-label="tableHeader"
                 :table-option="tableOpction">
      </TableList>
    </el-col>
  </el-main>
</template>

<script>
import TableList from '@/components/table/tableList'
import Pagination from '@/components/table/Pagination'
import {
  getAdminList,
  updateState,
  deleteAdmin
} from "@/api/systemManage";

export default {
  components: {TableList, Pagination},
  data() {
    return {
      // 许可证明：
      permissions: {
        add: true, //添加许可
        edit: true,  //修改
        enable: false,  //通过
        detail: true,  //详情
        templateDownload: false,//模板下载
        batchRetPassword: false,//批量重置密码
        batchDelete: true,//批量删除
        userExport: false,//用户导出
        userImport: false,//用户导入
        batchEnable: false,//batchEnable
        batchBan: true//批量禁用
      },
      isSubmitLoading: false,//提交按钮懒加载
      DeletelistiD: [],//批量选择
      enbleList: [],//授权列表
      tableData: [],//表格数据
      tableHeader: [
        {label: '', list: 'index'},
        {label: '管理员账号', list: 'AccountName'},
        {label: '管理员姓名', list: 'Name'},
        {label: '账号等级', list: 'Grade'},
        {label: '状态', list: 'AccountState'},
        {label: '创建时间', list: 'date'},
      ],//表格头
      tableOpction: {
        label: '操作',
        width: '230px',
        value: 0,
        options: [{
          label: '编辑',
          key: 0,
          type: 'text',
          show: (row) => {
            return row.AccountName !== 'admin' && this.permissions.edit
          },
          icon: 'el-icon-edit-outline',
          method: (row) => {
            this.handleEdit(row)
          }
        }, {
          label: '删除',
          key: 0,
          type: 'text',
          show: (row) => {
            return row.AccountName !== 'admin' && this.permissions.detail
          },
          icon: 'el-icon-tickets',
          method: (row) => {
            this.handleDelete(row)
          }
        }, {
          label: '启用',
          key: 0,
          style: '{"color": "#0f0"}',
          type: 'text',
          // 显示隐藏总控制，条件在方法里面写，返回true或者false
          show: (row) => {
            if (row.AccountName !== 'admin' && row.AccountState === "禁用") {
              return true
            } else {
              return false
            }
          },
          icon: 'el-icon-circle-check',
          method: (row) => {
            this.enable(row)
          }
        }, {
          label: '禁用',
          key: 0,
          show: (row) => {
            if ( row.AccountName !== 'admin' && row.AccountState === "启用" ) {
              return true
            } else {
              return false
            }
          },
          style: '{"color": "#f00"}',
          type: 'text',
          icon: 'el-icon-circle-check',
          method: (row) => {
            this.enable(row)
          }
        }]
      },//表格操作
      SearchItem: {
        AccountName: '',
        AccountState: '',
      },//查询体
      lastItem: {
        AccountName: '',
        AccountState: '',
      },//末尾查询体
      total: 0,//总数
      pageSize: '20',//分页大小
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
          datas.sessionid = this.sessionid,
          Object.entries(that.SearchItem).map((item, index) => {
            that.lastItem[item[0]] = that.SearchItem[item[0]]
          })
      that.isSubmitLoading = true//开始按钮懒加载
      const res = await getAdminList(datas)  //查询全部信息
      that.isSubmitLoading = false//关闭按钮懒加载
      if (res.data.Account) {
        res.data.Account.map(function (v, k) { //格式化每行中需要重命名的数据列
          v.index=k+1
          if (v.Grade === 0) {
            v.Grade = '超级管理员'
          } else if (v.Grade === 1) {
            v.Grade = '普通'
          }
          if (v.AccountState === 0) {
            v.AccountState = '启用'
          } else if (v.AccountState === 1) {
            v.AccountState = '禁用'
          }
          v.date=that.getdate(v.date)
        })
      } else {
        res.data.Account = []
      }
      this.tableData = res.data.Account//更新列表数据
      this.total = res.data.totle//更新总数
    },
    //时间戳转换器
    getdate(nS ) {
      return new Date(parseInt(nS) ).toLocaleString().replace(/:\d{1,2}$/, ' ');
    },
    //全选
    handleSelectionChange(vals) {
      const that = this
      that.DeletelistiD = []  //先清空选择项和授权项
      that.enbleList = [] //
      vals.map(function (v, k) {
        that.DeletelistiD.push(v.CardID)
        that.enbleList.push({id: v.id, enable: v.enable})
      })
    },
    // 搜索
    SearchNoteList() {
      this.currentPage = 1
      this.fetchData()
    },
    // 编辑
    handleEdit(data) {
      const that = this
      that.$router.push({
        path: '/systemManage/edit',
        query: {
          id: data.id,
          type:'编辑',
          data:data
        }
      })
    },
    // 添加
    handleAdd(data){
      const that = this
      if (that.total>=11){
        return that.$message.warning('子管理员数量已经超出十条限制，请先删除子管理员')
      }
      that.$router.push({
        path: '/systemManage/add',
        query: {
          type:'新增'
        }
      })
    },
    // 启用 重载列表
    async enable(row) {
      const that = this
      let tips = ''
      let enable=0;
      if(row.AccountState==="启用"){
        tips = '禁用';
        enable=1
      } else {
        tips = '启用';
        enable=0
      }
      let data = {
        sessionid:that.sessionid,
        AccountState:enable+"",
        AccountID: row.AccountID
      }
      that.$confirm('请确认是否' + tips +'?' , '提示', {
        type: 'warning'
      }).then(async () => {
        that.isSubmitLoading = true
        const response = await updateState(data)
        that.isSubmitLoading = false
        if (response.data.result === 0) {
          that.$message.success('状态更改成功')
        } else {
          that.$message.error("状态更改失败")
        }
        this.fetchData()
      }).catch(() => {
        return false
      })
    },
    // 删除当前数据 重载列表
    async handleDelete(data) {
      const that = this
      that.$confirm('删除会清空数据库中除日志相关的其他全部用户信息, 是否继续?', '提示', {
        type: 'warning'
      }).then(async () => {
        that.isSubmitLoading = true
        let datas={
          sessionid:that.sessionid,
          AccountID:data.AccountID
        }
        const res = await deleteAdmin(datas)
        if (res.data.result === 0) {
          that.$message.success('删除成功');
        } else if (res.data.result === -1) {
          that.$message.erarr('删除失败');
        }
        that.isSubmitLoading = false
        that.fetchData()
      }).catch(() => {
        return false
      })
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