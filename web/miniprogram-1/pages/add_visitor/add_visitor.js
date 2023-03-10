// pages/add_visitor/add_visitor.js
let request = require("../../utils/Interface.js")
var dateTimePicker = require('../../utils/dateTimePicker.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    utoken: '',
    uid: '',
    objectArray: [],
    index: 0,
    name: '',
    phone: '',
    power: '',
    date: '2018-10-01',
    time: '12:00',
    dateTimeArray: null,
    dateTime1: null,
    startYear: 2000,
    endYear: 2050,
    down:false,
    //结束时间
    end_date: '2018-10-01',
    end_time: '12:00',
    end_dateTimeArray: null,
    end_dateTime1: null,
    end_startYear: 2021,
    end_endYear: 2050
  },

  bindPickerChange: function(e) {
    this.setData({
      index: e.detail.value
    })
  },
  
  //表单提交
  doSubmit () {
    var that = this;
    //时间判断
    
    //开始时间
    var startTime = `${that.data.dateTimeArray[0][that.data.dateTime1[0]]}/${that.data.dateTimeArray[1][that.data.dateTime1[1]]}/${that.data.dateTimeArray[2][that.data.dateTime1[2]]} ${that.data.dateTimeArray[3][that.data.dateTime1[3]]}:${that.data.dateTimeArray[4][that.data.dateTime1[4]]}`;
    var ddtime = new Date(startTime);
    var stime = ddtime.getTime();
    //结束时间
    var endTime = `${that.data.end_dateTimeArray[0][that.data.end_dateTime1[0]]}/${that.data.end_dateTimeArray[1][that.data.end_dateTime1[1]]}/${that.data.end_dateTimeArray[2][that.data.end_dateTime1[2]]} ${that.data.end_dateTimeArray[3][that.data.end_dateTime1[3]]}:${that.data.end_dateTimeArray[4][that.data.end_dateTime1[4]]}`;

    var etime = new Date(endTime);
    var endtime = etime.getTime();
    if(endtime <= stime){
      wx.showToast({
        title: '结束时间必须大于来访时间',
        icon:'none'
      })
      return ;
    }

    let myreg = /^[1][3,4,5,7,8][0-9]{9}$/; //手机号码正则验证 
    if(!myreg.test(that.data.phone)){
      wx.showToast({
        title: '请输入正确号码!',
        icon: 'error',
        duration: 1500
      })
      return ;
    }
    if(!that.data.name){
      wx.showToast({
        title: '请输入访客姓名!',
        icon: 'error',
        duration: 1500
      })
      return ;
    }

    wx.showLoading({
      title: '正在发出邀请',
      mask: true,
    })
    let theData = {
      name: that.data.name,
      startTime: startTime,
      endTime:endTime,
      phone: that.data.phone,
      power: that.data.power,
    }
    let dataStr = '?sessionid='+ that.data.utoken + '&PersonID=' + that.data.uid + '&VisitorApplyName=' + theData.name + '&VisitorBeginTime=' + theData.startTime + '&VisitorEndTime=' + theData.endTime + '&VisitorApplyPhone=' + theData.phone + '&VisitorType=2'
    let theUrl = 'wx/visitorsubmit' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        
        wx.hideLoading()
        wx.showModal({
          content: res.msg,
          showCancel: false,
          success (res) {
            getApp().globalData.visitorPageType = 'visitor'
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
      
    })
  
  },
  initRule () {
    wx.showLoading({
      title: '权限加载中',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&PersonID=' + that.data.uid
    let theUrl = 'wx/getownerrule' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        that.setData({
          objectArray: res.Rules
        })
        wx.hideLoading()
      }, function (err) {
       
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
		let timestamp = Date.parse(new Date())
		timestamp = timestamp / 1000
		let n = timestamp * 1000
		let date = new Date(n)
		let Y = date.getFullYear()
		let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1)
		let D = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()

		this.setData({
			dateEnd: Y + '-' + M + '-' + D
    })
        // 获取完整的年月日 时分秒，以及默认显示的数组
        var obj = dateTimePicker.dateTimePicker(this.data.startYear, this.data.endYear);
        var obj1 = dateTimePicker.dateTimePicker(this.data.startYear, this.data.endYear);
        // 精确到分的处理，将数组的秒去掉
        var lastArray = obj1.dateTimeArray.pop();
        var lastTime = obj1.dateTime.pop();
        obj.dateTime.pop();
        this.setData({
          dateTime: obj.dateTime,
          dateTimeArray: obj1.dateTimeArray,
          dateTime1: obj1.dateTime,
          end_dateTime: obj.dateTime,
          end_dateTimeArray: obj1.dateTimeArray,
          end_dateTime1: obj1.dateTime
        });
  },
  //来访时间
  changeDate(e){
    this.setData({ date:e.detail.value});
  },
  changeTime(e){
    this.setData({ time: e.detail.value });
  },


  changeDateTime1(e) {
    this.setData({ dateTime1: e.detail.value });
  },

  changeDateTimeColumn1(e) {
    var arr = this.data.dateTime1, dateArr = this.data.dateTimeArray;
    arr[e.detail.column] = e.detail.value;
    dateArr[2] = dateTimePicker.getMonthDay(dateArr[0][arr[0]], dateArr[1][arr[1]]);

    this.setData({
      dateTimeArray: dateArr,
      dateTime1: arr
    });
  },
  //来访时间结束
  //结束时间开始
  changeDate_end(e){
  
  },

  changeDateTime1_end(e) {
    var that =this;
    that.setData({end_dateTime1: e.detail.value ,down:true});
  },

  // 结束时间结束
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