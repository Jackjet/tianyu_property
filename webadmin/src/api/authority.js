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
    url: '/orgman/rule/queryRule',
    method: 'post',
    params
  })
}

// 查询设备
export function findAllDevice(params) {
  return request({
    url: '/orgman/device/deviceList',
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
export function findById(params) {
  return request({
    url: '/orgman/rule/queryListByPersonID',
    method: 'post',
    params
  })
}

// 更新
export function permissionedit(data) {
  return request({
    url: '/orgman/rule/permissionedit',
    method: 'post',
    data
  })
}

//删除
export function deleterule(params) {
  return request({
    url: '/orgman/rule/deleterule',
    method: 'post',
    params
  })
}