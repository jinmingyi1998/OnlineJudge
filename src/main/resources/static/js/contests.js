cont = new Vue({
    el: "#contest-list",
    data: {
        contests: [],
        search_string: "",
        ready: false,
        number: 0,
        first: true,
        last: true,
        totalPages: 1,
        totalElements: 1,
        size: 30,
        empty: false,
        npage: []
    },
    methods: {
        search_contest() {
            this.get_page(0);
        },
        get_page(to_page) {
            if (to_page < 0 || to_page >= this.totalPages) {
                return null;
            }
            this.ready = false;
            var that = this;
            url = '/api/contest?page=' + to_page + '&search=' + this.search_string;
            axios.get(url)
                .then(function (response) {
                    response = response.data
                    that.contests = response.content;
                    that.totalElements = response.totalElements;
                    that.size = response.size;
                    that.totalPages = response.totalPages;
                    that.last = response.last;
                    that.first = response.first;
                    that.number = response.number;
                    that.empty = response.empty;
                    that.npage = [];
                    for (var i = Math.max(0, that.number - 5); i < Math.min(that.totalPages, that.number + 6); i++) {
                        that.npage.push(i)
                    }
                }).catch(function (error) {
                console.log(error);
            });
            that.ready = true;
        }
    },
    created() {
        var that = this;
        axios.get('/api/contest')
            .then(function (response) {
                response = response.data
                that.contests = response.content;
                that.totalElements = response.totalElements;
                that.size = response.size;
                that.totalPages = response.totalPages;
                that.last = response.last;
                that.first = response.first;
                that.number = response.number;
                that.empty = response.empty;
                that.npage = [];
                for (var i = Math.max(0, that.number - 5); i < Math.min(that.totalPages, that.number + 6); i++) {
                    that.npage.push(i)
                }
            })
            .catch(function (error) {
                console.log(error);
            });
        that.ready = true;
    }
});