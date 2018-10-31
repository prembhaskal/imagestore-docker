
/**



**/


function init() {
    startEventsFeed();
}

function getEventCounters(evtData, successCallback, errorCallback) {
    let configUrl = 'http://' + getServerEndPoint() + '/getEventDetails';

    $.ajax({
        url: configUrl,
        type: "GET",
        data: evtData,
        contentType: "application/json; charset=utf-8"
    })
        .done(successCallback)
        .fail(errorCallback);
}

function getServerEndPoint() {
    let currenthost = window.location.host;
    console.log(currenthost);
    return currenthost;
}

function startEventsFeed() {
    var evtTypes = ['IMAGE_STORE', 'IMAGE_RETREIVE', 'IMAGE_DELETE', 'ALBUM_CREATE', 'ALBUM_DELETE'];
    let getEvents = function () {
        
        for (let i = 0; i < evtTypes.length; i++) {
            getEventCounters({ evtType: evtTypes[i] },
                function (data) {
                    updateEventCounterData( evtTypes[i], data);
                    console.log('obtained data for event: ' + evtTypes[i] + ' data: ' + data);
                },
                function () {
                    console.error('error getting data for event ' + evtTypes[i]);
                })
        }
        
    };

    setInterval(getEvents, 1 * 5000);
}

function updateEventCounterData( evtType, data) {
    let elem = document.getElementById(evtType);
    if (elem) {
        elem.innerHTML = 'evtType: ' + evtType + '\t\t count: ' + data;
    }
}

window.addEventListener("load", init, false);
