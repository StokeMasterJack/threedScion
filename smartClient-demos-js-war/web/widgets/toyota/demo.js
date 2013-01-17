var threedSession;

function onThreedReady() {

    var factory = new c3i.smartClient.model.ThreedSessionFactory();

    factory.setRepoBase("/configurator-content-v2");
//    factory.setRepoBase("http://smartsoftdev.net/configurator-content-v2");
    factory.setSeries("toyota", 2012, "avalon");

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


