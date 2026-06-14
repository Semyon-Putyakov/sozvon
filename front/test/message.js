async function sendMessage() {
    if (!currentChatId) { alert("Выберите чат"); return; }

    const chatId = currentChatId;
    const senderId = getStoredUserId();
    const textInput = document.getElementById('message-text');
    const text = textInput.value.trim();

    try {const data = await apiRequest('/api/messages', {
            method: 'POST',
            body: JSON.stringify({
                chatId: parseInt(chatId),
                senderId: parseInt(senderId),
                text
            })
        });

        textInput.value = '';

        await loadMessages(chatId, 0, true);

        showOutput(data);
    } catch (error) {showOutput(`Ошибка отправки сообщения: ${error.message}`);}
}

async function editMessage(messageId) {
    const senderId = Number(getStoredUserId());
    const inputEl = document.getElementById(`message-input-${messageId}`);
    const newText = inputEl.value.trim();

    if (!newText) {
        alert("Введите текст сообщения");
        return;
    }

    try {
        await apiRequest(`/api/messages/${messageId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ senderId, text: newText })
        });

        alert("Сообщение обновлено");
        inputEl.blur(); // снимаем фокус
        await loadMessages(currentChatId, 0, true);
    } catch (error) {
        console.error('Ошибка редактирования сообщения:', error);
        alert(`Ошибка редактирования сообщения: ${error.message}`);
    }
}



async function deleteMessage() {
    const messageId = document.getElementById('message-id').value;
    const userId = document.getElementById('sender-id').value;
    
    try {const data = await apiRequest('/api/messages', {
            method: 'DELETE',
            body: JSON.stringify({
                messageId: parseInt(messageId),
                userId: parseInt(userId)})});
        showOutput(data);
    } catch (error) {
        showOutput(`Ошибка удаления сообщения: ${error.message}`);
    }
}

async function getChatMessages() {
    const chatId = document.getElementById('chat-id-message').value;
    const userId = document.getElementById('sender-id').value;
    
    try {
        const data = await apiRequest(`/api/messages/chat/${chatId}?userId=${userId}&page=0&size=50`);
        showOutput(data);
    } catch (error) {
        showOutput(`Ошибка получения сообщений: ${error.message}`);
    }
}