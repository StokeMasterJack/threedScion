var threedSession;

(function ($) {
    $.QueryString = (function (a) {
        if (a == "") return {};
        var b = {};
        for (var i = 0; i < a.length; ++i) {
            var p = a[i].split('=');
            if (p.length != 2) continue;
            b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
        }
        return b;
    })(window.location.search.substr(1).split('&'))
})(jQuery);

function onThreedReady() {

    var factory = new c3i.smartClient.model.ThreedSessionFactory();

//    factory.setRepoBase("/configurator-content-v2");


    var repoBase = $.QueryString["repoBase"];

    if (!repoBase) {
        repoBase = "http://smartsoftdev.net/configurator-content-v2";
    }

    $("#repoBase").val(repoBase);

    factory.setRepoBase(repoBase);
    factory.setSeries("toyota", 2011, "avalon");

    factory.createSession().success(function (_threedSession) {
        threedSession = _threedSession;
        threedSession.setPicks(["Base", "V6", "6AT", "070"]); //initial picks
        threedSession.scan();
    });

    $("#viewSelector").change(viewSelector_onChange);

    function viewSelector_onChange(ev) {
        threedSession.setViewIndex(ev.target.selectedIndex);
    }

}

$(function () {
    $("#repoBase").change(function () {
        window.location.href = "?repoBase=" + $(this).val();
    });
});


