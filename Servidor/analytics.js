const axios = require('axios');

// Envia analytics pro mobile e desktop
async function sendAnalytics(data) {
    try {
        // Aqui você coloca os endpoints dos apps
        await axios.post('http://localhost:5600/analytics', data);
        await axios.post('http://localhost:5700/analytics', data);
    } catch (err) {
        console.error("Erro enviando analytics:", err.message);
    }
}

module.exports = {
    sendAnalytics
};