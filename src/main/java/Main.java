import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Kullanim: java Main <cimar10006> [--watch 30]");
            return;
        }

        String username = args[0];
        int watchSeconds = -1;
        for (int i = 1; i < args.length - 1; i++) {
            if (args[i].equals("--watch")) {
                watchSeconds = Integer.parseInt(args[i + 1]);
            }
        }

       RobloxApiClient client = new RobloxApiClient();

        System.out.println("'" + username + "' kullanicisi araniyor...");
        Long userId = client.resolveUserId(username);
        if (userId == null) {
            System.out.println("Kullanici bulunamadi: " + username);
            return;
        }
        System.out.println("Bulundu, userId = " + userId);

        List<RobloxApiClient.Friend> friends = client.getFriends(userId);
        if (friends.isEmpty()) {
            System.out.println("Arkadas listesi bos gorunuyor (ya da gizlilik ayarlari nedeniyle gorulemiyor).");
            return;
        }
        System.out.println(friends.size() + " arkadas bulundu.\n");

        do {
            printStatuses(client, friends);
            if (watchSeconds > 0) {
                System.out.println("\n--- " + watchSeconds + " saniye sonra tekrar kontrol edilecek ---\n");
                Thread.sleep(watchSeconds * 1000L);
            }
        } while (watchSeconds > 0);
    }

    private static void printStatuses(RobloxApiClient client, List<RobloxApiClient.Friend> friends) {
        List<Long> ids = friends.stream().map(RobloxApiClient.Friend::id).collect(Collectors.toList());
        Map<Long, RobloxApiClient.PresenceInfo> presences = client.getPresence(ids);

        for (RobloxApiClient.Friend f : friends) {
            RobloxApiClient.PresenceInfo p = presences.get(f.id());
            String status = p == null ? "Durum alinamadi" : p.statusText();
            String displayName = (f.displayName() == null || f.displayName().isBlank()) ? f.username() : f.displayName();
            System.out.printf("%-25s %s%n", displayName, status);
        }
    }
}