package com.demo.malmo.chat;

import com.demo.malmo.chat.facade.ChatFacade;
import com.demo.malmo.chat.service.ChatService;
import com.demo.malmo.chat.vo.ChatAiRoomBookMarkResponse;
import com.demo.malmo.chat.vo.ChatBookMarkResponse;
import com.demo.malmo.chat.vo.ChatMessageResponse;
import com.demo.malmo.chat.vo.ChatMessageResponse.ChatUserMessage;
import com.demo.malmo.chat.vo.ChatRequest;
import com.demo.malmo.chat.vo.ChatRoomResponse;
import com.demo.malmo.chat.vo.ChatStreamRequest;
import com.demo.malmo.chat.vo.ChatStreamResponse;
import com.demo.malmo.global.base.BaseResponse;
import com.demo.malmo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "ChatController", description = "채팅방을 관리합니다.")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final UserService userService;
    private final ChatFacade chatFacade;
    private final ChatService chatService;

    @Operation(summary = "새로운 대화 입력", description = "최초 대화의 경우 categoryId 필수, 기존 채팅방인 경우 roomId 필수")
    @PostMapping(value = "/chat")
    public ChatUserMessage newChat(@RequestHeader("user-id") String userId, @RequestBody ChatRequest request) {
        userService.getUser(userId);
        var chatUserMessage = chatFacade.newChat(request, userId);
        return new ChatMessageResponse.ChatUserMessage(chatUserMessage);
    }

    @Operation(summary = "ai 실시간 대화", description = "AI 대화를 생성하여 sse 반환")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatStreamResponse> chatStream(@RequestHeader("user-id") String userId, @RequestBody ChatStreamRequest request) {
        userService.getUser(userId);
        return chatFacade.chatStream(request);
    }

    @Operation(summary = "채팅방 목록 조회", description = "최신순으로 나열")
    @GetMapping("/chat/rooms")
    public ChatRoomResponse getChatRooms(@RequestHeader("user-id") String userId) {
        userService.getUser(userId);
        return new ChatRoomResponse(chatService.getChatRooms(userId));
    }

    @Operation(summary = "채팅방 대화 조회", description = "최신순으로 나열 , 페이징 미구현(해야하나...ㅠ)")
    @GetMapping("/chat/messages/{roomId}")
    public ChatMessageResponse getChatMessages(@RequestHeader("user-id") String userId, @PathVariable Long roomId) {
        userService.getUser(userId);
        return new ChatMessageResponse(chatService.getChatMessages(roomId));
    }

    @Operation(summary = "채팅 북마크 조회", description = "최신순으로 나열 , 페이징 미구현(해야하나...ㅠ)")
    @GetMapping("/chat/bookmark")
    public ChatBookMarkResponse getChatMessages(@RequestHeader("user-id") String userId) {
        userService.getUser(userId);
        var aiMessages = chatService.findBookMarkedChatAiMessages(userId);
        return new ChatBookMarkResponse(aiMessages);
    }

    @Operation(summary = "ai 모자 채팅 내역 북마크", description = "스위치 형식으로 북마크 설정/해제")
    @PutMapping("/chat/bookmark/{aiChatId}")
    public BaseResponse bookmarkChatMessage(@RequestHeader("user-id") String userId, @PathVariable Long aiChatId) {
        userService.getUser(userId);
        var aiMessage = chatService.updateBookmark(aiChatId);
        return new ChatAiRoomBookMarkResponse(aiMessage.isBookmarked());
    }


    @Operation(summary = "채팅방 삭제", description = "채팅방 삭제")
    @DeleteMapping("/chat/rooms/{roomId}")
    public BaseResponse deleteChatRoom(@RequestHeader("user-id") String userId, @PathVariable Long roomId) {
        userService.getUser(userId);
        chatService.deleteRoom(roomId);
        return new BaseResponse();
    }

    @Operation(summary = "ai 모자 대화 삭제", description = "{aiChatId}에 해당하는 ai 모자 대화 삭제")
    @DeleteMapping("/chat/chat/{aiChatId}")
    public BaseResponse deleteChatMessage(@RequestHeader("user-id") String userId,
        @PathVariable Long aiChatId) {
        userService.getUser(userId);
        chatService.deleteAiChat(aiChatId);
        return new BaseResponse();

    }


}
