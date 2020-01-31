var cid = location.pathname.split('/');
cid = cid[cid.length - 1];
vue_solu = new Vue({
    el: "#vue-solutions",
    data: {
        contest: {
            id: cid
        },
        solutions: [],
        totalElements: 0,
        totalPages: 0,
        last: true,
        number: 0,
        size: 30,
        numberOfElements: 0,
        first: true,
        empty: true,
        npage: []
    },
    created: function () {
        var that = this;
        url = '/api/contest/status/' + cid;
        axios.get(url).then(function (res) {
            res = res.data;
            that.solutions = res.content;
            that.totalPages = res.totalPages;
            that.totalElements = res.totalElements;
            that.last = res.last;
            that.number = res.number;
            that.size = res.size;
            that.first = res.first;
            that.numberOfElements = res.numberOfElements;
            that.empty = res.empty;
            that.npage = [];
            for (var i = Math.max(0, that.number - 3); i < Math.min(that.totalPages, that.number + 3); i++) {
                that.npage.push(i)
            }
        })
    },
    methods: {
        get_page(page) {
            if (page >= this.totalPages || page < 0) return;
            var that = this;
            url = "/api/contest/status/" + cid + "?page=" + page;
            axios.get(url).then(function (res) {
                res = res.data;
                that.solutions = res.content;
                that.totalPages = res.totalPages;
                that.totalElements = res.totalElements;
                that.last = res.last;
                that.number = res.number;
                that.size = res.size;
                that.first = res.first;
                that.numberOfElements = res.numberOfElements;
                that.empyt = res.empty;
                that.npage = [];
                for (var i = Math.max(0, that.number - 3); i < Math.min(that.totalPages, that.number + 3); i++) {
                    that.npage.push(i)
                }
            })
        }
    }
})