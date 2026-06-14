const API_BASE_URL = 'http://localhost:1010';

async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const token = authToken || getStoredToken();

    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` }),
            ...options.headers
        }
    };

    try {
        const response = await fetch(url, { ...defaultOptions, ...options });

        const text = await response.text(); // <-- ВАЖНО: сначала текст

        let data = null;

        if (text) {
            try {
                data = JSON.parse(text);
            } catch (e) {

                if (e.message.includes('Unexpected end of JSON input')) {
                    data = null;
                } else {
                    throw new Error('Некорректный JSON от сервера');
                }
            }
        }

        if (!response.ok) {
            throw new Error(data?.message || ' ');
        }

        return data;

    } catch (error) {
        showOutput(error.message);

    }
}

//////////////////////// Работа с JWT
let authToken = null;

function setAuthToken(token) {
    authToken = token;
    setItemWithExpiry('authToken', token, 24 * 60 * 60 * 1000);
    showSections();
}

function getStoredToken() {
    return getItemWithExpiry('authToken');
}
////////////////////////


//////////////////////// Работа с Username
function setUsername(username) {
    setItemWithExpiry('username', username, 24 * 60 * 60 * 1000);
}

function getStoredUsername() {
    return getItemWithExpiry('username'); // надо сделать хранение значения
}
////////////////////////


//////////////////////// Работа с ID
function setUserId(id) {
    setItemWithExpiry('userId', id, 24 * 60 * 60 * 1000);
}

function getStoredUserId() {
    return getItemWithExpiry('userId'); // надо сделать хранение значения
}
////////////////////////


//////////////////////// Util
// Установка времени хранения id, или token, или username
function setItemWithExpiry(key, value, ttlMs) {
    const expiresAt = Date.now() + ttlMs;
    const data = { value, expiresAt };
    localStorage.setItem(key, JSON.stringify(data));
}
// Вывод ошибок
function showOutput(message) {
    const output = document.getElementById('output');

    if (message == null || message === '' || message === undefined) {
        output.textContent = '';
        output.style.display = 'none'; // скрываем
        return;
    }

    output.style.display = 'block'; // показываем

    if (typeof message === 'object') {
        output.textContent = JSON.stringify(message, null, 2);
    } else {
        output.textContent = String(message);
    }
}
// Вывод секций
function showSections() {
    const sections = ['user-section', 'chat-section', 'message-section', 'logout-section'];
    sections.forEach(section => {
        const element = document.getElementById(section);
        if (element) {
            element.style.display = 'block';
        }
    });
}
// Достаёт значение из localStorage и проверяет, не истёк ли срок его действия.
function getItemWithExpiry(key) {
    const item = localStorage.getItem(key);
    if (!item) return null;

    try {
        const data = JSON.parse(item);
        if (Date.now() > data.expiresAt) {
            localStorage.removeItem(key);
            return null;
        }
        return data.value;
    } catch (e) {
        localStorage.removeItem(key);
        return null;
    }
}
////////////////////////

// Восстановление после перезагрузки
window.addEventListener('load', () => { // че делает
    const storedToken = getStoredToken();
    const storedUsername = getStoredUsername();
    const storedUserId = getStoredUserId();

    if (storedToken) {
        authToken = storedToken;
        userName = storedUsername
        showSections();                // Показываем нужные секции
    }

    if (storedUsername) {
        const usernameEl = document.getElementById('current-username-view');
        if (usernameEl) {
            usernameEl.textContent = storedUsername; // Показываем имя
        }
    }

    if (storedUserId) {
        // Можно восстановить id пользователя, если нужно
    }

    const authSection = document.getElementById('auth-section');
    const logoutSection = document.getElementById('logout-section');

    if (storedToken) {
        if (authSection) authSection.style.display = 'none';
        if (logoutSection) logoutSection.style.display = 'block';
    } else {
        if (authSection) authSection.style.display = 'block';
        if (logoutSection) logoutSection.style.display = 'none';
    }
});
