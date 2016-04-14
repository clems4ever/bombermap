package com.game.wargame.Controller.Communication.Game;

import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;
import com.game.wargame.Controller.Communication.RabbitMQ.RabbitMQSocket;
import com.game.wargame.Controller.Registry.OnRegistryEntryAddedListener;
import com.game.wargame.Controller.Registry.OnRegistryEntryFoundListener;
import com.game.wargame.Controller.Registry.OnRegistryEntryRemovedListener;
import com.game.wargame.Model.Registry.RegistryEntry;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sergei on 10/04/16.
 */
public class RegistryManagerSocket {
    private ISocket mSocket;
    private final static String REGISTRY_QUEUE = "registry";
    private final static String ACTION_TOKEN = "action";
    private final static String NICKNAME_TOKEN = "nickname";
    private final static String EMAIL_TOKEN = "email";
    private final static String REGID_TOKEN = "reg_id";
    private final static String SEARCH_ACTION = "search";
    private final static String REGISTER_ACTION = "register";
    private final static String UNREGISTER_ACTION = "unregister";

    OnRegistryEntryAddedListener mOnRegistryAddedListener;

    public RegistryManagerSocket(final ISocket socket) {
        mSocket = socket;
    }

    public void setOnRegistryEventListener(OnRegistryEntryAddedListener onRegistryAddedListener) {
        mOnRegistryAddedListener = onRegistryAddedListener;
    }

    public void search(String nickname, final OnRegistryEntryFoundListener onRegistryEntryFoundListener) {
        JSONObject message = new JSONObject();
        try {
            message.put(ACTION_TOKEN, SEARCH_ACTION);
            message.put(NICKNAME_TOKEN, nickname);
            mSocket.call(REGISTRY_QUEUE, message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) throws JSONException {
                    String email = message.getString(EMAIL_TOKEN);
                    String nickname = message.getString(NICKNAME_TOKEN);
                    String regId = message.getString(REGID_TOKEN);
                    RegistryEntry registryEntry = new RegistryEntry(email, nickname, regId);
                    onRegistryEntryFoundListener.onRegistryEntryFound(registryEntry);
                }
            });
        } catch (JSONException e) {
        }
    }

    public void register(RegistryEntry registryEntry, final OnRegistryEntryAddedListener onRegistryEntryAddedListener) {
        JSONObject message = new JSONObject();
        try {
            message.put(ACTION_TOKEN, REGISTER_ACTION);
            message.put(NICKNAME_TOKEN, registryEntry.getNickname());
            message.put(EMAIL_TOKEN, registryEntry.getEmail());
            message.put(REGID_TOKEN, registryEntry.getRegId());
            mSocket.call(REGISTRY_QUEUE, message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) throws JSONException {
                    String email = message.getString(EMAIL_TOKEN);
                    String nickname = message.getString(NICKNAME_TOKEN);
                    String regId = message.getString(REGID_TOKEN);
                    RegistryEntry registryEntry = new RegistryEntry(email, nickname, regId);
                    onRegistryEntryAddedListener.onRegistryEntryAdded(registryEntry);
                }
            });
        } catch (JSONException e) {
        }
    }

    public void unregister(RegistryEntry registryEntry, final OnRegistryEntryRemovedListener onRegistryEntryRemovedListener) {
        JSONObject message = new JSONObject();
        try {
            message.put(ACTION_TOKEN, UNREGISTER_ACTION);
            message.put(NICKNAME_TOKEN, registryEntry.getNickname());
            message.put(EMAIL_TOKEN, registryEntry.getEmail());
            message.put(REGID_TOKEN, registryEntry.getRegId());
            mSocket.call(REGISTRY_QUEUE, message, new ISocket.OnRemoteEventReceivedListener() {
                @Override
                public void onRemoteEventReceived(JSONObject message) throws JSONException {
                    String email = message.getString(EMAIL_TOKEN);
                    onRegistryEntryRemovedListener.onRegistryEntryRemoved(email);
                }
            });
        } catch (JSONException e) {
        }
    }

}
