package com.demo.malmo.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRoleEnum {

    // TODO: 7/25/24 token 분리 
    WHITE_HAT("NTA0MjU2MWZlZTcxNDJiYyBdmB26fM9O5XCDoDkBgCxhAOyFLsqP9K2m0yJ7b1g9", "T1uJ6v9Mnos2RRUCtaDaqABiGNjX0T0WmKL7NNcu"),
    BLACK_HAT("NTA0MjU2MWZlZTcxNDJiYyBdmB26fM9O5XCDoDkBgCxhAOyFLsqP9K2m0yJ7b1g9", "T1uJ6v9Mnos2RRUCtaDaqABiGNjX0T0WmKL7NNcu"),
    GREEN_HAT("NTA0MjU2MWZlZTcxNDJiYyBdmB26fM9O5XCDoDkBgCxhAOyFLsqP9K2m0yJ7b1g9", "T1uJ6v9Mnos2RRUCtaDaqABiGNjX0T0WmKL7NNcu"),
    YELLOW_HAT("NTA0MjU2MWZlZTcxNDJiYyBdmB26fM9O5XCDoDkBgCxhAOyFLsqP9K2m0yJ7b1g9", "T1uJ6v9Mnos2RRUCtaDaqABiGNjX0T0WmKL7NNcu"),
    RED_HAT("NTA0MjU2MWZlZTcxNDJiYyBdmB26fM9O5XCDoDkBgCxhAOyFLsqP9K2m0yJ7b1g9", "T1uJ6v9Mnos2RRUCtaDaqABiGNjX0T0WmKL7NNcu"),
    BLUE_HAT("NTA0MjU2MWZlZTcxNDJiYyBdmB26fM9O5XCDoDkBgCxhAOyFLsqP9K2m0yJ7b1g9", "T1uJ6v9Mnos2RRUCtaDaqABiGNjX0T0WmKL7NNcu"),
    BLUE_HAT_BEGIN("NTA0MjU2MWZlZTcxNDJiYyBdmB26fM9O5XCDoDkBgCxhAOyFLsqP9K2m0yJ7b1g9", "T1uJ6v9Mnos2RRUCtaDaqABiGNjX0T0WmKL7NNcu"),
    ;


    private final String clovaKey;
    private final String gatewayKey;
}
