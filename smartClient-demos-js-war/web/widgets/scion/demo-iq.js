var threedSession;

function onThreedReady() {

    var factory = new c3i.smartClient.model.ThreedSessionFactory();

    factory.setRepoBase("/configurator-content-v2");
    factory.setSeries("scion", 2012, "iq");

    factory.createSession().success(function (threedSession) {

        threedSession.setPicks(["1F7","WC"]); //initial picks

        threedSession.scan();
    });
}


