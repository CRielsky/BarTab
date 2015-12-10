module.exports = function(Location) {
    // Get range for restaurants using the len param
    // Add of subtract to get a box for review range
    Location.byWait = function(lat, lon, len, cb){
        var lat_diff = len / 69.172;
        var lon_diff = len / 53;

        var top_lat = lat + lat_diff;
        var bot_lat = lat - lat_diff;
        var top_lon = lon + lon_diff;
        var bot_lon = lon - lon_diff;

        Location.find({order: 'avg_wait_time ASC',
                     where: {and: [{latitude: {between: [bot_lat, top_lat]}},
                                   {longitude: {between: [bot_lon, top_lon]}}]}},
            function(err, locations){
                if(err) cb(null, err);
                else cb(null, locations);
            }
        );
    }

    // Only searches for restaurants within specified range
    // Sorts based on straight line distance from location to each restaurant
    // Restaurants then returned with new dist attribute
    Location.byDistance = function(lat, lon, len, cb){
        var lat_diff = len / 69.172;
        var lon_diff = len / 53;

        var top_lat = lat + lat_diff;
        var bot_lat = lat - lat_diff;
        var top_lon = lon + lon_diff;
        var bot_lon = lon - lon_diff;

        Location.find({order: 'avg_atmosphere DESC',
                     where: {and: [{latitude: {between: [bot_lat, top_lat]}},
                                   {longitude: {between: [bot_lon, top_lon]}}]}},
            function(err, locations){
                if(err) cb(null, err);
                locations.sort(function(a, b){
                    var a_lat_dist = Math.abs(lat - a.latitude);
                    var a_lon_dist = Math.abs(lon - a.longitude);
                    var b_lat_dist = Math.abs(lat - b.latitude);
                    var b_lon_dist = Math.abs(lon - b.longitude);

                    var a_ang_dist = Math.sqrt(Math.pow(a_lat_dist, 2)
                                             + Math.pow(a_lon_dist, 2));
                    var b_ang_dist = Math.sqrt(Math.pow(b_lat_dist, 2)
                                             + Math.pow(b_lon_dist, 2));
                    var a_dist = a_ang_dist * 57.53246;
                    var b_dist = b_ang_dist * 57.53246;

                    a.dist = a_dist;
                    b.dist = b_dist;

                    return a_ang_dist > b_ang_dist;
                });
                cb(null, locations);
            }
        );
    }

    // Searches for restaurants within specified restaurant
    // Returns restaurants sorted by their atmosphere rating
    Location.byRating = function(lat, lon, len, cb){
        var lat_diff = len / 69.172;
        var lon_diff = len / 53;

        var top_lat = lat + lat_diff;
        var bot_lat = lat - lat_diff;
        var top_lon = lon + lon_diff;
        var bot_lon = lon - lon_diff;

        Location.find({order: 'avg_atmosphere DESC',
                     where: {and: [{latitude: {between: [bot_lat, top_lat]}},
                                   {longitude: {between: [bot_lon, top_lon]}}]}},
            function(err, locations){
                if(err) cb(null, err);
                else cb(null, locations);
            }
        );
    }

    // Must declare remote method for each declared api call
    // Each has the same parameters of latitude, longitude, and
    // and length in miles from current location to search
    Location.remoteMethod(
        'byWait',
        {
            http: {path: '/byWait', verb: 'get'},
            accepts: [{arg: 'lat', type: 'number'},
                      {arg: 'lon', type: 'number'},
                      {arg: 'len', type: 'number'}],
            returns: {arg: 'locations',  type: 'string'}
        }
    );

    Location.remoteMethod(
        'byDistance',
        {
            http: {path: '/byDistance', verb: 'get'},
            accepts: [{arg: 'lat', type: 'number'},
                      {arg: 'lon', type: 'number'},
                      {arg: 'len', type: 'number'}],
            returns: {arg: 'locations', type: 'string'}
        }
    );

    Location.remoteMethod(
        'byRating',
        {
            http: {path: '/byRating', verb: 'get'},
            accepts: [{arg: 'lat', type: 'number'},
                      {arg: 'lon', type: 'number'},
                      {arg: 'len', type: 'number'}],
            returns: {arg: 'locations', type: 'string'}
        }
    );
}
