var code_editor;
$(function () {
    code_editor = editormd("code-editor", {
        width: "100%",
        gfm: true,
        height: 500,
        watch: false,
        toolbar: false,
        codeFold: true,
        searchReplace: true,
        autoFocus: false,
        placeholder: "Enjoy coding!",
        editorTheme: "tomorrow-night-bright",
        previewTheme: "dark",
        theam: "dark",
        mode: "clike",
        path: '/editor/lib/',
    });
});
vue_history = new Vue({
    el: "#vue-history",
    data: {
        status: [],
        problem_id: pid
    },
    methods: {
        get_data() {
            var that = this;
            axios.get('/api/status/user/latest/submit/' + pid).then(function (res) {
                that.status = res.data;
            })
        }
    },
    created: function () {
        var that = this;
        axios.get('/api/status/user/latest/submit/' + pid).then(function (res) {
            that.status = res.data;
        })
    }
});
var tags = new Vue({
    el: "#vue-tags",
    data: {
        ready: false,
        tags: [],
        color: ["red", "blue", "green", "orange", "yellow", "pink", "brown", "purple", "olive", "teal"]
    },
    methods: {
        colorClass() {
            return this.color[Math.floor(Math.random() * 10)]
        }
    }
});
var prom = new Vue({
    el: '#vue-problem',
    data: {
        problem: {},
        code: "",
        language: "c",
        share: true,
        dataready: false
    },
    methods: {
        change_lang() {
            if (this.language === "java") {
                code_editor.setCodeMirrorOption("mode", "clike");
            } else if (this.language.indexOf("py") === 0) {
                code_editor.setCodeMirrorOption("mode", "python");
            } else if (this.language.indexOf("c") === 0) {
                code_editor.setCodeMirrorOption("mode", "clike");
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
                if (res.data != "success") {
                    alert(res.data);
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
            that.problem = response.data;
            tags.tags = that.problem.tags;
            tags.ready = true;
            $("title").text(response.data.title);
            $(function () {
                $(".md-text").each(function () {
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
                    });
                    $(this).attr("id", "");
                });
            });
            that.dataready = true;
        }).catch(function (e) {
            console.log(e);
            location.href = "/4O4";
        })
    }
});