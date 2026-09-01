import java.net.URI;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class RobloxApiClient {

    private final HttpClient http;
private final String roblosecurityCookie;

public RobloxApiClient(
        String roblosecurityCookie,
        String proxyHost,
        int proxyPort
) {
    HttpClient.Builder builder = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10));

    // Proxy bilgisi varsa proxy kullan
    if (proxyHost != null &&
            !proxyHost.isBlank() &&
            proxyPort > 0) {

        builder.proxy(
                ProxySelector.of(
                        new InetSocketAddress(
                                proxyHost,
                                proxyPort
                        )
                )
        );
    }

    this.http = builder.build();
    this.roblosecurityCookie = roblosecurityCookie;
}

// Proxy olmadan kullanım
public RobloxApiClient(String roblosecurityCookie) {
    this(
            roblosecurityCookie,
            null,
            0
    );
}

// ============================================================
// AVATAR URL'LERİNİ GETİR
// ============================================================

public Map<Long, String> getAvatarUrls(List<Long> userIds) {

    Map<Long, String> result = new LinkedHashMap<>();

    if (userIds == null || userIds.isEmpty()) {
        return result;
    }

    int batchSize = 100;

    for (int start = 0;
         start < userIds.size();
         start += batchSize) {

        List<Long> batch = userIds.subList(
                start,
                Math.min(start + batchSize, userIds.size())
        );

        String ids = batch.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String url =
                "https://thumbnails.roblox.com/v1/users/avatar-headshot"
                        + "?userIds=" + ids
                        + "&size=150x150"
                        + "&format=Png"
                        + "&isCircular=false";

        Map<String, Object> response = getJson(url);

        List<Object> data =
                MiniJson.asArray(response.get("data"));

        for (Object object : data) {

            Map<String, Object> avatar =
                    MiniJson.asObject(object);

            Long userId =
                    MiniJson.asLong(
                            avatar.get("targetId")
                    );

            String imageUrl =
                    MiniJson.asString(
                            avatar.get("imageUrl")
                    );

            if (userId != null && imageUrl != null) {
                result.put(userId, imageUrl);
            }
        }
    }

    return result;
}

