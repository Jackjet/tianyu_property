<!-- 发卡管理 -->
<template>
  <el-main class="main">
    <el-col :span="24">
      <el-form :inline="true" class='el-InputForm'>
        <el-form-item label="设备名称">
          <el-input clearable size="medium" placeholder="请输入设备名称" v-model="SearchItem.DeviceName">
          </el-input>
        </el-form-item>
        <el-form-item style='margin-left: 15px;'>
          <el-button size='medium' @click="SearchNoteList" icon="el-icon-search">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-col :span="24" class="center">
      <div style="padding-top: 20px;">
        <el-button type="primary" plain size='mini' @click="handleAdd" icon="el-icon-plus">新增</el-button>
        <el-button type="danger" plain size='mini' @click="handleBatchDelete" icon="el-icon-delete">删除</el-button>
        <el-button type="warning" plain size='mini' @click="handleLose" icon="el-icon-edit-outline">挂失</el-button>
      </div>
      <TableList :table-data='tableData' v-loading="isSubmitLoading" @onHandleSelectionChange="handleSelectionChange" :table-selection="tableSelection" :table-label="tableHeader" :table-option="tableOpction">
      </TableList>
    </el-col>
    <el-col :span='24'>
      <pagination ref="page" :total="total" @pageChange="pageChange"></pagination>
    </el-col>
    <el-dialog width="40%" title="卡片挂失" :visible.sync="dialogFormVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="持卡人">
          <el-select v-model="form.FullName" filterable placeholder="请选择持卡人" @change="changeFullName" style="width:100%">
            <el-option v-for="item in sureLoseList" :key="item.CardNumber" :label="item.FullName" :value="item.CardID"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="卡号">
          <el-input v-model="form.CardID" placeholder="请输入卡号"></el-input>
        </el-form-item>
        <el-form-item label="权限">
          <div class="Authority-box">
            <p v-for="item in form.Authority" :key="item.DeviceID">{{item.DeviceName+"--"+item.LiftRule}}F</p>
          </div>
        </el-form-item>
      </el-form>
      <p style="padding-left:80px;color:#169BD5">注意：仅住户乘梯卡和计次卡支持挂失。</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitLose">确 定</el-button>
      </div>
    </el-dialog>
  </el-main>
</template>

