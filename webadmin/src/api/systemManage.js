/*
 * @Author: your name
 * @Date: 2020-08-16 16:50:58
 * @LastEditTime: 2020-09-18 00:43:59
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \webadmin\src\api\user.js
 */
import request from '@/utils/request'

export function getAdminList(params) {
    return request({
        url: '/orgman/adminList',
        method: 'post',
        params
    })
}

export function updateState(params) {
    return request({
        url: '/orgman/updateState',
        method: 'post',
        params
    })
}

export function setUpAccount(params) {
    return request({
        url: '/orgman/setupaccount',
        method: 'post',
        params
    })
}

export function upDateAccount(params) {
    return request({
        url: '/orgman/updateaccount',
        method: 'post',
        params
    })
}
export function deleteAdmin(params) {
    return request({
        url: '/orgman/deleteAdmin',
        method: 'post',
        params
    })
}