// Cookie olmadan kullanım
public RobloxApiClient() {
    this(null);
}
    // ============================================================
    // KULLANICI ADINDAN USER ID BUL
    // ============================================================

    public Long resolveUserId(String username) {
        String body =
                "{\"usernames\":[\"" +
                escapeJson(username) +
                "\"],\"excludeBannedUsers\":false}";

        Map<String, Object> response =
                postJson(
                        "https://users.roblox.com/v1/usernames/users",
                        body
                );

        List<Object> data =
                MiniJson.asArray(response.get("data"));

        if (data.isEmpty()) {
            return null;
        }

        Map<String, Object> user =
                MiniJson.asObject(data.get(0));

        return MiniJson.asLong(user.get("id"));
    }

    // ============================================================
    // ARKADAŞLARI GETİR
    // ============================================================

    public List<Friend> getFriends(long userId) {

        String url =
                "https://friends.roblox.com/v1/users/"
                        + userId
                        + "/friends";

        Map<String, Object> response = getJson(url);

        List<Object> data =
                MiniJson.asArray(response.get("data"));

        if (data.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> ids = new ArrayList<>();

        for (Object object : data) {

            Map<String, Object> friend =
                    MiniJson.asObject(object);

            Long id =
                    MiniJson.asLong(friend.get("id"));

            if (id != null) {
                ids.add(id);
            }
        }

        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, String[]> names =
                getUsernamesByIds(ids);

        List<Friend> friends =
                new ArrayList<>();

        for (Long id : ids) {

            String[] nameData =
                    names.get(id);

            String username =
                    nameData != null && nameData[0] != null
                            ? nameData[0]
                            : "ID:" + id;

            String displayName =
                    nameData != null
                            ? nameData[1]
                            : null;

            friends.add(
                    new Friend(
                            id,
                            username,
                            displayName
                    )
            );
        }

        return friends;
    }

    // ============================================================
    // KULLANICI ADLARINI GETİR
    // ============================================================

    private Map<Long, String[]> getUsernamesByIds(
            List<Long> userIds) {

        Map<Long, String[]> result =
                new LinkedHashMap<>();

        int batchSize = 90;

        for (int start = 0;
             start < userIds.size();
             start += batchSize) {

            List<Long> batch =
                    userIds.subList(
                            start,
                            Math.min(
                                    start + batchSize,
                                    userIds.size()
                            )
                    );

            String ids =
                    batch.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));

            String body =
                    "{\"userIds\":[" +
                    ids +
                    "],\"excludeBannedUsers\":false}";

            Map<String, Object> response =
                    postJson(
                            "https://users.roblox.com/v1/users",
                            body
                    );

            List<Object> data =
                    MiniJson.asArray(
                            response.get("data")
                    );

            for (Object object : data) {

                Map<String, Object> user =
                        MiniJson.asObject(object);

                Long id =
                        MiniJson.asLong(
                                user.get("id")
                        );

                if (id == null) {
                    continue;
                }

                String username =
                        MiniJson.asString(
                                user.get("name")
                        );

                String displayName =
                        MiniJson.asString(
                                user.get("displayName")
                        );

                result.put(
                        id,
                        new String[]{
                                username,
                                displayName
                        }
                );
            }
        }

        return result;
    }

    // ============================================================
    // PRESENCE
    // ============================================================

    public Map<Long, PresenceInfo> getPresence(
            List<Long> userIds) {

        Map<Long, PresenceInfo> result =
                new LinkedHashMap<>();

        if (userIds == null ||
            userIds.isEmpty()) {

            return result;
        }

        // 50'lik gruplar halinde gönderiyoruz.
        int batchSize = 50;

        for (int start = 0;
             start < userIds.size();
             start += batchSize) {

            List<Long> batch =
                    userIds.subList(
                            start,
                            Math.min(
                                    start + batchSize,
                                    userIds.size()
                            )
                    );

            String ids =
                    batch.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));

            String body =
                    "{\"userIds\":[" +
                    ids +
                    "]}";

            Map<String, Object> response =
                    postJson(
                            "https://presence.roblox.com/v1/presence/users",
                            body
                    );

            List<Object> presences =
                    MiniJson.asArray(
                            response.get("userPresences")
                    );

            for (Object object : presences) {

                Map<String, Object> p =
                        MiniJson.asObject(object);

                Long uid =
                        MiniJson.asLong(
                                p.get("userId")
                        );

                if (uid == null) {
                    continue;
                }

                int presenceType =
                        MiniJson.asInt(
                                p.get("userPresenceType")
                        );

                String lastLocation =
                        MiniJson.asString(
                                p.get("lastLocation")
                        );

                Long placeId =
                        p.get("placeId") == null
                                ? null
                                : MiniJson.asLong(
                                        p.get("placeId")
                                );

                Long universeId =
                        p.get("universeId") == null
                                ? null
                                : MiniJson.asLong(
                                        p.get("universeId")
                                );

                String gameId =
                        MiniJson.asString(
                                p.get("gameId")
                        );

                result.put(
                        uid,
                        new PresenceInfo(
                                uid,
                                presenceType,
                                lastLocation,
                                placeId,
                                universeId,
                                gameId,
                                null
                        )
                );
            }
        }

        // ========================================================
        // OYUN İSİMLERİNİ BUL
        // ========================================================

        List<Long> universeIds =
                result.values()
                        .stream()
                        .filter(p -> p.presenceType() == 2)
                        .map(PresenceInfo::universeId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());

        Map<Long, String> gameNames =
                getGameNames(universeIds);

        // ========================================================
        // OYUN İSMİNİ PRESENCE'A EKLE
        // ========================================================

        Map<Long, PresenceInfo> updated =
                new LinkedHashMap<>();

        for (PresenceInfo p : result.values()) {

            String gameName = null;

            if (p.universeId() != null) {
                gameName =
                        gameNames.get(
                                p.universeId()
                        );
            }

            updated.put(
                    p.userId(),
                    new PresenceInfo(
                            p.userId(),
                            p.presenceType(),
                            p.lastLocation(),
                            p.placeId(),
                            p.universeId(),
                            p.gameId(),
                            gameName
                    )
            );
        }

        return updated;
    }

    // ============================================================
    // OYUN İSİMLERİNİ GETİR
    // ============================================================

    private Map<Long, String> getGameNames(
            List<Long> universeIds) {

        Map<Long, String> result =
                new LinkedHashMap<>();

        if (universeIds == null ||
            universeIds.isEmpty()) {

            return result;
        }

        String ids =
                universeIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

        String url =
                "https://games.roblox.com/v1/games?universeIds="
                        + ids;

        Map<String, Object> response =
                getJson(url);

        List<Object> data =
                MiniJson.asArray(
                        response.get("data")
                );

        for (Object object : data) {

            Map<String, Object> game =
                    MiniJson.asObject(object);

            Long id =
                    MiniJson.asLong(
                            game.get("id")
                    );

            String name =
                    MiniJson.asString(
                            game.get("name")
                    );

            if (id != null && name != null) {
                result.put(id, name);
            }
        }

        return result;
    }

    // ============================================================
    // GET
    // ============================================================

    private Map<String, Object> getJson(
            String url) {

        try {

            HttpRequest.Builder builder =
                    HttpRequest.newBuilder(
                            URI.create(url)
                    )
                    .timeout(
                            Duration.ofSeconds(10)
                    )
                    .header(
                            "Accept",
                            "application/json"
                    )
                    .GET();

            addCookieIfPresent(builder);

            HttpResponse<String> response =
                    http.send(
                            builder.build(),
                            HttpResponse.BodyHandlers.ofString(
                                    StandardCharsets.UTF_8
                            )
                    );

            checkStatus(response, url);

            return MiniJson.asObject(
                    MiniJson.parse(
                            response.body()
                    )
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "GET istegi basarisiz (" +
                    url +
                    "): " +
                    e.getMessage(),
                    e
            );
        }
    }

    // ============================================================
    // POST
    // ============================================================

    private Map<String, Object> postJson(
            String url,
            String jsonBody) {

        try {

            HttpRequest.Builder builder =
                    HttpRequest.newBuilder(
                            URI.create(url)
                    )
                    .timeout(
                            Duration.ofSeconds(10)
                    )
                    .header(
                            "Accept",
                            "application/json"
                    )
                    .header(
                            "Content-Type",
                            "application/json"
                    )
                    .POST(
                            BodyPublishers.ofString(
                                    jsonBody,
                                    StandardCharsets.UTF_8
                            )
                    );

            addCookieIfPresent(builder);

            HttpResponse<String> response =
                    http.send(
                            builder.build(),
                            HttpResponse.BodyHandlers.ofString(
                                    StandardCharsets.UTF_8
                            )
                    );

            checkStatus(response, url);

            return MiniJson.asObject(
                    MiniJson.parse(
                            response.body()
                    )
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "POST istegi basarisiz (" +
                    url +
                    "): " +
                    e.getMessage(),
                    e
            );
        }
    }

    // ============================================================
    // COOKIE
    // ============================================================

    private void addCookieIfPresent(
            HttpRequest.Builder builder) {

        if (roblosecurityCookie != null &&
            !roblosecurityCookie.isBlank()) {

            builder.header(
                    "Cookie",
                    ".ROBLOSECURITY=" +
                    roblosecurityCookie
            );
        }
    }

    // ============================================================
    // HTTP STATUS
    // ============================================================

    private void checkStatus(
            HttpResponse<String> response,
            String url) {

        if (response.statusCode() >= 400) {

            throw new RuntimeException(
                    "HTTP " +
                    response.statusCode() +
                    " (" +
                    url +
                    "): " +
                    response.body()
            );
        }
    }

    // ============================================================
    // JSON ESCAPE
    // ============================================================

    private static String escapeJson(
            String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    // ============================================================
    // FRIEND
    // ============================================================

    public record Friend(
            long id,
            String username,
            String displayName
    ) {
    }

    // ============================================================
    // PRESENCE
    // ============================================================

    public record PresenceInfo(
            long userId,
            int presenceType,
            String lastLocation,
            Long placeId,
            Long universeId,
            String gameId,
            String gameName
    ) {

        public String statusText() {

            return switch (presenceType) {

                case 0 ->
                        "Cevrimdisi";

                case 1 ->
                        "Cevrimici (sitede)";

                case 2 -> {

                    if (gameName != null &&
                        !gameName.isBlank()) {

                        yield "Oyunda: " +
                                gameName;
                    }

                    if (lastLocation != null &&
                        !lastLocation.isBlank()) {

                        yield "Oyunda: " +
                                lastLocation;
                    }

                    yield "Oyunda";
                }

                case 3 ->
                        "Roblox Studio'da";

                default ->
                        "Bilinmiyor";
            };
        }
    }
}