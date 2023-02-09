<template>
  <el-main class="main">
    <el-col :span="24">
      <el-form :inline="true" class='el-InputForm'>

        <el-form-item label="卡状态">
          <el-select size="small" clearable v-model="SearchItem.CardState" placeholder="请选择卡状态">
            <el-option label="正常" value="0"></el-option>
            <el-option label="禁用" value="1"></el-option>
            <el-option label="拉黑" value="2"></el-option>
            <el-option label="挂失中" value="3"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input clearable size="small" placeholder="请输入姓名" v-model="SearchItem.FullName">
          </el-input>
        </el-form-item>
        <el-form-item label="卡类型">
          <el-select clearable size="small" v-model="SearchItem.CardType" placeholder="请选择卡类型">
            <el-option label="乘梯卡" value="1"></el-option>
            <el-option label="计数卡" value="2"></el-option>
            <el-option label="投入/退出" value="3"></el-option>
            <el-option label="挂失" value="4"></el-option>
            <el-option label="恢复" value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="子卡类型">
          <el-select clearable size="small" v-model="SearchItem.CardSonType" placeholder="请选择子卡类型">
            <el-option label="残疾人或老年人卡" value="1"></el-option>
            <el-option label="VIP 卡" value="2"></el-option>
            <el-option label="单层自动登记" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button size="small" @click="SearchNoteList" icon="el-icon-search">搜索</el-button>
        </el-form-item>
      </el-form>
      <div style="padding-top: 20px;">
        <el-button type="danger" plain size='mini' @click="handleDelete" icon="el-icon-delete">删除</el-button>
      </div>
    </el-col>

    <el-col :span='24'>
      <TableList :table-data='tableData' v-loading="isSubmitLoading" @onHandleSelectionChange="handleSelectionChange" :table-selection="tableSelection" :table-label="tableHeader">
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
  getQueryCard,
  deleteCard
} from "@/api/cardNumber";

export default {
  components: { TableList, Pagination },
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
        batchDelete: false,//批量删除
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
        { label: '卡号', list: 'CardNumber' },
        { label: '卡类型', list: 'CardType' },
        { label: '子卡类型', list: 'CardSonType' },
        { label: '姓名', list: 'FullName' },
        { label: '权限', list: 'rule' },
        { label: '手机号', list: 'Phone' },
        { label: '状态', list: 'CardState' },
      ],//表格头
      SearchItem: {
        CardState: '',
        FullName: '',
        CardType: '',
        CardSonType: ''
      },//查询体
      lastItem: {
        CardState: '',
        FullName: '',
        CardType: '',
        CardSonType: ''
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
      console.log(datas)
      const res = await getQueryCard(datas)  //查询全部信息
      that.isSubmitLoading = false//关闭按钮懒加载
      if (res.data.Cards) {
        res.data.Cards.map(function (v, k) { //格式化每行中需要重命名的数据列
          if (v.CardState === 0) {
            v.CardState = '正常'
          } else if (v.CardState === 1) {
            v.CardState = '禁用'
          } else if (v.CardState === 2) {
            v.CardState = '拉黑'
          } else if (v.CardState === 3) {
            v.CardState = '挂失中'
          }

          if (v.CardType === 1) {
            v.CardType = '乘梯卡'
          } else if (v.CardType === 2) {
            v.CardType = '计数卡'
          } else if (v.CardType === 3) {
            v.CardType = '投入/退出'
          } else if (v.CardType === 4) {
            v.CardType = '挂失'
          } else if (v.CardType === 5) {
            v.CardType = '恢复'
          }

          if (v.CardSonType === 0) {
            v.CardSonType = ''
          } else if (v.CardSonType === 1) {
            v.CardSonType = '残疾人或老年人'
          } else if (v.CardSonType === 2) {
            v.CardSonType = 'VIP卡'
          } else if (v.CardSonType === 3) {
            v.CardSonType = '单层自动登记'
          }
          let rules = ""
          v.rule.map(item => {
            rules += item.DeviceName;
            let itemArr = item.LiftRule.split(',');
            rules += itemArr[0] + "至" + itemArr[itemArr.length - 1] + "楼";
          })
          v.rule = rules
        })
      } else {
        res.data.Cards = []
      }
      this.tableData = res.data.Cards//更新列表数据
      this.total = res.data.totle//更新总数
    },

    //全选
    handleSelectionChange(vals) {
      const that = this
      that.DeletelistiD = []  //先清空选择项和授权项
      that.enbleList = [] //
      vals.map(function (v, k) {
        that.DeletelistiD.push(v.CardID)
        that.enbleList.push({ id: v.id, enable: v.enable })
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

    // 删除当前数据 重载列表
    async handleDelete(data) {
      const that = this
      if (that.DeletelistiD.length === 0) {
        return that.$message.warning('请先选择用户')
      }
      that.$confirm('删除会清空数据库中除日志相关的其他全部用户信息, 是否继续?', '提示', {
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
    async deleteCard(sessionid, CardID) {
      let datas = {
        sessionid: sessionid,
        CardID: CardID
      }
      const response = await deleteCard(datas)
    }
  }
}


</script>
<style lang="scss" scoped>
.el-button{
  color: #fff;
  background-color: #656ABE;
}
</style>