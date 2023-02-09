// pages/visitor_history/visitor_history.js
const request = require("../../utils/Interface.js")
const util = require('../../utils/util.js')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    utoken: '',
    uid: '',
    name: '',
    visitorArray: [],
  },

  toVisitorDetail (e) {
  },
  toDetail(e){
    let theID = e.currentTarget.dataset.bean.VisitorApplyID;
    wx.navigateTo({url: '../visitor_history_info/visitor_history_info?theID=' + theID})
  },
  initVisitorHistory () {
    wx.showLoading({
      title: '列表加载中',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid=' + that.data.utoken + '&PersonID=' + that.data.uid + '&VisitorApplyName=' + that.data.name + '&VisitorType=2' + '&VisitorStatus=0'
    let theUrl = 'accesscontrol/visitor/WXHistoryRecord' + dataStr

    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        var list = [];
         res.VisitorApplys.forEach(element=>{
          if(element.VisitorStatus !=5){
              list.push(element);
          }
        })
        list.forEach(element => {
          element.inTime = element.VisitorEndTime > new Date().getTime() ? 'in' : 'out'
          element.xing = element.VisitorApplyName.substr(0, 1)
          element.startTime = util.formatTime(new Date(element.VisitorBeginTime))
          element.endTime = util.formatTime(new Date(element.VisitorEndTime))
          element.statusText = element.VisitorStatus + '' === '4' ? '邀请已取消' : '邀请已过期'
        });
        that.setData({
          visitorArray:list,
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
    })
    this.initVisitorHistory()
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