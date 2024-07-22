package com.demo.malmo.chat;

import com.demo.malmo.chat.facade.ChatFacade;
import com.demo.malmo.chat.request.ChatAiRoomBookMarkResponse;
import com.demo.malmo.chat.request.ChatBookMarkResponse;
import com.demo.malmo.chat.request.ChatMessageResponse;
import com.demo.malmo.chat.request.ChatRequest;
import com.demo.malmo.chat.request.ChatResponse;
import com.demo.malmo.chat.request.ChatRoomResponse;
import com.demo.malmo.chat.service.ChatService;
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

    @Operation(summary = "새로운 대화 입력", description = """
        최초 대화의 경우 categoryId 필수, 기존 채팅방인 경우 roomId 필수 <br>
        한번의 요청으로 6번의 대화가 순서 보장하지 않고 생성됩니다. <br>
        <b>TODO 특정 모자의 요청은 아직 미구현 :: 개발 예정</b> <br>
        사용자의 대화를 입력하면 AI 대화를 생성하여 sse 반환""")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> newChat(@RequestHeader("user-id") String userId, @RequestBody ChatRequest request) {
        userService.getUser(userId);
        return chatFacade.newChat(request, userId);
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
