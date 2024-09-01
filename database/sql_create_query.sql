create table User
(
    userId    varchar(256)                       not null
        primary key comment '사용자의 고유 식별자',
    email     varchar(100)                       not null comment '사용자의 이메일 주소',
    deleted   tinyint                            null comment '사용자가 삭제되었는지 여부를 나타내는 플래그 (0: 삭제 안됨, 1: 삭제됨)',
    deletedAt datetime(6)                        null comment '사용자가 삭제된 시간',
    updatedAt datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '갱신일자',
    createdAt datetime default CURRENT_TIMESTAMP null comment '생성일자'
) comment '유저';

create table ChatRoom
(
    id        bigint auto_increment
        primary key comment '채팅방의 고유 식별자',
    category  varchar(100)                       not null comment '채팅방의 카테고리',
    roomName  varchar(256)                       null comment '채팅방의 이름',
    userId    varchar(255)                       not null comment '채팅방을 생성한 사용자의 ID',
    deleted   tinyint                            null comment '채팅방이 삭제되었는지 여부를 나타내는 플래그 (0: 삭제 안됨, 1: 삭제됨)',
    deletedAt datetime(6)                        null comment '채팅방이 삭제된 시간',
    updatedAt datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '갱신일자',
    createdAt datetime default CURRENT_TIMESTAMP null comment '생성일자',
    constraint fk_ChatRoom_User foreign key (userId) references User (userId)
) comment '대화 방';

create table ChatUserMessage
(
    id              bigint auto_increment
        primary key comment '사용자 메시지의 고유 식별자',
    chatRoomId      bigint                             null comment '메시지가 전송된 채팅방을 나타내는 ChatRoom 테이블의 외래키',
    relyAiMessageId bigint                             null comment 'ai message 파생 유저 대화인 경우 ChatAiMessage id (그외의 경우 nullable)',
    phase           int      default 1                 not null comment '채팅 페이즈',
    message         text                               null comment '사용자 메시지의 내용',
    deleted         tinyint                            null comment '메시지가 삭제되었는지 여부를 나타내는 플래그 (0: 삭제 안됨, 1: 삭제됨)',
    deletedAt       datetime(6)                        null comment '메시지가 삭제된 시간',
    updatedAt       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '갱신일자',
    createdAt       datetime default CURRENT_TIMESTAMP null comment '생성일자',
    constraint fk_ChatUserMessage_ChatRoom foreign key (chatRoomId) references ChatRoom (id)
) comment '유저 대화 내역';

create table ChatAiMessage
(
    id                bigint auto_increment
        primary key comment 'AI 메시지의 고유 식별자',
    chatRoomId        bigint                             not null comment '메시지가 전송된 채팅방을 나타내는 ChatRoom 테이블의 외래키',
    chatUserMessageId bigint                             not null comment '이 AI 메시지가 응답하는 사용자 메시지를 나타내는 ChatUserMessage 테이블의 외래키',
    role              varchar(64)                        not null comment '메시지 전송자의 역할, 일반적으로 AI 또는 시스템',
    message           text                               not null comment 'AI 메시지의 내용',
    bookmarked        tinyint  default 0                 not null comment '메시지가 북마크 되었는지 여부를 나타내는 플래그 (0: 북마크 안됨, 1: 북마크 됨)',
    bookmarkedAt      datetime                           null comment '메시지가 북마크된 시간',
    deleted           tinyint                            null comment '메시지가 삭제되었는지 여부를 나타내는 플래그 (0: 삭제 안됨, 1: 삭제됨)',
    deletedAt         datetime(6)                        null comment '메시지가 삭제된 시간',
    updatedAt         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '갱신일자',
    createdAt         datetime default CURRENT_TIMESTAMP null comment '생성일자',
    constraint fk_ChatAiMessage_ChatRoom foreign key (chatRoomId) references ChatRoom (id),
    constraint fk_ChatAiMessage_ChatUserMessage foreign key (chatUserMessageId) references ChatUserMessage (id)
) comment 'ai 채팅 대화 내역';
