<!-- 设备管理 -->
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
          <!-- <el-button size='medium' @click="reset" icon="el-icon-refresh-left">重置</el-button> -->
        </el-form-item>
      </el-form>
    </el-col>
    <el-col :span="24" class="center">
      <div style="padding-top: 20px;">
        <el-button type="primary" plain size='mini' @click="handleAdd" icon="el-icon-top-right">单条导入</el-button>
        <el-upload style="display: inline-block;margin:0 10px" ref="upload" :show-file-list="false" :action="actionUrl" :headers='headers' :data="sessionid" :on-success="handleSuccess" :on-error="hadleError" limit="1">
          <el-button plain size='mini' type="primary" icon="el-icon-upload2">批量导入</el-button>
        </el-upload>
        <el-button type="primary" plain size='mini' @click="outExport" icon="el-icon-download">导出模板</el-button>
        <el-button type="danger" plain size='mini' @click="handleBatchDelete" icon="el-icon-delete">删除</el-button>
      </div>
      <TableList :table-data='tableData' v-loading="isSubmitLoading" @onHandleSelectionChange="handleSelectionChange" :table-selection="tableSelection" :table-label="tableHeader" :table-option="tableOpction">
      </TableList>
    </el-col>
    <el-col :span='24'>
      <pagination ref="page" :total="total" @pageChange="pageChange"></pagination>
    </el-col>
  </el-main>
</template>

<script>
import { query_device, delete_device } from '@/api/device.js'
import { outExcel } from '@/utils/export'
import TableList from '@/components/table/tableList'
import Pagination from '@/components/table/Pagination'
export default {
  components: {
    TableList,
    Pagination
  },
  data() {
    return {
      actionUrl: process.env.VUE_APP_BASE_API + 'orgman/device/carduploads',
      headers: {
        Authorization: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
      },
      sessionid: JSON.parse(sessionStorage.getItem("UserInfo")).sessionid,
      SearchItem: {
        DeviceName: "",
        DeviceType: "2"
      },
      tableSelection: {
        key: true,
        type: 'selection',
        detaile: false
      },
      isSubmitLoading: false,
      tableData: [],
      tableHeader: [
        // { label: '设备序号', list: 'DeviceID' },
        { label: '设备梯号', list: 'UnifiedID' },
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
              return '<span>梯控</span>'
            }
          }
        },
        { label: '设备型号', list: 'DeviceModel' },
        { label: '创建时间', list: 'DeviceInstalTimel' }
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
  created() { },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.SearchItem.sessionid = this.sessionid;
      this.SearchItem.pagesize = this.pageSize;
      this.SearchItem.currentpage = this.currentPage;
      query_device(this.SearchItem).then(res => {
        this.tableData = res.data.Devices;
        this.total = res.data.totle;
      })
    },
    // 导出模板
    outExport() {
      outExcel('请确认是否下载模板?', "orgman/device/exportModel", { sessionid: this.sessionid }, '设备模板');
    },
    // 上传成功
    handleSuccess(response, file, fileList) {
      this.$refs.upload.clearFiles();
      if (response.result === 0) {
        this.$message.success(response.msg)
      } else {
        this.$message.error(response.msg)
      }
    },
    // 上传失败
    hadleError(error, file, fileList) {
      this.$refs.upload.clearFiles();
      const result = JSON.parse(error.message);
      this.$message.error(result.msg)
    },
    handleAdd() {
      this.$router.push({
        path: '/device/add',
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
      this.$confirm('请确认是否删除?', '提示', {
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
        path: '/device/edit',
        query: row
      })
    },
    handleDelete(row) {
      this.$confirm('请确认是否删除?', '提示', {
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
.el-button {
  color: #fff;
  background-color: #656abe;
}
</style>