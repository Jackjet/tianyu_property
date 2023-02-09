// pages/pending_history_info/pending_history_info.js
const request = require("../../utils/Interface.js")
const util = require('../../utils/util.js')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    utoken: '',
    uid: '',
    theID: '',
    xing: '',
    name: '',
    status: '',
    startTime: '',
    endTime: '',
    phone: '',
    VisitorStatus: '',
    inTime: 'out',
  },

  doAgree () {
    const that = this
    wx.showModal({
      content: '再次确认是否同意该访客申请？',
      cancelText: '关闭',
      confirmText: '同意',
      confirmColor: '#08a82a',
      success (res) {
        if (res.confirm) {
          that.doPendingAgree()
        }
      }
    })
  },
  doRefuse () {
    const that = this
    wx.showModal({
      content: '再次确认是否拒绝该访客申请？',
      cancelText: '关闭',
      confirmText: '拒绝',
      confirmColor: '#d43418',
      success (res) {
        if (res.confirm) {
          that.doPendingRefuse()
        }
      }
    })
  },
  doPendingAgree () {
    wx.showLoading({
      title: '请求已发出',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&VisitorStatus=2' + '&VisitorApplyID=' + that.data.theID
    let theUrl = 'accesscontrol/visitor/VisitorRecordUpdate' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        wx.showModal({
          content: res.msg,
          showCancel: false,
          success (res) {
            that.initInfo()
          }
        })
      }, function (err) {
        console.log(err)
    })
  },
  doPendingRefuse () {
    wx.showLoading({
      title: '请求已发出',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&VisitorStatus=3' + '&VisitorApplyID=' + that.data.theID
    let theUrl = 'accesscontrol/visitor/VisitorRecordUpdate' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        wx.showModal({
          content: res.msg,
          showCancel: false,
          success (res) {
            that.initInfo()
          }
        })
      }, function (err) {
        console.log(err)
    })
  },
  initInfo () {
    wx.showLoading({
      title: '详情加载中',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid=' + that.data.utoken + '&VisitorApplyID=' + that.data.theID
    let theUrl = 'wx/queryVisitorByVisitorApplyID' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        let statusLabel = ''
        switch (res.VisitorStatus + '') {
          case '2':
            statusLabel = '申请已通过'
            break;
          case '3':
            statusLabel = '申请已拒绝'
            break;
          default:
            statusLabel = '申请已过期'
            break;
        }
        let nowTime = new Date().getTime()
        that.setData({
          xing: res.VisitorApplyName.substr(0, 1),
          name: res.VisitorApplyName,
          startTime: util.formatTime(new Date(res.VisitorBeginTime)),
          endTime: util.formatTime(new Date(res.VisitorEndTime)),
          status: statusLabel,
          VisitorStatus: res.VisitorStatus,
          phone: res.VisitorApplyPhone,
          inTime: nowTime >= res.VisitorEndTime ? 'out' : 'in'
          // power: powerArr,
        })
      }, function (err) {
        console.log(err)
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      utoken: wx.getStorageSync('sessionid'),
      uid: wx.getStorageSync('Personid'),
      theID: options.theID,
    })
    this.initInfo()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})