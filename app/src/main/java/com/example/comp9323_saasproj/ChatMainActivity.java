package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.comp9323_saasproj.adapter.RecentConversationsAdapter;
import com.example.comp9323_saasproj.databinding.ActivityChatMainBinding;
import com.example.comp9323_saasproj.listeners.ConversionListener;
import com.example.comp9323_saasproj.models.ChatMessage;
import com.example.comp9323_saasproj.models.User;
import com.example.comp9323_saasproj.utilities.Constants;
import com.example.comp9323_saasproj.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatMainActivity extends AppCompatActivity implements ConversionListener {

    // The main page for chat feature, where shows recent chats.
    // Need to use the info stored in preference manager.
    // Need conversation contents stored in a list.
    // Need a conversation adapter to fill containers with real values.

    private ActivityChatMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        setListeners();
        listenConversations();
    }

    private void init() {
        // Initialize the adapter and set the view of this page.
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        // Connect to Firestore database.
        database = FirebaseFirestore.getInstance();
    }

    // Set listeners for two buttons on this page.
    private void setListeners() {
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UserChatActivity.class)));
        binding.imageBack.setOnClickListener(v ->
                onBackPressed());
    }

    // Load the E-mail of current user for further displaying on the page.
    private void loadUserDetails() {
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // The function gets the record of the info of last chat between two users from Firestore.
    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL))
                .addSnapshotListener(eventListener);
    }

    // Event listener is provided by Firestore library.
    // We set the rules in it, so that we can update info when data in database is modified.
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            for(DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    // If a new record is added, then we get and store it in the list.
                    String senderEmail = documentChange.getDocument().getString(Constants.KEY_SENDER_EMAIL);
                    String receiverEmail = documentChange.getDocument().getString(Constants.KEY_RECEIVER_EMAIL);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderEmail = senderEmail;
                    chatMessage.receiverEmail = receiverEmail;
                    if(preferenceManager.getString(Constants.KEY_EMAIL).equals(senderEmail)){
                        chatMessage.conversionEmail = documentChange.getDocument().getString(Constants.KEY_RECEIVER_EMAIL);
                    }else{
                        chatMessage.conversionEmail = documentChange.getDocument().getString(Constants.KEY_SENDER_EMAIL);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                    /* If an existing record has changed, usually when the last message is updated,
                    then we find corresponding record stored in the list and update it.
                     */
                    for(int i = 0; i < conversations.size(); i++){
                        String senderEmail = documentChange.getDocument().getString(Constants.KEY_SENDER_EMAIL);
                        String receiverEmail = documentChange.getDocument().getString(Constants.KEY_RECEIVER_EMAIL);
                        if (conversations.get(i).senderEmail.equals(senderEmail) && conversations.get(i).receiverEmail.equals(receiverEmail)){
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            // Sort records in the list by date.
            // This ensures current chats are ordered by date when displaying.
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            // Configure the scroll, show items, and remove the progress bar.
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    // Select a conversation and move to typing page.
    @Override
    public void onConversionClicked(User user) {

        Intent intent = new Intent(getApplicationContext(), LiveChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}