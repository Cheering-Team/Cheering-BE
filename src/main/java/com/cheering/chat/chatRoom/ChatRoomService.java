package com.cheering.chat.chatRoom;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.chat.*;
import com.cheering.chat.session.ChatSession;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.fan.CommunityType;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final ChatRepository chatRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final TeamRepository teamRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BadWordService badWordService;
    private final S3Util s3Util;
    private final EntityManager entityManager;
    private final FcmServiceImpl fcmService;
    private final DateTimeFormatter groupKeyFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public ChatRoomResponse.IdDTO createChatRoom(Long communityId, String name, String description, MultipartFile image, Integer max, User user) {
        if(badWordService.containsBadWords(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }
        if(badWordService.containsBadWords(description)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }
        Fan curUser = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        String imageUrl;
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

    @Transactional
    public List<ChatRoomResponse.ChatRoomDTO> getMyChatRooms(User user) {
        List<ChatRoom> publicChatRooms = chatRoomRepository.findPublicByUser(user);

        return publicChatRooms.stream().map((chatRoom -> {
            ChatSession chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoom.getId(), user).orElseThrow(()-> new CustomException(ExceptionCode.CHAT_SESSION_NOT_FOUND));

            Pageable pageable = PageRequest.of(0, 1);

            List<Chat> chats = chatRepository.findLastChat(chatRoom, ChatType.MESSAGE, pageable);
            String lastMessage = chats.isEmpty() ? null : chats.get(0).getContent();
            LocalDateTime lastMessageTime = chats.isEmpty() ? null : chats.get(0).getCreatedAt();

            Integer unreadCount = chatRepository.countUnreadMessages(chatRoom, ChatType.MESSAGE, chatSession.getLastExitTime());

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

    public ChatResponse.ChatListDTO getChats(Long chatRoomId, LocalDateTime cursorDate, int size, User user) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Order.desc("createdAt")));

        Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoomId, user);

        LocalDateTime enterDate = chatSession.isEmpty() ? LocalDateTime.now() : chatSession.get().getCreatedAt();

        List<ChatGroup> chatGroups = new ArrayList<>();
        List<Chat> chats = chatRepository.findByChatRoomIdAndCreatedAtBefore(chatRoomId, Objects.requireNonNullElseGet(cursorDate, LocalDateTime::now), enterDate, pageable);
        if(chats.isEmpty()) {
            return new ChatResponse.ChatListDTO(chatGroups, false);
        }
        Chat lastChat = chats.get(chats.size() - 1);
        List<Chat> extraChats = chatRepository.findByGroupKeyAndCreatedAtBefore(chatRoomId, lastChat.getCreatedAt(), enterDate, lastChat.getGroupKey());
        chats.addAll(extraChats);
        lastChat = chats.get(chats.size() - 1);
        boolean hasNext = chatRepository.existsByChatRoomIdAndBeforeLastChat(chatRoomId, lastChat.getCreatedAt(), enterDate);

        ChatGroup curGroup = null;

        for(Chat chat: chats) {
            if(curGroup == null || !curGroup.getGroupKey().equals(chat.getGroupKey())){
                List<String> messages = new ArrayList<>();
                messages.add(chat.getContent());
                curGroup = new ChatGroup(chat.getType(), chat.getCreatedAt(), new FanResponse.FanDTO(chat.getWriter()), messages, chat.getGroupKey());
                chatGroups.add(curGroup);
            } else {
                curGroup.getMessages().add(0, chat.getContent());
                curGroup.setCreatedAt(chat.getCreatedAt());
            }
        }

        return new ChatResponse.ChatListDTO(chatGroups, hasNext);
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
    @Transactional
    public void sendMessage(ChatRequest.ChatRequestDTO requestDTO, Long chatRoomId) {
        LocalDateTime now = LocalDateTime.now();
        String groupKey = generateGroupKey(requestDTO.writerId(), now);

        simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId, new ChatResponse.ChatResponseDTO("MESSAGE", requestDTO.content(), now, requestDTO.writerId(), requestDTO.writerImage(), requestDTO.writerName(), groupKey, null));

        if(requestDTO.chatRoomType().equals("PUBLIC")){
            ChatRoom chatRoom = entityManager.getReference(ChatRoom.class, chatRoomId);
            Fan fan = entityManager.getReference(Fan.class, requestDTO.writerId());

            Chat chat = Chat.builder()
                    .type(ChatType.MESSAGE)
                    .chatRoom(chatRoom)
                    .writer(fan)
                    .content(requestDTO.content())
                    .groupKey(groupKey)
                    .build();

            chatRepository.save(chat);

            List<User> users = chatSessionRepository.findByChatRoomExceptMe(chatRoom, requestDTO.writerId());

            users.forEach(user -> {
                Integer count = getUnreadChats(user);
                user.getDeviceTokens().forEach(deviceToken -> fcmService.sendChatMessageTo(deviceToken.getToken(), count));
            });
        }
    }

    @Transactional
    public void removeUserFromRoom(Long chatRoomId, String sessionId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()->new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
        Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomIdAndSessionId(chatRoom.getId(), sessionId);
        if(chatSession.isEmpty()) {
            return;
        }
        Fan fan = chatSession.get().getFan();
        chatSessionRepository.delete(chatSession.get());
        Integer count = chatSessionRepository.countByChatRoom(chatRoom);
        simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId + "/participants", new ChatResponse.ChatResponseDTO("SYSTEM_EXIT", fan.getName() + "님이 나가셨습니다", LocalDateTime.now(), fan.getId(), fan.getImage(), fan.getName(), fan.getId() + "_SYSTEM_EXIT", count));

        if(chatRoom.getType().equals(ChatRoomType.PUBLIC)){
            Chat chat = Chat.builder()
                    .type(ChatType.SYSTEM_EXIT)
                    .chatRoom(chatRoom)
                    .writer(fan)
                    .content(fan.getName() + "님이 나가셨습니다")
                    .groupKey(fan.getId() + "_SYSTEM_EXIT")
                    .build();
            chatRepository.save(chat);
        }

    }

    @Transactional
    public void updateExitTime(Long chatRoomId, User user) {
        Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoomId, user);
        if(chatSession.isEmpty()) {
            return;
        }
        chatSession.get().setLastExitTime(LocalDateTime.now());
        chatSessionRepository.save(chatSession.get());
    }

    @Transactional
    public Integer getUnreadChats(User user) {
        List<ChatSession> chatSessions = chatSessionRepository.findByUser(user);

        return chatSessions.stream().mapToInt(chatSession -> chatRepository.countUnreadMessages(chatSession.getChatRoom(), ChatType.MESSAGE, chatSession.getLastExitTime())).sum();
    }

    private String generateGroupKey(Long writerId, LocalDateTime timestamp) {
        return writerId + "_" + groupKeyFormatter.format(timestamp);
    }
}
