var que = location.search;
var query = Object();
query.page = 0;
$(function () {
    if (que.indexOf("?") != -1) {
        var str = que.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            query[strs[i].split("=")[0]] = (strs[i].split("=")[1]);
        }
    }
});