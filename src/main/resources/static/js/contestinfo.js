var code_editor;

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
                previewCodeHighlight: true,
                htmlDecode:"style,script,iframe|on*",
            });
            $(this).attr("id", tid);
        });
    });
}

var cont = new Vue({
    el: "#contest-content",
    data: {
        cid:cid,
        password: "",
        attend: false,
        dataready: false,
        contest: {
            problems: []
        },
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
        timeLeft: "",
        percentage: 0,
        code: "",
        creator: false
    },
    methods: {
        init_window() {
            setInterval(this.time_left, 500);
            $(".progress").progress();
            $(function () {
                code_editor = editormd("code-editor", {
                    width: "100%",
                    height: 500,
                    watch: false,
                    toolbar: false,
                    codeFold: true,
                    searchReplace: true,
                    autoFocus: false,
                    value: "",
                    placeholder: "Enjoy coding!",
                    editorTheme: "mdn-like",
                    previewTheme: "dark",
                    theam: "dark",
                    mode: "clike",
                    path: '/editor/lib/',
                });
            });
        },
        time_left() {
            days_of_month = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
            let dd = 0;
            let hh = 0;
            let mm = 0;
            let ss = 0;
            if (this.contest.started && !this.contest.ended) {
                dend = new Date(this.contest.endTime);
                dsta = new Date(this.contest.startTime);
                dn = new Date();
                d3 = new Date(dend - dn);
                ss = d3.getUTCSeconds();
                mm = d3.getUTCMinutes();
                hh = d3.getUTCHours();
                dd = d3.getUTCDate() - 1;
                dd += days_of_month[d3.getUTCMonth()];
                let len = dend - dsta;
                let gon = dn - dsta;
                per = gon / len * 100;
                $("#time-pogress").progress("set percent", per);
            }
            this.timeLeft = dd + (hh < 10 ? ":0" : ":") + hh + (mm < 10 ? ":0" : ":") + mm + (ss < 10 ? ":0" : ":") + ss;
            // setInterval(this.time_left,500);
        },
        change_lang() {
            if (this.language === "java") {
                code_editor.setCodeMirrorOption("mode", "clike");
            } else if (this.language.indexOf("py") === 0) {
                code_editor.setCodeMirrorOption("mode", "python");
            } else if (this.language.indexOf("c") === 0) {
                code_editor.setCodeMirrorOption("mode", "clike");
            } else if (this.language.indexOf("go") === 0) {
                code_editor.setCodeMirrorOption("mode", "go");
            }
        },
        change_problem(id) {
            if (id === this.pid) return;
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
            this.code = code_editor.getMarkdown();
            var that = this;
            axios.post('/api/contest/submit/' + this.pid + "/" + cid, {
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
                        that.init_window();
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
        axios.get('/api/contest/' + cid)
            .then(function (response) {
                that.contest = response.data;
                if (that.contest.password != "password") {
                    that.attend = true;
                    if (that.contest.problems.length > 0) {
                        that.problem = that.contest.problems[0];
                        that.pid = that.problem.tempId;
                    }
                    render_md();
                    $('title').text(that.contest.title);
                    that.init_window();
                    that.dataready = true;
                    axios.get("/api/contest/background/access/" + cid)
                        .then(function (res) {
                            if (res.data == "success") {
                                that.creator = true;
                            }
                        }).catch(function (e) {
                        console.log(e)
                    });
                } else {
                    that.attend = false;
                }
            }).catch(function (e) {
            if (e.response.status == 404) {
                location.href = "/contest";
            }
            console.log(e);
        });
    }
});