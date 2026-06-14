//////////////////////// login
async function login() {
    const loginValue = document.getElementById('login').value;
    const password = document.getElementById('password').value;

    try {
        const data = await apiRequest('/authentication/login', {
            method: 'POST',
            body: JSON.stringify({ login: loginValue, password })
        });

        setAuthToken(data.token);
        setUserId(data.id);

        const username = data.username;
        setUsername(username);

        const usernameEl = document.getElementById('current-username-view');
        if (usernameEl) usernameEl.textContent = username;

        const logoutSection = document.getElementById('logout-section');
        if (logoutSection) logoutSection.style.display = 'block';

        const authSection = document.getElementById('auth-section');
        if (authSection) authSection.style.display = 'none';

        showOutput('Успешный вход!');

    } catch (error) {
        showOutput(`${error.message}`);
    }
}
////////////////////////

//////////////////////// register
async function register() {
    const login = document.getElementById('login').value;
    const password = document.getElementById('password').value;
    const username = document.getElementById('username').value;

    try {
        const data = await apiRequest('/authentication/registration', {
            method: 'POST',
            body: JSON.stringify({ login, password, username })
        });

        showOutput('Успешная регистрация!');

    } catch (error) {
        showOutput(`${error.message}`);
    }
}
////////////////////////

//////////////////////// logout
function logout() {
    authToken = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');

    showOutput("Вы вышли из системы");

    location.reload();
}

