<!--
 * 侧边栏组件
-->
<script src="../../router/index.js"></script>
<template>
  <div class="left-nav">
    <div class="nav-logo">
      <img class="theLogo" src="../../assets/image/logo.png" />
      <span class="nav-title" v-show="titleShow">{{ title }}</span>
      <el-button type="text" class="open-btn" @click="toggleCollapse">
        <i v-show="!isCollapse" class="el-icon-s-fold"></i>
        <i v-show="isCollapse" class="el-icon-s-unfold"></i>
      </el-button>
    </div>
    <el-scrollbar class="nav-scrollbar" style="box-shadow: 0 0 30px #e6e6e6;">
      <el-menu class="el-menu-vertical-demo" :default-active="onRoutes" :unique-opened="true" :collapse="isCollapse">
        <template v-for="(item, index) in items">
          <template v-if="item.children && item.children.length > 0">
            <el-submenu :index="index" :key="index">
              <template slot="title">
                <b class="chooseLine"></b>
                <i class="icon" v-if="item.icon" v-html="theIcon[item.icon] ? theIcon[item.icon].icon: ''"></i>
                <span style="font-size: 15px; padding-left: 10px;" slot="title">{{ item.name }}</span>
              </template>
              <template v-for="(subItem, ele) in item.children">
                <el-submenu v-if="subItem.children && subItem.children.length > 0" :index="index + '-' + ele" :key="index + '-' + ele">
                  <template slot="title">
                    <b class="chooseLine"></b>
                    <i class="icon" v-if="subItem.icon" v-html="theIcon[subItem.icon] ? theIcon[subItem.icon].icon: ''"></i>
                    <span style="font-size: 13px; padding-left: 10px;" slot="title">{{ subItem.name }}</span>
                  </template>
                  <el-menu-item v-for="(threeItem, i) in subItem.children" :key="index + '-' + ele + '-' + i" :index="threeItem.path" @click="clickMenu(threeItem)">
                    <b class="chooseLine"></b>
                    <i class="icon" v-if="threeItem.icon" v-html="theIcon[threeItem.icon] ? theIcon[threeItem.icon].icon: ''"></i>
                    <span style="font-size: 12px; padding-left: 10px;" slot="title">{{ threeItem.name }}</span>
                  </el-menu-item>
                </el-submenu>
                <el-menu-item v-else :index="subItem.path" :key="index + '-' + ele" @click="clickMenu(subItem)">
                  <b class="chooseLine"></b>
                  <i class="icon" v-if="subItem.icon" v-html="theIcon[subItem.icon] ? theIcon[subItem.icon].icon: ''"></i>
                  <span style="font-size: 13px; padding-left: 10px;" slot="title">{{ subItem.name }}</span>
                </el-menu-item>
              </template>
            </el-submenu>
          </template>
          <template v-else>
            <el-menu-item :index="item.path" :key="index" @click="clickMenu(item)">

              <i class="icon" v-if="item.icon" v-html="theIcon[item.icon] ? theIcon[item.icon].icon : ''"></i>
              <span style="font-size: 15px; padding-left: 10px;" slot="title">{{ item.name }}</span>
            </el-menu-item>
          </template>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>
 
