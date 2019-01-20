function startUI() {
    // Get the overlay display and make it visible
    document.getElementById("loginOverlay").style.display = "block";
    window.addEventListener('keyup', function(event) {
        if (event.keyCode === 27) {
            closeLogin();
        }
    });

    // Create the UI
    var ui = new firebaseui.auth.AuthUI(firebase.auth());

    // Create the configuration for the UI
    var uiConfig = {
        callbacks: {
            signInSuccessWithAuthResult: function (authResult, redirectUrl) {
                // User successfully signed in.
                // Return type determines whether we continue the redirect automatically
                // or whether we leave that to developer to handle.
                Toastify({
                    text: "Logged in successfully!",
                    duration: 5000,
                    newWindow: false,
                    close: true,
                    gravity: "top", // `top` or `bottom`
                    positionLeft: true, // `true` or `false`
                    backgroundColor: "linear-gradient(to right, #00b09b, #96c93d)"
                }).showToast();
                document.getElementById("loginOverlay").style.display = "none";
                return true;
            },
            uiShown: function () {
                // The widget is rendered.
                // Hide the loader.
                document.getElementById('loader').style.display = 'none';
            }
        },
        // Will use popup for IDP Providers sign-in flow instead of the default, redirect.
        signInFlow: 'popup',
        signInSuccessUrl: '/',
        signInOptions: [
            // Leave the lines as is for the providers you want to offer your users.
            firebase.auth.GoogleAuthProvider.PROVIDER_ID
        ],
        // Terms of service url.
        tosUrl: '<your-tos-url>',
        // Privacy policy url.
        privacyPolicyUrl: '<your-privacy-policy-url>'
    };

    // Start the UI in the auth container with the required configuration
    ui.start('#firebaseui-auth-container', uiConfig);
}

function updateButtonOnLogin() {
    firebase.auth().onAuthStateChanged(function (user) {
        console.log("We are checking if this user is logged in");
        if (user) {
            console.log("Yes, the user is logged in!");
            document.getElementById("userbtn").innerText = "Log Out";
        } else {
            console.log("Unfortunately, the user is not logged in");
            document.getElementById("userbtn").innerText = "Log In";
        }
    });
}

function checkIfLoggedIn(){
    btnText = document.getElementById("userbtn").innerText;
    if (btnText === "LOG OUT") {
        return true;
    }
    return false;
}

function toggleLogin() {
    if (checkIfLoggedIn()) {
        // The user is logged in
        firebase.auth().signOut().then(function () {
            Toastify({
                text: "Successfully signed out",
                duration: 5000,
                newWindow: false,
                close: true,
                gravity: "top", // `top` or `bottom`
                positionLeft: true, // `true` or `false`
                backgroundColor: "linear-gradient(to right, #5f156b, #360a3d)"
            }).showToast();
            console.log("Successfully logged out the user!");
        }, function (error) {
            console.log("Unable to logout the user");
            // An error happened.
        });
    } else {
        console.log("Logging the user in...");
        // The user is logged OUT
        // Start the login UI
        startUI();
    }
}

function closeLogin(){
    document.getElementById("loginOverlay").style.display = "none";
}