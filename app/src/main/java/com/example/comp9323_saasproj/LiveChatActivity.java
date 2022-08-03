package com.example.comp9323_saasproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.comp9323_saasproj.adapter.ChatAdapter;
import com.example.comp9323_saasproj.databinding.ActivityLiveChatBinding;
import com.example.comp9323_saasproj.models.ChatMessage;
import com.example.comp9323_saasproj.models.User;
import com.example.comp9323_saasproj.utilities.Constants;
import com.example.comp9323_saasproj.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LiveChatActivity extends AppCompatActivity {

    // This activity handles what happens in real-time chat.
    // Need an ActivityLiveChatBinding object to set content view.
    // Need an User object to store info of the receiver.
    // Need a list to store messages.
    // Need a ChatAdapter to fill message blobs with a real message.
    // Need a String variable to store the ID of certain conversation.
    private ActivityLiveChatBinding binding;
    private User receiveUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiveDetails();
        init();
        listenMessages();
    }

    // Function sending a message.
    private void sendMessage() {

        // Package all attributes of a message in a hash map and add it to database.
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
        message.put(Constants.KEY_RECEIVER_EMAIL, receiveUser.email);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        // If it is the first conversation between two users, then add a new conversation record to 'conversation' table.
        if (conversionId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            conversion.put(Constants.KEY_RECEIVER_EMAIL, receiveUser.email);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        // Fill this blank message blob with the content of message.
        binding.inputMessage.setText(null);
    }

    // Find all messages between two certain users for further displaying on the screen.
    private void listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL))
                .whereEqualTo(Constants.KEY_RECEIVER_EMAIL, receiveUser.email)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_EMAIL, receiveUser.email)
                .whereEqualTo(Constants.KEY_RECEIVER_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL))
                .addSnapshotListener(eventListener);
    }

    // Initialization
    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                preferenceManager.getString(Constants.KEY_EMAIL)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    // Set event listener to monitor changes in table 'messages'.
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        // Add new message records to the table.
        if(value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderEmail = documentChange.getDocument().getString(Constants.KEY_SENDER_EMAIL);
                    chatMessage.receiverEmail = documentChange.getDocument().getString(Constants.KEY_RECEIVER_EMAIL);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            // Sort records by creating time.
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            // If no existing messages between two users, then monitor whether this situation changes.
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }else{
                // If there exists messages, then monitor whether new records are inserted.
                // If inserted, scroll to the position of it.
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.porgressBar.setVisibility(View.GONE);
        if(conversionId == null){
            checkForConversion();
        }
    };

    // Get the info of receiver from previous page and set the title of this page to be receiver's E-mail.
    private void loadReceiveDetails() {
        receiveUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textEmail.setText(receiveUser.email);
    }

    // Set listeners for back button and sending button.
    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    // Add a new conversation (which means two users has never talked with each other before this) to the table of 'conversations'.
    private void addConversion(HashMap<String, Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    // Update existing conversation with the content and creating time of the last message.
    // This is used to display recent chat.
    private void updateConversion(String message){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }

    // Get conversation info.
    private void checkForConversion() {
        if(chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_EMAIL),
                    receiveUser.email
            );
            checkForConversionRemotely(
                 receiveUser.email,
                 preferenceManager.getString(Constants.KEY_EMAIL)
            );
        }
    }

    // Request database to get records of conversations.
    private void checkForConversionRemotely(String senderEmail, String receiverEmail){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_EMAIL, senderEmail)
                .whereEqualTo(Constants.KEY_RECEIVER_EMAIL, receiverEmail)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    // Get E-mails of all other users except for the user him/herself.
    // It is used to list all available users in 'Select user' when starting to chat.
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };
}