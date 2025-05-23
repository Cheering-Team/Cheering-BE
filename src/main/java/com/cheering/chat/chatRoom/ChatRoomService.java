package com.cheering.chat.chatRoom;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.chat.*;
import com.cheering.chat.session.ChatSession;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.fan.CommunityType;
import com.cheering.match.Match;
import com.cheering.matchRestriction.MatchRestrictionRepository;
import com.cheering.meet.Meet;
import com.cheering.meet.MeetGender;
import com.cheering.meet.MeetService;
import com.cheering.meetfan.MeetFan;
import com.cheering.meet.MeetRepository;
import com.cheering.meetfan.MeetFanRepository;
import com.cheering.meetfan.MeetFanRole;
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
import java.util.*;


@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final ChatRepository chatRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final TeamRepository teamRepository;
    private final MeetRepository meetRepository;
    private final MeetFanRepository meetFanRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MatchRestrictionRepository matchRestrictionRepository;
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

        Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoomId, user);

        if(chatRoom.getCommunityType().equals(CommunityType.TEAM)) {
            Team team = teamRepository.findById(chatRoom.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
            return chatRoom.getType().equals(ChatRoomType.OFFICIAL) ? new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, null, team, null) : new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, chatRoom.getManager(), team, chatSession.map(ChatSession::getNotificationsEnabled).orElse(true));
        } else {
            Player player = playerRepository.findById(chatRoom.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
            return chatRoom.getType().equals(ChatRoomType.OFFICIAL) ? new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, null, player, null) : new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, chatRoom.getManager(), player, chatSession.map(ChatSession::getNotificationsEnabled).orElse(true));
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

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
        ChatRoomType chatRoomType = chatRoom.getType();

        ChatGroup curGroup = null;

        for (Chat chat : chats) {
            FanResponse.FanDTO writerDTO;

            if (chatRoomType == ChatRoomType.CONFIRM || chatRoomType == ChatRoomType.PRIVATE) {
                writerDTO = new FanResponse.FanDTO(
                        chat.getWriter().getId(),
                        chat.getWriter().getType(),
                        chat.getWriter().getMeetName(),
                        chat.getWriter().getMeetImage()
                );
            } else {
                writerDTO = new FanResponse.FanDTO(chat.getWriter());
            }

            if (curGroup == null || !curGroup.getGroupKey().equals(chat.getGroupKey())) {
                List<String> messages = new ArrayList<>();
                messages.add(chat.getContent());
                curGroup = new ChatGroup(chat.getType(), chat.getCreatedAt(), writerDTO, messages, chat.getGroupKey());
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

        Fan writerFan = fanRepository.findById(requestDTO.writerId())
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        if (requestDTO.chatRoomType().equals("CONFIRM")) {
            simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId, new ChatResponse.ChatResponseDTO("MESSAGE", requestDTO.content(), now, requestDTO.writerId(), writerFan.getMeetImage(), writerFan.getMeetName(), groupKey, null));
        } else {
            simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId, new ChatResponse.ChatResponseDTO("MESSAGE", requestDTO.content(), now, requestDTO.writerId(), requestDTO.writerImage(), requestDTO.writerName(), groupKey, null));
        }

        if(requestDTO.chatRoomType().equals("PUBLIC") ||
                requestDTO.chatRoomType().equals("PRIVATE") ||
                requestDTO.chatRoomType().equals("CONFIRM")) {

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
                String content = requestDTO.content();
                Long communityId = chatRoom.getCommunityId();
                String title;
                String body;

                if (requestDTO.chatRoomType().equals("PRIVATE")) {
                    title = requestDTO.writerName();
                    body = content;
                } else if (requestDTO.chatRoomType().equals("CONFIRM")){
                    title = "[" +  chatRoom.getMeet().getTitle() + "]";
                    body = writerFan.getMeetName() + ": " + content;
                } else {
                    title = "[" + chatRoom.getName() + "]";
                    body = writerFan.getName() + ": " + content;
                }

                // 푸시 알림 전송
                user.getDeviceTokens().forEach(deviceToken ->
                        fcmService.sendChatMessageTo(
                                deviceToken.getToken(),
                                count,
                                communityId,
                                chatRoomId,
                                title,
                                body
                        )
                );
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
        simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId + "/participants", new ChatResponse.ChatResponseDTO("SYSTEM", fan.getName() + "님이 나가셨습니다", LocalDateTime.now(), fan.getId(), fan.getImage(), fan.getName(), fan.getId() + "_SYSTEM", count));

        if (chatRoom.getType().equals(ChatRoomType.PUBLIC) || chatRoom.getType().equals(ChatRoomType.CONFIRM) || chatRoom.getType().equals(ChatRoomType.PRIVATE)) {
            Chat chat = Chat.builder()
                    .type(ChatType.SYSTEM)
                    .chatRoom(chatRoom)
                    .writer(fan)
                    .content(fan.getName() + "님이 나가셨습니다")
                    .groupKey(fan.getId() + "_SYSTEM")
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
        List<ChatSession> chatSessions = chatSessionRepository.findPublicByUser(user);

        return chatSessions.stream().mapToInt(chatSession -> chatRepository.countUnreadMessages(chatSession.getChatRoom(), ChatType.MESSAGE, chatSession.getLastExitTime())).sum();
    }

    public String generateGroupKey(Long writerId, LocalDateTime timestamp) {
        return writerId + "_" + groupKeyFormatter.format(timestamp);
    }

    @Transactional
    public ChatRoomResponse.IdDTO createConfirmedChatRoom(Long communityId, Meet meet, Integer max, User user) {

        Fan curUser = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<ChatRoom> existingConfirmedRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(communityId, ChatRoomType.CONFIRM );
        if (existingConfirmedRoom.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_CHAT_ROOM);
        }

        Optional<Player> player = playerRepository.findById(communityId);

        ChatRoom chatRoom = ChatRoom.builder()
                .communityId(communityId)
                .name("모임 확정 채팅방")
                .description("확정된 멤버들이 대화하는 채팅방입니다.")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-confirm-chatroom.png")
                .type(ChatRoomType.CONFIRM)
                .manager(curUser) // Manager of the chat room
                .communityType(player.isPresent() ? CommunityType.PLAYER : CommunityType.TEAM)
                .max(max)
                .meet(meet) // Meet 정보 설정
                .build();

        chatRoomRepository.save(chatRoom);
        return new ChatRoomResponse.IdDTO(chatRoom.getId());
    }

    @Transactional
    public ChatRoomResponse.IdWithConditionDTO createPrivateChatRoom(Long communityId, Long meetId, User user) {

        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        boolean isRestricted = matchRestrictionRepository.existsByMatchIdAndUser(meet.getMatch().getId(), user);
        if (isRestricted) {
            throw new CustomException(ExceptionCode.USER_RESTRICTED_FOR_MATCH);
        }

        Fan manager = meetFanRepository.findByMeetAndRole(meet, MeetFanRole.MANAGER)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND))
                .getFan();

        Fan applicant = fanRepository.findByCommunityIdAndUser(communityId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<ChatRoom> existingPrivateRoom = chatRoomRepository.findPrivateChatRoomByParticipantsAndMeet(
                manager, applicant, ChatRoomType.PRIVATE, meetId);

        // 기존 채팅방이 존재할 경우 채팅방 ID 반환 (같은 모임)
        if (existingPrivateRoom.isPresent()) {
            return new ChatRoomResponse.IdWithConditionDTO(existingPrivateRoom.get().getId(), true);
        }

        Integer currentCount = meetFanRepository.countByMeet(meet);
        if(currentCount.equals(meet.getMax())) {
            throw new CustomException(ExceptionCode.MEET_MAX);
        }

        int currentYear = java.time.Year.now().getValue();
        int currentAge = currentYear - applicant.getUser().getAge() + 1;

        boolean isConditionMatched = true;
        if (meet.getGender() != null && meet.getGender() != MeetGender.ANY &&
                !meet.getGender().toString().equalsIgnoreCase(applicant.getUser().getGender().toString())) {
            isConditionMatched = false;
        }
        if (meet.getAgeMin() != null && currentAge < meet.getAgeMin()) {
            isConditionMatched = false;
        }
        if (meet.getAgeMax() != null && currentAge > meet.getAgeMax()) {
            isConditionMatched = false;
        }

        ChatRoom privateChatRoom = ChatRoom.builder()
                .communityId(communityId)
                .name(applicant.getMeetName())
                .description("방장과 자유롭게 이야기해보세요")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-private-chatroom.png")
                .type(ChatRoomType.PRIVATE)
                .manager(manager)
                .communityType(manager.getType())
                .max(2)
                .meet(meet)
                .build();
        chatRoomRepository.save(privateChatRoom);

        chatSessionRepository.save(
                ChatSession.builder()
                        .sessionId(UUID.randomUUID().toString()) // 임시 세션 ID
                        .chatRoom(privateChatRoom)
                        .fan(manager)
                        .lastExitTime(LocalDateTime.now())
                        .build()
        );

        chatSessionRepository.save(
                ChatSession.builder()
                        .sessionId(UUID.randomUUID().toString()) // 임시 세션 ID
                        .chatRoom(privateChatRoom)
                        .fan(applicant)
                        .lastExitTime(LocalDateTime.now())
                        .build()
        );

        if (!isConditionMatched) {
            String systemMessage = "모임 조건과 일치하지 않는 대화 신청자입니다.\n" +
                    "충분한 대화를 통해 멤버 확정을 검토해주세요.";
            Chat systemChat = Chat.builder()
                    .type(ChatType.SYSTEM)
                    .chatRoom(privateChatRoom)
                    .writer(manager)
                    .content(systemMessage)
                    .groupKey(manager.getId() + "_SYSTEM")
                    .build();
            chatRepository.save(systemChat);
        }

        return new ChatRoomResponse.IdWithConditionDTO(privateChatRoom.getId(), isConditionMatched);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse.ChatRoomDTO> getPrivateChatRoomsForManager(Long meetId, User user) {

        // 모임의 매니저 확인
        MeetFan manager = meetFanRepository.findByMeetIdAndRole(meetId, MeetFanRole.MANAGER)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        // 채팅 메시지가 있는 1:1 대화방 조회
        List<ChatRoom> privateChatRooms = chatRoomRepository.findPrivateChatRoomsWithMessages(
                manager.getFan().getId(),
                ChatRoomType.PRIVATE,
                meetId,
                ChatType.MESSAGE
        );

        return privateChatRooms.stream().map(chatRoom -> {

            ChatSession chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoom.getId(), user)
                    .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_SESSION_NOT_FOUND));

            // 상대방 팬 정보 가져오기
            Fan opponentFan = chatSessionRepository.findOpponentFanByChatRoomAndUser(chatRoom.getId(), user)
                    .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

            // 마지막 메시지 가져오기
            Pageable pageable = PageRequest.of(0, 1);
            List<Chat> chats = chatRepository.findLastChat(chatRoom, ChatType.MESSAGE, pageable);
            String lastMessage = chats.isEmpty() ? null : chats.get(0).getContent();
            LocalDateTime lastMessageTime = chats.isEmpty() ? null : chats.get(0).getCreatedAt();

            // 읽지 않은 메시지 개수
            Integer unreadCount = chatRepository.countUnreadMessages(chatRoom, ChatType.MESSAGE, chatSession.getLastExitTime());

            // 현재 대화방 참가자 수
            Integer count = chatSessionRepository.countByChatRoom(chatRoom);

            return new ChatRoomResponse.ChatRoomDTO(
                    chatRoom.getId(),
                    opponentFan.getMeetName(),
                    opponentFan.getMeetImage(),
                    chatRoom.getDescription(),
                    chatRoom.getMax(),
                    chatRoom.getType(),
                    count,
                    null,
                    null,
                    null,
                    true,
                    lastMessage,
                    lastMessageTime,
                    unreadCount,
                    chatRoom.getMeet().getId(),
                    null
            );
        }).toList();
    }

    @Transactional
    public void sendJoinRequest(ChatRequest.ChatRequestDTO requestDTO, Long chatRoomId) {

        LocalDateTime now = LocalDateTime.now();

        ChatRoom chatRoom = entityManager.getReference(ChatRoom.class, chatRoomId);

        Meet meet = chatRoom.getMeet();

        if (meet == null || meet.getManager().equals(requestDTO.writerId())) {
            throw new CustomException(ExceptionCode.USER_FORBIDDEN); // 방장 아닌 경우 - 권한X
        }
        if (meet.getMax().equals(meetFanRepository.countByMeet(meet))) {
            simpMessagingTemplate.convertAndSend(
                    "/topic/chatRoom/" + chatRoomId,
                    new ChatResponse.ChatResponseDTO(
                            "ERROR",
                            "2015",
                            now,
                            requestDTO.writerId(),
                            requestDTO.writerImage(),
                            requestDTO.writerName(),
                            requestDTO.writerId()+"_JOIN_REQUEST",
                            null
                    )
            );
            return;
        }

        simpMessagingTemplate.convertAndSend(
                "/topic/chatRoom/" + chatRoomId,
                new ChatResponse.ChatResponseDTO(
                        "JOIN_REQUEST",
                        "해당 모임에 참여를 확정하겠습니까?",
                        now,
                        requestDTO.writerId(),
                        requestDTO.writerImage(),
                        requestDTO.writerName(),
                        requestDTO.writerId()+"_JOIN_REQUEST",
                        null
                )
        );

        Fan manager = entityManager.getReference(Fan.class, requestDTO.writerId());

        Chat chat = Chat.builder()
                .type(ChatType.JOIN_REQUEST)
                .chatRoom(chatRoom)
                .writer(manager)
                .content("해당 모임에 참여를 확정하겠습니까?")
                .groupKey(requestDTO.writerId()+"_JOIN_REQUEST")
                .build();

        chatRepository.save(chat);

        List<User> users = chatSessionRepository.findByChatRoomExceptMe(chatRoom, requestDTO.writerId());

        users.forEach(user -> {
            Integer count = getUnreadChats(user);
            Long communityId = chatRoom.getCommunityId();
            String title = "[" +  meet.getTitle() + "]";
            String body = "모임 참여 확정 요청이 도착했습니다.";

            // 푸시 알림 전송
            user.getDeviceTokens().forEach(deviceToken ->
                    fcmService.sendChatMessageTo(
                            deviceToken.getToken(),
                            count,
                            communityId,
                            chatRoomId,
                            title,
                            body
                    )
            );
        });
    }

    // 확정 질문 수락 -> 모임 가입
    @Transactional
    public void acceptJoinRequest(ChatRequest.ChatRequestDTO requestDTO, Long chatRoomId) {

        ChatRoom privateChatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Fan fan = fanRepository.findById(requestDTO.writerId())
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        Meet meet = privateChatRoom.getMeet();
        Match match = meet.getMatch();

        LocalDateTime now = LocalDateTime.now();

        boolean isRestricted = matchRestrictionRepository.existsByMatchIdAndUser(match.getId(), fan.getUser());
        if (isRestricted) {
            simpMessagingTemplate.convertAndSend(
                    "/topic/chatRoom/" + chatRoomId,
                    new ChatResponse.ChatResponseDTO(
                            "ERROR",
                            "2013",
                            now,
                            fan.getId(),
                            fan.getMeetImage(),
                            fan.getMeetName(),
                            requestDTO.writerId()+"_JOIN_ACCEPT",
                            null
                    )
            );
        }

        MeetFan meetFan = meetFanRepository.findByMeetAndFanUser(meet, fan.getUser())
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        if (meet.getMax().equals(meetFanRepository.countByMeet(meet))) {
            simpMessagingTemplate.convertAndSend(
                    "/topic/chatRoom/" + chatRoomId,
                    new ChatResponse.ChatResponseDTO(
                            "ERROR",
                            "2015",
                            now,
                            fan.getId(),
                            fan.getMeetImage(),
                            fan.getMeetName(),
                            requestDTO.writerId()+"_JOIN_ACCEPT",
                            null
                    )
            );
            return;
        }

        boolean MatchDuplicatedMeet = meetRepository.existsByMatchAndMeetFansFanUser(meet.getMatch().getId(), fan.getUser());

        if(MatchDuplicatedMeet) {
            throw new CustomException(ExceptionCode.DUPLICATE_MEET);
        }

        simpMessagingTemplate.convertAndSend(
                "/topic/chatRoom/" + chatRoomId,
                new ChatResponse.ChatResponseDTO(
                        "JOIN_ACCEPT",
                        "모임 참여를 확정하였습니다.",
                        now,
                        requestDTO.writerId(),
                        requestDTO.writerImage(),
                        requestDTO.writerName(),
                        requestDTO.writerId()+"_JOIN_ACCEPT",
                        null
                )
        );

        // 역할 업데이트
        meetFan.setRole(MeetFanRole.MEMBER);
        meetFanRepository.save(meetFan);

        ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        String sessionId = UUID.randomUUID().toString();

        ChatSession chatSession = ChatSession.builder()
                .chatRoom(confirmChatRoom)
                .fan(fan)
                .sessionId(sessionId)
                .build();

        chatSessionRepository.save(chatSession);

        Chat chat = Chat.builder()
                .type(ChatType.JOIN_ACCEPT)
                .chatRoom(privateChatRoom)
                .writer(fan)
                .content("모임 참여를 확정하였습니다.")
                .groupKey(requestDTO.writerId()+"_JOIN_ACCEPT")
                .build();

        chatRepository.save(chat);

        List<User> users = chatSessionRepository.findByChatRoomExceptMe(privateChatRoom, requestDTO.writerId());

        users.forEach(user -> {
            Integer count = getUnreadChats(user);
            Long communityId = privateChatRoom.getCommunityId();
            String title = "[" +  confirmChatRoom.getMeet().getTitle() + "]";
            String body = fan.getMeetName() + "님이 모임 참여를 확정하였습니다.";

            // 푸시 알림 전송
            user.getDeviceTokens().forEach(deviceToken ->
                    fcmService.sendChatMessageTo(
                            deviceToken.getToken(),
                            count,
                            communityId,
                            chatRoomId,
                            title,
                            body
                    )
            );
        });
    }


    public ChatRoomResponse.PrivateChatRoomDTO getPrivateChatRoomById(Long chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(chatRoom.getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Fan opponentFan = chatSessionRepository.findOpponentFanByChatRoomAndUser(chatRoomId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoom.getId(), user);

        boolean isConfirmed;

        if(chatRoom.getMeet().getManager().equals(curFan)) {
            Optional<MeetFan> optionalMeetFan = meetFanRepository.findByMeetAndFanUser(chatRoom.getMeet(), opponentFan.getUser());

            isConfirmed = optionalMeetFan.isPresent() && optionalMeetFan.get().getRole() != MeetFanRole.APPLIER && optionalMeetFan.get().getRole() != MeetFanRole.LEFT;
        } else {
            Optional<MeetFan> optionalMeetFan = meetFanRepository.findByMeetAndFanUser(chatRoom.getMeet(), curFan.getUser());

            isConfirmed = optionalMeetFan.isPresent() && optionalMeetFan.get().getRole() != MeetFanRole.APPLIER && optionalMeetFan.get().getRole() != MeetFanRole.LEFT;
        }

        return new ChatRoomResponse.PrivateChatRoomDTO(chatRoom, opponentFan.getMeetName(), opponentFan.getMeetImage(), opponentFan.getUser().getAge(), opponentFan.getUser().getGender(), curFan, isConfirmed, chatSession.get().getNotificationsEnabled());
    }

    @Transactional
    public void enableChatNotification(Long chatRoomId, User user) {
        ChatSession chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoomId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_SESSION_NOT_FOUND));

        chatSession.setNotificationsEnabled(true);
        chatSessionRepository.save(chatSession);
    }
    @Transactional
    public void disableChatNotification(Long chatRoomId, User user) {
        ChatSession chatSession = chatSessionRepository.findByChatRoomIdAndUser(chatRoomId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_SESSION_NOT_FOUND));

        chatSession.setNotificationsEnabled(false);
        chatSessionRepository.save(chatSession);
    }

}
