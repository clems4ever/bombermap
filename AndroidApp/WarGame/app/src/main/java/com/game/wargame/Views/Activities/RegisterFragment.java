package com.game.wargame.Views.Activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.game.wargame.Controller.Communication.Game.RegistryManagerSocket;
import com.game.wargame.Controller.Communication.IConnectionManager;
import com.game.wargame.Controller.Registry.OnRegistryEntryAddedListener;
import com.game.wargame.Controller.Registry.OnRegistryEntryRemovedListener;
import com.game.wargame.Model.Registry.RegistryEntry;
import com.game.wargame.R;

import static com.game.wargame.Model.Registry.RegistryEntry.*;

/**
 * Created by sergei on 10/04/16.
 */
public class RegisterFragment extends Fragment {

    private IConnectionManager mConnectionManager;
    public static final String REGISTRY_PREFIX = "[REGISTRY]";

    public void setConnectionManager(IConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.register, container, false);

        initView(fragment);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView(View view) {
        Button registerButton = (Button) view.findViewById(R.id.register_button);
        final TextView nicknameText = (TextView) view.findViewById(R.id.nickname_edittext);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String username = nicknameText.getText().toString();

                if (!username.isEmpty()) {
                    register(username);
                } else {
                    displayAlertDialog();
                }
            }
        });
    }

    private void displayAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());

        alertDialog.setTitle("Wrong username");
        alertDialog.setMessage("Please type a valid user name.");

        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void register(final String username) {
        RegistryManagerSocket registryManagerSocket = mConnectionManager.getSocketFactory().buildRegistryManagerSocket();

        //TODO: require registry data from GCM service
        RegistryEntry registryEntry = new RegistryEntry("email@gmail.com", username, "xxx");

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString(EMAIL_TOKEN, registryEntry.getEmail());
        editor.putString(NICKNAME_TOKEN, registryEntry.getNickname());
        editor.putString(REGID_TOKEN, registryEntry.getRegId());
        editor.commit();

        Log.d(REGISTRY_PREFIX, "saving entry: " + registryEntry.getNickname() + " " + registryEntry.getEmail() + " " + registryEntry.getRegId());

        registryManagerSocket.register(registryEntry, new OnRegistryEntryAddedListener() {
            @Override
            public void onRegistryEntryAdded(RegistryEntry registryEntry) {
                saveRegId(registryEntry);
                unregister(username);
            }
        });
    }

    public void unregister(String username) {
        RegistryManagerSocket registryManagerSocket = mConnectionManager.getSocketFactory().buildRegistryManagerSocket();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String email  = preferences.getString(EMAIL_TOKEN, "");
        String nickname = preferences.getString(NICKNAME_TOKEN, "");
        String regId = preferences.getString(REGID_TOKEN, "");

        if (!email.isEmpty() && !nickname.isEmpty() && !regId.isEmpty()) {
            RegistryEntry registryEntry = new RegistryEntry("email@gmail.com", username, "xxx");

            Log.d(REGISTRY_PREFIX, "removing entry: " + registryEntry.getNickname() + " " + registryEntry.getEmail() + " " + registryEntry.getRegId());

            registryManagerSocket.unregister(registryEntry, new OnRegistryEntryRemovedListener() {
                @Override
                public void onRegistryEntryRemoved(String email) {
                    Log.d(REGISTRY_PREFIX, "entry removed : " + email);
                }
            });
        }
    }

    public void saveRegId(RegistryEntry registryEntry) {
        Log.d(REGISTRY_PREFIX, "entry saved : " + registryEntry.getNickname() + " " + registryEntry.getEmail() + " " + registryEntry.getRegId());
    }
}
