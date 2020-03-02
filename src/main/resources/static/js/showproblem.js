var code_editor;
$(function () {
    code_editor = editormd("code-editor", {
        width: "100%",
        gfm: true,
        height: 300,
        watch: false,
        toolbar: false,
        codeFold: true,
        searchReplace: true,
        autoFocus: false,
        placeholder: "Enjoy coding!",
        editorTheme: "mdn-like",
        previewTheme: "dark",
        theam: "dark",
        mode: "clike",
        path: '/editor/lib/',
        pluginPath: "/editor/plugins/"
    });
});
var prom = new Vue({
    el: '#vue-problem',
    data: {
        problem: {},
        code: "",
        language: "c",
        share: true,
        dataready: false,
        isAccepted: false,
        ready: false,
        tags: [],
        color: ["red", "blue", "green", "orange", "yellow", "pink", "brown", "purple", "olive", "teal"],
        status: [],
        problem_id: pid
    },
    methods: {
        get_history_data() {
            var that = this;
            axios.get('/api/status/user/latest/submit/' + pid).then(function (res) {
                that.status = res.data;
            })
        },
        colorClass() {
            return this.color[Math.floor(Math.random() * 10)]
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
        submit: function () {
            this.code = code_editor.getMarkdown();
            var that = this;
            axios.post('/api/problems/submit/' + pid, {
                language: that.language,
                source: that.code,
                share: that.share
            }).then(function (res) {
                console.log(res.data)
                if (res.data.code != 200) {
                    alert(res.data.message);
                    return;
                } else {
                    vue_history.get_data();
                    scrollTo(0, 0);//x,y
                }
            })
        }
    },
    created() {
        var that = this;
        axios.get('/api/problems/' + pid).then(function (response) {
            if (response.data.code != 200) {
                return;
            }

            that.problem = response.data.data;
            that.tags = that.problem.tags;
            that.ready = true;
            $("title").text(response.data.data.title);
            $(function () {
                $(".md-text").each(function () {
                    $(this).attr("id", "editormd-view");
                    editormd.markdownToHTML("editormd-view", {
                        gfm: true,
                        htmlDecode: "style,script,iframe|on*",
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
                    });
                    $(this).attr("id", "");
                });
            });
            that.dataready = true;
        }).catch(function (e) {
            console.log(e);
            location.href = "/4O4";
        });
        axios.get("/api/problems/is/accepted/" + pid)
            .then(function (res) {
                if (res.data.message == 'success') {
                    that.isAccepted = res.data.data;
                } else {
                    console.log(res.data);
                }
            }).catch(function (e) {
            console.log(e);
        });
        this.get_history_data();
    }
});