/*
 * @Author: your name
 * @Date: 2020-08-16 16:50:58
 * @LastEditTime: 2020-09-18 00:43:59
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \webadmin\src\api\user.js
 */
import request from '@/utils/request'

export function getVisitorList(params) {
    return request({
        url: '/accesscontrol/visitor/historyRecordList',
        method: 'post',
        params
    })
}

export function visitorRecordUpdate(params) {
    return request({
        url: '/accesscontrol/visitor/VisitorRecordUpdate',
        method: 'post',
        params
    })
}
