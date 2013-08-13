var threedSession;

var picks = ["Base", "V6", "6AT", "070"];

function onThreedReady() {
    if(window.console) console.log("onThreedReady");

    var factory = new c3i.smartClient.model.ThreedSessionFactory();

    factory.setRepoBase("/configurator-content-v2");
    factory.setProfileKey("wStd");
    factory.setSeries("toyota", 2013, "avalon");
    var threedSessionFuture = factory.createSession();
    if(window.console) console.log("threedSessionFuture: " + threedSessionFuture);


    threedSessionFuture.success(function () {
        if(window.console) console.log("threedSession success");
        threedSession = threedSessionFuture.getResult();
        threedSession.setPicks(picks);

        threedSession.addImageStackChangeListener(function () {
            refreshView();
        });

        threedSession.addViewChangeListener(function () {
            refreshView();
        });


    });

    $("#viewSelector").change(viewSelector_onChange);

}

function viewSelector_onChange(ev) {
    threedSession.setViewIndex(ev.target.selectedIndex);
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


