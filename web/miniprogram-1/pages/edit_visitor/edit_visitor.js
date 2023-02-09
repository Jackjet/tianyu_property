// pages/edit_visitor/edit_visitor.js
const request = require("../../utils/Interface.js")
const util = require('../../utils/util.js')
var dateTimePicker = require('../../utils/dateTimePicker.js');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    utoken: '',
    uid: '',
    theID: '',
    xing: '',
    name: 'asdf',
    phone: 'adf',
    dateEnd: '',
    date: '2018-10-01',
    time: '12:00',
    dateTimeArray: null,
    dateTime: null,
    dateTimeArray1: null,
    dateTime1: null,
    startYear: 2000,
    endYear: 2050,
    down:false,
    //结束时间
    end_date: '2018-10-01',
    end_time: '12:00',
    end_dateTimeArray: null,
    end_dateTime: null,
    end_dateTimeArray1: null,
    end_dateTime1: null,
    end_startYear: 2021,
    end_endYear: 2050,
    power: [],
  },

  bindPickerChange: function(e) {
    this.setData({
      index: e.detail.value
    })
  },
  startDateChange: function(e) {
    this.setData({
      startDate: e.detail.value
    })
  },
  startTimeChange: function(e) {
    this.setData({
      startTime: e.detail.value + ':00'
    })
  },
  endDateChange: function(e) {
    if (this.data.startDate === '') {
      wx.showModal({
        content: '请先选择来访日期',
        showCancel: false
      })
      return false
    }
    this.setData({
      endDate: e.detail.value
    })
  },
  endTimeChange: function(e) {
    if (this.data.startTime === '') {
      wx.showModal({
        content: '请先选择来访时间',
        showCancel: false
      })
      return false
    }
    if (this.data.endDate === '') {
      wx.showToast({
        title: '请先选择结束日期',
        icon: 'error',
        duration: 2000
      })
      return false
    }
    let startTamp = new Date(this.data.startDate + ' ' + this.data.startTime).getTime();
    let endTamp = new Date(this.data.endDate + ' ' + e.detail.value + ':00').getTime();
    if (startTamp >= endTamp) {
      wx.showModal({
        content: '结束时间必须超过来访时间',
        showCancel: false
      })
      return false
    }
    this.setData({
      endTime: e.detail.value + ':00'
    })
  },
  doSubmit () {
    wx.showLoading({
      title: '提交中...',
      mask: true,
    })
    const that = this

  //开始时间
  var startTime = `${that.data.dateTimeArray[0][that.data.dateTime1[0]]}/${that.data.dateTimeArray[1][that.data.dateTime1[1]]}/${that.data.dateTimeArray[2][that.data.dateTime1[2]]} ${that.data.dateTimeArray[3][that.data.dateTime1[3]]}:${that.data.dateTimeArray[4][that.data.dateTime1[4]]}`;
  var ddtime = new Date(startTime);
  var stime = ddtime.getTime();
  //结束时间
  var endTime = `${that.data.end_dateTimeArray[0][that.data.end_dateTime1[0]]}/${that.data.end_dateTimeArray[1][that.data.end_dateTime1[1]]}/${that.data.end_dateTimeArray[2][that.data.end_dateTime1[2]]} ${that.data.end_dateTimeArray[3][that.data.end_dateTime1[3]]}:${that.data.end_dateTimeArray[4][that.data.end_dateTime1[4]]}`;
  var etime = new Date(endTime);
  var endtime = etime.getTime();

  if(endtime<=stime){
    wx.showToast({
      title: '结束时间必须大于来访时间',
      icon:'none'
    })
    return;
  }

    let theData = {
      startTime:startTime,
      endTime:endTime 
    }

    let dataStr = '?sessionid='+ that.data.utoken + '&VisitorApplyID=' + that.data.theID + '&VisitorBeginTime=' + theData.startTime + '&VisitorEndTime=' + theData.endTime
    let theUrl = 'wx/updateVisitorTime' + dataStr
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
        console.log(err)
    })
  },
  initInfo () {
  
    wx.showLoading({
      title: '详情加载中',
      mask: true,
    })
    const that = this
    let dataStr = '?sessionid='+ that.data.utoken + '&VisitorApplyID=' + that.data.theID
    let theUrl = 'wx/queryVisitorByVisitorApplyID' + dataStr
    request.doPost(theUrl, {},
      function (res) {
        wx.hideLoading()
        let powerArr = []
        res.rules.forEach(element => {
          powerArr.push(element.DeviceName + ' ' + element.LiftRule + '层')
        });
        var year = util.formatTime(new Date(res.VisitorBeginTime)).substr(2,2);
        var datetime0 = [];
        //来访时间拼接
        datetime0[0] =  parseInt(year);
        datetime0[1] =  parseInt(util.formatTime(new Date(res.VisitorBeginTime)).substr(5,2))-1;
        datetime0[2] =  parseInt(util.formatTime(new Date(res.VisitorBeginTime)).substr(8,2))-1;
        datetime0[3] =  parseInt(util.formatTime(new Date(res.VisitorBeginTime)).substr(11,2));
        datetime0[4] =  parseInt(util.formatTime(new Date(res.VisitorBeginTime)).substr(14,2));

        var end_date = [];
        //结束时间拼接
        end_date[0] =  parseInt(util.formatTime(new Date(res.VisitorEndTime)).substr(2,2));
        end_date[1] =  parseInt(util.formatTime(new Date(res.VisitorEndTime)).substr(5,2))-1;
        end_date[2] =  parseInt(util.formatTime(new Date(res.VisitorEndTime)).substr(8,2))-1;
        end_date[3] =  parseInt(util.formatTime(new Date(res.VisitorEndTime)).substr(11,2));
        end_date[4] =  parseInt(util.formatTime(new Date(res.VisitorEndTime)).substr(14,2));

        that.setData({
          dateTime1: datetime0,
          end_dateTime1:end_date,
          xing: res.VisitorApplyName.substr(0, 1),
          name: res.VisitorApplyName,
          phone: res.VisitorApplyPhone,
          power: powerArr,
        })
      }, function (err) {
        console.log(err)
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
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
    this.setData({
      utoken: wx.getStorageSync('sessionid'),
      uid: wx.getStorageSync('Personid'),
      theID: options.theID,
    })
      // 获取完整的年月日 时分秒，以及默认显示的数组
      var obj = dateTimePicker.dateTimePicker(this.data.startYear, this.data.endYear);
      var obj1 = dateTimePicker.dateTimePicker(this.data.startYear, this.data.endYear);
      // 精确到分的处理，将数组的秒去掉
      var lastArray = obj1.dateTimeArray.pop();
      var lastTime = obj1.dateTime.pop();
      obj.dateTime.pop();
      this.setData({
        dateTimeArray: obj.dateTimeArray,
        dateTimeArray1: obj1.dateTimeArray,
        dateTime1: obj1.dateTime,
        end_dateTime: obj.dateTime,
        end_dateTimeArray: obj.dateTimeArray,
        end_dateTimeArray1: obj1.dateTimeArray,
        end_dateTime1: obj1.dateTime
      });
      console.log(this.data.dateTimeArray1[0][this.data.dateTime1[0]])
    this.initInfo()
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
  var arr = this.data.dateTime1, dateArr = this.data.dateTimeArray1;

  arr[e.detail.column] = e.detail.value;
  dateArr[2] = dateTimePicker.getMonthDay(dateArr[0][arr[0]], dateArr[1][arr[1]]);

  this.setData({
    dateTimeArray1: dateArr,
    dateTime1: arr
  });
},

//来访时间结束
//结束时间开始
changeDate_end(e){
  console.log(e);
  this.setData({end_date:e.detail.value});
},

changeTime_end(e){
  console.log(e)
  this.setData({ end_time: e.detail.value });
},

changeDateTime_end(e){

  this.setData({ end_dateTime1: e.detail.value,down:true});
},

changeDateTime1_end(e) {
  console.log(e)
  this.setData({ end_dateTime1: e.detail.value });
},

changeDateTimeColumn_end(e) {
  var arr = this.data.end_dateTime1, dateArr = this.data.end_dateTimeArray1;

  arr[e.detail.column] = e.detail.value;
  dateArr[2] = dateTimePicker.getMonthDay(dateArr[0][arr[0]], dateArr[1][arr[1]]);

  this.setData({
    end_dateTimeArray1: dateArr,
    end_dateTime1: arr
  });
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