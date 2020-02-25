$(function () {
    $("body").on("click", ".view-code", function () {
        var id = $(this).attr("id");
        $.get(
            {
                url: "/api/status/view/" + id,
                success: function (data) {
                    var solution = data;
                    $("#modal-id").text(solution.id);
                    $("#modal-ce").text(solution.info);
                    $("#modal-username").text(solution.user.username);
                    $("#modal-problem").text(solution.problem['id']);
                    $("#modal-result").text(solution.normalResult);
                    $("#modal-language").text(solution.normalLanguage);
                    $("#modal-submit-time").text(solution.normalSubmitTime);
                    $("#modal-memory").text(solution.memory);
                    $("#modal-length").text(solution.length);
                    $("#modal-time").text(solution.time);
                    $("#source_code").text(solution.source);
                    PR.prettyPrint();
                    $("#codemodal").modal('show');
                    if (solution.share) {
                        $("#modal-share").text("Sharing");
                        $("#modal-share").attr("class", "ui button green");
                    } else {
                        $("#modal-share").text("Not Shared");
                        $("#modal-share").attr("class", "ui red button");
                    }
                }
            }
        );
    });
    $("#modal-share").click(function () {
        $.post({
            url: "/api/status/share/" + $("#modal-id").text(),
            success: function (data) {
                if (data == true) {
                    $("#modal-share").text("Sharing");
                    $("#modal-share").attr("class", "ui button green");
                } else {
                    $("#modal-share").text("Not Shared");
                    $("#modal-share").attr("class", "ui button red");
                }
            }
        });
    });
});