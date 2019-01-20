function initMapOld() {
    // The location of UCSC
    // 36.991587, -122.058241
    var ucsc = {lat: 36.991587, lng: -122.058241};
    // The map, centered at UCSC
    var map = new google.maps.Map(
        document.getElementById('map'), {zoom: 15, center: ucsc});
    // The marker, positioned at UCSC
    //var marker = new google.maps.Marker({position: ucsc, map: map});
}
function initMap() {
    var map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 36.991587, lng: -122.058241},
        zoom: 15
    });

    var input = document.getElementById('event_loc');

    var autocomplete = new google.maps.places.Autocomplete(input);

    // Bind the map's bounds (viewport) property to the autocomplete object,
    // so that the autocomplete requests use the current map bounds for the
    // bounds option in the request.
    autocomplete.bindTo('bounds', map);

    // Set the data fields to return when the user selects a place.
    autocomplete.setFields(
        ['address_components', 'geometry', 'icon', 'name']);
}
