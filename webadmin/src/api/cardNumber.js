/*
 * @Author: your name
 * @Date: 2020-08-16 16:50:58
 * @LastEditTime: 2020-09-18 00:43:59
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \webadmin\src\api\user.js
 */
import request from '@/utils/request'

export function getQueryCard(params) {
    return request({
        url: '/orgman/card/query_card',
        method: 'post',
        params
    })
}
export function deleteCard(params) {
    return request({
        url: '/orgman/card/deleteCard',
        method: 'post',
        params
    })
}