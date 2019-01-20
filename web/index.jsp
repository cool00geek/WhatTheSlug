<%--
  Created by IntelliJ IDEA.
  User: vinay
  Date: 1/18/19
  Time: 11:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cruzhacks.whattheslug.Test" %>
<html>
<head>
    <!-- The Webpage title -->
    <title>What the SLUG is Going On?</title>

    <!-- Stylesheets to make it look nice and purdy -->
    <link type="text/css" rel="stylesheet" href="CSS/Style.css"/>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/toastify-js/src/toastify.min.css">
    <link type="text/css" rel="stylesheet" href="https://cdn.firebase.com/libs/firebaseui/3.1.1/firebaseui.css"/>
    <link type="text/css" rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">

    <!-- Icons -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

    <!-- Import all the required scripts -->
    <script src="JS/map.js" type="text/javascript"></script>
    <script src="JS/firebase.js" type="text/javascript"></script>
    <script src="JS/event.js" type="text/javascript"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/toastify-js"></script>
    <script src="https://www.gstatic.com/firebasejs/5.8.0/firebase.js"></script>
    <script src="https://cdn.firebase.com/libs/firebaseui/3.1.1/firebaseui.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
    <script>
        // Initialize Firebase
        var config = {
            apiKey: "AIzaSyBl3mzRnGg3YtK2jrhE1FigncSGWic4hLk",
            authDomain: "whattheslugisgoingon.firebaseapp.com",
            databaseURL: "https://whattheslugisgoingon.firebaseio.com",
            projectId: "whattheslugisgoingon",
            storageBucket: "whattheslugisgoingon.appspot.com",
            messagingSenderId: "331521457146"
        };
        firebase.initializeApp(config);
    </script>
</head>
<body>

<!-- Show the map using the map.js file -->
<div id="map"></div>

<!--Show the login/logout button -->
<a class="waves-effect waves-light btn" onclick="toggleLogin()" id="userbtn">Log In</a>

<!-- Show the add new event button -->
<a class="btn-floating btn-large waves-effect waves-light red" id="floating-button" onclick="newEvent()"><i
        class="material-icons">add</i></a>

<!-- Show the table with all the events -->
<table id="eventTable">
    <tr>
        <td class="tableText">
            <p class="eventName">CruzHacks</p>
            <br>
            Date: 01/20/19 at 8:00 AM
            <br>
            Stevenson Event Center
        </td>
        <td class="tableText">
            <button class="likeBtn">
                Like!
            </button>
            <br>
            <button class="dislikeBtn">
                Dislike!
            </button>
        </td>
    </tr>
    <tr>
        <td class="tableText">
            <p class="eventName">Concert: Ozomatli</p>
            <br>
            Date: 01/20/19 at 8:00PM
            <br>
            Catalyst
        </td>
        <td class="tableText">
            <button class="likeBtn">
                Like!
            </button>
            <br>
            <button class="dislikeBtn">
                Dislike!
            </button>
        </td>
    </tr>
    <tr>
        <td class="tableText">
            <p class="eventName">Concert: The Melvins</p>
            <br>
            Date: 01/20/19 at 9:00PM
            <br>
            Catalyst
        </td>
        <td class="tableText">
            <button class="likeBtn">
                Like!
            </button>
            <br>
            <button class="dislikeBtn">
                Dislike!
            </button>
        </td>
    </tr>
</table>

<!-- Show the overlay container to prompt for google login -->
<div id="loginOverlay">
    <a class="waves-effect waves-light btn" onclick="closeLogin()" id="cancelLogin"><i class="material-icons left">cancel</i>Close</a>
    <div id="firebaseui-auth-container"></div>
    <div id="loader">Loading...</div>
</div>

<!-- Show the overlay container to create a new event -->
<div id="newEventOverlay">
    <a class="waves-effect waves-light btn" onclick="closeNewEvent()" id="cancelSubmit"><i class="material-icons left">cancel</i>Close</a>

    <!--<form class="col s12" action="event.jsp" method="POST">-->
    <form class="col s12" action="database" method="POST">
        <div class="row" id="topRow">
            <div class="input-field col s12">
                <input id="event_name" type="text" class="validate" name="event_name" required>
                <label for="event_name">Event Name*</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <input id="event_date" type="date" class="validate" placeholder="" name="event_date" required>
                <label for="event_date">Event Date*</label>
            </div>
            <div class="input-field col s6">
                <input id="event_time" type="time" class="validate" placeholder="" name="event_time" required>
                <label for="event_time">Event Time*</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s12">
                <input id="event_loc" type="text" class="validate" placeholder="" name="event_loc" required>
                <label for="event_loc">Event Location*</label>
            </div>
        </div>
        <div class="row" id="bottomRow">
            <div class="input-field col s12">
                <input id="event_room" type="text" class="validate" name="event_room">
                <label for="event_room">Event Room</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s12">
                <button class="btn waves-effect waves-light" id="submitButton" type="submit" name="action">Submit
                    <i class="material-icons right">send</i>
                </button>
            </div>
        </div>
    </form>
</div>

<script>
    updateButtonOnLogin();
    confirmAdd();
</script>

<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAr8kTt8rGT2oVI7hU4C4pJw7uvkzjYwnI&libraries=places&callback=initMap"
        async defer></script>
</body>
</html>

<!--
Sources!

- Google maps: https://developers.google.com/maps/documentation/javascript/adding-a-google-map
- FAB Plus button: https://codepen.io/simoberny/pen/pJZJQY
- Show the overlay for firebase: https://www.w3schools.com/howto/howto_css_overlay.asp
- Check if logged in: https://stackoverflow.com/questions/37873608/how-do-i-detect-if-a-user-is-already-logged-in-firebase and https://firebase.google.com/docs/auth/web/manage-users
- To update the text of the login/logout button: https://permadi.com/tutorial/jsInnerHTMLDOM/index.html
- Logout user from firebase: https://stackoverflow.com/questions/37343309/best-way-to-implement-logout-in-firebase-v3-0-1-firebase-unauth-is-removed-afte
- Make escape hide the login window: https://stackoverflow.com/questions/6542413/bind-enter-key-to-specific-button-on-page, https://stackoverflow.com/questions/1160008/which-keycode-for-escape-key-with-jquery
- Check if date is valid: https://stackoverflow.com/questions/1353684/detecting-an-invalid-date-date-instance-in-javascript
- Materialize theme: https://materializecss.com/getting-started.html
- JS calendar: https://www.cssscript.com/date-picker-calendar-purejscalendar/
-->