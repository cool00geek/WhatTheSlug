function newEvent(){
    console.log("Attempting to create a new event");
    if (!checkIfLoggedIn()){
        // The user is not logged in
        // They should first be prompted to login, then we can decide whether we want to add an event or what
        toggleLogin();
        return;
    }

    window.addEventListener('keyup', function(event){
       if (event.keyCode === 27){
           closeNewEvent();
       }
    });

    document.getElementById("newEventOverlay").style.display = "block";
}

function closeNewEvent(){
    document.getElementById("newEventOverlay").style.display = "none";
}

function confirmAdd(){
    var url = document.URL.toString();
    console.log(url);
    var param = url.slice(url.lastIndexOf("/") + 1);
    console.log(param);
    if (param === "#success"){
        console.log("Success!");
        Toastify({
            text: "Successfully added event!",
            duration: 5000,
            newWindow: false,
            close: true,
            gravity: "top", // `top` or `bottom`
            positionLeft: true, // `true` or `false`
            backgroundColor: "linear-gradient(to right, #00b09b, #96c93d)"
        }).showToast();
        window.location.replace('/');
    } else if (param === "#baddate"){
        Toastify({
            text: "Date format incorrect!",
            duration: 5000,
            newWindow: false,
            close: true,
            gravity: "top", // `top` or `bottom`
            positionLeft: true, // `true` or `false`
            backgroundColor: "linear-gradient(to right, #cc0000, #ff0000)"
        }).showToast();
        window.location.replace('/');
    } else if (param === "#badtime"){
        Toastify({
            text: "Time format incorrect!",
            duration: 5000,
            newWindow: false,
            close: true,
            gravity: "top", // `top` or `bottom`
            positionLeft: true, // `true` or `false`
            backgroundColor: "linear-gradient(to right, #cc0000, #ff0000)"
        }).showToast();
        window.location.replace('/');
    }

}

// https://github.com/apvarun/toastify-js i <3 this library
