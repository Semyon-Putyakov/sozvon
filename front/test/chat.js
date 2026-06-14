let currentChatId = null;
let currentPage = 0;
let allMessagesLoaded = false;
let isLoading = false;
const pageSize = 100;
let participantsVisible = false;

async function getUserChats() {
    const userId = getStoredUserId();
    const chatsListDiv = document.getElementById('user-chats-list');
    chatsListDiv.innerHTML = '';

    try {
        const data = await apiRequest(`/api/chats/user/${userId}`);

        if (!data || !data.object || data.object.length === 0) {
            chatsListDiv.textContent = 'Чаты не найдены';
            return;
        }

        const chats = data.object;

        chats.forEach(chat => {
            const chatBtn = document.createElement('button');
            chatBtn.textContent = chat.title;
            chatBtn.dataset.chatId = chat.id;
            chatBtn.style.display = 'block';
            chatBtn.style.marginBottom = '5px';
            chatBtn.onclick = () => {
                openChat(chat.id,chat.title);
            };

            chatsListDiv.appendChild(chatBtn);
        });
    } catch (error) {
        chatsListDiv.textContent = `Ошибка получения чатов пользователя: ${error.message}`;
    }
}

async function openChat(chatId) {
    currentChatId = chatId;
    currentPage = 0;
    allMessagesLoaded = false;

    const messagesWindow = document.getElementById('chat-messages-window');
    messagesWindow.innerHTML = '';

    await loadMessages(chatId, currentPage, true);

    messagesWindow.onscroll = async function () {
        if (allMessagesLoaded || isLoading) return;

        if (messagesWindow.scrollTop <= 10) {
            currentPage++;
            const prevScrollHeight = messagesWindow.scrollHeight;

            await loadMessages(chatId, currentPage, false);

            messagesWindow.scrollTop = messagesWindow.scrollHeight - prevScrollHeight;
        }
    };
}

async function loadMessages(chatId, page, scrollToBottom) {
    isLoading = true;
    const messagesWindow = document.getElementById('chat-messages-window');
    const userId = getStoredUserId();

    try {
        const data = await apiRequest(`/api/messages/chat/${chatId}?userId=${userId}&page=${page}&size=${pageSize}`);
        const messages = data?.object || [];

        if (!messages.length || messages.length < pageSize) {
            allMessagesLoaded = true;
        }

        if (page === 0 && !messages.length) {
            messagesWindow.textContent = 'Сообщений нет';
            isLoading = false;
            return;
        }

        const fragment = document.createDocumentFragment();

        messages.forEach(msg => {
            const msgBox = document.createElement('div');
            msgBox.style.cssText = `
        border:1px solid #ccc; 
        padding:8px; 
        margin-bottom:5px; 
        border-radius:5px; 
        background-color:#f9f9f9;
        position: relative;
    `;

            const senderEl = document.createElement('div');
            senderEl.textContent = msg.senderName;
            senderEl.style.cssText = 'font-weight:bold; margin-bottom:4px;';
            msgBox.appendChild(senderEl);

            const textEl = document.createElement('span');
            textEl.textContent = msg.text;
            msgBox.appendChild(textEl);

            if (Number(msg.senderId) === Number(userId)) {
                const editBtn = document.createElement('button');
                editBtn.textContent = 'Изменить';
                editBtn.style.cssText = `
            position: absolute;
            top: 5px;
            right: 5px;
            font-size: 12px;
        `;

                editBtn.onclick = () => {
                    textEl.style.display = 'none';
                    editBtn.style.display = 'none';

                    const inputEl = document.createElement('input');
                    inputEl.type = 'text';
                    inputEl.value = msg.text;
                    inputEl.style.width = '70%';

                    const saveBtn = document.createElement('button');
                    saveBtn.textContent = 'Сохранить';
                    saveBtn.style.marginLeft = '5px';

                    saveBtn.onclick = async () => {
                        try {
                            await apiRequest(`/api/messages`, {
                                method: 'PUT',
                                body: JSON.stringify({
                                    messageId: parseInt(msg.id),
                                    newText: inputEl.value,
                                    senderId: parseInt(msg.senderId)
                                })
                            });

                            textEl.textContent = inputEl.value;
                            inputEl.remove();
                            saveBtn.remove();
                            textEl.style.display = '';
                            editBtn.style.display = '';
                        } catch (err) {
                            alert(`Ошибка обновления: ${err.message}`);
                        }
                    };

                    msgBox.appendChild(inputEl);
                    msgBox.appendChild(saveBtn);
                };

                msgBox.appendChild(editBtn);

                const deleteBtn = document.createElement('button');
                deleteBtn.textContent = 'Удалить';
                deleteBtn.style.cssText = `
            display: block;
            margin-top: 8px;
            font-size: 12px;
            color: red;
        `;

                deleteBtn.onclick = async () => {
                    if (!confirm("Удалить сообщение?")) return;
                    try {
                        await apiRequest('/api/messages', {
                            method: 'DELETE',
                            body: JSON.stringify({
                                messageId: parseInt(msg.id),
                                userId: parseInt(msg.senderId)
                            })
                        });
                        msgBox.remove();
                    } catch (err) {
                        alert(`Ошибка удаления: ${err.message}`);
                    }
                };

                msgBox.appendChild(deleteBtn);
            }

            fragment.insertBefore(msgBox, fragment.firstChild);
        });

        if (scrollToBottom) {
            messagesWindow.appendChild(fragment);
            messagesWindow.scrollTop = messagesWindow.scrollHeight;
        } else {
            const firstMessage = messagesWindow.firstChild;
            const scrollTopBefore = messagesWindow.scrollTop;
            const firstMessageOffset = firstMessage ? firstMessage.offsetTop : 0;

            messagesWindow.insertBefore(fragment, messagesWindow.firstChild);

            if (firstMessage) {
                messagesWindow.scrollTop = scrollTopBefore + (messagesWindow.firstChild.offsetTop - firstMessageOffset);
            }
        }

    } catch (error) {
        console.error('Ошибка загрузки сообщений:', error);
        messagesWindow.textContent = `Ошибка загрузки сообщений: ${error.message}`;
    }

    isLoading = false;
}

