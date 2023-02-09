const app = getApp();
let drawQrcode = require("../../utils/erweima.js");
Page({
  /**
   * 页面的初始数据
   */
  data: {
      shows: false,    //控制下拉列表的显示隐藏，false隐藏、true显示
      userInfo:[],     //用户详情
      userData:[],     //用户头像昵称
      isJoin:false,    //是否加入了家庭组
      level:'' ,       //权限成员还是户主
      result:'',       //二维码的标识
      selectDatas:[],  
      indexs: 0,       //选择的下拉列表下标
      text:"",
      opt:true,       //箭头的动画
  },

 // 点击下拉显示框
 selectTaps() {
  this.setData({
    shows: !this.data.shows,
     opt: !this.data.opt
  });
},

// 点击下拉列表
optionTaps(e) {
  let Indexs = e.currentTarget.dataset.index; //获取点击的下拉列表的下标
  this.setData({
    indexs: Indexs,
    shows: !this.data.shows
  });
},
// 跳转使用说明页
toHelp () {
  wx.navigateTo({url: '/pages/help/help'})
},
/**
 * 扫描二维码
 */
getScancode: function () {
  var _this = this;
  if(_this.data.userData.NickName=="微信用户"){
      wx.showToast({
        title: '请获取头像昵称',
        icon:'error'
      })
      return ;
  }
  wx.scanCode({
    success: (res) => {
      // 扫描的是物业二维码
        if(res.path == "pages/index/index?power=1"){
          wx.navigateTo({
            url: '/pages/register/register?power=1',
          })
        }else{
          //扫描的是户主二维码
          var groupid =  this.foo(res.path).groupid;  //匹配path参数
          wx.navigateTo({
            url: '/pages/register/register?power=2&groupid='+ groupid
          })
        }
    }
  })
},

//正则截取参数
 foo:function(s) {
  var request = {};
  var pairs = s.split('&');
  for (var i = 0; i < pairs.length; i++) {
    var pair = pairs[i].split('=');
    request[pair[0]] = pair[1];
  }
  return request;
},

/**
 * 生成二维码
 */
ewmChange(){
  var timestamp = Date.parse(new Date());
        var sNum = Math.floor(timestamp / 1000);
        var qr =  wx.getStorageSync('qrcode');
        var numStr = sNum.toString(16) + ''
        var numArr = []
        for (let index = 0; index < 4; index++) {
          numArr.push(numStr.substr(index * 2, 2))
        }
        var lastStr = numArr.reverse().join('');
        //获取当前时间  
        var n = timestamp * 1000;
        var date = new Date(n);
        let size = {}
        size.w = wx.getSystemInfoSync().windowWidth / 750 * 450
        size.h = size.w
        var that = this;
        drawQrcode({
          width: size.w,
          height: size.h,
          canvasId: 'myQrcode',
          text: qr+lastStr,
          // v1.0.0+版本支持在二维码上绘制图片
        })
},

 /**
   * 请求后台通行码
   */
  touchCode:function(){
    var that = this;
    var uid      = wx.getStorageSync('sessionid');
    var personid = wx.getStorageSync('Personid');
      wx.request({
        url: app.globalData.url+ 'wx/generateQR',
        method:'POST',
        data:{
          sessionid:uid,
          PersonID:personid
        },
        header: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
        },    
        success(res){ 
            wx.setStorageSync('qrcode',res.data.QR)
            that.ewmChange();
        }
      })
  },

//创建二维码
searchCode:function(){
  var that = this;
  var qr =wx.getStorageSync('qrcode');
  if(qr) that.ewmChange();
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this;
    let qr = wx.getStorageSync('qrcode');
    if(qr){ 
      that.ewmChange();
      that.oneFlush();   //调用一次性定时器
    }
    var userInfo = wx.getStorageSync('userInfo');
    var userData = wx.getStorageSync('userData');
    that.setData({'userInfo':userInfo,'userData':userData});
    var uid = wx.getStorageSync('sessionid');
    if(!uid){
      wx.getUserInfo({
        success: function(res) {
          that.Login(res);
        }
      })
    }

    var power = wx.getStorageSync('power');
    if(!power){
      that.setData({isJoin:false});
      that.hideTab();
    }else{
      that.showTab();
      that.setData({isJoin:true,power:power});
    }
  },


//一次性定时器
oneFlush:function () {
  var that = this;
  setTimeout(function () {
    that.flushCode();    //设置一次性定时器5分钟触发
  },3000)
},


// 3秒生成新二维码
flushCode:function(){
  var that = this;
  if(wx.getStorageSync('qrcode')){
    setInterval(function () {
      that.ewmChange();  //周期性定时器
      },3000)
  }
},

//显示tabbar
showTab:function(){
  this.getTabBar().setData({
    show:true    //显示tabbar
  })
},

//隐藏tabbar
hideTab:function () {
  this.getTabBar().setData({
    show:false  //隐藏tabbar
  })
},

//刷新再次请求登录数据
goLogin(){
  var that =this;
  wx.getUserInfo({
    success: function(res) {
      that.Login(res,true);
    }
  })
},

// 登录
Login:function(e,once){
  var that =this;
  if(!once){
    wx.showLoading({
      title: '正在自动登录',
      mask:true
    })
  }
  wx.login({
    success (res) { 
      var encryptedData = e.encryptedData;
      var iv = e.iv; 
     let NewData={
      code:res.code,
      appId:'wxfdec81f1fa23c24f',
      iv:iv,
      encryptedData:encryptedData
     }
      if (res.code) {
        //发起网络请求
        wx.request({
          url: app.globalData.url + 'wx/login',
          method:'POST',
          data: NewData,
          header: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
          },
          success(res){
            if(res.statusCode==200){
                wx.setStorageSync('userInfo',res.data);
                wx.setStorageSync('Personid',res.data.personid);
                wx.setStorageSync('sessionid',res.data.sessionid);
                wx.setStorageSync('userData',{'NickName':res.data.NickName,'AvatarUrl':res.data.AvatarUrl});
                var userData = wx.getStorageSync('userData');
             that.setData({userInfo:res.data,userData:userData})
             if(res.data.Person){
                wx.setStorageSync('power', res.data.Person.GroupPower);
                that.setData({isJoin:true,power:res.data.Person.GroupPower})
                that.touchCode();   //生成二维码
                that.oneFlush();    //在调用一次性定时器
                that.showTab();     //显示tabbar
             }else{
               //如果没有,有种可能是用户已被后台删除,所以就需要清除之前的部分缓存
               wx.setStorageSync('power', '');
               that.setData({isJoin:false,power:''});
               that.hideTab();
             }
              wx.hideLoading({});
              if(!once){
                wx.showToast({
                  title: '登录成功',
                  icon: 'success',
                  duration: 1000
                })   
              }           
            }else{
              if(!once){
                wx.hideLoading({})
                wx.showToast({
                  title: '登录失败',
                  icon: 'error',
                  duration: 1000
                })   
              }      
            }      
          }
        })
      } 
    }
  })
},

