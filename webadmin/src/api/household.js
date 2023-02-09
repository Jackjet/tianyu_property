/*
 * @Author: lyy
 * @Date: 2020-09-08 18:26:43
 * @LastEditTime: 2020-09-12 20:48:38
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \web\src\api\organization.js
 */

import request from '@/utils/request'

// 查询
export function findAll(params) {
  return request({
    url: '/orgman/person/queryallusergroup',
    method: 'post',
    params
  })
}

// 邀请
export function inviteperson(params) {
  return request({
    url: '/orgman/person/inviteperson',
    method: 'post',
    params
  })
}

// 详情
export function queryfamily(params) {
  return request({
    url: '/wx/queryfamily',
    method: 'post',
    params
  })
}

// 更新
export function personapproval(params) {
  return request({
    url: '/orgman/person/generalAudit',
    method: 'post',
    params
  })
}

//删除
export function delusergroup(params) {
  return request({
    url: '/orgman/person/delusergroup',
    method: 'post',
    params
  })
}

//编辑
export function editGroup(params) {
  return request({
    url: '/orgman/person/editGroup',
    method: 'post',
    params
  })
}