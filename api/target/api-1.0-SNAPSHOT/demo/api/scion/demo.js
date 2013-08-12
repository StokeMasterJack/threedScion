var threedSession;

function onThreedReady() {
    if (window.console) console.log("onThreedReady");
    var factory = new c3i.smartClient.model.ThreedSessionFactory();
    if (window.console) console.log("ThreedSessionFactory created");
//factory.setRepoBase("/configurator-content-v2");
//factory.setImageBaseUrl("http://media.scion.com/configurator-content-v2");
//factory.setImageBaseUrl("http://smartsoftdev.net/configurator-content-v2");
    factory.setProfileKey("wStd");
    factory.setSeries("scion", 2013, "frs");
    var threedSessionFuture = factory.createSession();
    threedSessionFuture.success(function () {
        threedSession = threedSessionFuture.getResult();
        threedSession.setPicks(["37J"]);
        threedSession.addImageStackChangeListener(function () {
            refreshView();
        });
    });
}

function refreshView() {
    var imageStack = threedSession.getImageStack();
    var images = imageStack.getImageArray();
    var maxImageIndex = images.length - 1;
    var imageElements = $("#imageStack img");
    for (var index = 0; index < imageElements.length; index++) {
        var img = imageElements[index];
        if (index <= maxImageIndex) {
            img.style.display = "block";
            var image = images[index];
            img.setAttribute("src", image.getSrc());
        } else {
            img.style.display = "none";
        }
    }
}