//获取头像昵称
getUserProfile(e) {
  var that = this;
    var personid = wx.getStorageSync('Personid');
    var sessionid = wx.getStorageSync('sessionid');
  wx.getUserProfile({
    desc: '用于完善用户资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
    success: (res) => {
      if(res.errMsg=='getUserProfile:ok'){
        wx.request({
          url: app.globalData.url+'wx/updatePersonInfo',
          method:'POST',
          data:{
             PersonID:personid,
             sessionid:sessionid,
             WxAvatarUrl:res.userInfo.avatarUrl,
             nickName:res.userInfo.nickName
          },
          header: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
          },
          success(res){
             wx.setStorageSync('userData',res.data);
             that.setData({userData:res.data});
          }
        })
      }
    }
  })
},

//重置sessionid
resetData:function(e){
  var that = this;
  wx.login({
    success (res) { 
      var encryptedData = e.encryptedData;
      var iv = e.iv; 
     let NewData={
      code:res.code,
      appId:'wxfdec81f1fa23c24f',
      iv:iv,
      encryptedData:encryptedData
     }
      if (res.code) {
        //发起网络请求
        wx.request({
          url: app.globalData.url + 'wx/login',
          method:'POST',
          data: NewData,
          header: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
          },
          success(res){
            if(res.statusCode==200){
                // 重新设置sessionid
               wx.setStorageSync('sessionid',res.data.sessionid);
            } 
          }
        })
      }
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
    var that = this;
        //去刷新信息
        var uid  = wx.getStorageSync('sessionid');
        if(uid){
          wx.getUserInfo({
            success: function(res) {
              that.resetData(res);
            }
          })
        }

    if (typeof this.getTabBar === 'function' &&
    this.getTabBar()) {
    this.getTabBar().setData({
      selected:1
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
      //刷新再次请求登录接口
      var that =this;
      that.goLogin();
      setTimeout(function () {
        wx.stopPullDownRefresh()
      },1000);
    
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