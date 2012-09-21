var isArmed = false;
var startX = null;
var mouseDown = false;
var enabled = true;

var target;

var DELTA_X_FOR_ANGLE_CHANGE = 50;

var scrollListener = null;

function activateDragToSpin(_scrollListener) {
    log("dts");

    scrollListener = _scrollListener;

    target = document.querySelector(".DragToSpin");
    target.addEventListener("touchstart", onTouchStart, false);
    target.addEventListener("touchend", onTouchEnd, false);
    target.addEventListener("touchcancel", onTouchCancel, false);
    target.addEventListener("touchleave", onTouchLeave, false);
    target.addEventListener("touchmove", onTouchMove, false);

//    scrollListener = {
//        angleNext:function () {
//            log("angleNext");
//        },
//        anglePrevious:function () {
//            log("anglePrevious");
//        }
//    };
}

function onTouchStart(ev) {
    console.log("onTouchStart");
    ev.preventDefault();
    mouseDown = true;
    arm(ev);
}


function onTouchEnd(ev) {
    console.log("onTouchEnd");
}

function onTouchCancel(ev) {
    console.log("onTouchCancel");
}

function onTouchLeave(ev) {
    console.log("onTouchLeave");
}

function onTouchMove(ev) {
    console.log("onTouchMove");

    if (!isArmed) return;
    if (scrollListener == null) return;

    var touches = ev.changedTouches;
        var touch = touches[0];
    var mouseX = touch.clientX;

    var deltaX = mouseX - startX;
    if (deltaX == 0) return;

    if (Math.abs(deltaX) >= DELTA_X_FOR_ANGLE_CHANGE) {
        if (deltaX > 0) {
            scrollListener.angleNext();
        }
        else if (deltaX < 0) {
            scrollListener.anglePrevious();
        }
        else {
            throw  "Invalid deltaX: [" + deltaX + "]";
        }
        startX = mouseX;
    }
}

function log(msg) {
    var el = document.querySelector(".Log");
    var div = el.appendChild(document.createElement("div"));
    div.innerText = msg;
}

function setCursorMove() {
    target.style.cursor = "move";
}

function setCursorDefault() {
    target.style.cursor = "default";
}

function arm(ev) {
    if (!enabled) {
        return;
    }
    ev.preventDefault();
    isArmed = true;
    //startX = ev.getX();
    var touches = ev.changedTouches;
    var touch = touches[0];
    startX = touch.clientX;
}

function disarm() {
    isArmed = false;
    startX = null;
    setCursorDefault();
}