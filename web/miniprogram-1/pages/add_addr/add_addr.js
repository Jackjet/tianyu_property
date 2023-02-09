var app =getApp();
const {utils} = require('../../utils/util.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {

  },
    // 表单提交
    formSubmit: function (e) { 
      if (!e.detail.value.dong || !e.detail.value.ceng || !e.detail.value.hu) {
        wx.showToast({
          title: '请全部填写',
          icon: 'error',
          duration: 1000
        })
        setTimeout(function () {
          wx.hideToast()
        }, 1000)
          return ; 
      } else {
        var sessionid = wx.getStorageSync('sessionid');
        var PersonID  = wx.getStorageSync('Personid');
        var UserGroupName = e.detail.value.dong+'栋'+e.detail.value.ceng+'层'+e.detail.value.hu+'户';
        wx.request({
          url: app.globalData.url+'wx/NewAddress',
          header: {
            "Content-Type": "application/x-www-form-urlencoded"
          },
          method: "POST",
          data: {
                sessionid:sessionid,
                PersonID:PersonID,  
                UserGroupName:UserGroupName
              },
          success: function (res) {
            if (res.data.result != 0) {
              wx.showToast({
                title:res.data.msg,
                icon: 'error',
                duration: 1500
              })
            }else {
              wx.showToast({
                title: '提交成功,请到物业核实添加梯控权限!',//这里打印出登录成功
                icon: 'none',
                duration: 2000,
              })
           setTimeout(function () {
                wx.switchTab({
                  url: '/pages/my/my',
                })
              },2000)
            }
          }
        })
      }
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