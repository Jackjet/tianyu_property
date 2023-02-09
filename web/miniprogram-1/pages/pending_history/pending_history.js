// pages/pending_history/pending_history.js
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
    pendingArray: [],
  },

  toVisitorDetail (e) {
  },
  toDetail(e){
    let theID = e.currentTarget.dataset.bean.VisitorApplyID;
    wx.navigateTo({url: '../pending_history_info/pending_history_info?theID=' + theID})
  },
  initPendingHistory () {
    wx.showLoading({
      title: '列表加载中',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid=' + that.data.utoken + '&PersonID=' + that.data.uid + '&VisitorApplyName=' + that.data.name + '&VisitorType=1' + '&VisitorStatus=1'
    let theUrl = 'accesscontrol/visitor/WXHistoryRecord' + dataStr

    request.doPost(theUrl, {},
      function (res) {
        var list = [];
        res.VisitorApplys.forEach(element=>{
         if(element.VisitorStatus !=5){
             list.push(element);
         }
       })
        wx.hideLoading()
        list.forEach(element => {
          element.inTime = element.VisitorEndTime > new Date().getTime() ? 'in' : 'out'
          element.xing = element.VisitorApplyName.substr(0, 1)
          element.startTime = util.formatTime(new Date(element.VisitorBeginTime))
          element.endTime = util.formatTime(new Date(element.VisitorEndTime))
          switch (element.VisitorStatus + '') {
            case '2':
              element.statusText = '申请已通过'
              break;
            case '3':
              element.statusText = '申请已拒绝'
              break;
            default:
              element.statusText = '申请已过期'
              break;
          }
        });
        that.setData({
          pendingArray:list,
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
    this.initPendingHistory()
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
    this.initPendingHistory()
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