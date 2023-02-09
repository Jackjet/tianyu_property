// pages/my/my.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    level:0,     //权限
    userinfo:[]  //用户信息
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var userinfo = wx.getStorageSync('userInfo');
    var power = wx.getStorageSync('power');
    var that = this;
    that.setData({level:power,userinfo:userinfo});
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
    if (typeof this.getTabBar === 'function' &&
    this.getTabBar()) {
    this.getTabBar().setData({
      selected:2
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

  clear:function (params) {
    wx.clearStorage();
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  
  toVi:function (params) {
    wx.switchTab({
      url: '/pages/visitor_info/visitor_info',
    })
  }
})
