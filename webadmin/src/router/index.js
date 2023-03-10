import Vue from 'vue'
import VueRouter from 'vue-router'
import bus from '@/utils/bus'

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/pages/404'),
    hidden: true
  },
  {
    path: '/',
    component: () => import('@/views/layout/index'),
    redirect: '/homePage',
    children: [{
      path: '/homePage',
      name: '首页',
      label: '默认主页',
      component: () => import('@/views/home/index')
    },
    // 设备管理模块
    {
      path: '/device',
      name: '设备列表',
      label: '设备管理设备列表页',
      component: () => import('@/views/pages/device/index')
    },
    {
      path: '/device/add',
      name: '新增',
      label: '设备管理设备列表页',
      component: () => import('@/views/pages/device/addOrEdit')
    },
    {
      path: '/device/edit',
      name: '编辑',
      label: '设备管理设备列表页',
      component: () => import('@/views/pages/device/addOrEdit')
    },

    // 发卡管理模块
    {
      path: '/cardManage',
      name: '发卡器列表',
      label: '发卡管理发卡器列表页',
      component: () => import('@/views/pages/cardManage/index')
    },
    {
      path: '/cardManage/add',
      name: '新增',
      label: '发卡管理发卡器列表页',
      component: () => import('@/views/pages/cardManage/addOrEdit')
    },
    {
      path: '/cardManage/edit',
      name: '编辑',
      label: '发卡管理发卡器列表页',
      component: () => import('@/views/pages/cardManage/addOrEdit')
    },
    {
      path: '/cardManage/detail',
      name: '详情',
      label: '发卡管理发卡器列表页',
      component: () => import('@/views/pages/cardManage/addOrEdit')
    },
    {
      path: '/cardEdit/detail',
      name: '系统发卡',
      label: '发卡管理发卡器列表页',
      component: () => import('@/views/pages/cardManage/cardEdit')
    },

    // 住户管理模块
    {
      path: '/household',
      name: '住户列表',
      label: '住户管理住户列表页',
      component: () => import('@/views/pages/household/index')
    },

    // 访客管理模块
    {
      path: '/visitor',
      name: '访客列表',
      label: '访客管理访客列表页',
      component: () => import('@/views/pages/visitor/index')
    },

    // 权限管理模块
    {
      path: '/authority',
      name: '权限列表',
      label: '权限管理权限列表页',
      component: () => import('@/views/pages/authority/index')
    },

    // 卡号管理模块
    {
      path: '/cardNumber',
      name: '卡列表',
      label: '卡号管理卡列表页',
      component: () => import('@/views/pages/cardNumber/index')
    },

    // 系统管理模块
    {
      path: '/systemManage',
      name: '管理员列表',
      label: '管理员列表页',
      component: () => import('@/views/pages/systemManage/user/index')
    },
    {
      path: '/systemManage/add',
      name: '添加管理员',
      label: '添加管理员信息页',
      component: () => import('@/views/pages/systemManage/user/add')
    },
    {
      path: '/systemManage/edit',
      name: '编辑管理员',
      label: '编辑管理员信息页',
      component: () => import('@/views/pages/systemManage/user/add')
    },

    // 系统默认区域无需更改

    // 日志管理相关路由
    {
      path: '/operationLogs',
      name: '列表',
      label: '操作日志列表页',
      component: () => import('@/views/pages/logManage/operationLog/index')
    }, {
      path: '/operationLogDetail',
      name: '详情',
      label: '操作日志详情页',
      component: () => import('@/views/pages/logManage/operationLog/detail')
    },
    {
      path: '/userLogs',
      name: '列表',
      label: '用户日志列表页',
      component: () => import('@/views/pages/logManage/userLog/index')
    }, {
      path: '/userLogDetail',
      name: '详情',
      label: '用户日志详情页',
      component: () => import('@/views/pages/logManage/userLog/detail')
    },
    // 消息管理相关路由
    {
      path: '/messages',
      name: '列表',
      label: '消息列表页',
      component: () => import('@/views/pages/messageManage/messages/index')
    }, {
      path: '/messageDetail',
      name: '详情',
      label: '消息详情页',
      component: () => import('@/views/pages/messageManage/messages/detail')
    },
    // 用户管理相关路由
    {
      path: '/user',
      name: '列表',
      label: '用户列表页',
      component: () => import('@/views/pages/systemManage/user/index')
    }, {
      path: '/userAdd',
      name: '新增',
      label: '用户新增页',
      component: () => import('@/views/pages/systemManage/user/add')
    }, {
      path: '/userEdit',
      name: '编辑',
      label: '用户编辑页',
      component: () => import('@/views/pages/systemManage/user/add')
    }, , {
      path: '/userDetail',
      name: '详情',
      label: '用户详情页',
      component: () => import('@/views/pages/systemManage/user/add')
    },
    // 组织机构相关路由
    {
      path: '/organization',
      name: '列表',
      label: '组织机构列表页',
      component: () => import('@/views/pages/systemManage/organization/index')
    }, {
      path: '/organizationAdd',
      name: '新增',
      label: '组织机构新增页',
      component: () => import('@/views/pages/systemManage/organization/add')
    }, {
      path: '/organizationEdit',
      name: '编辑',
      label: '组织机构编辑页',
      component: () => import('@/views/pages/systemManage/organization/add')
    },
    // 菜单管理相关路由
    {
      path: '/menus',
      name: '列表',
      label: '菜单列表页',
      component: () => import('@/views/pages/systemManage/menu/index')
    }, {
      path: '/menuAdd',
      name: '新增',
      label: '菜单新增功能页',
      component: () => import('@/views/pages/systemManage/menu/addOrEdit')
    }, {
      path: '/menuEdit',
      name: '编辑',
      label: '菜单编辑功能页',
      component: () => import('@/views/pages/systemManage/menu/addOrEdit')
    },
    // 角色管理相关路由
    {
      path: '/role',
      name: '列表',
      label: '角色管理列表页',
      component: () => import('@/views/pages/systemManage/role/index')
    }, {
      path: '/roleAdd',
      name: '新增',
      label: '角色新增功能页',
      component: () => import('@/views/pages/systemManage/role/addOrEdit')
    }, {
      path: '/roleEdit',
      name: '编辑',
      label: '角色编辑功能页',
      component: () => import('@/views/pages/systemManage/role/addOrEdit')
    }, {
      path: '/roleUser',
      name: '用户列表',
      label: '角色绑定用户列表页',
      component: () => import('@/views/pages/systemManage/role/users')
    }, {
      path: '/rolePower',
      name: '权限分配',
      label: '角色权限分配页',
      component: () => import('@/views/pages/systemManage/role/assignPremission')
    },
    // 系统设置相关路由
    {
      path: '/dataBackup',
      name: '设置',
      label: '数据备份设置页',
      component: () => import('@/views/pages/systemSet/dataBackup')
    }, {
      path: '/service',
      name: '详情',
      label: '服务器信息详情页',
      component: () => import('@/views/pages/systemSet/service')
    }]
  }
]

const router = new VueRouter({
  routes
})
export default router
