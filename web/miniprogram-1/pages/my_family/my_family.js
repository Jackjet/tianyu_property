const app = getApp();
const {utils} = require('../../utils/util.js');
Page({
  /**
   * 页面的初始数据
   */
  data: {
    list:[],
    passed:true,
    power:'',
    totalList:[],
    total:'' //成员人数
  },

  // 获取已通过的
  passed:function(){
     this.getFamily();
     this.setData({passed:true})
  
  },


  //获取待审核的
  NoPassed:function(){
  var no_list = this.data.totalList;
  var list=[];
  no_list.forEach(item=>{
  if(item.Status==1){
    item.select = 0
    list.push(item);
  }
  this.setData({list:list,passed:false})
 })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      //获取家庭组成员
      var that = this;
      // 获取家庭权限
      var power = wx.getStorageSync('power');
      that.setData({power:power})
      that.getFamily()
  },

  // 
  getFamily:function(){
    wx.showLoading({
      title: '正在加载',
    })
    var that = this;
    var sessionid = wx.getStorageSync('sessionid');
    var PersonID = wx.getStorageSync('Personid');
    var GroupID = wx.getStorageSync('userInfo').Person.GroupID;
    wx.request({
      url: app.globalData.url +'wx/queryfamily',
      method:'POST',
      data:{
        sessionid:sessionid,
        GroupID:GroupID,
        PersonID:PersonID,
        Flag:2
      },
      header: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
      },
      success(res){
        if(res.code==500){
          wx.showToast({
            title: '系统繁忙',
          })
        }
        wx.hideLoading();
        var list = []
        res.data.Persons.forEach(function(item,index){
          if(item.Status==2){
            list.push(item);
          }
        })
        var total = list.length;
       that.setData({list:list,totalList:res.data.Persons,total:total})
      }
    })
  },

 
  // 选择事件
  selectList(e) {
    const index = e.currentTarget.dataset.index;    // 获取data- 传进来的index
    let list = this.data.list;                      // 获取列表
    const select = list[index].select;              // 获取当前选中状态
    list[index].select = !select;                   // 改变状态
    this.setData({
        list:list
    });
                            
},

//同意or拒绝方法
consent:function(e){
  var that = this;

  var sessionid = wx.getStorageSync('sessionid');
  var is = e.target.dataset.is;
 
  if(is=="yes"){
    var Status = 2;
  }else{
    var Status = 3;
  }
  var  GroupId = this.data.list;
  var userGroup = [];
    GroupId.forEach(item=>{
      if(item.select){
          userGroup.push(item);
      }
    })
 
  var PersonID = wx.getStorageSync('Personid');
  var list = this.data.list;
   userGroup.forEach(item=>{
    wx.request({
      url: app.globalData.url+ 'wx/updategroupstatus',
      method:'POST',
      data:{
        sessionid:sessionid,
        UserGroupID:item.UserGroupID,
        PersonID:PersonID,
        PassivePersonID:item.PersonID,
        Status:Status
      },
      header: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' ,
      },
      success(res){
        if(res.statusCode==200){
        var  newList=[];
        if(is=='yes'){
          wx.showToast({
            title:'已同意',
            mask:true    
          })
        }else{
          wx.showToast({
            title:'已拒绝',
            mask:true    
          })
        }
          list.forEach(item=>{
            //未选中的数据绑定
            if(item.select==0){    
               newList.push(item);
            }
        }),
        that.setData({list:newList})
        }
      }
    })
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