// pages/power/power.js
const app = getApp();
const {utils} = require('../../utils/util.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    powerList:[],   //权限数据
    power:'',       //权限等级
    user:''
  }, 

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      let that = this;
      let power = wx.getStorageSync('power');
      let user = wx.getStorageSync('userData');
    if(power){
      that.setData({power:power,user:user})
    }
      that.getPower();
  },

  // 获取访问权限
  getPower:function(){
  let that = this;
  var uid  = wx.getStorageSync('sessionid');
  var personid = wx.getStorageSync('Personid');
    wx.request({
      url:app.globalData.url + 'wx/getownerrule',
      method:'POST',
      data:{
        sessionid :uid,
        PersonID:personid
      },
      header: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
      },
      success(res){
        that.setData({powerList:res.data.Rules})
      }
    })
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