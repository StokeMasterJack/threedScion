var threedSession;

function onThreedReady() {

    var factory = new c3i.smartClient.model.ThreedSessionFactory();

    factory.setRepoBase("/configurator-content-v2");
    factory.setSeries("toyota", 2011, "avalon");

    factory.createSession().success(function (_threedSession) {
        threedSession = _threedSession;
        threedSession.setPicks(["Base", "V6", "6AT", "070"]); //initial picks
        threedSession.scan();
    });

}


