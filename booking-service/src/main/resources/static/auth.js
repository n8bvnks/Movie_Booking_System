// shared by every page - keeps the login token in the browser and adds it to every request
// each page loads this with <script src="auth.js"></script> before its own script

// the token, username and role are saved in the browser when the user logs in
function getToken() { return localStorage.getItem("token"); }
function getUsername() { return localStorage.getItem("username"); }
function getRole() { return localStorage.getItem("role"); }

// called by the login page after a successful login
function saveLogin(data) {
    localStorage.setItem("token", data.token);
    localStorage.setItem("username", data.username);
    localStorage.setItem("role", data.role);
}

// forget the login and go back to the login page
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    window.location.href = "login.html";
}

// called at the top of every page except login, if there is no token the user is not logged in
function requireLogin() {
    if (!getToken()) {
        window.location.href = "login.html";
    }
}

// use this instead of fetch for every /api/v1 call, it adds the token to the request
// if the server says 401 (no token, or the server restarted and forgot it) send them to log in again
async function authFetch(url, options = {}) {
    options.headers = Object.assign({}, options.headers, {
        "Authorization": "Bearer " + getToken()
    });

    const response = await fetch(url, options);

    if (response.status === 401) {
        logout();
    }
    return response;
}

// shows "Logged in as alice (CUSTOMER)" and a log out button in the element with id loginBar
function showLoginBar() {
    const bar = document.getElementById("loginBar");
    if (!bar) return;
    bar.textContent = "Logged in as " + getUsername() + " (" + getRole() + ") ";

    const button = document.createElement("button");
    button.textContent = "Log out";
    button.addEventListener("click", logout);
    bar.appendChild(button);
}