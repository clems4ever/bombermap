package com.game.wargame.Model.Registry;

/**
 * Created by sergei on 10/04/16.
 */
public class RegistryEntry {

    public static final String EMAIL_TOKEN = "email";
    public static final String NICKNAME_TOKEN = "nickname";
    public static final String REGID_TOKEN = "reg_id";

    private String mEmail;
    private String mNickname;
    private String mRegId;

    public RegistryEntry(String email, String nickname, String regId) {
        mEmail = email;
        mNickname = nickname;
        mRegId = regId;
    }

    public String getEmail() {
        return mEmail;
    }
    public String getNickname() {
        return mNickname;
    }
    public String getRegId() {
        return mRegId;
    }

}
