let selectedUserIdForChat = null;

async function searchUserForChat() {
    const username = document.getElementById('username-search-chat').value.trim();
    const resultsDiv = document.getElementById('search-results-chat');
    resultsDiv.innerHTML = '';

    if (!username) {
        resultsDiv.textContent = 'Введите имя пользователя';
        return;
    }

    try {
        const data = await apiRequest(`/api/users/by-username/${username}`);

        if (!data.object) {
            resultsDiv.textContent = 'Пользователь не найден';
            selectedUserIdForChat = null;
            return;
        }

        const user = data.object;
        selectedUserIdForChat = user.id;

        const userBox = document.createElement('div');
        userBox.textContent = `${user.username}`;
        resultsDiv.appendChild(userBox);

        const titleInput = document.createElement('input');
        titleInput.type = 'text';
        titleInput.id = 'chat-title-create-private';
        titleInput.placeholder = 'Название чата';
        resultsDiv.appendChild(titleInput);

        const createBtn = document.createElement('button');
        createBtn.textContent = 'Создать чат';
        createBtn.onclick = createPrivateChat;
        resultsDiv.appendChild(createBtn);
        location.reload();
    } catch (error) {
        resultsDiv.textContent = error.message;
        selectedUserIdForChat = null;
    }
}

let selectedUsersForGroup = [];

async function searchUsersForGroup() {
    const username = document.getElementById('group-username-search').value.trim();
    const resultsDiv = document.getElementById('group-search-results');
    resultsDiv.innerHTML = ''; // очищаем прошлые результаты

    if (!username) {
        resultsDiv.textContent = 'Введите имя пользователя';
        return;
    }

    try {
        const data = await apiRequest(`/api/users/by-username/${username}`);

        if (!data.object) {
            resultsDiv.textContent = 'Пользователь не найден';
            return;
        }

        const user = data.object;

        if (selectedUsersForGroup.find(u => u.id === user.id)) {
            resultsDiv.textContent = 'Пользователь уже выбран';
            return;
        }

        selectedUsersForGroup.push({ id: user.id, username: user.username });

        renderSelectedGroupUsers();
        location.reload();
    } catch (error) {
        resultsDiv.textContent = `Ошибка поиска пользователя: ${error.message}`;
    }
}

function renderSelectedGroupUsers() {
    const listDiv = document.getElementById('group-selected-users');
    listDiv.innerHTML = '';

    selectedUsersForGroup.forEach((user, index) => {
        const userDiv = document.createElement('div');
        userDiv.textContent = `${user.username}`;

        const removeBtn = document.createElement('button');
        removeBtn.textContent = 'Удалить';
        removeBtn.onclick = () => {
            selectedUsersForGroup.splice(index, 1);
            renderSelectedGroupUsers();
        };

        userDiv.appendChild(removeBtn);
        listDiv.appendChild(userDiv);
    });
}

async function updateUser() {
    const id = getStoredUserId();
    const username = document.getElementById('update-or-delete-username').value;


    try {
        const data = await apiRequest('/api/users', {
            method: 'PUT',
            body: JSON.stringify({ id: parseInt(id), username })
        });

        showOutput(data.message);
    } catch (error) {
        showOutput(`${error.message}`);
    }
}

async function deleteUser() {
    const userId = document.getElementById('update-or-delete-username').value;

    try {
        const data = await apiRequest(`/api/users/${userId}`, {
            method: 'DELETE'
        });


        showOutput(data.message);
            const currentUserId = getStoredUserId();

        if (parseInt(userId) === currentUserId) {
            logout();
        }
    } catch (error) {
        showOutput(`${error.message}`);
    }
}