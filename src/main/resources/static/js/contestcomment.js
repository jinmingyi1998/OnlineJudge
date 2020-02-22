var mdeditor;
$(function () {
    mdeditor = editormd("comment-editor", {
        path: "/editor/lib/",
        width: "100%",
        height: 340,
        gfm: true,
        toolbar: false,
        toc: false,
        tocm: false,
        autoLoadKaTeX: true,
        pageBreak: true,
        atLink: true,    // for @link
        emailLink: false,    // for mail address auto link
        tex: true,
        taskList: false,   // Github Flavored Markdown task lists
        flowChart: false,
        sequenceDiagram: true,
        previewCodeHighlight: true,
        onchange: function () {
            vue_comment.change();
        }
    });
});
var cid = location.pathname.split('/');
cid = cid[cid.length - 1];

function render_md() {
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
                previewCodeHighlight: true
            });
            $(this).attr("id", "");
        });
    });
}

vue_comment = new Vue({
    el: "#vue-comment",
    data: {
        comments: [],
        contest: {id: cid},
        reply_text: "",
        reply_id: ""
    },
    methods: {
        change() {
            this.reply_text = mdeditor.getMarkdown();
            if (this.reply_text.length < 2) this.reply_id = 0;
        },
        replya(id) {
            this.reply_id = id;
            var name = "";
            for (c of this.comments) {
                if (c.id == id) {
                    name = c.user.name;
                    break;
                }
            }
            this.reply_text = "@" + name + " : ";
            mdeditor.setMarkdown(this.reply_text);
        },
        post_comment() {
            var that = this;
            this.reply_text = mdeditor.getMarkdown();
            var url = "/api/contest/comments/post/" + cid;
            axios.post(url, {replyText: that.reply_text, replyId: that.reply_id}).then(function (res) {
                if (res.data != "success") {
                    alert(res.data);
                    return;
                }
                location.reload();
            }).catch(function (err) {
                console.error(err);
            });
        },
        get_sons(id) {
            var sons = [];
            for (c of this.comments) {
                if (c.fatherId == id) {
                    sons.push(c);
                }
            }
            return sons;
        }
    },
    created: function () {
        var that = this;
        url = '/api/contest/comments/' + cid;
        axios.get(url)
            .then(function (res) {
                that.comments = res.data;
                for (c of that.comments) {
                    c.reply_action = that.replya;
                    c.sons = that.get_sons(c.id);
                }
                render_md();
            });
    }
})