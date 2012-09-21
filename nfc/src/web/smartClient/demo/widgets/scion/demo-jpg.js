var threedSession;

function onThreedReady() {

    var factory = new c3i.smartClient.model.ThreedSessionFactory();

    factory.setRepoBase("/configurator-content-v2");
    factory.setProfileKey("w720");
    factory.setSeries("scion", 2013, "frs");

    factory.createSession().success(function (_threedSession) {
        threedSession = _threedSession;
        threedSession.setPicks(["37J"]); //initial picks
        threedSession.scan();
    });

}


