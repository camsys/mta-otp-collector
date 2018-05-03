$(document).ready(function() {
    $("#submit").click(function() {
        var route = $("#route").val();
        var stop = $("#stop").val();

        var obaAge, otpAge;

        var obaKey = $("#obaKey").val();

        $.get("/realtime/MTABUS/siri/stop-monitoring.json", {"MonitoringRef": "MTA_" + stop, "key": obaKey, "_": 1525294437358}, function(data) {
            $("#obaResults").empty();
            $("#obaResults").append("<h4>BusTime Results</h4>")
            clearInterval(obaAge);
            var list = $("<ul></ul>").appendTo("#obaResults");
            var sd = data.Siri.ServiceDelivery;
            var timestamp = Number.MAX_VALUE;
            sd.StopMonitoringDelivery.forEach(function(smd) {
                smd.MonitoredStopVisit.forEach(function(v) {
                    var d = v.MonitoredVehicleJourney;
                    var route = d.PublishedLineName
                    var trip = d.FramedVehicleJourneyRef.DatedVehicleJourneyRef;
                    var pred = d.MonitoredCall.ExpectedArrivalTime;
                    var departure = pred ? formatDate(new Date(pred).getTime() / 1000): "no prediction";
                    var present = d.MonitoredCall.Extensions.Distances.PresentableDistance;
                    var item = $("<li></li>").text(route + " " + trip + " " + departure + " " + present);
                    list.append(item);
                    timestamp = Math.min(timestamp, new Date(v.RecordedAtTime).getTime() / 1000)
                });
            });
            var ageBox = $("<div class='ageBox'></div>").appendTo("#obaResults");
            obaAge = ageInterval(ageBox, timestamp);
        })

        var otpUrl = $("#otpUrl").val();
        var otpApi = $("#otpApi").val();
        var params = {"stops": "MTA:" + stop, "apikey": otpApi, "numberOfDepartures": 10, "timeRange": 3600};
        getNearby(otpUrl, params, "#otpResults", otpAge, function(f) { otpAge = f }, true);
    })
});

