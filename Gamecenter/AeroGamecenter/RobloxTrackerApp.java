import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class RobloxTrackerApp {

    // ============================================================
    // RENKLER
    // ============================================================

    private static final Color BG =
            new Color(14, 18, 32);

    private static final Color PANEL =
            new Color(23, 29, 49);

    private static final Color CARD =
            new Color(30, 37, 61);

    private static final Color CARD_HOVER =
            new Color(39, 47, 76);

    private static final Color PURPLE =
            new Color(130, 75, 255);

    private static final Color PURPLE_DARK =
            new Color(80, 45, 170);

    private static final Color GREEN =
            new Color(50, 220, 120);

    private static final Color RED =
            new Color(255, 80, 90);

    private static final Color TEXT =
            new Color(235, 238, 250);

    private static final Color SUBTEXT =
            new Color(150, 160, 185);


    // ============================================================
    // UI
    // ============================================================

    private JFrame frame;

    private JTextField usernameField;

    private JButton connectButton;
    private JButton refreshButton;

    private JSpinner intervalSpinner;

    private JLabel statusLabel;
    private JLabel friendCountLabel;

    private JPanel friendsPanel;
    private JScrollPane scrollPane;

    private javax.swing.Timer refreshTimer;

    private final RobloxApiClient client;

    private List<RobloxApiClient.Friend> friends;

    private Map<Long, String> avatarUrls =
            new HashMap<>();


    // ============================================================
    // MAIN
    // ============================================================
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new RobloxTrackerApp().show());
}

public RobloxTrackerApp() {
    this.client = new RobloxApiClient();
    buildUi();
}

