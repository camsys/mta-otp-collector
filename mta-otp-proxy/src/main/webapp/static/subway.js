$(document).ready(function() {
    $("#submit").click(function() {
        var route = $("#route").val();
        var stop = $("#stop").val();

        var rtAge, otpAge;

        $.get("/realtime/MTASBWY/stopTimes", {"route": route, "stop": stop}, function(data) {
            $("#rtResults").empty();
            $("#rtResults").append("<h4>RT results</h4>");
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
        getNearby(otpUrl, params, "#otpResults", otpAge, function(f) { otpAge = f });
    })
});

