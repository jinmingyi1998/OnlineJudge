var TurndownService = function () {
    "use strict";

    function t(t, e) {
        return Array(e + 1).join(t)
    }

    var e = ["address", "article", "aside", "audio", "blockquote", "body", "canvas", "center", "dd", "dir", "div", "dl", "dt", "fieldset", "figcaption", "figure", "footer", "form", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "header", "hgroup", "hr", "html", "isindex", "li", "main", "menu", "nav", "noframes", "noscript", "ol", "output", "p", "pre", "section", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "ul"];

    function r(t) {
        return -1 !== e.indexOf(t.nodeName.toLowerCase())
    }

    var n = ["area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"];

    function i(t) {
        return -1 !== n.indexOf(t.nodeName.toLowerCase())
    }

    var o = n.join();
    var s = {};

    function a(t) {
        for (var e in this.options = t,
            this._keep = [],
            this._remove = [],
            this.blankRule = {
                replacement: t.blankReplacement
            },
            this.keepReplacement = t.keepReplacement,
            this.defaultRule = {
                replacement: t.defaultReplacement
            },
            this.array = [],
            t.rules)
            this.array.push(t.rules[e])
    }

    function l(t, e, r) {
        for (var n = 0; n < t.length; n++) {
            var i = t[n];
            if (u(i, e, r))
                return i
        }
    }

    function u(t, e, r) {
        var n = t.filter;
        if ("string" == typeof n) {
            if (n === e.nodeName.toLowerCase())
                return !0
        } else if (Array.isArray(n)) {
            if (n.indexOf(e.nodeName.toLowerCase()) > -1)
                return !0
        } else {
            if ("function" != typeof n)
                throw new TypeError("`filter` needs to be a string, array, or function");
            if (n.call(t, e, r))
                return !0
        }
    }

    function c(t) {
        var e = t.nextSibling || t.parentNode;
        return t.parentNode.removeChild(t),
            e
    }

    function h(t, e, r) {
        return t && t.parentNode === e || r(e) ? e.nextSibling || e.parentNode : e.firstChild || e.nextSibling || e.parentNode
    }

    s.paragraph = {
        filter: "p",
        replacement: function (t) {
            return "\n\n" + t + "\n\n"
        }
    },
        s.lineBreak = {
            filter: "br",
            replacement: function (t, e, r) {
                return r.br + "\n"
            }
        },
        s.heading = {
            filter: ["h1", "h2", "h3", "h4", "h5", "h6"],
            replacement: function (e, r, n) {
                var i = Number(r.nodeName.charAt(1));
                return "setext" === n.headingStyle && i < 3 ? "\n\n" + e + "\n" + t(1 === i ? "=" : "-", e.length) + "\n\n" : "\n\n" + t("#", i) + " " + e + "\n\n"
            }
        },
        s.blockquote = {
            filter: "blockquote",
            replacement: function (t) {
                return "\n\n" + (t = (t = t.replace(/^\n+|\n+$/g, "")).replace(/^/gm, "> ")) + "\n\n"
            }
        },
        s.list = {
            filter: ["ul", "ol"],
            replacement: function (t, e) {
                var r = e.parentNode;
                return "LI" === r.nodeName && r.lastElementChild === e ? "\n" + t : "\n\n" + t + "\n\n"
            }
        },
        s.listItem = {
            filter: "li",
            replacement: function (t, e, r) {
                t = t.replace(/^\n+/, "").replace(/\n+$/, "\n").replace(/\n/gm, "\n    ");
                var n = r.bulletListMarker + "   "
                    , i = e.parentNode;
                if ("OL" === i.nodeName) {
                    var o = i.getAttribute("start")
                        , s = Array.prototype.indexOf.call(i.children, e);
                    n = (o ? Number(o) + s : s + 1) + ".  "
                }
                return n + t + (e.nextSibling && !/\n$/.test(t) ? "\n" : "")
            }
        },
        s.indentedCodeBlock = {
            filter: function (t, e) {
                return "indented" === e.codeBlockStyle && "PRE" === t.nodeName && t.firstChild && "CODE" === t.firstChild.nodeName
            },
            replacement: function (t, e, r) {
                return "\n\n    " + e.firstChild.textContent.replace(/\n/g, "\n    ") + "\n\n"
            }
        },
        s.fencedCodeBlock = {
            filter: function (t, e) {
                return "fenced" === e.codeBlockStyle && "PRE" === t.nodeName && t.firstChild && "CODE" === t.firstChild.nodeName
            },
            replacement: function (t, e, r) {
                var n = ((e.firstChild.className || "").match(/language-(\S+)/) || [null, ""])[1];
                return "\n\n" + r.fence + n + "\n" + e.firstChild.textContent + "\n" + r.fence + "\n\n"
            }
        },
        s.horizontalRule = {
            filter: "hr",
            replacement: function (t, e, r) {
                return "\n\n" + r.hr + "\n\n"
            }
        },
        s.inlineLink = {
            filter: function (t, e) {
                return "inlined" === e.linkStyle && "A" === t.nodeName && t.getAttribute("href")
            },
            replacement: function (t, e) {
                return "[" + t + "](" + e.getAttribute("href") + (e.title ? ' "' + e.title + '"' : "") + ")"
            }
        },
        s.referenceLink = {
            filter: function (t, e) {
                return "referenced" === e.linkStyle && "A" === t.nodeName && t.getAttribute("href")
            },
            replacement: function (t, e, r) {
                var n, i, o = e.getAttribute("href"), s = e.title ? ' "' + e.title + '"' : "";
                switch (r.linkReferenceStyle) {
                    case "collapsed":
                        n = "[" + t + "][]",
                            i = "[" + t + "]: " + o + s;
                        break;
                    case "shortcut":
                        n = "[" + t + "]",
                            i = "[" + t + "]: " + o + s;
                        break;
                    default:
                        var a = this.references.length + 1;
                        n = "[" + t + "][" + a + "]",
                            i = "[" + a + "]: " + o + s
                }
                return this.references.push(i),
                    n
            },
            references: [],
            append: function (t) {
                var e = "";
                return this.references.length && (e = "\n\n" + this.references.join("\n") + "\n\n",
                    this.references = []),
                    e
            }
        },
        s.emphasis = {
            filter: ["em", "i"],
            replacement: function (t, e, r) {
                return t.trim() ? r.emDelimiter + t + r.emDelimiter : ""
            }
        },
        s.strong = {
            filter: ["strong", "b"],
            replacement: function (t, e, r) {
                return t.trim() ? r.strongDelimiter + t + r.strongDelimiter : ""
            }
        },
        s.code = {
            filter: function (t) {
                var e = t.previousSibling || t.nextSibling
                    , r = "PRE" === t.parentNode.nodeName && !e;
                return "CODE" === t.nodeName && !r
            },
            replacement: function (t) {
                if (!t.trim())
                    return "";
                var e = "`"
                    , r = ""
                    , n = ""
                    , i = t.match(/`+/gm);
                if (i)
                    for (/^`/.test(t) && (r = " "),
                         /`$/.test(t) && (n = " "); -1 !== i.indexOf(e);)
                        e += "`";
                return e + r + t + n + e
            }
        },
        s.image = {
            filter: "img",
            replacement: function (t, e) {
                var r = e.alt || ""
                    , n = e.getAttribute("src") || ""
                    , i = e.title || "";
                return n ? "![" + r + "](" + n + (i ? ' "' + i + '"' : "") + ")" : ""
            }
        },
        a.prototype = {
            add: function (t, e) {
                this.array.unshift(e)
            },
            keep: function (t) {
                this._keep.unshift({
                    filter: t,
                    replacement: this.keepReplacement
                })
            },
            remove: function (t) {
                this._remove.unshift({
                    filter: t,
                    replacement: function () {
                        return ""
                    }
                })
            },
            forNode: function (t) {
                return t.isBlank ? this.blankRule : (e = l(this.array, t, this.options)) ? e : (e = l(this._keep, t, this.options)) ? e : (e = l(this._remove, t, this.options)) ? e : this.defaultRule;
                var e
            },
            forEach: function (t) {
                for (var e = 0; e < this.array.length; e++)
                    t(this.array[e], e)
            }
        };
    var f = "undefined" != typeof window ? window : {};
    var p, d, g = function () {
        var t = f.DOMParser
            , e = !1;
        try {
            (new t).parseFromString("", "text/html") && (e = !0)
        } catch (r) {
        }
        return e
    }() ? f.DOMParser : (p = function () {
    }
        ,
        function () {
            var t = !1;
            try {
                document.implementation.createHTMLDocument("").open()
            } catch (e) {
                window.ActiveXObject && (t = !0)
            }
            return t
        }() ? p.prototype.parseFromString = function (t) {
                var e = new window.ActiveXObject("htmlfile");
                return e.designMode = "on",
                    e.open(),
                    e.write(t),
                    e.close(),
                    e
            }
            : p.prototype.parseFromString = function (t) {
                var e = document.implementation.createHTMLDocument("");
                return e.open(),
                    e.write(t),
                    e.close(),
                    e
            }
        ,
        p);

    function m(t) {
        var e;
        "string" == typeof t ? e = (d = d || new g).parseFromString('<x-turndown id="turndown-root">' + t + "</x-turndown>", "text/html").getElementById("turndown-root") : e = t.cloneNode(!0);
        return function (t) {
            var e = t.element
                , r = t.isBlock
                , n = t.isVoid
                , i = t.isPre || function (t) {
                    return "PRE" === t.nodeName
                }
            ;
            if (e.firstChild && !i(e)) {
                for (var o = null, s = !1, a = null, l = h(a, e, i); l !== e;) {
                    if (3 === l.nodeType || 4 === l.nodeType) {
                        var u = l.data.replace(/[ \r\n\t]+/g, " ");
                        if (o && !/ $/.test(o.data) || s || " " !== u[0] || (u = u.substr(1)),
                            !u) {
                            l = c(l);
                            continue
                        }
                        l.data = u,
                            o = l
                    } else {
                        if (1 !== l.nodeType) {
                            l = c(l);
                            continue
                        }
                        r(l) || "BR" === l.nodeName ? (o && (o.data = o.data.replace(/ $/, "")),
                            o = null,
                            s = !1) : n(l) && (o = null,
                            s = !0)
                    }
                    var f = h(a, l, i);
                    a = l,
                        l = f
                }
                o && (o.data = o.data.replace(/ $/, ""),
                o.data || c(o))
            }
        }({
            element: e,
            isBlock: r,
            isVoid: i
        }),
            e
    }

    function v(t) {
        return t.isBlock = r(t),
            t.isCode = "code" === t.nodeName.toLowerCase() || t.parentNode.isCode,
            t.isBlank = function (t) {
                return -1 === ["A", "TH", "TD"].indexOf(t.nodeName) && /^\s*$/i.test(t.textContent) && !i(t) && !function (t) {
                    return t.querySelector && t.querySelector(o)
                }(t)
            }(t),
            t.flankingWhitespace = function (t) {
                var e = ""
                    , r = "";
                if (!t.isBlock) {
                    var n = /^[ \r\n\t]/.test(t.textContent)
                        , i = /[ \r\n\t]$/.test(t.textContent);
                    n && !y("left", t) && (e = " "),
                    i && !y("right", t) && (r = " ")
                }
                return {
                    leading: e,
                    trailing: r
                }
            }(t),
            t
    }

    function y(t, e) {
        var n, i, o;
        return "left" === t ? (n = e.previousSibling,
            i = / $/) : (n = e.nextSibling,
            i = /^ /),
        n && (3 === n.nodeType ? o = i.test(n.nodeValue) : 1 !== n.nodeType || r(n) || (o = i.test(n.textContent))),
            o
    }

    var b = Array.prototype.reduce
        , w = /^\n*/
        , _ = /\n*$/;

    function x(t) {
        if (!(this instanceof x))
            return new x(t);
        var e = {
            rules: s,
            headingStyle: "setext",
            hr: "* * *",
            bulletListMarker: "*",
            codeBlockStyle: "indented",
            fence: "```",
            emDelimiter: "_",
            strongDelimiter: "**",
            linkStyle: "inlined",
            linkReferenceStyle: "full",
            br: "  ",
            blankReplacement: function (t, e) {
                return e.isBlock ? "\n\n" : ""
            },
            keepReplacement: function (t, e) {
                return e.isBlock ? "\n\n" + e.outerHTML + "\n\n" : e.outerHTML
            },
            defaultReplacement: function (t, e) {
                return e.isBlock ? "\n\n" + t + "\n\n" : t
            }
        };
        this.options = function (t) {
            for (var e = 1; e < arguments.length; e++) {
                var r = arguments[e];
                for (var n in r)
                    r.hasOwnProperty(n) && (t[n] = r[n])
            }
            return t
        }({}, e, t),
            this.rules = new a(this.options)
    }

    function S(t) {
        var e = this;
        return b.call(t.childNodes, (function (t, r) {
                var n = "";
                return 3 === (r = new v(r)).nodeType ? n = r.isCode ? r.nodeValue : e.escape(r.nodeValue) : 1 === r.nodeType && (n = k.call(e, r)),
                    A(t, n)
            }
        ), "")
    }

    function C(t) {
        var e = this;
        return this.rules.forEach((function (r) {
                "function" == typeof r.append && (t = A(t, r.append(e.options)))
            }
        )),
            t.replace(/^[\t\r\n]+/, "").replace(/[\t\r\n\s]+$/, "")
    }

    function k(t) {
        var e = this.rules.forNode(t)
            , r = S.call(this, t)
            , n = t.flankingWhitespace;
        return (n.leading || n.trailing) && (r = r.trim()),
        n.leading + e.replacement(r, t, this.options) + n.trailing
    }

    function A(t, e) {
        var r, n, i, o = (r = e,
            n = [t.match(_)[0], r.match(w)[0]].sort(),
            (i = n[n.length - 1]).length < 2 ? i : "\n\n");
        return (t = t.replace(_, "")) + o + (e = e.replace(w, ""))
    }

    return x.prototype = {
        turndown: function (t) {
            if (!function (t) {
                return null != t && ("string" == typeof t || t.nodeType && (1 === t.nodeType || 9 === t.nodeType || 11 === t.nodeType))
            }(t))
                throw new TypeError(t + " is not a string, or an element/document/fragment node.");
            if ("" === t)
                return "";
            var e = S.call(this, new m(t));
            return C.call(this, e)
        },
        use: function (t) {
            if (Array.isArray(t))
                for (var e = 0; e < t.length; e++)
                    this.use(t[e]);
            else {
                if ("function" != typeof t)
                    throw new TypeError("plugin must be a Function or an Array of Functions");
                t(this)
            }
            return this
        },
        addRule: function (t, e) {
            return this.rules.add(t, e),
                this
        },
        keep: function (t) {
            return this.rules.keep(t),
                this
        },
        remove: function (t) {
            return this.rules.remove(t),
                this
        },
        escape: function (t) {
            return t.replace(/\\(\S)/g, "\\\\$1").replace(/^(#{1,6} )/gm, "\\$1").replace(/^([-*_] *){3,}$/gm, (function (t, e) {
                    return t.split(e).join("\\" + e)
                }
            )).replace(/^(\W* {0,3})(\d+)\. /gm, "$1$2\\. ").replace(/^([^\\\w]*)[*+-] /gm, (function (t) {
                    return t.replace(/([*+-])/g, "\\$1")
                }
            )).replace(/^(\W* {0,3})> /gm, "$1\\> ").replace(/\*+(?![*\s\W]).+?\*+/g, (function (t) {
                    return t.replace(/\*/g, "\\*")
                }
            )).replace(/_+(?![_\s\W]).+?_+/g, (function (t) {
                    return t.replace(/_/g, "\\_")
                }
            )).replace(/`+(?![`\s\W]).+?`+/g, (function (t) {
                    return t.replace(/`/g, "\\`")
                }
            )).replace(/[\[\]]/g, "\\$&")
        }
    },
        x
}();
