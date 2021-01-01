package org.multiplay.chat.swing;

import org.multiplay.chat.Chat;
import org.multiplay.chat.UserInterface;
import org.multiplay.client.Friend;
import org.multiplay.client.LocalPlayer;
import org.multiplay.client.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ScrollPaneConstants;

public class ChatFrame extends JFrame implements UserInterface {
    private final Chat chat;

    private JButton loginButton = new JButton("Login");
    private JLabel guestLabel = new JLabel("Guest account: ");
    private JLabel playerName = new JLabel();

    private DefaultListModel<Session> channelsListModel = new DefaultListModel<Session>();
    private JList channelsList = new JList(channelsListModel);

    private DefaultListModel<Friend> friendsListModel = new DefaultListModel<Friend>();
    private JList friendsList = new JList(friendsListModel);

    private JPanel channelsAndFriendsPanel = new JPanel();
    private JScrollPane channelsAndFriendsPanelScrollPane = new JScrollPane(channelsAndFriendsPanel);

    private JPanel chatPanel = new JPanel();
    private JPanel messagesPane = new JPanel();
    private JScrollPane messagesScrollPane = new JScrollPane(messagesPane);
    private JTextField messageEntryField = new JTextField();

    private LocalPlayer player;

    public ChatFrame() {
        super("Multiplay Chat");
        chat = new Chat();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                chat.close();
            }
        });

        configurePanes();
        layoutPanes();
    }

    private void configurePanes() {
        Dimension minChannelsDimension = new Dimension(200, 400);
        Dimension minChatDimension = new Dimension(400, 400);

        Dimension minChatMessageScrollDimension = new Dimension(400, 350);
        Dimension minChatMessageEntryDimension = new Dimension(400, 50);

        channelsList.setBackground(Color.YELLOW);
        friendsList.setBackground(Color.ORANGE);

        channelsAndFriendsPanel.setMinimumSize(minChannelsDimension);
        channelsAndFriendsPanel.setPreferredSize(minChannelsDimension);
        channelsAndFriendsPanel.setBackground(Color.RED);


        chatPanel.setMinimumSize(minChatDimension);
        chatPanel.setBackground(Color.WHITE);

        messagesScrollPane.setMinimumSize(minChatMessageScrollDimension);
        messagesScrollPane.setWheelScrollingEnabled(true);
        messagesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        messageEntryField.setMinimumSize(minChatMessageEntryDimension);
        messageEntryField.setPreferredSize(minChatMessageEntryDimension);
        messageEntryField.setBackground(Color.LIGHT_GRAY);
        messageEntryField.setEnabled(false);
    }

    private void layoutPanes() {
        Container pane = getContentPane();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5,5,5));
        topPanel.setAlignmentX(0f);
        topPanel.add(loginButton);
        topPanel.add(guestLabel);
        topPanel.add(playerName);

        channelsAndFriendsPanel.setLayout(new BoxLayout(channelsAndFriendsPanel, BoxLayout.PAGE_AXIS));
        channelsAndFriendsPanel.setAlignmentX(0f);
        channelsAndFriendsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5,5,5));
        Box channelsBox = Box.createHorizontalBox();
        channelsBox.setAlignmentX(0f);
        channelsBox.add(new JLabel("Channels"));
        channelsBox.add(new JButton("+"));
        channelsAndFriendsPanel.add(channelsBox);
        channelsAndFriendsPanel.add(channelsList);
        channelsAndFriendsPanel.add(Box.createVerticalStrut(10));
        Box friendsBox = Box.createHorizontalBox();
        friendsBox.setAlignmentX(0f);
        friendsBox.add(new JLabel("Friends"));
        friendsBox.add(new JButton("+"));
        channelsAndFriendsPanel.add(friendsBox);
        channelsAndFriendsPanel.add(friendsList);

        messageEntryField.setBorder(BorderFactory.createEmptyBorder(5, 5,5,5));
        chatPanel.setLayout(new BorderLayout());
        chatPanel.add(messagesScrollPane, BorderLayout.CENTER);
        chatPanel.add(messageEntryField, BorderLayout.PAGE_END);

        pane.add(topPanel, BorderLayout.PAGE_START);
        pane.add(channelsAndFriendsPanelScrollPane, BorderLayout.LINE_START);
        pane.add(chatPanel, BorderLayout.CENTER);
    }

    public void start(String[] args) throws IOException {
        chat.setUserInterface(this);
        chat.start(args);
    }

    public static void main(String[] args) throws IOException {
        ChatFrame frame = new ChatFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.start(args);
        frame.setVisible(true);
    }

    @Override
    public void updatePlayer(LocalPlayer player) {
        this.player = player;
        playerName.setText(player.getDisplayName());
        if (player.isAuthenticated()) {
            guestLabel.setVisible(false);
            loginButton.setText("Logout");
        }
        else {
            guestLabel.setVisible(true);
            loginButton.setText("Login");
        }
    }

    @Override
    public void updateChannels() {

    }

    @Override
    public void updateFriends() {
        player.fetchFriendsAsync().thenAccept(friends -> {
            for (Friend friend : friends) {
                friendsListModel.addElement(friend);
            }
        });
    }

    @Override
    public void updateMessages() {

    }
}