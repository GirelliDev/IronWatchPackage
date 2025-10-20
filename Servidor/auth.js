const crypto = require('crypto');

function generateCode(length = 6) {
    return Math.floor(Math.random() * (10 ** length)).toString().padStart(length, '0');
}

function verifyCode(input, correct) {
    return input === correct;
}

module.exports = {
    generateCode,
    verifyCode
};