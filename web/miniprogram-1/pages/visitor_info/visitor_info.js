// pages/visitor_info/visitor_info.js
const request = require("../../utils/Interface.js")
const util = require('../../utils/util.js')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    utoken: '',
    uid: '',
    nowPage: 'visitor',
    visitorSearch: '',
    pendingSearch: '',
    visitorArray: [],
    pendingArray: [],
  },
  toInvitation: function () {
    if (this.data.nowPage === 'visitor') {
      return false
    }
    this.setData({
      nowPage: 'visitor',
      visitorArray: [],
      pendingArray: [],
    })
    this.initVisitor()
  },
  toPending: function () {
    if (this.data.nowPage === 'pending') {
      return false
    }
    this.setData({
      nowPage: 'pending',
      visitorArray: [],
      pendingArray: [],
    })
    this.initPending()
  },
  initVisitor () {
    wx.showLoading({
      title: '加载中',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&PersonID=' + that.data.uid + '&VisitorApplyName=' + that.data.visitorSearch + '&VisitorType=2' + '&VisitorStatus=0'
    let theUrl = 'accesscontrol/visitor/InvitationList' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        if (res.length === 0) {
          that.setData({
            visitorArray: [],
          })
          return false
        }
        for (let index = 0; index < res.length; index++) {
          res[index].VisitorBeginTime = util.formatTime(new Date(res[index].VisitorBeginTime)),
          res[index].VisitorEndTime = util.formatTime(new Date(res[index].VisitorEndTime)),
          res[index].xing = res[index].VisitorApplyName.substr(0, 1)
        }
        that.setData({
          visitorArray: res
        })
      }, function (err) {
        console.log(err)
    })
  },
  initPending () {
    wx.showLoading({
      title: '加载中',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&PersonID=' + that.data.uid + '&VisitorApplyName=' + that.data.visitorSearch + '&VisitorType=1' + '&VisitorStatus=1'
    let theUrl = 'accesscontrol/visitor/InvitationList' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        if (res.length === 0) {
          that.setData({
            pendingArray: [],
          })
          return false
        }
        for (let index = 0; index < res.length; index++) {
          res[index].VisitorBeginTime = util.formatTime(new Date(res[index].VisitorBeginTime)),
          res[index].VisitorEndTime = util.formatTime(new Date(res[index].VisitorEndTime)),
          res[index].xing = res[index].VisitorApplyName.substr(0, 1)
        }
        that.setData({
          pendingArray: res
        })
      }, function (err) {
        console.log(err)
    })
  },
  doSearchVisitor () {
    this.initVisitor()
  },
  doSearchPending () {
    this.initPending()
  },
  toVisitorHistory () {
    wx.navigateTo({url: '../visitor_history/visitor_history'})
  },
  toPendingHistory () {
    wx.navigateTo({url: '../pending_history/pending_history'})
  },
  toVisitorDetail (e) {
    let theID = e.currentTarget.dataset.bean.VisitorApplyID;
    wx.navigateTo({url: '../visitor_details/visitor_details?theID=' + theID})
  },
  doVisitorDeleta (e) {
    wx.showLoading({
      title: '请求已发出',
      mask: true,
    })
    const that = this
    let theID = e.currentTarget.dataset.bean.VisitorApplyID;
    let dataStr = '?sessionid='+ that.data.utoken + '&VisitorStatus=4' + '&VisitorApplyID=' + theID
    let theUrl = 'accesscontrol/visitor/VisitorRecordUpdate' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        wx.showModal({
          content: res.msg,
          showCancel: false,
          success (res) {
            that.initVisitor()
          }
        })
      }, function (err) {
        console.log(err)
    })
  },
  toVisitorAdd () {
    wx.navigateTo({url: '../add_visitor/add_visitor'})
  },
  toPendingDetail (e) {
    let theID = e.currentTarget.dataset.bean.VisitorApplyID;
    wx.navigateTo({url: '../approval_visitor/approval_visitor?theID=' + theID})
  },
  doAgree (e) {
    const that = this
    let theID = e.currentTarget.dataset.bean.VisitorApplyID;
    wx.showModal({
      content: '再次确认是否同意该访客申请？',
      cancelText: '关闭',
      confirmText: '同意',
      confirmColor: '#08a82a',
      success (res) {
        if (res.confirm) {
          that.doPendingAgree(theID)
        }
      }
    })
  },
  doRefuse (e) {
    const that = this
    let theID = e.currentTarget.dataset.bean.VisitorApplyID;
    wx.showModal({
      content: '再次确认是否拒绝该访客申请？',
      cancelText: '关闭',
      confirmText: '拒绝',
      confirmColor: '#d43418',
      success (res) {
        if (res.confirm) {
          that.doPendingRefuse(theID)
        }
      }
    })
  },
  doPendingAgree (theID) {
    wx.showLoading({
      title: '请求已发出',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&VisitorStatus=2' + '&VisitorApplyID=' + theID
    let theUrl = 'accesscontrol/visitor/VisitorRecordUpdate' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        wx.showModal({
          content: res.msg,
          showCancel: false,
          success (res) {
            that.initPending()
          }
        })
      }, function (err) {
        console.log(err)
    })
  },
  doPendingRefuse (theID) {
    wx.showLoading({
      title: '请求已发出',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&VisitorStatus=3' + '&VisitorApplyID=' + theID
    let theUrl = 'accesscontrol/visitor/VisitorRecordUpdate' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        wx.showModal({
          content: res.msg,
          showCancel: false,
          success (res) {
            that.initPending()
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
    this.setData({
      utoken: wx.getStorageSync('sessionid'),
      uid: wx.getStorageSync('Personid'),
    })
    this.setData({
      nowPage: getApp().globalData.visitorPageType
    })
    if (this.data.nowPage === 'visitor') {
      this.initVisitor()
    } else if (this.data.nowPage === 'pending') {
      this.initPending()
    }
    if (typeof this.getTabBar === 'function' &&
    this.getTabBar()) {
    this.getTabBar().setData({
      selected:0
    })
  }
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