const {
    MAX_CONNECTIONS_PER_IP
} = require('./config');

const connectionsPerIP = {};

function rateLimit(ip) {
    if (!connectionsPerIP[ip]) connectionsPerIP[ip] = 0;
    connectionsPerIP[ip]++;
    if (connectionsPerIP[ip] > MAX_CONNECTIONS_PER_IP) return false;
    return true;
}

function resetRateLimit(ip) {
    if (connectionsPerIP[ip]) connectionsPerIP[ip]--;
}

function printMenu(title, options) {
    const {
        MENU_COLOR,
        MENU_RESET
    } = require('./config');
    console.clear();
    console.log(MENU_COLOR);
    console.log(`====== ${title} ======`);
    options.forEach((opt, i) => console.log(`${i+1}. ${opt}`));
    console.log(MENU_RESET);
}

module.exports = {
    rateLimit,
    resetRateLimit,
    printMenu
};