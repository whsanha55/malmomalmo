package com.demo.malmo.chat.enums;

public enum ChatRoleEnum {

    WHITE_HAT,
    BLACK_HAT,
    GREEN_HAT,
    YELLOW_HAT,
    RED_HAT,
    BLUE_HAT,
    BLUE_HAT_BEGIN,
    SUMMARY,
    SUMMARY_ROOM_NAME,
    ;

    public boolean isHat() {
        return this.name().contains("HAT");
    }

}
