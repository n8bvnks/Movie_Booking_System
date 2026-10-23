// shared by the pages that update live (index.html and admin.html), loaded after auth.js
// same idea as client.js in the messaging lab: connect, subscribe, fetch a snapshot, then apply only newer events
//
// a page calls startLive({ onSnapshot, onEvent, onStatus })
//   onSnapshot(showings) - the full list of showings, replace what the page has
//   onEvent(event)       - one ShowingCreated or SeatsChanged event, event.showing is the showing as it is now
//   onStatus(text)       - optional, shows "connecting", "live", "disconnected..." on the page

// the websocket server runs in booking-service on its own port (live.ws.port in application.properties)
const LIVE_PORT = 8084;

function startLive(handlers) {
    let lastVersion = 0;      // the newest version this page has applied
    let ready = false;        // false until the snapshot has been applied
    let pending = [];         // events that arrive while the snapshot is still loading
    let socket = null;
    let connectionEpoch = 0;  // stops answers belonging to an old connection from being applied
    let retryTimer = null;

    function status(text) {
        if (handlers.onStatus) handlers.onStatus(text);
    }

    // W2 in the lab - an update is only applied when its version is greater than the last one applied
    // so a late or repeated event can never put old seat numbers back on the page
    function shouldApply(update) {
        return update.version > lastVersion;
    }

    function applyEvent(update) {
        if (!shouldApply(update)) return;
        lastVersion = update.version;
        handlers.onEvent(update);
    }

    function connect() {
        clearTimeout(retryTimer);
        const epoch = ++connectionEpoch;
        ready = false;
        pending = [];

        status("connecting");
        // location.hostname so it also works when the page is opened from another computer or phone
        const ws = new WebSocket("ws://" + location.hostname + ":" + LIVE_PORT);
        socket = ws;

        // subscribe straight away, the token proves the user is logged in
        ws.onopen = () => {
            if (epoch !== connectionEpoch) return;
            ws.send(JSON.stringify({ type: "subscribe", token: getToken() }));
        };

        ws.onmessage = async ({ data }) => {
            if (epoch !== connectionEpoch) return;
            const message = JSON.parse(data);

            if (message.type === "subscribed") {
                // the server is now sending events to this tab, so fetching the snapshot cannot miss anything
                try {
                    const response = await authFetch("/api/v1/live/snapshot", { cache: "no-store" });
                    if (!response.ok) throw new Error("Snapshot HTTP " + response.status);
                    const snapshot = await response.json();
                    if (epoch !== connectionEpoch) return;

                    // the snapshot is the truth, so its version replaces lastVersion
                    // (after a server restart the version counter starts again from 0)
                    lastVersion = snapshot.version;
                    handlers.onSnapshot(snapshot.showings);
                    ready = true;

                    // events that arrived during the fetch are applied now, oldest first, only if newer than the snapshot
                    const queued = pending.sort((a, b) => a.version - b.version);
                    pending = [];
                    queued.forEach(applyEvent);
                    status("live");
                } catch (error) {
                    status("could not load the snapshot, retrying");
                    ws.close();
                }

            } else if (message.type === "ShowingCreated" || message.type === "SeatsChanged") {
                if (ready) applyEvent(message);
                else pending.push(message);

            } else if (message.type === "error") {
                // an old token (for example the server was restarted) means the user has to log in again
                if (message.message === "Login required") logout();
                status(message.message);
            }
        };

        // if the connection drops, try again, a reconnect always fetches a fresh snapshot
        ws.onclose = () => {
            if (epoch !== connectionEpoch) return;
            status("disconnected, reconnecting");
            socket = null;
            retryTimer = setTimeout(connect, 1500);
        };

        ws.onerror = () => status("connection error");
    }

    connect();
}