function render_md() {
    $(function () {
        $(".md-text").each(function () {
            var tid = $(this).attr("id");
            $(this).attr("id", "editormd-view");
            editormd.markdownToHTML("editormd-view", {
                gfm: true,
                toc: true,
                tocm: false,
                tocStartLevel: 1,
                tocTitle: "目录",
                tocDropdown: false,
                tocContainer: "",
                markdown: "",
                autoLoadKaTeX: true,
                pageBreak: true,
                atLink: true,    // for @link
                emailLink: false,    // for mail address auto link
                tex: true,
                taskList: false,   // Github Flavored Markdown task lists
                flowChart: true,
                sequenceDiagram: true,
                previewCodeHighlight: true
            });
            $(this).attr("id", tid);
        });
    });
}

var cont = new Vue({
    el: "#contest-content",
    data: {
        password: "",
        attend: false,
        dataready: false,
        contest: {},
        pid: 1,
        problem: {
            problem: {
                timeLimit: "",
                memoryLimit: "",
                input: "",
                output: "",
                sampleInput: "",
                sampleOutput: "",
                hint: "",
            },
            tempTitle: ""
        },
        language: "c",
        share: false,
        code: ""
    },
    methods: {
        change_problem(id) {
            if (id ===this.pid)return;
            this.pid = id;
            this.dataready = false;
            for (var i = 0; i < this.contest.problems.length; i++) {
                if (this.contest.problems[i].tempId == this.pid) {
                    this.problem = this.contest.problems[i];
                    break;
                }
            }
            $("#problem-description").empty();
            $("#problem-description").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-description").children("textarea").text(this.problem.problem.description);
            $("#problem-input").empty();
            $("#problem-input").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-input").children("textarea").text(this.problem.problem.input);
            $("#problem-output").empty();
            $("#problem-output").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-output").children("textarea").text(this.problem.problem.output);
            $("#problem-hint").empty();
            $("#problem-hint").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-hint").children("textarea").text(this.problem.problem.hint);
            render_md()
            this.dataready = true;
        },
        submit() {
            var that = this;
            axios.post('/api/contest/' + cid + '/submit/' + this.pid, {
                language: that.language,
                source: that.code,
                share: that.share
            }).then(function (res) {
                console.log(res.data)
                if (res.data != "success") {
                    alert(res.data);
                } else {
                    scrollTo(0, 0);//x,y
                }
            })
        },
        check_password() {
            var that = this;
            that.dataready = false;
            // this.attend=true;
            axios.get('/api/contest/' + cid + '?password=' + this.password)
                .then(function (response) {
                    that.contest = response.data;
                    if (that.contest.password != "password") {
                        that.attend = true;
                        that.problem = that.contest.problems[0]
                        that.pid = that.problem.tempId;
                        render_md();
                        that.dataready = true;
                    } else {
                        that.attend = false;
                    }
                });
        }
    },
    created() {
        var that = this;
        that.dataready = false;
        // this.attend=true;
        axios.get('/api/contest/' + cid)
            .then(function (response) {
                that.contest = response.data;
                if (that.contest.password != "password") {
                    that.attend = true;
                    that.problem = that.contest.problems[0]
                    that.pid = that.problem.tempId;
                    render_md();
                    $('title').text(that.contest.title);
                    that.dataready = true;
                } else {
                    that.attend = false;
                }
            });
    }
});