public void show() {
    frame.setVisible(true);
}

    // ============================================================
    // UI OLUŞTUR
    // ============================================================

    private void buildUi() {

        frame =
                new JFrame("Aero GameCenter");

        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        frame.setSize(950, 700);

        frame.setMinimumSize(
                new Dimension(750, 500)
        );

        frame.setLocationRelativeTo(null);

        frame.setLayout(
                new BorderLayout()
        );

        frame.getContentPane()
                .setBackground(BG);


        // --------------------------------------------------------
        // HEADER
        // --------------------------------------------------------

        JPanel header =
                createHeader();


        // --------------------------------------------------------
        // SOL PANEL
        // --------------------------------------------------------

        JPanel sidebar =
                createSidebar();


        // --------------------------------------------------------
        // ANA İÇERİK
        // --------------------------------------------------------

        JPanel content =
                createContent();


        // --------------------------------------------------------
        // ALT DURUM ÇUBUĞU
        // --------------------------------------------------------

        JPanel footer =
                createFooter();


        frame.add(
                header,
                BorderLayout.NORTH
        );

        frame.add(
                sidebar,
                BorderLayout.WEST
        );

        frame.add(
                content,
                BorderLayout.CENTER
        );

        frame.add(
                footer,
                BorderLayout.SOUTH
        );


        // --------------------------------------------------------
        // EVENTS
        // --------------------------------------------------------

        connectButton.addActionListener(
                e -> connect()
        );

        usernameField.addActionListener(
                e -> connect()
        );

        refreshButton.addActionListener(
                e -> refreshStatuses()
        );
    }


    // ============================================================
    // HEADER
    // ============================================================

    private JPanel createHeader() {

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(PANEL);

        header.setBorder(
                new EmptyBorder(
                        14,
                        20,
                        14,
                        20
                )
        );


        // LOGO ALANI

        JPanel logoPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        logoPanel.setOpaque(false);


        JLabel logo =
                new JLabel("◈");

        logo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        32
                )
        );

        logo.setForeground(PURPLE);


        JPanel titlePanel =
                new JPanel(
                        new GridLayout(2, 1)
                );

        titlePanel.setOpaque(false);


        JLabel title =
                new JLabel("AERO GAMECENTER");

        title.setForeground(TEXT);

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );


        JLabel subtitle =
                new JLabel(
                        "Roblox Friend Tracker"
                );

        subtitle.setForeground(SUBTEXT);

        subtitle.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );


        titlePanel.add(title);
        titlePanel.add(subtitle);

        logoPanel.add(logo);
        logoPanel.add(titlePanel);


        // SAĞ BAĞLANTI DURUMU

        JLabel connection =
                new JLabel("● Roblox API");

        connection.setForeground(GREEN);

        connection.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );


        header.add(
                logoPanel,
                BorderLayout.WEST
        );

        header.add(
                connection,
                BorderLayout.EAST
        );


        return header;
    }


    // ============================================================
    // SIDEBAR
    // ============================================================

    private JPanel createSidebar() {

        JPanel sidebar =
                new JPanel();

        sidebar.setLayout(
                new BoxLayout(
                        sidebar,
                        BoxLayout.Y_AXIS
                )
        );

        sidebar.setBackground(
                new Color(18, 23, 40)
        );

        sidebar.setPreferredSize(
                new Dimension(180, 0)
        );

        sidebar.setBorder(
                new EmptyBorder(
                        20,
                        15,
                        20,
                        15
                )
        );


        JLabel section =
                new JLabel("DASHBOARD");

        section.setForeground(SUBTEXT);

        section.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        section.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        11
                )
        );


        sidebar.add(section);

        sidebar.add(
                Box.createVerticalStrut(15)
        );


        JButton friendsButton =
                createSidebarButton(
                        "👥   Arkadaşlar"
                );

        JButton activityButton =
                createSidebarButton(
                        "🎮   Oyun Durumu"
                );

        JButton settingsButton =
                createSidebarButton(
                        "⚙   Ayarlar"
                );


        sidebar.add(friendsButton);

        sidebar.add(
                Box.createVerticalStrut(8)
        );

        sidebar.add(activityButton);

        sidebar.add(
                Box.createVerticalStrut(8)
        );

        sidebar.add(settingsButton);


        sidebar.add(
                Box.createVerticalGlue()
        );


        JLabel version =
                new JLabel("v1.0.0");

        version.setForeground(
                new Color(90, 100, 125)
        );

        version.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        sidebar.add(version);


        return sidebar;
    }


    private JButton createSidebarButton(
            String text) {

        JButton button =
                new JButton(text);

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setFocusPainted(false);

        button.setForeground(TEXT);

        button.setBackground(CARD);

        button.setBorderPainted(false);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        0,
                        0
                )
        );

        return button;
    }


    // ============================================================
    // ANA İÇERİK
    // ============================================================

    private JPanel createContent() {

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );

        content.setBackground(BG);

        content.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );


        // --------------------------------------------------------
        // SEARCH PANEL
        // --------------------------------------------------------

        JPanel searchPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        searchPanel.setOpaque(false);


        usernameField =
                new JTextField();

        usernameField.setPreferredSize(
                new Dimension(300, 42)
        );

        usernameField.setBackground(CARD);

        usernameField.setForeground(TEXT);

        usernameField.setCaretColor(TEXT);

        usernameField.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        15
                )
        );

        usernameField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        60,
                                        70,
                                        100
                                )
                        ),
                        new EmptyBorder(
                                0,
                                12,
                                0,
                                12
                        )
                )
        );


        connectButton =
                createPurpleButton(
                        "Bağlan"
                );


        refreshButton =
                createDarkButton(
                        "↻ Yenile"
                );


        JPanel rightButtons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        rightButtons.setOpaque(false);

        rightButtons.add(connectButton);
        rightButtons.add(refreshButton);


        searchPanel.add(
                usernameField,
                BorderLayout.CENTER
        );

        searchPanel.add(
                rightButtons,
                BorderLayout.EAST
        );


        // --------------------------------------------------------
        // FRIENDS HEADER
        // --------------------------------------------------------

        JPanel listHeader =
                new JPanel(
                        new BorderLayout()
                );

        listHeader.setOpaque(false);


        friendCountLabel =
                new JLabel(
                        "ARKADAŞLAR"
                );

        friendCountLabel.setForeground(TEXT);

        friendCountLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );


        JLabel intervalLabel =
                new JLabel(
                        "Otomatik yenileme:"
                );

        intervalLabel.setForeground(SUBTEXT);


        intervalSpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                30,
                                5,
                                600,
                                5
                        )
                );

        intervalSpinner.setPreferredSize(
                new Dimension(
                        65,
                        28
                )
        );


        JPanel intervalPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        intervalPanel.setOpaque(false);

        intervalPanel.add(intervalLabel);

        intervalPanel.add(intervalSpinner);

        intervalPanel.add(
                new JLabel("sn")
        );


        listHeader.add(
                friendCountLabel,
                BorderLayout.WEST
        );

        listHeader.add(
                intervalPanel,
                BorderLayout.EAST
        );


        // --------------------------------------------------------
        // FRIEND LIST
        // --------------------------------------------------------

        friendsPanel =
                new JPanel();

        friendsPanel.setLayout(
                new BoxLayout(
                        friendsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        friendsPanel.setBackground(BG);


        scrollPane =
                new JScrollPane(
                        friendsPanel
                );

        scrollPane.setBorder(null);

        scrollPane.getViewport()
                .setBackground(BG);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);


        JPanel topContent =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );

        topContent.setOpaque(false);

        topContent.add(
                searchPanel,
                BorderLayout.NORTH
        );

        topContent.add(
                listHeader,
                BorderLayout.SOUTH
        );


        content.add(
                topContent,
                BorderLayout.NORTH
        );

        content.add(
                scrollPane,
                BorderLayout.CENTER
        );


        return content;
    }


    // ============================================================
    // FOOTER
    // ============================================================

    private JPanel createFooter() {

        JPanel footer =
                new JPanel(
                        new BorderLayout()
                );

        footer.setBackground(PANEL);

        footer.setBorder(
                new EmptyBorder(
                        8,
                        20,
                        8,
                        20
                )
        );


        statusLabel =
                new JLabel("Hazır.");

        statusLabel.setForeground(SUBTEXT);


        footer.add(
                statusLabel,
                BorderLayout.WEST
        );


        return footer;
    }


    // ============================================================
    // BUTTONS
    // ============================================================

    private JButton createPurpleButton(
            String text) {

        JButton button =
                new JButton(text);

        button.setPreferredSize(
                new Dimension(110, 42)
        );

        button.setFocusPainted(false);

        button.setForeground(Color.WHITE);

        button.setBackground(PURPLE);

        button.setBorderPainted(false);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        return button;
    }


    private JButton createDarkButton(
            String text) {

        JButton button =
                new JButton(text);

        button.setPreferredSize(
                new Dimension(100, 42)
        );

        button.setFocusPainted(false);

        button.setForeground(TEXT);

        button.setBackground(CARD);

        button.setBorderPainted(false);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        return button;
    }


    // ============================================================
    // BAĞLAN
    // ============================================================

    private void connect() {

        String username =
                usernameField.getText()
                        .trim();

        if (username.isEmpty()) {

            statusLabel.setText(
                    "Önce kullanıcı adı gir."
            );

            return;
        }


        connectButton.setEnabled(false);

        statusLabel.setText(
                "Kullanıcı aranıyor: " +
                        username
        );


        friendsPanel.removeAll();

        friendsPanel.revalidate();

        friendsPanel.repaint();


        if (refreshTimer != null) {

            refreshTimer.stop();
        }


        new SwingWorker<
                ConnectionResult,
                Void
                >() {

            @Override
            protected ConnectionResult
            doInBackground()
                    throws Exception {

                Long userId =
                        client.resolveUserId(
                                username
                        );

                if (userId == null) {

                    throw new RuntimeException(
                            "Kullanıcı bulunamadı: " +
                                    username
                    );
                }


                List<RobloxApiClient.Friend>
                        loadedFriends =
                        client.getFriends(
                                userId
                        );


                List<Long> ids =
                        loadedFriends.stream()
                                .map(
                                        RobloxApiClient.Friend::id
                                )
                                .collect(
                                        Collectors.toList()
                                );


                Map<Long, String>
                        loadedAvatars =
                        client.getAvatarUrls(
                                ids
                        );


                return new ConnectionResult(
                        loadedFriends,
                        loadedAvatars
                );
            }


            @Override
            protected void done() {

                connectButton.setEnabled(true);

                try {

                    ConnectionResult result =
                            get();

                    friends =
                            result.friends;

                    avatarUrls =
                            result.avatarUrls;


                    if (friends.isEmpty()) {

                        statusLabel.setText(
                                "Arkadaş listesi boş görünüyor."
                        );

                        return;
                    }


                    friendCountLabel.setText(
                            "ARKADAŞLAR (" +
                                    friends.size() +
                                    ")"
                    );


                    statusLabel.setText(
                            friends.size() +
                                    " arkadaş bulundu."
                    );


                    refreshStatuses();


                    int intervalSec =
                            (Integer)
                                    intervalSpinner
                                            .getValue();


                    refreshTimer =
                            new javax.swing.Timer(
                                    intervalSec * 1000,
                                    e -> refreshStatuses()
                            );


                    refreshTimer.start();


                } catch (Exception ex) {

                    statusLabel.setText(
                            "Hata: " +
                                    rootMessage(ex)
                    );
                }
            }

        }.execute();
    }


    // ============================================================
    // DURUMLARI YENİLE
    // ============================================================

    private void refreshStatuses() {

        if (friends == null ||
                friends.isEmpty()) {

            return;
        }


        refreshButton.setEnabled(false);

        statusLabel.setText(
                "Arkadaş durumları güncelleniyor..."
        );


        List<Long> ids =
                friends.stream()
                        .map(
                                RobloxApiClient.Friend::id
                        )
                        .collect(
                                Collectors.toList()
                        );


        new SwingWorker<
                Map<Long,
                        RobloxApiClient.PresenceInfo>,
                Void
                >() {

            @Override
            protected Map<Long,
                    RobloxApiClient.PresenceInfo>
            doInBackground()
                    throws Exception {

                return client.getPresence(ids);
            }


            @Override
            protected void done() {

                refreshButton.setEnabled(true);

                try {

                    Map<Long,
                            RobloxApiClient.PresenceInfo>
                            presences =
                            get();


                    updateFriendCards(
                            presences
                    );


                    statusLabel.setText(
                            "Son güncelleme: " +
                                    LocalTime.now()
                                            .withNano(0)
                    );


                } catch (Exception ex) {

                    statusLabel.setText(
                            "Hata: " +
                                    rootMessage(ex)
                    );
                }
            }

        }.execute();
    }


    // ============================================================
    // FRIEND CARD'LARI OLUŞTUR
    // ============================================================

    private void updateFriendCards(
            Map<Long,
                    RobloxApiClient.PresenceInfo>
                    presences) {


        friendsPanel.removeAll();


        for (RobloxApiClient.Friend friend
                : friends) {


            RobloxApiClient.PresenceInfo presence =
                    presences.get(
                            friend.id()
                    );


            // Offline arkadaşları gizle
            if (presence == null ||
                    presence.presenceType() == 0) {

                continue;
            }


            FriendCard card =
                    new FriendCard(
                            friend,
                            presence,
                            avatarUrls.get(
                                    friend.id()
                            )
                    );


            friendsPanel.add(card);

            friendsPanel.add(
                    Box.createVerticalStrut(8)
            );
        }


        if (friendsPanel.getComponentCount() == 0) {

            JLabel empty =
                    new JLabel(
                            "Şu anda çevrimiçi arkadaş bulunamadı."
                    );

            empty.setForeground(SUBTEXT);

            empty.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            empty.setBorder(
                    new EmptyBorder(
                            40,
                            0,
                            0,
                            0
                    )
            );

            friendsPanel.add(empty);
        }


        friendsPanel.revalidate();

        friendsPanel.repaint();
    }


    // ============================================================
    // FRIEND CARD
    // ============================================================

    private class FriendCard
            extends JPanel {


        private final JLabel avatarLabel;


        FriendCard(
                RobloxApiClient.Friend friend,
                RobloxApiClient.PresenceInfo presence,
                String avatarUrl
        ) {

            setLayout(
                    new BorderLayout(
                            15,
                            0
                    )
            );


            setBackground(CARD);


            setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            82
                    )
            );


            setPreferredSize(
                    new Dimension(
                            0,
                            82
                    )
            );


            setBorder(
                    new EmptyBorder(
                            10,
                            14,
                            10,
                            14
                    )
            );


            // ----------------------------------------------------
            // AVATAR
            // ----------------------------------------------------

            avatarLabel =
                    new JLabel(
                            createPlaceholderAvatar()
                    );

            avatarLabel.setPreferredSize(
                    new Dimension(
                            60,
                            60
                    )
            );


            add(
                    avatarLabel,
                    BorderLayout.WEST
            );


            if (avatarUrl != null) {

                loadAvatarAsync(
                        avatarUrl,
                        avatarLabel
                );
            }


            // ----------------------------------------------------
            // İSİM VE DURUM
            // ----------------------------------------------------

            JPanel info =
                    new JPanel(
                            new GridLayout(
                                    2,
                                    1
                            )
                    );

            info.setOpaque(false);


            String name =
                    friend.displayName() == null ||
                            friend.displayName().isBlank()

                            ? friend.username()

                            : friend.displayName();


            JLabel nameLabel =
                    new JLabel(name);

            nameLabel.setForeground(TEXT);

            nameLabel.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            16
                    )
            );


            JLabel statusLabel =
                    new JLabel(
                            presence.statusText()
                    );


            statusLabel.setFont(
                    new Font(
                            "SansSerif",
                            Font.PLAIN,
                            13
                    )
            );


            if (presence.presenceType() == 2) {

                statusLabel.setForeground(
                        GREEN
                );

            } else {

                statusLabel.setForeground(
                        new Color(
                                100,
                                180,
                                255
                        )
                );
            }


            info.add(nameLabel);
            info.add(statusLabel);


            add(
                    info,
                    BorderLayout.CENTER
            );


            // ----------------------------------------------------
            // ONLINE DOT
            // ----------------------------------------------------

            JLabel online =
                    new JLabel("●");

            online.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            22
                    )
            );


            online.setForeground(
                    presence.presenceType() == 2
                            ? GREEN
                            : new Color(
                                    100,
                                    180,
                                    255
                            )
            );


            add(
                    online,
                    BorderLayout.EAST
            );
        }
    }


    // ============================================================
    // AVATAR ASYNC LOAD
    // ============================================================

    private void loadAvatarAsync(
            String url,
            JLabel target
    ) {

        new SwingWorker<ImageIcon, Void>() {

            @Override
            protected ImageIcon
            doInBackground()
                    throws Exception {

                BufferedImage image =
                        ImageIO.read(
                                new URL(url)
                        );


                if (image == null) {

                    return null;
                }


                Image scaled =
                        image.getScaledInstance(
                                58,
                                58,
                                Image.SCALE_SMOOTH
                        );


                return new ImageIcon(
                        scaled
                );
            }


            @Override
            protected void done() {

                try {

                    ImageIcon icon =
                            get();

                    if (icon != null) {

                        target.setIcon(
                                icon
                        );
                    }

                } catch (Exception ignored) {
                }
            }

        }.execute();
    }


    // ============================================================
    // PLACEHOLDER AVATAR
    // ============================================================

    private ImageIcon createPlaceholderAvatar() {

        BufferedImage image =
                new BufferedImage(
                        58,
                        58,
                        BufferedImage.TYPE_INT_ARGB
                );


        Graphics2D g =
                image.createGraphics();


        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        g.setColor(
                new Color(
                        60,
                        70,
                        110
                )
        );


        g.fillOval(
                2,
                2,
                54,
                54
        );


        g.setColor(
                new Color(
                        150,
                        160,
                        210
                )
        );


        g.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );


        g.drawString(
                "?",
                22,
                37
        );


        g.dispose();


        return new ImageIcon(
                image
        );
    }


    // ============================================================
    // HATA MESAJI
    // ============================================================

    private static String rootMessage(
            Exception ex) {

        Throwable t =
                ex;


        while (t.getCause() != null) {

            t =
                    t.getCause();
        }


        return t.getMessage() != null

                ? t.getMessage()

                : t.toString();
    }


    // ============================================================
    // CONNECTION RESULT
    // ============================================================

    private static class ConnectionResult {

        private final List<RobloxApiClient.Friend>
                friends;

        private final Map<Long, String>
                avatarUrls;


        ConnectionResult(
                List<RobloxApiClient.Friend> friends,
                Map<Long, String> avatarUrls
        ) {

            this.friends =
                    friends;

            this.avatarUrls =
                    avatarUrls;
        }
    }
}