import request from '@/utils/request'

// 单条导入
export function addCardIssuer(params) {
    return request({
        url: '/orgman/device/addCardIssuer',
        method: 'post',
        params
    })
}

// 列表
export function query_card(params) {
    return request({
        url: '/orgman/card/query_card',
        method: 'post',
        params
    })
}
// 编辑
export function updateCardIssuer(params) {
    return request({
        url: '/orgman/device/updateCardIssuer',
        method: 'post',
        params
    })
}
// 删除
export function deleteCard(params) {
    return request({
        url: '/orgman/card/deleteCard',
        method: 'post',
        params
    })
}

// 发卡下发页面下拉框获取人员ID，卡号以及定位人员功能
export function personQueryAll(params) {
    return request({
        url: '/orgman/person/queryAll',
        method: 'post',
        params
    })
}

// 获取卡信息
export function queryreturncard(params) {
    return request({
        url: '/orgman/device/queryreturncard',
        method: 'post',
        params
    })
}

// 挂失卡号列表
export function reportLossInFormation(params) {
    return request({
        url: '/orgman/card/reportLossInFormation',
        method: 'post',
        params
    })
}

// 发卡
export function rl_distributioncards(data) {
    return request({
        url: '/orgman/device/rl_distributioncards',
        method: 'post',
        data
    })
}

// 设备列表
export function deviceList(params) {
    return request({
        url: '/orgman/device/deviceList',
        method: 'post',
        params
    })
}

// 挂失
export function ReportLoss(params) {
    return request({
        url: '/orgman/card/ReportLoss',
        method: 'post',
        params
    })
}

// 可以挂失列表
export function CardLostList(params) {
    return request({
        url: '/orgman/card/CardLostList',
        method: 'post',
        params
    })
}