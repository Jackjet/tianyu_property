// pages/invite/invite.js
var app =getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
      code:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      //请求生成邀请码
    var that = this;
    that.getCode();
  },

getCode:function(){
  var uid      =   wx.getStorageSync('sessionid');
  var GroupID  =   wx.getStorageSync('userInfo').Person.GroupID;
  var that     =   this;
  wx.request({
    url: app.globalData.url+ 'orgman/person/inviteperson',
    method:'POST',
    data:{
      sessionid:uid,
      power:2,
      GroupID:GroupID
    },
    header: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
    },
    success(res){      
      if(res.data.Result==602){
        wx.getUserInfo({
          success: function(res) {
           utils.resetLog(res);
          }
        })
        setTimeout(function(){
          that.resgetCode();
        },3000);
      }  
      if(res.data.result==0){
        that.setData({'code':'https://www.dingdingkaimen.cn'+res.data.imgurl})
      }else{
        wx.showToast({
          title: '系统错误',
          icon:'error'
        })
      }
    }
  })
},
resgetCode:function(){
  var uid      =   wx.getStorageSync('sessionid');
  var GroupID  =   wx.getStorageSync('userInfo').Person.GroupID;
  var that     =   this;
  wx.request({
    url: app.globalData.url+ 'orgman/person/inviteperson',
    method:'POST',
    data:{
      sessionid:uid,
      power:2,
      GroupID:GroupID
    },
    header: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
    },
    success(res){      
      if(res.data.result==0){
        that.setData({'code':'https://www.dingdingkaimen.cn'+res.data.imgurl})
      }else{
        wx.showToast({
          title: '系统错误',
          icon:'error'
        })
      }
    }
  })
},

shareCode:function(res)
{
  var url =res.currentTarget.dataset.img;
  wx.downloadFile({
    url:url,
    success: (res) => {
      wx.showShareImageMenu({
        path: res.tempFilePath
      })
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