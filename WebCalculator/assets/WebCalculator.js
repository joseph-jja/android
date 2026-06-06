var WebCalculator = (function() {
    var self;
    self = {
        touchEnabled : false,
        onLoadEventStack : []
    };
    if ("ontouchstart" in document.documentElement
            || "touchstart" in document.documentElement) {
        self.touchEnabled = true
    }
    return self
})();

WebCalculator.string = {
    removeDoubleSpaces : function(a) {
        var b = /\s\s/g;
        if (a) {
            return a.replace(b, " ")
        } else {
            return a
        }
    },
    ltrim : function(a) {
        if (a) {
            return a.replace(/^\s+/g, "")
        } else {
            return a
        }
    },
    rtrim : function(a) {
        if (a) {
            return a.replace(/\s+$/g, "")
        } else {
            return a
        }
    },
    trim : function(b) {
        var a = WebCalculator.string;
        return a.ltrim(a.rtrim(b))
    }
};

(function(f) {
    var c = f.document, e = f.location, d = f.navigator, b, g;
    g = f.WebCalculator;
    b = g.string.trim;
    var a = (function() {
        var h = function(p, o) {
            return new h.base.select(p, o, l)
        }, l, n = /^([a-z]*)?(#\w+)/, m = /^([a-zA-Z]*)?(\.\w+)/, j = /^[a-zA-Z0-9]+$/, k = /^([a-z]*)(\.\w*)?\s?\,\s?([a-z]*)(\.\w*)?/i, i;
        h.base = h.prototype = {
            constructor : h,
            select : function(v, r, t) {
                var p, s, o, u, q;
                if (!v) {
                    return this
                }
                if (v.nodeType) {
                    this.context = this[0] = v;
                    this.length = 1;
                    return this
                }
                if (v === "body" && !r && c.body) {
                    this.context = c;
                    this[0] = c.body;
                    this.slctr = v;
                    this.length = 1;
                    return this
                }
                if (typeof v === "string") {
                    if (v.charAt(0) === "<" && v.charAt(v.length - 1) === ">"
                            && v.length >= 3) {
                        throw ("This is for selectors only, not creating nodes!")
                    } else {
                        p = n.exec(v)
                    }
                    if (p && (p[1] || !r)) {
                        if (p[1] && !p[2]) {
                            throw ("This is for selectors only, not creating nodes! Maybe you want jQuery instead?")
                        } else {
                            s = c.getElementById(p[2].substring(1));
                            if (s && s.parentNode) {
                                if (s.id !== p[2]) {
                                    return t.find.call(this, v)
                                }
                                this.length = 1;
                                this[0] = s
                            }
                            this.context = c;
                            this.slctr = v;
                            return this
                        }
                    } else {
                        if (!r || r.selector) {
                            return (r || t).find.call(this, v)
                        } else {
                            return this.constructor(r).find(v)
                        }
                    }
                } else {
                    if (typeof v === "function") {
                        throw ("This is for simple selectors only, not function selectors! Maybe you want jQuery instead?")
                    }
                }
                if (v.slctr !== undefined) {
                    this.slctr = v.slctr;
                    this.context = v.context
                }
                q = this || [];
                if (v != null) {
                    if (v.length == null
                            || typeof v === "string"
                            || typeof v === "function"
                            || typeof v === "regexp"
                            || (v && typeof v === "object" && "setInterval" in v)) {
                        push.call(q, v)
                    } else {
                        if (i(q)) {
                            q.concat(v)
                        } else {
                            h.base.copy(q, v)
                        }
                    }
                }
                return q
            },
            slctr : "",
            length : 0,
            get : function(p) {
                var o = 0;
                if (typeof p !== "undefined") {
                    o = p
                }
                return this[p]
            },
            copy : function(p, o) {
                var u, q, s, r;
                u = p.length;
                q = o.length;
                r = u + q;
                for (s = u; s < r; s += 1) {
                    p[s] = o[s - u]
                }
                return p
            },
            idSelector : n,
            classSelector : m,
            elementSelector : j
        };
        h.base.select.prototype = h.base;
        clean = g.string.removeDoubleSpaces;
        i = function(p) {
            var o = Object.prototype.toString;
            return (o.apply(p).toLowerCase().indexOf("collection") !== -1 || o
                    .apply(p).toLowerCase().indexOf("array") !== -1)
        };
        h.base.find = function(o) {
            var q = this.document, p, u, s, r, t, w = Object.prototype.toString, x, v;
            if (typeof o !== "string") {
                throw ("Selector should be of type string.Maybe you want jQuery?")
            }
            v = function(B, A, z) {
                var y;
                y = function(H, F, E) {
                    var D, G, C;
                    G = H.getElementsByTagName(F);
                    H = [];
                    D = G.length;
                    for (C = 0; C < D; C += 1) {
                        if (G[C].className.indexOf(E) !== -1) {
                            H.push(G[C])
                        }
                    }
                    return H
                };
                if (A.substring(0, 1) === "#") {
                    B = B.getElementById(A.substring(1))
                } else {
                    if (A.substring(0, 1) === ".") {
                        if (c.getElementsByClassName) {
                            B = B.getElementsByClassName(A.substring(1))
                        } else {
                            if (c.querySelectorAll) {
                                B = B.querySelectorAll(A)
                            } else {
                                B = y(B, "*", A.substring(1))
                            }
                        }
                    } else {
                        if (z) {
                            if (c.querySelectorAll) {
                                B = B.querySelectorAll(A + z)
                            } else {
                                B = y(B, A, z.substring(1))
                            }
                        } else {
                            B = B.getElementsByTagName(A)
                        }
                    }
                }
                return B
            };
            x = function(E, D, z) {
                var B, A, y, C;
                if (typeof E === "undefined" || !E) {
                    E = c
                }
                if (!i(E)) {
                    E = v(E, D, z)
                } else {
                    A = [], y = E.length;
                    for (C = 0; C < y; C += 1) {
                        B = v(E[C], D, z);
                        A = h.base.copy(A, B)
                    }
                    E = A
                }
                return E
            };
            r = clean(o).split(" ");
            t = r.length;
            for (s = 0; s < t; s += 1) {
                p = r[s].match(n);
                if (p) {
                    q = x(q, p[2])
                } else {
                    p = r[s].match(m);
                    if (p && p[2]) {
                        if (p[1] && p[1] !== "") {
                            q = x(q, p[1], p[2])
                        } else {
                            q = x(q, p[2])
                        }
                    } else {
                        p = r[s].match(j);
                        if (p) {
                            q = x(q, p[0])
                        }
                    }
                }
            }
            if (q) {
                if (q.length) {
                    u = q.length;
                    for (s = 0; s < u; s += 1) {
                        this[s] = q[s]
                    }
                } else {
                    u = 1;
                    this[0] = q
                }
            } else {
                u = 0
            }
            this.length = u;
            return this
        };
        l = h(c);
        return h
    })();
    a.base.select.prototype = a.base;
    f.WebCalculator.selector = a
})(window);


WebCalculator.selector.prototype.html = function(d, c) {
    var e, a, b;
    if (this.length <= 0) {
        return
    }
    if (!c) {
        c = 0
    }
    if (c < this.length) {
        e = this[c];
        if (e && (d || d == "")) {
            a = typeof d;
            a = a.toLowerCase();
            if (a.indexOf("string") !== -1 || a.indexOf("number") !== -1
                    || a === "string" || a === "number") {
                b = new String(e.tagName);
                if (b.toLowerCase() === "input") {
                    e.value = d
                } else {
                    e.innerHTML = d
                }
            } else {
                if (e && a === "object" || a.indexOf("object") !== -1) {
                    e.appendChild(d)
                }
            }
        } else {
            if (e) {
                b = new String(e.tagName);
                if (b.toLowerCase() === "input") {
                    return e.value
                } else {
                    if (typeof e.value === "string") {
                        return e.innerHTML
                    }
                }
            }
        }
    }
};

WebCalculator.dom = {
    createElement : function(d, c, b) {
        var f, g, e = WebCalculator.selector;
        if (typeof c === "string") {
            g = e("#" + c).get(0)
        } else {
            g = c
        }
        if (!d) {
            return g
        }
        if (b && b.id) {
            f = e("#" + b.id).get(0)
        }
        if (!f) {
            f = document.createElement(d)
        }
        if (f && b && b.id) {
            f.id = b.id
        }
        if (f && g) {
            g.appendChild(f)
        }
        if (f && b) {
            var a;
            for (a in b) {
                f[a] = b[a]
            }
        }
        return f
    },
    setContent : function(b, a) {
        WebCalculator.selector(b).html(a)
    }
};
WebCalculator.dom.screen = {
    maxx: function () {
        return window.innerWidth || (document.documentElement && document.documentElement.clientWidth) || (document.documentElement && document.documentElement.offsetWidth) || (document.body && document.body.clientWidth) || (document.body && document.body.offsetWidth) || 0
    },
    maxy: function () {
        return window.innerHeight || (document.documentElement && document.documentElement.clientHeight) || (document.documentElement && document.documentElement.offsetHeight) || (document.body && document.body.clientHeight) || (document.body && document.body.offsetHeight) || 0
    }
};

WebCalculator.events = (function() {
    var a = {};
    a.eventObj = "";
    if (window.attachEvent && !window.addEventListener) {
        a.addEvent = function(f, d, e, c) {
            var b = f.attachEvent("on" + d, e);
            if (!b) {
                throw ("Event " + d + " could not be added!")
            }
        }
    } else {
        if (window.addEventListener) {
            a.addEvent = function(e, c, d, b) {
                e.addEventListener(c, d, b)
            }
        }
    }
    if (window.detachEvent && !window.removeEventListener) {
        a.removeEvent = function(f, d, e, c) {
            var b = f.detachEvent("on" + d, e);
            if (!b) {
                throw ("Event " + d + " could not be removed!")
            }
        }
    } else {
        if (window.removeEventListener) {
            a.removeEvent = function(e, c, d, b) {
                e.removeEventListener(c, d, b)
            }
        }
    }
    a.getEvent = function(b) {
        this.eventObj = (window.event) ? window.event : b;
        return this.eventObj
    };
    a.getTarget = function() {
        if (this.eventObj.srcElement) {
            return this.eventObj.srcElement
        } else {
            if (this.eventObj.target) {
                return this.eventObj.target
            }
        }
    };
    return a
})();

WebCalculator.events.addOnLoad = function(a) {
    WebCalculator.onLoadEventStack.push(a)
};
WebCalculator.events.doOnLoad = function() {
    var d, c, a, b = WebCalculator.onLoadEventStack;
    c = b.length;
    for (d = 0; d < c; d += 1) {
        a = b[d];
        if (a && (typeof a).toLowerCase() === "function") {
            a()
        }
    }
};
WebCalculator.events.addEvent(window, "load", WebCalculator.events.doOnLoad, false);

WebCalculator.MathLibrary = function() {
};
WebCalculator.MathLibrary.prototype = Math;
WebCalculator.math = new WebCalculator.MathLibrary();
WebCalculator.math.add = function() {
    var b = 0;
    for ( var a = 0; a < arguments.length; a += 1) {
        b += (+arguments[a])
    }
    return b
};
WebCalculator.math.subtract = function(a, b) {
    return ((+a) - (+b))
};
WebCalculator.math.multiply = function() {
    var b = 1;
    for ( var a = 0; a < arguments.length; a += 1) {
        var c = b;
        b = (+c) * (+arguments[a])
    }
    return b
};
WebCalculator.math.divide = function(a, b) {
    return ((+a) / (+b))
};
WebCalculator.math.square = function(a) {
    return WebCalculator.math.pow(a, 2)
};
WebCalculator.math.cube = function(a) {
    return WebCalculator.math.pow(a, 3)
};
WebCalculator.math.inverse = function(a) {
    return WebCalculator.math.multiply(-1, a)
};
WebCalculator.math.oneOver = function(a) {
    return WebCalculator.math.divide(1, a)
};
WebCalculator.math.factorial = function(a) {
    var b = WebCalculator.math;
    if ((+a) <= 1) {
        return 1
    }
    var c = (+a) - 1;
    return b.multiply(a, b.factorial(c))
};
WebCalculator.math.hexidecimal = [ "0", "1", "2", "3", "4", "5", "6", "7", "8",
        "9", "A", "B", "C", "D", "E", "F" ];
WebCalculator.math.convertFromBaseTenToBaseX = function(b, a) {
    var d = WebCalculator.math;
    var e = d.hexidecimal[a % b];
    while (a >= b) {
        var c = d.subtract(a, (a % b));
        var a = d.divide(c, b);
        if (a >= b) {
            e = d.hexidecimal[a % b] + e
        } else {
            e = d.hexidecimal[a] + e
        }
    }
    return e
};
WebCalculator.math.convertFromBaseXToBaseTen = function(g, c) {
    var i = WebCalculator.math;
    var h = 0;
    var d = 1;
    function f(j) {
        for ( var k = 0; k < i.hexidecimal.length; k += 1) {
            if (i.hexidecimal[k] == j) {
                return k
            }
        }
        return ""
    }
    while (d <= c.length) {
        var a = i.pow(g, i.subtract(d, 1));
        var e = i.subtract(c.length, d);
        var b = f(c.charAt(e));
        h = i.add(h, i.multiply(b, a));
        d++
    }
    return h
};

WebCalculator.Calculator = function() {
    this.currentTotal = 0;
    this.lastMethod = "";
    this.currentValue = 0
};
WebCalculator.Calculator.prototype.clear = function() {
    this.currentTotal = 0;
    this.lastMethod = "";
    this.currentValue = 0;
    if (window.localStorage) {
        window.localStorage.setItem("WBcurrentValue", this.currentValue);
    }
};
WebCalculator.Calculator.prototype.appendStorage = function(a) {
    var b = this.currentValue;
    if (((+b) === 0) && (new String(b).indexOf(".") === -1)) {
        this.currentValue = a
    } else {
        if (a === ".") {
            if (b.indexOf(".") !== -1) {
                return
            }
        }
        if ( (+b) === 0 ) { 
            this.currentValue = new String(a);
        } else {
            this.currentValue = new String(b) + new String(a);
        }
    }
    if (window.localStorage) {
        window.localStorage.setItem("WBcurrentValue", this.currentValue);
    }
};
WebCalculator.Calculator.prototype.performLastMethod = function(b) {
    var a = this.currentValue;
    var c = this.lastMethod;
    if (c !== "") {
        this.currentTotal = c(this.currentTotal, a)
    } else {
        this.currentTotal = a
    }
    if (window.localStorage) {
        window.localStorage.setItem("WBcurrentValue", this.currentTotal);
    }
    this.currentValue = 0;
    this.lastMethod = b
};
WebCalculator.Calculator.prototype.changeStorage = function(b) {
    var a = this.currentValue;
    if (b) {
        this.currentValue = b(a)
    }
    if (window.localStorage) {
        window.localStorage.setItem("WBcurrentValue", this.currentValue);
    }
};
WebCalculator.Calculator.prototype.equals = function() {
    this.performLastMethod("");
    return this.currentTotal
};
WebCalculator.GUICalculator = function(b, a) {
    this.id = (a && a.id) ? a.id : "";
    if (!b) {
        throw ("Could not create WebCalculator.Calendar() because no parent object was specifiec!")
    }
    this.parentID = b
};
WebCalculator.GUICalculator.prototype = new WebCalculator.Calculator;
WebCalculator.GUICalculator.prototype.handle = {
    table : "",
    div : ""
};
WebCalculator.GUICalculator.prototype.render = function() {
    var w, v, l, e = [], f, u, m, h, q = 6, t, b, m, k, p = WebCalculator, c, a = p.selector, d = p.dom, x = p.events;
    l = a("#" + this.parentID).get(0);
    if (!l) {
        throw ("Could not get parent element to attach calendar to!")
    }
    this.handle.table = d.createElement("table", l, {
        className : "guiCalculator"
    });
    if ( d.screen.maxx() <= 380 ) {
        q = 4;
    }
    b = d.createElement("tr", this.handle.table);
    m = d.createElement("td", b);
    m.setAttribute("colspan", q);
    t = {
        id : "guiCalculator", 
        width: "90%"
    };
    c = m;
    this.handle.div = d.createElement("div", c, t);
    b = d.createElement("tr", this.handle.table);
    e = [ "1", "2", "3", "+", "sqrt", "n!", 
          "4", "5", "6", "-", "sin", "1/x",
          "7", "8", "9", "*", "cos", "x^2", 
          "+/-", "0", ".", "/", "tan", "x^3", 
          "=", "clear", "log", "exp", "PI", "x^y" ];
    if ( q === 4 ) { 
        e = [ "1", "2", "3", "+", 
              "4", "5", "6", "-", 
              "7", "8", "9", "*", 
              "+/-", "0", ".", "/",  
              "=", "clear", "log", "exp",
              "sin", "cos", "tan", "PI",
              "sqrt",  "1/x", "x^2", "x^y"  ];   
    }
    f = e.length;
    for (w = 0; w < f; w++) {
        u = {
            innerHTML : e[w]
        };
        m = d.createElement("td", b, u);
        if (w % q === (q - 1)) {
            b = d.createElement("tr", this.handle.table)
        }
    }
    var r = c.innerHTML;
    c.innerHTML = r;
    this.handle.div = a("#guiCalculator").get(0);
    this.handle.div.innerHTML = "0";
    var g = this;
    var n = function(i) {
        g.handleClick(i, g)
    };
    if (p.touchEnabled) {
        x.addEvent(this.handle.table, "touchstart", n, false)
    } else {
        x.addEvent(this.handle.table, "click", n, false)
    }
    if (window.localStorage && window.localStorage.getItem("WBcurrentValue")) {
        this.currentValue = window.localStorage.getItem("WBcurrentValue");
        this.handle.div.innerHTML = this.currentValue;
    }
};
WebCalculator.GUICalculator.prototype.handleClick = function(i, f) {
    var g = WebCalculator.events.getEvent(i), c, b = WebCalculator.math, h, j = [], a;
    c = WebCalculator.events.getTarget();
    h = f.handle.div.innerHTML;
    if (c.nodeName.toLowerCase() !== "td") {
        return
    }
    var d = c.innerHTML;
    if (!isNaN(d) || d === "A" || d === "B" || d === "C" || d === "D"
            || d === "E" || d === "F") {
        f.appendStorage(d);
        if (isNaN(h)) {
            f.handle.div.innerHTML = h + d
        } else {
            f.handle.div.innerHTML = f.currentValue
        }
    } else {
        j["."] = function(l, e, k) {
            l.appendStorage(k);
            l.handle.div.innerHTML = e + k
        };
        j["+/-"] = function(l, e, k) {
            l.changeStorage(b.inverse);
            l.handle.div.innerHTML = l.currentValue
        };
        j["+"] = function(l, e, k) {
            l.performLastMethod(b.add);
            l.handle.div.innerHTML = e + k
        };
        j["-"] = function(l, e, k) {
            l.performLastMethod(b.subtract);
            l.handle.div.innerHTML = e + k
        };
        j["*"] = function(l, e, k) {
            l.performLastMethod(b.multiply);
            l.handle.div.innerHTML = e + k
        };
        j["/"] = function(l, e, k) {
            l.performLastMethod(b.divide);
            l.handle.div.innerHTML = e + k
        };
        j["="] = function(l, e, k) {
            l.equals();
            l.handle.div.innerHTML = l.currentTotal;
            l.currentValue = l.currentTotal
        };
        j.clear = function(l, e, k) {
            l.clear();
            l.handle.div.innerHTML = l.currentTotal
        };
        j["n!"] = function(l, e, k) {
            l.changeStorage(b.factorial);
            l.handle.div.innerHTML = l.currentValue
        };
        j["1/x"] = function(l, e, k) {
            l.changeStorage(b.oneOver);
            l.handle.div.innerHTML = l.currentValue
        };
        j["x^2"] = function(l, e, k) {
            l.changeStorage(b.square);
            l.handle.div.innerHTML = l.currentValue
        };
        j["x^3"] = function(l, e, k) {
            l.changeStorage(b.cube);
            l.handle.div.innerHTML = l.currentValue
        };
        j.PI = function(l, e, k) {
            l.currentValue = Math.PI;
            l.handle.div.innerHTML = e + Math.PI
        };
        j["x^y"] = function(l, e, k) {
            l.performLastMethod(Math.pow);
            l.handle.div.innerHTML = e + "^"
        };
        a = j[d];
        if (!a) {
            if (Math.hasOwnProperty(d)) {
                f.changeStorage(Math[d]);
                f.handle.div.innerHTML = f.currentValue
            }
        } else {
            a(f, h, d)
        }
    }
};
