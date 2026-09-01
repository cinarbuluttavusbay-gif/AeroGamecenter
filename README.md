# AeroGamecenter

Roblox kullanıcılarının çevrimiçi durumunu ve mevcut aktivitelerini takip etmeye yarayan Java tabanlı bir uygulama.

## ✨ Özellikler

* Roblox kullanıcılarını kullanıcı adına göre takip etme
* Çevrimiçi / çevrimdışı durumunu görüntüleme
* Kullanıcının aktivite türünü tespit etme
* Mümkün olduğunda kullanıcının bulunduğu oyunu görüntüleme
* Kullanıcı bilgilerini otomatik olarak yenileme
* Sürekli takip için Watch Mode desteği
* Basit Java tabanlı kullanım

## 🛠️ Gereksinimler

* Java 17 veya üzeri
* İnternet bağlantısı

## 📦 Kurulum

Projeyi bilgisayarınıza indirin:

```bash
git clone <repository-url>
cd RobloxTracker
```

Projeyi derleyin:

```bash
javac *.java
```

## ▶️ Kullanım

Uygulamayı bir Roblox kullanıcı adı ile çalıştırın:

```bash
java Main <kullanıcı_adı>
```

Örnek:

```bash
java Main Roblox
```

### 👀 İzleme Modu

Kullanıcının durumunu belirli aralıklarla otomatik olarak kontrol etmek için `--watch` seçeneğini kullanabilirsiniz:

```bash
java Main <kullanıcı_adı> --watch 30
```

Bu komut kullanıcının durumunu her **30 saniyede bir** kontrol eder.

## 📁 Proje Yapısı

```text
RobloxTracker/
│
├── Main.java
├── RobloxApiClient.java
├── RobloxTrackerApp.java
├── README.md
└── ...
```

## ℹ️ Nasıl Çalışır?

Uygulama, kullanıcı bilgilerini ve çevrimiçi durumlarını almak için Roblox'un herkese açık web API'lerini kullanır.

Kullanıcının aktivitesine bağlı olarak aşağıdaki bilgiler görüntülenebilir:

* Çevrimdışı durumu
* Çevrimiçi durumu
* Oyun içinde olup olmadığı
* Mümkün olduğunda bulunduğu oyun hakkında bilgiler

## ⚠️ Sorumluluk Reddi

Bu proje Roblox Corporation ile bağlantılı değildir, Roblox tarafından desteklenmemektedir veya resmi olarak onaylanmamıştır.

Roblox, Roblox Corporation'ın tescilli ticari markasıdır.

## 📄 Lisans

Bu proje eğitim ve kişisel kullanım amaçlı geliştirilmiştir.
