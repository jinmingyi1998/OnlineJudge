/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

var cid;
$(function () {
    cid = $("main").attr("id");
    changeProblem($("main").attr("id"), $(".change-problem").first().attr("id"));
    $(".change-problem").first().addClass("active");
    $(".change-problem").click(function () {
        changeProblem(cid, $(this).attr("id"))
    });
    $("#refreshComment").click(getComments());
    $(function () {
        $("body").on('click', '.view-code', function () {
            var id = $(this).attr("id");
            $.post(
                {
                    url: "/oj/contest/status/view/" + id,
                    success: function (data) {
                        var solution = data;
                        $(".prettyprint").attr("class", "prettyprint");
                        $("#modal-id").text(solution.id);
                        try {
                            $("#modal-ce").text(solution.ce.info);
                        } catch (e) {
                        }
                        $("#modal-username").text(solution.user.username);
                        $("#modal-problem").text(solution.problem['id']);
                        $("#modal-result").text(solution.normalResult);
                        if (solution.result == "Accepted") {
                            $("#modal-result").attr("class", "text-success font-weight-bold");
                        } else {
                            $("#modal-result").attr("class", "text-danger");
                        }
                        $("#modal-language").text(solution.normalLanguage);
                        $("#modal-submit-time").text(solution.normalSubmitTime);
                        $("#modal-memory").text(solution.memory);
                        $("#modal-length").text(solution.length);
                        $("#modal-time").text(solution.time);
                        $("#source_code").text(solution.source);
                        PR.prettyPrint();
                        $("#codeModal").modal('show');
                        if (solution.share) {
                            $("#modal-share").text("Sharing");
                            $("#modal-share").attr("class", "btn btn-sm btn-success");
                        } else {
                            $("#modal-share").text("Not Shared");
                            $("#modal-share").attr("class", "btn btn-sm btn-danger");
                        }
                    }
                }
            );
        });
        $("#modal-share").click(function () {
            $.post({
                url: "/oj/status/share/" + $("#modal-id").text(),
                success: function (data) {
                    if (data == true) {
                        $("#modal-share").text("Sharing");
                        $("#modal-share").attr("class", "btn btn-sm btn-success");
                    } else {
                        $("#modal-share").text("Not Shared");
                        $("#modal-share").attr("class", "btn btn-sm btn-danger");
                    }
                }
            });
        });
    });
    getStatusOfMe();
    getRankOfContest();
    getComments();
});

function getStatusOfMe() {
    cid = $("main").attr("id");
    $.get({
        url: "/oj/contest/api/status/" + cid,
        success: function (data) {
            ve._data.status = data;
        }
    });
}

function getRankOfContest() {
    $.get(
        {
            url: "/oj/contest/api/rank/" + cid,
            success: function (rank) {
                peo = rank.people;
                psize = $("#problem-number").text();
                rankbody = $("#rank-tbody");
                rankbody.empty();
                peo.forEach(function (e) {
                    var html_str = "";
                    var plist = [];
                    html_str += "<tr><td>" + e.user.name + "</td>";
                    html_str += "<td>" + e.penalty + "</td>";
                    html_str += "<td>" + e.ac + "</td>";
                    e.problems.forEach(function (pp) {
                        plist[pp.pid] = pp;
                    });
                    for (var i = 1; i <= psize; i++) {
                        if (typeof (plist[i]) == "undefined") {
                            html_str += "<td></td>";
                            continue;
                        }
                        html_str += "<td ";
                        var str = " ";
                        if (plist[i].firstblood == true)
                            str += "class='bg-success'";
                        if (plist[i].ac == true) {
                            str += ">" + plist[i].duration + "(" + (parseInt(plist[i].wa) + 1) + ")";
                        } else {
                            str += "class='bg-danger'>(" + plist[i].wa + ")";
                        }
                        html_str += str + "</td>";
                    }
                    html_str += "</tr>";
                    rankbody.append(html_str);
                });
            }
        }
    );
}

function changeProblem(cid, pid) {
    $("#problem-container").show();
    var problem = Object();
    $.get({
        url: "/oj/contest/api/" + cid + "/problem/" + pid,
        success: function (problem) {
            $("#problem-title").text(problem.title);
            $("#problem-time-limit").text(problem.timeLimit + "ms");
            $("#problem-memory-limit").text(problem.memoryLimit + "Bytes");
            $("#problem-description").empty();
            $("#problem-description").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-description").children("textarea").text(problem.description);
            $("#problem-input").empty();
            $("#problem-input").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-input").children("textarea").text(problem.input);
            $("#problem-output").empty();
            $("#problem-output").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-output").children("textarea").text(problem.output);
            $("#problem-sample-input").text(problem.sampleInput);
            $("#problem-sample-output").text(problem.sampleOutput);
            $("#problem-hint").empty();
            $("#problem-hint").append(" <textarea style=\"display: none;\"></textarea>");
            $("#problem-hint").children("textarea").text(problem.hint);
            $("#submit_btn").attr("problem-id", pid);
            $(".md-problem").each(function () {
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
                    emailLink: true,    // for mail address auto link
                    tex: true,
                    taskList: true,   // Github Flavored Markdown task lists
                    flowChart: true,
                    sequenceDiagram: true,
                    previewCodeHighlight: true
                });
                $(this).attr("id", tid);
            });
        }
    });
}

function getComments() {
    $.get({
        url: "/oj/contest/api/" + cid + "/comments",
        success: function (res) {
            $("#comments").empty();
            for (var i = 0; i < res.length; i++) {
                $("#comments").append("<li  class=\"list-group-item\" >" +
                    "<a href=\"/user/" + res[i].user.id + "\">" + res[i].user.name + "</a>&nbsp; @ &nbsp;<span>" + res[i].normalPostTime
                    + "</span>" +
                    "<div class=\"md-comment\">" +
                    "<textarea style=\"display: none;\">" + res[i].text + "</textarea>" +
                    "</div>" +
                    "</li>")
            }
            $(".md-comment").each(function () {
                $(this).attr("id", "editormd-view");
                editormd.markdownToHTML("editormd-view", {
                    toc: true,
                    autoLoadKaTeX: true,
                    tex: true,
                    flowChart: true,
                    taskList: true,
                    sequenceDiagram: true,
                    previewCodeHighlight: true
                });
                $(this).attr("id", "");
            });
        }
    });
}