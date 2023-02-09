import request from '@/utils/request'

// 单条导入
export function deviceAddDevice(params) {
    return request({
        url: '/orgman/device/addDevice',
        method: 'post',
        params
    })
}

// 分页查询
export function query_device(params) {
    return request({
        url: '/orgman/device/query_device',
        method: 'post',
        params
    })
}
// 删除
export function delete_device(params) {
    return request({
        url: '/orgman/device/delete_device',
        method: 'post',
        params
    })
}
// 修改
export function updateDevice(params) {
    return request({
        url: '/orgman/device/updateDevice',
        method: 'post',
        params
    })
}