async function searchChats() {
    const title = document.getElementById('chat-title-search').value;
    const userId = getStoredUserId();
    const resultsDiv = document.getElementById('chat-search-results');
    resultsDiv.innerHTML = '';

    try {
        const data = await apiRequest(`/api/chats/search?title=${title}&userId=${userId}`);

        let chats = [];
        if (Array.isArray(data.object)) {
            chats = data.object;
        } else if (data.object) {
            chats = [data.object];
        }

        if (chats.length === 0) {
            resultsDiv.textContent = 'Чаты не найдены';
            return;
        }

        chats.forEach(chat => {
            const chatBtn = document.createElement('button');
            chatBtn.textContent = chat.title;
            chatBtn.dataset.chatId = chat.id;
            chatBtn.style.display = 'block';
            chatBtn.style.width = '100%';
            chatBtn.style.marginBottom = '5px';
            chatBtn.onclick = () => openChat(chat.id);
            resultsDiv.appendChild(chatBtn);
        });
    } catch (error) {
        resultsDiv.textContent = `Ошибка поиска чатов: ${error.message}`;
    }
}

async function createPrivateChat() {
    const userId = getStoredUserId();
    if (!userId || userId <= 0) {
        alert("Ваш ID не найден или некорректен. Войдите заново.");
        return;
    }

    if (!selectedUserIdForChat || selectedUserIdForChat <= 0) {
        alert("Выберите корректного пользователя для чата");
        return;
    }

    const titleInput = document.getElementById('chat-title-create-private');
    const title = titleInput.value.trim() || null;

    try {
        const payload = {
            title: title,
            isGroup: false,
            participantIds: [userId, selectedUserIdForChat]
        };

        const data = await apiRequest('/api/chats', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        alert(`Чат создан: ${data.message || JSON.stringify(data)}`);
        document.getElementById('search-results-chat').innerHTML = '';
        selectedUserIdForChat = null;
    } catch (error) {
        alert(`Ошибка создания чата: ${error.message}`);
    }
}

function toggleGroupChatForm() {
    const form = document.getElementById('group-chat-form');
    if (form.style.display === 'none') {
        form.style.display = 'block';
    } else {
        form.style.display = 'none';
    }
}

async function createGroupChat() {
    const userId = getStoredUserId();
    if (!userId || userId <= 0) {
        alert("Ваш ID не найден или некорректен. Войдите заново.");
        return;
    }

    if (selectedUsersForGroup.length === 0) {
        alert("Выберите участников для группового чата");
        return;
    }

    const titleInput = document.getElementById('group-chat-title');
    const title = titleInput.value.trim() || null;

    const participantIds = [userId, ...selectedUsersForGroup.map(u => u.id)];

    try {
        const payload = {
            title,
            isGroup: true,
            participantIds
        };

        const data = await apiRequest('/api/chats', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        alert(`Групповой чат создан: ${data.message || JSON.stringify(data)}`);
        selectedUsersForGroup = [];
        document.getElementById('group-search-results').innerHTML = '';
        document.getElementById('group-selected-users').innerHTML = '';
        titleInput.value = '';
    } catch (error) {
        alert(`Ошибка создания чата: ${error.message}`);
    }
}


function openChat(chatId, chatTitle) {
    currentChatId = chatId;
    participantsVisible = false;

    document.getElementById('chat-controls').style.display = 'block';
    document.getElementById('current-chat-title').textContent = chatTitle;
    document.getElementById('participant-add-form').style.display = 'none';
    document.getElementById('chat-participants').style.display = 'none';

    loadMessages(chatId, 0, true);
}

async function deleteCurrentChat() {
    if (!currentChatId) return;
    const userId = getStoredUserId();
    try {
        await apiRequest(`/api/chats/${currentChatId}?userId=${userId}`, { method: 'DELETE' });
        alert('Чат удалён');
        currentChatId = null;
        document.getElementById('chat-controls').style.display = 'none';
        getUserChats();
    } catch (err) {
        alert(`Ошибка удаления: ${err.message}`);
    }
}

function toggleParticipants() {
    if (!currentChatId) return;
    participantsVisible = !participantsVisible;

    const participantsDiv = document.getElementById('chat-participants');
    const addFormDiv = document.getElementById('participant-add-form');

    participantsDiv.style.display = participantsVisible ? 'block' : 'none';
    addFormDiv.style.display = participantsVisible ? 'block' : 'none';

    if (participantsVisible) {
        loadParticipants(currentChatId);
    }
}

async function loadParticipants(chatId) {
    try {
        const data = await apiRequest(`/api/chats/${chatId}/participants`);
        const container = document.getElementById('chat-participants');
        container.innerHTML = '';

        const users = Array.isArray(data.object) ? data.object : data.object ? [data.object] : [];
        if (users.length === 0) {
            container.textContent = 'Участников нет';
            return;
        }

        users.forEach(user => {
            const div = document.createElement('div');
            div.textContent = user.username;

            const removeBtn = document.createElement('button');
            removeBtn.textContent = 'Удалить';
            removeBtn.style.marginLeft = '5px';
            removeBtn.onclick = () => removeParticipantFromChat(chatId, user.id);

            div.appendChild(removeBtn);
            container.appendChild(div);
        });
    } catch (error) {
        console.error('Ошибка загрузки участников:', error);
    }
}

async function searchUserToAdd() {
    const username = document.getElementById('username-search')?.value.trim();
    if (!username || !currentChatId) return;

    try {
        const data = await apiRequest(`/api/users/by-username/${username}`);
        const container = document.getElementById('user-search-result');
        container.innerHTML = '';

        const user = data.object;
        if (!user) {
            container.textContent = 'Пользователь не найден';
            return;
        }

        const div = document.createElement('div');
        div.textContent = user.username;

        const addBtn = document.createElement('button');
        addBtn.textContent = 'Добавить';
        addBtn.onclick = () => addParticipantToChat(currentChatId, user.id);

        div.appendChild(addBtn);
        container.appendChild(div);
    } catch (error) {
        console.error('Ошибка поиска пользователя:', error);
    }
}

async function addParticipantToChat(chatId, participantId) {
    const userId = getStoredUserId();
    if (!chatId || !participantId || !userId) return;

    try {
        await apiRequest(`/api/chats/${chatId}/participants`, {
            method: 'POST',
            body: JSON.stringify({ chatId, userId, participantId })
        });
        alert('Участник добавлен');
        loadParticipants(chatId);
    } catch (error) {
        alert(`Ошибка добавления: ${error.message}`);
    }
}

async function removeParticipantFromChat(chatId, participantId) {
    const userId = getStoredUserId();
    if (!chatId || !participantId || !userId) return;

    try {
        await apiRequest(`/api/chats/${chatId}/participants/${participantId}?userId=${userId}`, { method: 'DELETE' });
        alert('Участник удалён');
        loadParticipants(chatId);
    } catch (error) {
        alert(`Ошибка удаления: ${error.message}`);
    }
}


