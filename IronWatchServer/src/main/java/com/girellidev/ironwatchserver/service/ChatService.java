package com.girellidev.ironwatchserver.service;

import com.girellidev.ironwatchserver.dao.ChatMessageDAO;
import com.girellidev.ironwatchserver.model.ChatMessage;
import com.girellidev.ironwatchserver.model.User;

public class ChatService {

    private final ChatMessageDAO chatMessageDAO;
    private final OpenAIService openAIService;

    public ChatService() {
        this.chatMessageDAO = new ChatMessageDAO();
        this.openAIService = new OpenAIService();
    }

    public String processMessage(User user, String content) {
        try {
            ChatMessage userMessage = new ChatMessage();
            userMessage.setEmpresaId(user.getEmpresaId());
            userMessage.setUsuarioId(user.getId());
            userMessage.setRole("user");
            userMessage.setContent(content);
            chatMessageDAO.insert(userMessage);

            String response = openAIService.generateResponse(user.getEmpresaId(), content);

            ChatMessage assistantMessage = new ChatMessage();
            assistantMessage.setEmpresaId(user.getEmpresaId());
            assistantMessage.setUsuarioId(user.getId());
            assistantMessage.setRole("assistant");
            assistantMessage.setContent(response);
            chatMessageDAO.insert(assistantMessage);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro interno no processamento do chat.";
        }
    }
}