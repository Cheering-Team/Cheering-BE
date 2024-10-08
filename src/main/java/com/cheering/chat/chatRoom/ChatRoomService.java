package com.cheering.chat.chatRoom;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.chat.Chat;
import com.cheering.chat.ChatRepository;
import com.cheering.chat.ChatRequest;
import com.cheering.chat.ChatResponse;
import com.cheering.chat.message.Message;
import com.cheering.chat.message.MessageRepository;
import com.cheering.chat.session.ChatSession;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.player.relation.PlayerUserResponse;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final PlayerRepository playerRepository;
    private final PlayerUserRepository playerUserRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BadWordService badWordService;
    private final S3Util s3Util;

    public ChatRoomResponse.IdDTO createChatRoom(Long playerId, String name, String description, MultipartFile image, Integer max, User user) {
        if(badWordService.containsBadWords(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        if(badWordService.containsBadWords(description)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Player player = playerRepository.findById(playerId).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
        PlayerUser curUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        String imageUrl = "";
        if(image == null) {
            imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-chat-profile.png";
        } else {
            imageUrl = s3Util.upload(image);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .description(description)
                .max(max)
                .image(imageUrl)
                .player(player)
                .creator(curUser)
                .type(ChatRoomType.PUBLIC)
                .build();

        chatRoomRepository.save(chatRoom);

        return new ChatRoomResponse.IdDTO(chatRoom.getId());
    }

    public List<ChatRoomResponse.ChatRoomSectionDTO> getChatRooms(Long playerId, User user) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        List<ChatRoom> officialChatRooms = chatRoomRepository.findOfficialByPlayer(player);
        List<ChatRoom> publicChatRooms = chatRoomRepository.findPublicByPlayer(player, curPlayerUser);

        List <ChatRoomResponse.ChatRoomDTO> officialChatRoomDTOs = officialChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoomId(chatRoom.getId());
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getPlayer(), null);
        } )).toList();

        List <ChatRoomResponse.ChatRoomDTO> publicChatRoomDTOs =  publicChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoomId(chatRoom.getId());
            Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomAndPlayerUser(chatRoom, curPlayerUser);
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getPlayer(), chatSession.isPresent());
        } )).toList();

        return List.of(new ChatRoomResponse.ChatRoomSectionDTO("official", officialChatRoomDTOs),
                new ChatRoomResponse.ChatRoomSectionDTO("public", publicChatRoomDTOs));
    }

    public List<ChatRoomResponse.ChatRoomSectionDTO> getMyChatRooms(User user) {
        List<PlayerUser> playerUsers = playerUserRepository.findByUserId(user.getId());

        List<Player> players = playerUsers.stream().map((PlayerUser::getPlayer)).toList();

        // 공식은 모두
        List<ChatRoom> officialChatRooms = chatRoomRepository.findOfficialByPlayerIn(players).stream().sorted(Comparator.comparing(chatRoom -> chatRoom.getPlayer().getTeam() != null ? 0 : 1)).toList();

        // 비공식은 내가 참여중인 채팅방만
        List<ChatRoom> publicChatRooms = chatRoomRepository.findPublicByPlayerIn(players).stream()
                .filter((chatRoom -> {
                    PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(chatRoom.getPlayer().getId(), user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));
                    return chatSessionRepository.findByChatRoomAndPlayerUser(chatRoom, curPlayerUser).isPresent();
                }))
                .sorted(Comparator.comparing(chatRoom -> chatRoom.getPlayer().getTeam() != null ? 0 : 1)).toList();

        List<ChatRoomResponse.ChatRoomDTO> officialChatRoomDTOs = officialChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoomId(chatRoom.getId());
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getPlayer(), null);
        } )).toList();

        List<ChatRoomResponse.ChatRoomDTO> publicChatRoomDTOs = publicChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoomId(chatRoom.getId());
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getPlayer(), true);
        } )).toList();

        return List.of(new ChatRoomResponse.ChatRoomSectionDTO("official", officialChatRoomDTOs),
                new ChatRoomResponse.ChatRoomSectionDTO("public", publicChatRoomDTOs));
    }

    public ChatRoomResponse.ChatRoomDTO getChatRoomById(Long chatRoomId, User user) {
        // 존재하지 않는 채팅방 -> 뒤로가기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(chatRoom.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        int count = chatSessionRepository.countByChatRoomId(chatRoomId);
        if(chatRoom.getType().equals(ChatRoomType.OFFICIAL)) {
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curPlayerUser);
        } else {
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curPlayerUser, chatRoom.getCreator());
        }
    }

    @Transactional
    public void addUserToRoom(Long chatRoomId, String sessionId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        PlayerUser playerUser = playerUserRepository.findByPlayerIdAndUserId(chatRoom.getPlayer().getId(), userId).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomAndPlayerUser(chatRoom, playerUser);

        if(chatSession.isEmpty()) {
            ChatSession newChatSession = ChatSession.builder()
                    .sessionId(sessionId)
                    .chatRoom(chatRoom)
                    .playerUser(playerUser)
                    .build();
            chatSessionRepository.save(newChatSession);
        } else {
            chatSession.get().setSessionId(sessionId);
            chatSessionRepository.save(chatSession.get());
        }

        broadcastUserCount(chatRoomId);
    }

    public void sendMessage(ChatRequest.ChatRequestDTO chatDTO, String sessionId) {
        Long chatRoomId = chatDTO.chatRoomId();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        ChatSession chatSession = chatSessionRepository.findByChatRoomAndSessionId(chatRoom, sessionId);

        PlayerUser curPlayerUser = chatSession.getPlayerUser();

        LocalDateTime now = LocalDateTime.now();

        // 비공식 채팅방만 채팅 저장
        if(chatRoom.getType().equals(ChatRoomType.PUBLIC)){
            Optional<Chat> chat = chatRepository.findByChatRoomAndWriterAndCreatedAtMinute(chatRoom.getId(), curPlayerUser.getId());
            if(chat.isPresent()) {
                Message message = Message.builder()
                        .message(chatDTO.message())
                        .chat(chat.get())
                        .build();
                messageRepository.save(message);
            } else {
                Chat newChat = Chat.builder()
                        .chatRoom(chatRoom)
                        .writer(curPlayerUser)
                        .build();
                chatRepository.save(newChat);

                Message message = Message.builder()
                        .message(chatDTO.message())
                        .chat(newChat)
                        .build();
                messageRepository.save(message);
            }
        }
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, new ChatResponse.ChatResponseDTO(chatDTO.message(), now, curPlayerUser));
    }

    @Transactional
    public void removeUserFromRoom(Long chatRoomId, String sessionId) {
        chatSessionRepository.deleteByChatRoomIdAndSessionId(chatRoomId, sessionId);

        broadcastUserCount(chatRoomId);
    }

    private void broadcastUserCount(Long chatRoomId) {
        int count = chatSessionRepository.countByChatRoomId(chatRoomId);
        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId + "/count", count);
    }

    public ChatResponse.ChatListDTO getChats(Long chatRoomId, Pageable pageable) {
        Page<Chat> chats = chatRepository.findByChatRoomId(chatRoomId, pageable);

        return new ChatResponse.ChatListDTO(chats, chats.getContent().stream().map((chat -> {
            List<String> messages = messageRepository.findByChat(chat).stream().map((Message::getMessage)).toList();
            return new ChatResponse.ChatDTO(messages, chat.getCreatedAt(), chat.getWriter());
        })).toList());
    }

    public List<PlayerUserResponse.PlayerUserDTO> getParticipants(Long chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
        if(chatRoom.getType().equals(ChatRoomType.OFFICIAL) || chatRoom.getCreator() == null) {
            return null;
        }

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(chatRoom.getPlayer().getId(), user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));
        List<PlayerUser> playerUsers = chatSessionRepository.findByChatRoom(chatRoom).stream().map(ChatSession::getPlayerUser).toList();

        List<PlayerUser> mutablePlayerUsers = new ArrayList<>(playerUsers);
        mutablePlayerUsers.remove(chatRoom.getCreator());
        mutablePlayerUsers.remove(curPlayerUser);

        List<PlayerUser> sortedPlayerUsers = new ArrayList<>();
        if(!chatRoom.getCreator().equals(curPlayerUser)) sortedPlayerUsers.add(curPlayerUser);

        sortedPlayerUsers.addAll(mutablePlayerUsers);

        return sortedPlayerUsers.stream().map((PlayerUserResponse.PlayerUserDTO::new)).toList();
    }

    @Transactional
    public void deleteChatRoom(Long chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(chatRoom.getPlayer().getId(), user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        if(chatRoom.getCreator().equals(curPlayerUser)) {
            chatRoomRepository.deleteById(chatRoomId);
        }
    }

    public void autoCreateChatRooms() {
        List<Player> players = playerRepository.findAllTeamIsNotNull();

        for(Player player : players){
            Optional<ChatRoom> chatRoom = chatRoomRepository.findByName(player.getKoreanName());

            if(chatRoom.isEmpty()) {
                ChatRoom newChatRoom = ChatRoom.builder()
                        .player(player)
                        .description(player.getKoreanName() + " 팬들끼리 응원해요!")
                        .image(player.getImage())
                        .name(player.getKoreanName())
                        .type(ChatRoomType.OFFICIAL)
                        .build();

                chatRoomRepository.save(newChatRoom);
            }
        }
    }
}
