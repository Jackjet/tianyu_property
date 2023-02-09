// let host = 'https://www.dingdingkaimen.cn/DistributionCard/';
const app = getApp();
let host =  app.globalData.url;
/**
 * HTTP 请求，
 * URL 接口
 * postData：参数
 * doSuccess：成功的回调函数
 * doFail：失败的回调函数
 */

// function Urltoken() {
//   let currentUser = wx.getStorageSync('token');
//   return currentUser ? currentUser : undefined
// }

function doPost(url, postData, Success, Fail) {
  wx.request({
    url: host + url,
    header: {
      "content-type": "application/json;charset=UTF-8",
    },
    data: postData,
    method: 'POST',
    success: function (res) {
      Success(res.data);
    },
    fail: function (XHR) {
      Fail(XHR);
    },
  })
}

function doPut(url, postData, Success, Fail) {
  wx.request({
    url: host + url,
    header: {
      "content-type": "application/json;charset=UTF-8",
    },
    data: postData,
    method: 'PUT',
    success: function (res) {
      Success(res.data);
    },
    fail: function (XHR) {
      Fail(XHR);
    },
  })
}

function doGet(url, Success, Fail) {
  wx.request({
    url: host + url,
    header: {
      "content-type": "application/json;charset=UTF-8",
      // "Authorization": 'token ' + Urltoken()
    },
    method: 'GET',
    success: function (res) {
      Success(res.data);
    },
    fail: function (XHR) {
      Fail(XHR);
    },
  })
}

function FormDatai(url, postData, Success, Fail) {
  wx.request({
    url: host + url,
    header: {
      "content-type": "application/json;charset=UTF-8",
    },
    data: postData,
    method: 'POST',
    success: function (res) {
      Success(res.data);
    },
    fail: function (XHR) {
      Fail(XHR);
    },
  })
}

function formatTime() {
  const date = new Date();
  let y = date.getFullYear();
  let m = date.getMonth() + 1;
  let d = date.getDate() - 1;
  m = m < 10 ? '0' + m : m
  d = d < 10 ? '0' + d : d
  let time = y + '-' + m + '-' + d
  return time
}

module.exports.http = host;
module.exports.doPost = doPost;
module.exports.doPut = doPut;
module.exports.doGet = doGet;
module.exports.FormDatai = FormDatai;
module.exports.formatTime = formatTime;