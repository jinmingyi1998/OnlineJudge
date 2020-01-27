Array.prototype.remove = function (from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};
var problems_list = new Vue({
    el: "#problem-list",
    data: {
        ready: false,
        problems: [],
        tags: [],
        totpage: 0,
        number: 0,
        size: 0,
        totelement: 0,
        first: false,
        last: false,
        npage: [],
        search_string: "",
        search_tag: [],
        color: ["red", "blue", "green", "orange", "yellow", "pink", "brown", "purple", "olive", "teal"]
    },
    methods: {
        search() {
            let ss = this.search_string;
            if (this.search_tag.length > 0)
                ss += "$$" + this.search_tag.toString();
            return ss;
        },
        colorClass(id) {
            try {
                return this.color[parseInt(id) % 10]
            } catch (e) {
                return this.color[Math.floor(Math.random() * 10)]
            }
        },
        add_search_tag(event) {
            ss = event.target.innerText.trim();
            if (this.search_tag.indexOf(ss) === -1)
                this.search_tag.push(ss);
            this.get_page(0);
        },
        remove_search_tag(event) {
            ss = event.target.parentNode.innerText.trim();
            if (this.search_tag.indexOf(ss) === -1) {
                return;
            }
            this.search_tag.remove(this.search_tag.indexOf(ss));
            this.get_page(0);
        },
        search_problem: function () {
            this.get_page(0);
        },
        get_page: function (to_page) {
            var that = this;
            if (to_page !== 0 && (to_page < 0 || to_page >= that.totpage))
                return null;
            url = "/api/problems?page=" + to_page + "&search=" + this.search();
            axios.get(url)
                .then(function (response) {
                    response = response.data;
                    that.problems = response.content;
                    that.totelement = response.totalElements;
                    that.size = response.size;
                    that.totpage = response.totalPages;
                    that.last = response.last;
                    that.first = response.first;
                    that.number = response.number;
                    that.npage = [];
                    for (var i = Math.max(0, that.number - 3); i < Math.min(that.totpage, that.number + 3); i++) {
                        that.npage.push(i)
                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    },
    created() {
        var that = this;
        axios.get('/api/problems')
            .then(function (response) {
                response = response.data
                that.problems = response.content;
                that.totelement = response.totalElements;
                that.size = response.size;
                that.totpage = response.totalPages;
                that.last = response.last;
                that.first = response.first;
                that.number = response.number;
                for (var i = Math.max(0, that.number - 3); i < Math.min(that.totpage, that.number + 3); i++) {
                    that.npage.push(i)
                }
            })
            .catch(function (error) {
                console.log(error);
            });
        axios.get("/api/problems/tags").then(function (response) {
            that.tags = response.data;
        });
        that.ready = true;
    }
});