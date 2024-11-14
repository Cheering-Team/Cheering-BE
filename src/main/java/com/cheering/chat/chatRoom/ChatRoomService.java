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
import com.cheering.fan.CommunityType;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final FanRepository fanRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BadWordService badWordService;
    private final S3Util s3Util;

    public ChatRoomResponse.IdDTO createChatRoom(Long communityId, String name, String description, MultipartFile image, Integer max, User user) {
        if(badWordService.containsBadWords(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }
        if(badWordService.containsBadWords(description)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }
        Fan curUser = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        String imageUrl = "";
        if(image == null) {
            imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-chat-profile.png";
        } else {
            imageUrl = s3Util.upload(image);
        }

        Optional<Player> player = playerRepository.findById(communityId);

        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .description(description)
                .max(max)
                .image(imageUrl)
                .communityId(communityId)
                .manager(curUser)
                .type(ChatRoomType.PUBLIC)
                .communityType(player.isPresent() ? CommunityType.PLAYER : CommunityType.TEAM)
                .build();

        chatRoomRepository.save(chatRoom);
        return new ChatRoomResponse.IdDTO(chatRoom.getId());
    }

    // 대표 채팅방 조회
    public ChatRoomResponse.ChatRoomDTO getOfficialChatRoom(Long communityId) {
        ChatRoom chatRoom = chatRoomRepository.findOfficialByCommunityId(communityId);

        int count = chatSessionRepository.countByChatRoom(chatRoom);
        return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, false);
    }

    // 일반 채팅방 조회
    public ChatRoomResponse.ChatRoomListDTO getChatRooms(Long communityId, String sortBy, String name, Pageable pageable, User user) {
        Page<ChatRoom> chatRoomList;

        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(sortBy.equals("participants")) {
            if(name.isEmpty()){
                chatRoomList = chatRoomRepository.findPublicByCommunityIdByCount(communityId, curFan, pageable);
            } else {
                chatRoomList = chatRoomRepository.findPublicByCommunityIdByCountWithName(communityId, curFan, name, pageable);
            }
        } else {
            if(name.isEmpty()){
                chatRoomList = chatRoomRepository.findPublicByCommunityIdByCreatedAt(communityId, curFan, pageable);
            } else {
                chatRoomList = chatRoomRepository.findPublicByCommunityIdByCreatedAtWithName(communityId, curFan, name, pageable);
            }
        }

       List<ChatRoomResponse.ChatRoomDTO> chatRoomDTOS = chatRoomList.getContent().stream().map((chatRoom -> {
           Integer count = chatSessionRepository.countByChatRoom(chatRoom);
           Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomAndFan(chatRoom, curFan);
           return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatSession.isPresent());
       })).toList();

        return new ChatRoomResponse.ChatRoomListDTO(chatRoomList, chatRoomDTOS);
    }

    public List<ChatRoomResponse.ChatRoomDTO> getMyOfficialChatRooms(User user) {
        List<ChatRoom> officialChatRooms = chatRoomRepository.findMyOfficial(user);

        return officialChatRooms.stream().map((chatRoom -> {
            Integer count = chatSessionRepository.countByChatRoom(chatRoom);
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, false);
        })).toList();
    }

    public List<ChatRoomResponse.ChatRoomDTO> getMyChatRooms(User user) {
        List<ChatRoom> publicChatRooms = chatRoomRepository.findPublicByUser(user);

        return publicChatRooms.stream().map((chatRoom -> {
            Fan fan = fanRepository.findByCommunityIdAndUser(chatRoom.getCommunityId(), user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
            ChatSession chatSession = chatSessionRepository.findByChatRoomAndFan(chatRoom, fan).orElseThrow(()-> new CustomException(ExceptionCode.CHAT_SESSION_NOT_FOUND));

            Pageable pageable = PageRequest.of(0, 1);
            List<Message> messages = messageRepository.findLastMessage(chatRoom, pageable);
            String lastMessage = messages.isEmpty() ? null : messages.get(0).getContent();
            LocalDateTime lastMessageTime = messages.isEmpty() ? null : messages.get(0).getCreatedAt();

            Integer unreadCount = messageRepository.countUnreadMessages(chatRoom, chatSession.getLastExitTime());

            Integer count = chatSessionRepository.countByChatRoom(chatRoom);
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, true, lastMessage, lastMessageTime, unreadCount);
        })).toList();
    }

    public ChatRoomResponse.ChatRoomDTO getChatRoomById(Long chatRoomId, User user) {
        // 존재하지 않는 채팅방 -> 뒤로가기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(chatRoom.getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        int count = chatSessionRepository.countByChatRoom(chatRoom);
        if(chatRoom.getCommunityType().equals(CommunityType.TEAM)) {
            Team team = teamRepository.findById(chatRoom.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
            return chatRoom.getType().equals(ChatRoomType.OFFICIAL) ? new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, null, team) : new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, chatRoom.getManager(), team);
        } else {
            Player player = playerRepository.findById(chatRoom.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
            return chatRoom.getType().equals(ChatRoomType.OFFICIAL) ? new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, null, player) : new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, chatRoom.getManager(), player);
        }
    }

    public ChatResponse.ChatListDTO getChats(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()->new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Page<Chat> chats = chatRepository.findByChatRoom(chatRoom, pageable);

        return new ChatResponse.ChatListDTO(chats, chats.getContent().stream().map((chat -> {
            List<String> messages = messageRepository.findByChat(chat).stream().map((Message::getContent)).toList();
            return new ChatResponse.ChatDTO(messages, chat.getCreatedAt(), chat.getWriter());
        })).toList());
    }

    public List<FanResponse.FanDTO> getParticipants(Long chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        if(chatRoom.getType().equals(ChatRoomType.OFFICIAL) || chatRoom.getManager() == null) {
            return null;
        }

        Fan curFan = fanRepository.findByCommunityIdAndUser(chatRoom.getCommunityId(), user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
        List<Fan> fans = chatSessionRepository.findByChatRoom(chatRoom).stream().map(ChatSession::getFan).toList();

        List<Fan> mutableFans = new ArrayList<>(fans);
        mutableFans.remove(chatRoom.getManager());
        mutableFans.remove(curFan);

        List<Fan> sortedFans = new ArrayList<>();
        if(!chatRoom.getManager().equals(curFan)) sortedFans.add(curFan);

        sortedFans.addAll(mutableFans);

        return sortedFans.stream().map((FanResponse.FanDTO::new)).toList();
    }

    @Transactional
    public void deleteChatRoom(Long chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(chatRoom.getCommunityId(), user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(chatRoom.getManager().equals(curFan)) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    public void autoCreateChatRooms() {
        List<Team> communities = teamRepository.findAll();

        for(Team community : communities){
            Optional<ChatRoom> chatRoom = chatRoomRepository.findByCommunityId(community.getId());

            if(chatRoom.isEmpty()) {
                ChatRoom newChatRoom = ChatRoom.builder()
                        .communityId(community.getId())
                        .description(community.getKoreanName() + " 팬들끼리 응원해요!")
                        .image(community.getImage())
                        .name(community.getKoreanName())
                        .type(ChatRoomType.OFFICIAL)
                        .communityType(CommunityType.TEAM)
                        .build();

                chatRoomRepository.save(newChatRoom);
            }
        }
    }

    // WS
    public void sendMessage(ChatRequest.ChatRequestDTO chatDTO, Long chatRoomId, String sessionId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        ChatSession chatSession = chatSessionRepository.findByChatRoomAndSessionId(chatRoom, sessionId);
        Fan curFan = chatSession.getFan();

        LocalDateTime now = LocalDateTime.now();

        // 비공식 채팅방만 채팅 저장
        if(chatRoom.getType().equals(ChatRoomType.PUBLIC)){
            Optional<Chat> chat = chatRepository.findByChatRoomAndWriterAndCreatedAtMinute(chatRoom.getId(), curFan.getId());
            if(chat.isPresent()) {
                Message message = Message.builder()
                        .content(chatDTO.message())
                        .chat(chat.get())
                        .build();
                messageRepository.save(message);
            } else {
                Chat newChat = Chat.builder()
                        .chatRoom(chatRoom)
                        .writer(curFan)
                        .build();
                chatRepository.save(newChat);

                Message message = Message.builder()
                        .content(chatDTO.message())
                        .chat(newChat)
                        .build();
                messageRepository.save(message);
            }
        }
        simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId, new ChatResponse.ChatResponseDTO(chatDTO.message(), now, curFan));
    }

    @Transactional
    public void removeUserFromRoom(Long chatRoomId, String sessionId) {
        chatSessionRepository.deleteByChatRoomIdAndSessionId(chatRoomId, sessionId);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()->new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
        Integer count = chatSessionRepository.countByChatRoom(chatRoom);
        simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId + "/participants", count);
    }

    @Transactional
    public void updateExitTime(Long chatRoomId, String sessionId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()->new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
        ChatSession chatSession = chatSessionRepository.findByChatRoomAndSessionId(chatRoom, sessionId);

        chatSession.setLastExitTime(LocalDateTime.now());
        chatSessionRepository.save(chatSession);
    }
}
