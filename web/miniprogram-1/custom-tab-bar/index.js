Component({
  data: {
    show:true,
    selected: null,
    color: "#fff",
    selectedColor: "#fff",
    list: [{
      pagePath: "/pages/visitor_info/visitor_info",
      iconPath: "/static/images/zu88.png",
      selectedIconPath: "/static/images/zu89.png",
      text: "访客"
    }, {
      pagePath: "/pages/lift/lift",
      iconPath: "/static/images/l.png",
      selectedIconPath: "/static/images/chengti.png",
      text: "乘梯"
    }, {
      pagePath: "/pages/my/my",
      iconPath: "/static/images/zu86.png",
      selectedIconPath: "/static/images/zu87.png",
      text: "我的"
    }
  ]
  },
  attached() {
  },
  methods: {
    switchTab(e) {
      const data = e.currentTarget.dataset
      const url = data.path
      wx.switchTab({url})
      this.setData({
        selected: data.index
      })
    }
  }
})