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
import com.cheering.community.Community;
import com.cheering.community.CommunityRepository;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanRepository;
import com.cheering.community.relation.FanResponse;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
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
    private final CommunityRepository communityRepository;
    private final FanRepository fanRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
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
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));
        Fan curUser = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

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
                .community(community)
                .manager(curUser)
                .type(ChatRoomType.PUBLIC)
                .build();

        chatRoomRepository.save(chatRoom);
        return new ChatRoomResponse.IdDTO(chatRoom.getId());
    }

    public List<ChatRoomResponse.ChatRoomSectionDTO> getChatRooms(Long communityId, User user) {
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));
        Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        List<ChatRoom> officialChatRooms = chatRoomRepository.findOfficialByCommunity(community);
        List<ChatRoom> publicChatRooms = chatRoomRepository.findPublicByCommunity(community, curFan);

        List <ChatRoomResponse.ChatRoomDTO> officialChatRoomDTOs = officialChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoom(chatRoom);
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getCommunity(), null);
        } )).toList();

        List <ChatRoomResponse.ChatRoomDTO> publicChatRoomDTOs =  publicChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoom(chatRoom);
            Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomAndFan(chatRoom, curFan);
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getCommunity(), chatSession.isPresent());
        } )).toList();

        return List.of(new ChatRoomResponse.ChatRoomSectionDTO("official", officialChatRoomDTOs),
                new ChatRoomResponse.ChatRoomSectionDTO("public", publicChatRoomDTOs));
    }

    public List<ChatRoomResponse.ChatRoomSectionDTO> getMyChatRooms(User user) {
        List<Fan> fans = fanRepository.findByUser(user);

        List<Community> communities = fans.stream().map((Fan::getCommunity)).toList();

        // 공식은 모두
        List<ChatRoom> officialChatRooms = chatRoomRepository.findOfficialByCommunityIn(communities).stream().sorted(Comparator.comparing(chatRoom -> chatRoom.getCommunity().getTeam() != null ? 0 : 1)).toList();

        // 비공식은 내가 참여중인 채팅방만
        List<ChatRoom> publicChatRooms = chatRoomRepository.findPublicByCommunityIn(communities).stream()
                .filter((chatRoom -> {
                    Fan curFan = fanRepository.findByCommunityAndUser(chatRoom.getCommunity(), user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
                    return chatSessionRepository.findByChatRoomAndFan(chatRoom, curFan).isPresent();
                }))
                .sorted(Comparator.comparing(chatRoom -> chatRoom.getCommunity().getTeam() != null ? 0 : 1)).toList();

        List<ChatRoomResponse.ChatRoomDTO> officialChatRoomDTOs = officialChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoom(chatRoom);
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getCommunity(), null);
        } )).toList();

        List<ChatRoomResponse.ChatRoomDTO> publicChatRoomDTOs = publicChatRooms.stream().map((chatRoom -> {
            int count = chatSessionRepository.countByChatRoom(chatRoom);
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, chatRoom.getCommunity(), true);
        } )).toList();

        return List.of(new ChatRoomResponse.ChatRoomSectionDTO("official", officialChatRoomDTOs),
                new ChatRoomResponse.ChatRoomSectionDTO("public", publicChatRoomDTOs));
    }

    public ChatRoomResponse.ChatRoomDTO getChatRoomById(Long chatRoomId, User user) {
        // 존재하지 않는 채팅방 -> 뒤로가기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityAndUser(chatRoom.getCommunity(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        int count = chatSessionRepository.countByChatRoom(chatRoom);
        if(chatRoom.getType().equals(ChatRoomType.OFFICIAL)) {
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan);
        } else {
            return new ChatRoomResponse.ChatRoomDTO(chatRoom, count, curFan, chatRoom.getManager());
        }
    }

    @Transactional
    public void addUserToRoom(Long chatRoomId, String sessionId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Fan fan = fanRepository.findByCommunityAndUser(chatRoom.getCommunity(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<ChatSession> chatSession = chatSessionRepository.findByChatRoomAndFan(chatRoom, fan);

        if(chatSession.isEmpty()) {
            ChatSession newChatSession = ChatSession.builder()
                    .sessionId(sessionId)
                    .chatRoom(chatRoom)
                    .fan(fan)
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
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, new ChatResponse.ChatResponseDTO(chatDTO.message(), now, curFan));
    }

    @Transactional
    public void removeUserFromRoom(Long chatRoomId, String sessionId) {
        chatSessionRepository.deleteByChatRoomIdAndSessionId(chatRoomId, sessionId);

        broadcastUserCount(chatRoomId);
    }

    private void broadcastUserCount(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()->new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        int count = chatSessionRepository.countByChatRoom(chatRoom);

        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId + "/count", count);
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

        Fan curFan = fanRepository.findByCommunityAndUser(chatRoom.getCommunity(), user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
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

        Fan curFan = fanRepository.findByCommunityAndUser(chatRoom.getCommunity(), user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(chatRoom.getManager().equals(curFan)) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    public void autoCreateChatRooms() {
        List<Community> communities = communityRepository.findAll();

        for(Community community : communities){
            Optional<ChatRoom> chatRoom = chatRoomRepository.findByName(community.getKoreanName());

            if(chatRoom.isEmpty()) {
                ChatRoom newChatRoom = ChatRoom.builder()
                        .community(community)
                        .description(community.getKoreanName() + " 팬들끼리 응원해요!")
                        .image(community.getImage())
                        .name(community.getKoreanName())
                        .type(ChatRoomType.OFFICIAL)
                        .build();

                chatRoomRepository.save(newChatRoom);
            }
        }
    }
}
