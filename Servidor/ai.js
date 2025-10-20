// ai.js
const fs = require('fs');
const OpenAI = require('openai');

function wait(ms) {
    return new Promise(res => setTimeout(res, ms));
}

function makeClient(apiKey) {
    if (!apiKey) throw new Error('API key obrigatória.');
    return new OpenAI({
        apiKey
    });
}

/**
 * queryOpenAI - envia texto para IA com systemPrompt opcional
 */
async function queryOpenAI(text, apiKey, options = {}) {
    const {
        model = 'gpt-4',
            temperature = 0.2,
            maxTokens = 1500,
            timeout = 20000,
            retries = 3,
            systemPrompt = null
    } = options;

    if (!text) throw new Error('Texto vazio enviado.');

    const client = makeClient(apiKey);
    let attempt = 0;
    let delay = 1000;

    while (attempt <= retries) {
        attempt++;
        const controller = new AbortController();
        const timer = setTimeout(() => controller.abort(), timeout);

        try {
            const messages = [];
            if (systemPrompt) messages.push({
                role: 'system',
                content: systemPrompt
            });
            messages.push({
                role: 'user',
                content: text
            });

            const resp = await client.chat.completions.create({
                model,
                messages,
                temperature,
                max_tokens: maxTokens,
                signal: controller.signal
            });

            clearTimeout(timer);

            if (resp && resp.choices && resp.choices.length > 0) {
                const choice = resp.choices[0];
                const content = (choice.message && choice.message.content) || choice.text || '';
                return content.toString();
            }

            throw new Error('Resposta inesperada da IA.');
        } catch (err) {
            clearTimeout(timer);

            const status = err.status || (err.response && err.response.status) || null;

            if (err.name === 'AbortError' || (err.message && err.message.includes('aborted'))) {
                if (attempt > retries) throw new Error(`Timeout após ${timeout}ms`);
                await wait(delay);
                delay *= 2;
                continue;
            }

            if (status === 429 || (status && status >= 500 && status < 600)) {
                if (attempt > retries) throw new Error(`OpenAI retornou status ${status} após ${attempt} tentativas`);
                await wait(delay + Math.floor(Math.random() * 300));
                delay *= 2;
                continue;
            }

            throw err;
        }
    }

    throw new Error('Falha desconhecida queryOpenAI.');
}

/**
 * transcribeAudio - transcreve arquivo de áudio (Whisper/OpenAI)
 */
async function transcribeAudio(filePath, apiKey, options = {}) {
    const {
        model = 'gpt-4o-transcribe',
            language = null,
            timeout = 60000,
            retries = 2
    } = options;

    if (!fs.existsSync(filePath)) throw new Error('Arquivo de áudio não encontrado: ' + filePath);

    const client = makeClient(apiKey);
    let attempt = 0;
    let delay = 1000;

    while (attempt <= retries) {
        attempt++;
        const controller = new AbortController();
        const timer = setTimeout(() => controller.abort(), timeout);

        try {
            const fileStream = fs.createReadStream(filePath);
            const resp = await client.audio.transcriptions.create({
                file: fileStream,
                model,
                ...(language ? {
                    language
                } : {}),
                signal: controller.signal
            });

            clearTimeout(timer);

            if (resp && (resp.text || resp.transcript || (resp.data && resp.data.text))) {
                const text = resp.text || resp.transcript || (resp.data && resp.data.text) || '';
                return text.toString();
            }

            throw new Error('Resposta inesperada da transcrição.');
        } catch (err) {
            clearTimeout(timer);

            const status = err.status || (err.response && err.response.status) || null;

            if (err.name === 'AbortError' || (err.message && err.message.includes('aborted'))) {
                if (attempt > retries) throw new Error(`Timeout transcrição após ${timeout}ms`);
                await wait(delay);
                delay *= 2;
                continue;
            }

            if (status === 429 || (status && status >= 500 && status < 600)) {
                if (attempt > retries) throw new Error(`Transcrição falhou com status ${status} após ${attempt} tentativas`);
                await wait(delay + Math.floor(Math.random() * 300));
                delay *= 2;
                continue;
            }

            throw err;
        }
    }

    throw new Error('Falha desconhecida na transcrição.');
}

module.exports = {
    queryOpenAI,
    transcribeAudio
};