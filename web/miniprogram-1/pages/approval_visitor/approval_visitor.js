// pages/approval_visitor/approval_visitor.js
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
    xing: '小',
    name: '小不点',
    startTime: '2020-01-04 20:00:00',
    endTime: '2020-01-04 20:00:00',
    phone: '12312312312',
    power: [],
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
        let powerArr = []
        // res.rules = [{DeviceName: '1号楼', LiftRule: '2,3,4'},{DeviceName: '1号楼', LiftRule: '2,3,4'},{DeviceName: '1号楼', LiftRule: '2,3,4'}]
        res.rules.forEach(element => {
          powerArr.push(element.DeviceName + ' ' + element.LiftRule + '层')
        });
        that.setData({
          xing: res.VisitorApplyName.substr(0, 1),
          name: res.VisitorApplyName,
          startTime: util.formatTime(new Date(res.VisitorBeginTime)),
          endTime: util.formatTime(new Date(res.VisitorEndTime)),
          phone: res.VisitorApplyPhone,
          power: powerArr,
        })
      }, function (err) {
        console.log(err)
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
            getApp().globalData.visitorPageType = 'pending'
            wx.switchTab({
              url: '../visitor_info/visitor_info',
              success: function (e) {
                var page = getCurrentPages().pop();
                if (page == undefined || page == null) return;
                page.onLoad();
              }
            })
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
            wx.switchTab({url: '../visitor_info/visitor_info?theType=pending'})
          }
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