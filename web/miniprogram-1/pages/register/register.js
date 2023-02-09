const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    power:1,
    list_addr:[],
    length:false,
    GroupPower:'',
    GroupID:'',
    name:'',
    formList:
    [{
      dong: null,
      ceng: null,
      hu: null
    }]
  },

  // 输入框取值
  customerinput(e) {
    let _this = this;
    let ids = e.target.dataset.id
    let attribute = e.target.dataset.attr
    let Values = e.detail.value
    let formi = this.data.formList[ids]
    if (attribute == 'dong') {
       formi.dong = Values+'栋'
    } else if (attribute == 'ceng') {
       formi.ceng = Values +'层'
    } else {
       formi.hu = Values +'户'
    }
    //最新的数组
    this.setData({
      formList: this.data.formList
    })
  },
  // 新增表单
  additem(e) {
    let forms = this.data.formList;
    var newItem = {
      dong: null,
      ceng: null,
      hu: null
    };
    let formss = forms.concat(newItem);
    this.setData({
      formList: formss
    })
  },
 
  //循环增加数据
  onAdd_addr: function () {
    var newItem = {
      dong: null,
      ceng: null,
      hu: null
    };
    let formss = forms.concat(newItem);
    this.setData({
      formList: formss
    })
  },

/**
 * 弹出数组
 */
  delAddr() {
    var newList = this.data.formList;
    newList.pop();
    this.setData({
      formList: newList
    })
  },
 
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var sid = wx.getStorageSync('sessionid');
    if(!sid){
        wx.switchTab({
          url: '/pages/lift/lift',
        })
    }
    var that = this;
    console.log(options.power);
    if(options.power=="1"){
      that.setData({power:1,GroupPower:1})  //power1代表户主
    }else{
      var groupid= options.groupid;
      that.setData({power:2,GroupPower:2,GroupID:groupid})  //power2代表成员
    }
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

  },

  // 表单提交
  formSubmit: function (e) { 
    var that =this;
    let UserGroupName= ''
    let myreg = /^[1][3,4,5,7,8][0-9]{9}$/; //手机号码正则验证
    let rePass = new RegExp('^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$', '');  //邮箱正则验证
      this.data.formList.map(item => {
         UserGroupName += item.dong+item.ceng+item.hu+',';
      })
      if(that.data.power==1){
        UserGroupName =  UserGroupName.substring(0, UserGroupName.lastIndexOf(','));
      }else{
        UserGroupName = null;
      }
      var power = that.data.power;
      // 代表户主
      if(power==1){
        if(!e.detail.value.dong0 || !e.detail.value.ceng0 || !e.detail.value.hu0){
          wx.showToast({
            title: '请填写正确住址',
            icon: 'error',
            duration: 1000
          })
          setTimeout(function () {
            wx.hideToast()
          }, 1000)
          return;
        }
      }
      if(e.detail.value.email.length){
        if(!rePass.test(e.detail.value.email)){
    
          wx.showToast({
            title: '邮箱格式错误',
            icon: 'error',
            duration: 1000
          })
          setTimeout(function () {
            wx.hideToast()
          }, 1000)
          return ;
        }
      }
    if(e.detail.value.name.length == 0) {
      wx.showToast({
        title: '姓名不能为空',
        icon: 'error',
        duration: 1000
      })
      setTimeout(function () {
        wx.hideToast()
      }, 1000)
      return ;
    } else if (!myreg.test(e.detail.value.phone)) {
      wx.showToast({
        title: '请输入正确号码!',
        icon: 'error',
        duration: 1500
      })
      return ;
    } else {
      var sessionid = wx.getStorageSync('sessionid');
      var PersonID  = wx.getStorageSync('Personid');
      var GroupID = that.data.GroupID;
      var GroupPower = that.data.GroupPower;
      wx.request({
        url: app.globalData.url+'wx/insertperson',
        header: {
          "Content-Type": "application/x-www-form-urlencoded"
        },
        method: "POST",
        data: {
              sessionid:sessionid,
              PersonID:PersonID,
              FullName: e.detail.value.name,
              Email: e.detail.value.email,
              PhoneNum: e.detail.value.phone,
              GroupPower:GroupPower,
              UserGroupName:UserGroupName,
              GroupID:GroupID
            },
        success: function (res) {
          console.log(res);
          if (res.data.result != 0) {
            wx.showToast({
              title:res.data.msg,
              icon: 'none',
              duration: 1500
            })
          }else {
            wx.showToast({
              title: '提交成功', //这里打印出登录成功
              icon: 'success',
              duration: 2000
            })
                        
            setTimeout(function(){      
                wx.switchTab({
                  url: '/pages/lift/lift',
                })
            },2000)
          }
        }
      })
    }
  },

})