<script>
import { query_device, delete_device } from '@/api/device.js'
import { queryreturncard, ReportLoss, CardLostList } from '@/api/cardManage.js'
import TableList from '@/components/table/tableList'
import Pagination from '@/components/table/Pagination'
export default {
  components: {
    TableList,
    Pagination
  },
  data() {
    return {
      sureLoseList: [],
      form: {
        FullName: "",
        CardID: "",
        Authority: ""
      },
      dialogFormVisible: false,
      actionUrl: process.env.VUE_APP_BASE_API + 'orgman/device/carduploads',
      headers: {
        Authorization: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
      },
      sessionid: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
      SearchItem: {
        DeviceName: ""
      },
      tableSelection: {
        key: true,
        type: 'selection',
        detaile: false
      },
      isSubmitLoading: false,
      tableData: [],
      tableHeader: [
        // { label: '设备梯号', list: 'UnifiedID' },
        { label: '设备名称', list: 'DeviceName' },
        { label: '设备标识', list: 'DeviceIdentification' },
        {
          type: 'html',
          label: '设备类型',
          list: 'DeviceType',
          code: (row) => {
            if (row.DeviceType === 1) {
              return '<span>门禁</span>'
            } else if (row.DeviceType === 2) {
              return '<span>门禁</span>'
            } else if (row.DeviceType === 3) {
              return '<span>发卡器</span>'
            }
          }
        },
        { label: '接入时间', list: 'DeviceInstalTimel' }
      ],
      tableOpction: {
        label: '操作',
        width: '300px',
        value: 0,
        options: [{
          label: '编辑',
          type: 'text',
          icon: '',
          State: false,
          method: (row) => {
            this.handleEdit(row)
          }
        },
        {
          label: '详情',
          type: 'text',
          icon: '',
          State: false,
          method: (row) => {
            this.handleDetail(row)
          }
        },
        {
          label: '发卡',
          type: 'text',
          icon: '',
          State: false,
          method: (row) => {
            this.handleCardDetail(row)
          }
        },
        {
          label: '删除',
          key: 0,
          type: 'text',
          icon: '',
          State: false,
          method: (row) => {
            this.handleDelete(row)
          }
        }]
      },
      total: 0,
      pageSize: '10',
      currentPage: '1',
      DeletelistiD: []
    }
  },
  created() {
    CardLostList({ sessionid: this.sessionid }).then(res => {
      this.sureLoseList = res.data;
    })
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.SearchItem.sessionid = this.sessionid;
      this.SearchItem.DeviceType = '3';
      this.SearchItem.pagesize = this.pageSize;
      this.SearchItem.currentpage = this.currentPage;
      query_device(this.SearchItem).then(res => {
        this.tableData = res.data.Devices;
        this.total = res.data.totle;
      })
    },
    handleAdd() {
      this.$router.push({
        path: '/cardManage/add',
        query: {
          type: '新增'
        }
      })
    },
    //全选
    handleSelectionChange(vals) {
      const that = this
      that.DeletelistiD = []
      vals.map(function (v, k) {
        that.DeletelistiD.push(v.DeviceID)
      })
    },
    // 多项删除
    async handleBatchDelete() {
      this.$confirm('设备删除时会同事清空设备对应人员的相应权限，请确认是否继续删除？', '提示', {
        type: 'warning'
      }).then(async () => {
        let num = 0;
        this.DeletelistiD.map(item => {
          delete_device({
            sessionid: this.sessionid,
            DeviceID: item
          }).then(res => {
            // console.log(res)
          })
        })
        this.$message.success('删除成功')
        this.fetchData();
      }).catch(() => {
        return false
      })
    },
    handleEdit(row) {
      row.type = '编辑'
      this.$router.push({
        path: '/cardManage/edit',
        query: row
      })
    },
    handleDetail(row) {
      row.type = '详情'
      this.$router.push({
        path: '/cardManage/detail',
        query: row
      })
    },
    changeFullName(val) {
      this.sureLoseList.forEach(item => {
        if (item.CardID === val) {
          this.form.CardID = item.CardID;
          this.form.Authority = item.rules
        }
      })
    },
    handleLose() {
      this.dialogFormVisible = true;
    },
    submitLose() {
      ReportLoss({ CardID: this.form.CardID, sessionid: this.sessionid }).then(res => {
        this.dialogFormVisible = false;
        if (res.data.result === 0) {
          this.$message.success('挂失成功')
        } else {
          this.$message.error(res.data.msg)
        }
      })
    },
    handleCardDetail(row) {
      // this.$router.push({
      //   path: '/cardEdit/detail',
      //   query: {
      //     DeviceID: row.DeviceID
      //   }
      // })
      queryreturncard({ DeviceID: row.DeviceID, sessionid: this.sessionid }).then(res => {
        if (res.data.cardNumber) {
          this.$router.push({
            path: '/cardEdit/detail',
            query: {
              DeviceID: row.DeviceID,
              Supply_CardNumber: res.data.cardNumber
            }
          })
        } else {
          this.$confirm('未刷卡片，请刷卡操作', '提示', {
            type: 'warning'
          }).then(async () => {
            return false
          }).catch(() => {
            return false
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('设备删除时会同事清空设备对应人员的相应权限，请确认是否继续删除？', '提示', {
        type: 'warning'
      }).then(async () => {
        delete_device({
          sessionid: this.sessionid,
          DeviceID: row.DeviceID
        }).then(res => {
          this.$message.success(res.data.msg)
          this.fetchData();
        })
      }).catch(() => {
        return false
      })
    },
    // 搜索
    SearchNoteList() {
      this.currentPage = 1
      this.$refs.page.Page(1)
      this.fetchData()
    },
    pageChange(item) {
      this.pageSize = item.limit
      this.currentPage = item.page
      this.fetchData()
    }
  }
}
</script>
<style lang='scss' scoped>
.Authority-box {
  border: 1px solid #dcdfe6;
  border-radius: 5px;
  padding-left: 10px;
  min-height: 40px;
}
.el-button {
  color: #fff;
  background-color: #656abe;
}
</style>