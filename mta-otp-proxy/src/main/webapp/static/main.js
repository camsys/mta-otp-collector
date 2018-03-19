$(document).ready(function() {
    $("#submit").click(function() {
        var route = $("#route").val();
        var stop = $("#stop").val();

        var rtAge, otpAge;

        $.get("/realtime/MTASBWY/stopTimes", {"route": route, "stop": stop}, function(data) {
            $("#rtResults").empty();
            clearInterval(rtAge);
            var list = $("<ul></ul>").appendTo("#rtResults")
            data.stopTimes.forEach(function(d) {
                var item = $("<li></li>").text(d.tripId + " " + formatDate(d.departure));
                list.append(item);
            })
            var ageBox = $("<div class='ageBox'></div>").appendTo("#rtResults");
            rtAge = ageInterval(ageBox, data.timestamp);
        })

        var otpUrl = $("#otpUrl").val();
        var otpApi = $("#otpApi").val();
        var params = {"stops": "MTASBWY:" + stop, "routes": "MTASBWY__" + route, "apikey": otpApi, "numberOfDepartures": 10, "timeRange": 3600};
        $.get(otpUrl + "/otp/routers/default/nearby", params, function(data) {
            $("#otpResults").empty();
            clearInterval(otpAge);
            var minTimestamp = undefined;
            data.forEach(function(item) {
                item.groups.forEach(function(group) {
                    //$("#otpResults").append("<h4>" + group.headsign + "</h4>");
                    var list = $("<ul></ul>").appendTo("#otpResults")
                    group.times.forEach(function(time) {
                        var tripId = time.tripId.split(":")[1]
                        var timefmt = formatDate(time.serviceDay + time.realtimeDeparture);
                        var state = time.realtimeState;
                        var item = $("<li></li>").text(tripId + " " + timefmt + " (" + state + ")");
                        list.append(item)
                        if (minTimestamp == undefined || (time.timestamp != -1 && time.timestamp < minTimestamp))
                            minTimestamp = time.timestamp;
                    })
                })
            });

            var ageBox = $("<div class='ageBox'></div>").appendTo("#otpResults");
            otpAge = ageInterval(ageBox, minTimestamp);
        })
    })
})

function formatDate(epoch) {
    var date = new Date(0);
    date.setUTCSeconds(epoch);
    return date.toLocaleTimeString();
}

function ageInterval(sel, timestamp) {
    var setAge = function() {
        var age = Math.round(new Date().getTime() / 1000 - timestamp);
        $(sel).text("Age: " + age);
    };
    setAge();
    return setInterval(setAge, 1000);
}