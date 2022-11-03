function getNearby(otpUrl, params, selector, otpAge, cb, showRoute) {
    $.get(otpUrl + "/otp/routers/default/nearby", params, function(data) {
        $(selector).empty();
        $(selector).append("<h4>OTP Nearby Results</h4>")
        clearInterval(otpAge);
        var minTimestamp = undefined;
        data.forEach(function(item) {
            item.groups.forEach(function(group) {
                //$("#otpResults").append("<h4>" + group.headsign + "</h4>");
                var list = $("<ul></ul>").appendTo(selector)
                var route = group.route.shortName;
                group.times.forEach(function(time) {
                    var tripId = time.tripId.split(":")[1]
                    var timefmt = formatDate(time.serviceDay + time.realtimeDeparture);
                    var state = time.realtimeState;
                    var item = $("<li></li>").text((showRoute ? route + " " : "") + tripId + " " + timefmt + " (" + state + ")");
                    list.append(item)
                    if (minTimestamp == undefined || (time.timestamp != -1 && time.timestamp < minTimestamp))
                        minTimestamp = time.timestamp;
                })
            })
        });

        var ageBox = $("<div class='ageBox'></div>").appendTo(selector);
        otpAge = ageInterval(ageBox, minTimestamp);
        cb(otpAge);
    })
}

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