<script>
import bus from '@/utils/bus'
import { icons } from '@/icons/iconList'
import { findCurrentUserButton, findCurrentUserSidebar } from "@/api/role"
export default {
  name: 'MyAside',
  data() {
    return {
      title: '物业梯控管理系统',
      items: [{
        id: "a0000",
        name: "首页",
        icon: "firstPage",
        path: "/homePage"
      }, {
        id: "1",
        name: "设备管理",
        icon: "firstPage",
        path: "/device"
      }, {
        id: "2",
        name: "发卡管理",
        icon: "firstPage",
        path: "/cardManage"
      }, {
        id: "3",
        name: "住户管理",
        icon: "firstPage",
        path: "/household"
      }, {
        id: "4",
        name: "访客管理",
        icon: "firstPage",
        path: "/visitor"
      }, {
        id: "5",
        name: "权限管理",
        icon: "firstPage",
        path: "/authority"
      }, {
        id: "6",
        name: "卡号管理",
        icon: "firstPage",
        path: "/cardNumber"
      }],
      theIcon: icons,
      titleShow: true,
      isCollapse: false
    };
  },
  computed: {
    onRoutes() {
      return this.$route.path
    }
  },

  created () {
    this.initRole(JSON.parse(sessionStorage.getItem("UserInfo")).AccountID)
    if (JSON.parse(sessionStorage.getItem("UserInfo")).Grade===0){
      this.items.push( {
        id: "7",
        name: "系统管理",
        icon: "firstPage",
        path: "/systemManage"
      })
    }
    bus.$on('permissionData', () => {
      this.initRole(JSON.parse(sessionStorage.getItem("UserInfo")).AccountID)
    })
  },
  methods: {
    async initRole(id) {
      // console.log(id)
      // let buttons = await findCurrentUserButton({userId: id});
      // if (buttons.data.code === 1) {
      //   sessionStorage.setItem("UserButtons", JSON.stringify(buttons.data.data))
      // }
      // let menus = await findCurrentUserSidebar({userId: id})
      // if (menus.data.code === 1) {
      //   // sessionStorage.setItem("UserMenus", JSON.stringify(menus.data.data[0].menuTree))
      //   this.SetParameters(menus.data.data[0].menuTree)
      // }
    },
    // 设置系统名称延时，使切换动画更加平滑
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
      if (!this.isCollapse) {
        setTimeout(() => {
          this.titleShow = !this.isCollapse
        }, 500)
      } else {
        this.titleShow = !this.isCollapse
      }
    },
    SetParameters(key) {
      // let key = JSON.parse(sessionStorage.getItem("UserMenus"))
      const that = this
      let firstPage = {
        id: "a0000",
        name: "首页",
        icon: "firstPage",
        path: "/homePage"
      }
      if (key != null) {
        that.items = [firstPage, ...key]
      } else {
        that.items = [firstPage]
      }
    },
    // 点击侧边栏动作，获取路由存入面包屑
    clickMenu(info) {
      const that = this
      if (info.type && Number(info.type) === 1) {
        window.open(info.path, '_blank')
        return false
      } else {
        let breadcrumb = []
        that.items.forEach(item => {
          if (item.path && item.path === info.path) {
            breadcrumb.push({ 'title': item.name, 'path': item.path })
          } else {
            if (item.children) {
              item.children.forEach(item2 => {
                if (item2.path && item2.path === info.path) {
                  breadcrumb.push({ 'title': item.name, 'path': item2.path })
                  breadcrumb.push({ 'title': item2.name, 'path': item2.path })
                } else {
                  if (item2.children) {
                    item2.children.forEach((item3) => {
                      if (item3.path && item3.path === info.path) {
                        breadcrumb.push({ 'title': item.name, 'path': item3.path })
                        breadcrumb.push({ 'title': item2.name, 'path': item3.path })
                        breadcrumb.push({ 'title': item3.name, 'path': item3.path })
                      }
                    })
                  }
                }
              })
            }
          }
        })
        bus.$emit('menuData', breadcrumb)
        that.$router.push(info.path)
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.icon {
  fill: currentColor;
  overflow: hidden;
  /deep/ svg {
    margin-top: -5px;
    width: 20px;
    height: 20px;
  }
}

.left-nav {
  width: auto;
  height: 100%;
  .nav-logo {
    background: $color-MenuBg;
    z-index: 99;
    border-bottom: 1px solid #ffffff30;
    box-sizing: border-box;
    position: relative;
    height: 45px;
    line-height: 45px;
    padding-left: 45px;
    padding-right: 10px;
    border-bottom: 1px solid $color-HeaderBorder;
    background-color: #656ABE;
    box-sizing: border-box;
    .theLogo {
      position: absolute;
      left: 10px;
      width: 30px;
      height: 30px;
      margin-top: 7px;
    }
    .nav-title {
      color:#fff;
      font-weight: 400;
      font-size: 18px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
    .open-btn {
      position: absolute;
      left: calc(100% + 15px);
      color: #b5b5b5;
      font-size: 20px;
      z-index: 999;
    }
  }
  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
  }
  .nav-scrollbar {
    height: calc(100% - 45px);
    background-color: $color-MenuBg;
  }
  /deep/ .el-scrollbar__thumb {
    display: none;
  }
  .el-menu-vertical-demo {
    border: none;
    background-color: #ffffff00;
    /deep/ .el-submenu__title {
      &:hover {
        background-color: $color-MenuHoverBg;
      }
      &:focus {
        background-color: $color-MenuHoverBg;
      }
    }
    .el-menu-item {
      span {
        color: $color-MenuText;
        font-size: 16px;
      }
      &:hover {
        background-color: $color-MenuHoverBg;
      }
      &:focus {
        background-color: #ffffff00;
      }
    }
    .el-submenu {
      /deep/ .el-menu--inline {
        background-color: $color-MenuSubBg;
      }
      span {
        color: $color-MenuText;
        font-size: 16px;
      }
      .el-menu-item {
        span {
          color: $color-MenuText;
          font-size: 14px;
        }
      }
    }
    .chooseLine {
      position: absolute;
      left: 1px;
      top: 0;
      height: 100%;
      width: 5px;
      background-color: $color-MenuHoverText;
      display: none;
    }
    .el-menu-item.is-active {
      color: #fff;
      background-color: #656ABE;
      span {
        color: #fff;
      }
    }
  }
}
</style>
